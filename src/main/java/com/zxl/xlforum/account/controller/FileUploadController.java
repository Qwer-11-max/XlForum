package com.zxl.xlforum.account.controller;

import com.zxl.xlforum.account.api.FileUploadApi;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Validated
public class FileUploadController implements FileUploadApi {

    // 注入自定义存储路径（从配置文件读取）
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file) {
        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            return "上传失败：文件为空";
        }

        try {
            // 2. 确保存储目录存在（不存在则创建）
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 递归创建目录
            }

            // 3. 生成唯一文件名（避免覆盖）
            String originalFilename = file.getOriginalFilename();
            String suffix = null; // 后缀名
            if (originalFilename != null) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + suffix; // 用UUID生成唯一文件名

            // 4. 保存文件到目标路径
            File dest = new File(uploadDir + File.separator + fileName);
            file.transferTo(dest); // 核心方法：将上传文件写入目标文件

            return "上传成功：" + dest.getAbsolutePath();
        } catch (IOException e) {
            return "上传失败：" + e.getMessage();
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 确定内容类型
        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
