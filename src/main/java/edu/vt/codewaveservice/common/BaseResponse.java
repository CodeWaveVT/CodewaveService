package edu.vt.codewaveservice.common;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;


    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code,T data){
        this(code,data,"");
    }

    public BaseResponse(ErrorCode error){
        this(error.getCode(),null,error.getMessage());
    }
}
