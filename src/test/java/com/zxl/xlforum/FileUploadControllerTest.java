package com.zxl.xlforum;

import com.zxl.xlforum.account.controller.FileUploadController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(FileUploadController.class)
@TestPropertySource(properties = "file.upload-dir=src/test/resources/test-uploads") // 测试专用上传目录
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${file.upload-dir}")
    private String testUploadDir;

    private static String uploadedFileName; // 记录上传成功的文件名，用于下载测试


    // 测试前初始化目录
    @BeforeEach
    void setUp() throws IOException {
        Path dirPath = Paths.get(testUploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }


    // 批量测试上传功能：参数为[测试文件, 预期结果前缀]
    @ParameterizedTest
    @MethodSource("fileUploadTestParams")
    @Order(1)
    void testUploadFile(MockMultipartFile testFile, String expectedResultPrefix) throws Exception {
        mockMvc.perform(multipart("/upload") // 假设FileUploadApi的上传路径是/upload
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith(expectedResultPrefix)));

        // 如果是成功的用例，记录文件名（用于后续下载测试）
        if (expectedResultPrefix.startsWith("上传成功")) {
            String response = mockMvc.perform(multipart("/upload").file(testFile))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            // 从响应中提取文件名（格式："上传成功：{path}/{filename}"）
            uploadedFileName = response.substring(response.lastIndexOf(File.separator) + 1);
        }
    }


    // 测试下载功能（依赖上传成功的文件）
    @Test
    @Order(2)
    void testDownloadFile_Success() throws Exception {
        Assumptions.assumeTrue(uploadedFileName != null, "未找到上传成功的文件，跳过下载测试");

        mockMvc.perform(get("/download/" + uploadedFileName)) // 假设FileUploadApi的下载路径是/download
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }


    // 测试下载不存在的文件
    @Test
    @Order(3)
    void testDownloadFile_NotFound() throws Exception {
        String nonExistentFile = "non_existent_file.txt";
        mockMvc.perform(get("/download/" + nonExistentFile))
                .andExpect(status().is5xxServerError()) // 原代码抛出RuntimeException，会导致500
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof RuntimeException));
    }


    // 提供上传测试的参数：(测试文件, 预期结果前缀)
    static Stream<Object[]> fileUploadTestParams() {
        // 正常文件（txt）
        MockMultipartFile normalTxtFile = new MockMultipartFile(
                "file", // 参数名（需与接口一致）
                "test.txt", // 原始文件名
                MediaType.TEXT_PLAIN_VALUE,
                "测试文本内容".getBytes()
        );

        // 正常文件（jpg）
        MockMultipartFile normalImgFile = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF} // 简单的jpg头
        );

        // 空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        return Stream.of(
                new Object[]{normalTxtFile, "上传成功"},
                new Object[]{normalImgFile, "上传成功"},
                new Object[]{emptyFile, "上传失败：文件为空"}
        );
    }
}