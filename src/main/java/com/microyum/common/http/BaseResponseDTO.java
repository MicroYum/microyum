package com.microyum.common.http;


public class BaseResponseDTO {
    private Integer code;
    private String message;
    private Object data;
    private long count;

    public final static BaseResponseDTO OK = new BaseResponseDTO(HttpStatus.OK);

    public static BaseResponseDTO OK(Object data) {
        return new BaseResponseDTO(HttpStatus.OK, data);
    }

    public BaseResponseDTO() {
    }

    public BaseResponseDTO(HttpStatus status, Object object) {
        this.code = status.value();
        this.message = status.getMessage();
        this.data = object;
    }

    public BaseResponseDTO(HttpStatus status) {
        this.code = status.value();
        this.message = status.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setBaseResponseDTO(HttpStatus status, Object object) {
        this.code = status.value();
        this.message = status.getMessage();
        this.data = object;
    }

    public void setBaseResponseDTO(HttpStatus status) {
        this.code = status.value();
        this.message = status.getMessage();
    }
}
