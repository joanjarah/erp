package com.erp.accounting.dto;

import java.io.Serializable;

public class ApiResponseDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private T data;
    private Long timestamp;
    private String path;

    public ApiResponseDTO() {
    }

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setStatus("SUCCESS");
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        response.setPath(null);
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message, String path) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setStatus("ERROR");
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        response.setPath(path);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
