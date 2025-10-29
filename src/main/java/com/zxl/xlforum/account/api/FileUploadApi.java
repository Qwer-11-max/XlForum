package com.zxl.xlforum.account.api;

import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@RequestMapping("/")
public interface FileUploadApi {
    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    String uploadFile(
            @RequestParam("file")
            @Valid
            MultipartFile file
    );

    /**
     * 文件下载接口
     * @param fileName
     * @return
     */
    @GetMapping("/download/{fileName:.+}")
    ResponseEntity<Resource> downloadFile(
            @PathVariable
            String fileName
    ) throws MalformedURLException;
}
