package com.microyum.common.http;

/**
 * @author SyaKa
 * @date 2019/8/21
 * Desc: HttpStatusBase
 */
public enum HttpStatus {


    // 2xx 请求成功
    OK(200, "Success"),
    OK_LAYUI(0, "Success"),

    // 3xx Redirection
    REDIRECTION(300, "Redirection"),

    // 4xx 业务逻辑错误
    CLIENT_ERROR(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    DATA_NOT_FOUND(402, "Data not found"),
    FORBIDDEN(403, "Forbidden"),
    DATA_SHOULD_NOT_EXIST(404, "Data should not exist"),

        // 5xx  内部服务器错误
    INTERNAL_SERVER_ERROR(500, "Service unavailable"),

    /**  a problem has  occurred in database **/
    ERROR_IN_DATABASE(501, "a problem has occurred in database"),

    /** a problem has  occurred in cache **/
    ERROR_IN_CACHE(502, "a problem has occurred in cache");

    private final int value;

    private final String message;


    HttpStatus(int value, String msg) {
        this.value = value;
        this.message = msg;
    }


    public int value() {
        return this.value;
    }

    public String getMessage() {
        return this.message;
    }
}
