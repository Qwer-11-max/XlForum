package com.zxl.xlforum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 测试文件清理工具类，测试完成后手动调用
 */
public class FileCleanupUtil {

    private static final Logger log = LoggerFactory.getLogger(FileCleanupUtil.class);

    /**
     * 清理指定目录下的所有文件和子目录
     * @param dirPath 目录路径（建议使用测试专用目录，如src/test/resources/test-uploads）
     */
    public static void cleanupTestUploads(String dirPath) {
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) {
            log.info("目录不存在：{}", dirPath);
            return;
        }

        try {
            // 递归删除目录下的所有文件和子目录
            boolean deleted = org.springframework.util.FileSystemUtils.deleteRecursively(new File(dirPath));
            if (deleted) {
                log.info("成功清理目录：{}", dirPath);
            } else {
                log.warn("清理目录失败：{}", dirPath);
            }
        } catch (Exception e) {
            log.error("清理目录时发生异常：{}", e.getMessage(), e);
        }
    }

    // 手动执行清理的入口（可在IDE中直接运行）
    public static void main(String[] args) {
        // 注意：此处路径需与测试类中@TestPropertySource配置的一致
        String testUploadDir = "src/test/resources/test-uploads";
        cleanupTestUploads(testUploadDir);
    }
}