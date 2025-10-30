package com.zxl.xlforum.common.RestAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 专门处理参数验证异常和其他常见异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常 - ConstraintViolationException
     * 通常发生在 @RequestParam, @PathVariable 等参数验证失败时
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        // 提取所有验证错误信息
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("message", "参数验证失败");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // 提取详细的字段错误信息
        Map<String, String> fieldErrors = violations.stream()
                .collect(Collectors.toMap(
                        violation -> {
                            // 提取字段名，例如 "changePassword.newPassword"
                            String path = violation.getPropertyPath().toString();
                            // 简化路径，只保留最后一部分
                            return path.contains(".") ?
                                    path.substring(path.lastIndexOf('.') + 1) : path;
                        },
                        ConstraintViolation::getMessage
                ));

        errorResponse.put("errors", fieldErrors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理请求体参数验证异常 - MethodArgumentNotValidException
     * 通常发生在 @RequestBody 参数验证失败时
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("message", "请求参数无效");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // 提取字段级错误信息
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "参数错误"
                ));

        errorResponse.put("errors", fieldErrors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理 Servlet 异常
     */
    @ExceptionHandler(jakarta.servlet.ServletException.class)
    public ResponseEntity<Map<String, Object>> handleServletException(
            jakarta.servlet.ServletException ex) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("message", "服务器处理请求时发生错误");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // 如果是包装了其他异常，显示根本原因
        if (ex.getCause() != null) {
            errorResponse.put("detail", ex.getCause().getMessage());
        } else {
            errorResponse.put("detail", ex.getMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("message", "系统内部错误");
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("detail", ex.getMessage());

        // 生产环境中建议不要返回详细的异常信息
        // 可以在这里记录日志
        ex.printStackTrace(); // 开发环境可以保留

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}