package com.github.godshang.devtool.common;

import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private T data;
    private String msg;

    public static <T> Result createSuccess(T data) {
        Result result = new Result();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static Result createFail(String msg) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMsg(msg);
        return result;
    }
}
