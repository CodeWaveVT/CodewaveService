package edu.vt.codewaveservice.common;

public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "wrong request param请求参数错误"),
    NOT_LOGIN_ERROR(40100, "need login 未登录"),
    NO_AUTH_ERROR(40101, "no permission 无权限"),
    NOT_FOUND_ERROR(40400, "request data not found 请求数据不存在"),
    TOO_MANY_REQUEST(42900, "too much request 请求过于频繁"),
    FORBIDDEN_ERROR(40300, "forbidden 禁止访问"),
    SYSTEM_ERROR(50000, "internel error 系统内部异常"),
    OPERATION_ERROR(50001, "fail 操作失败");
    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
