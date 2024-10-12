package com.github.godshang.devtool.common;

public class DevToolException extends RuntimeException {

    public DevToolException() {
        super();
    }

    public DevToolException(String message) {
        super(message);
    }

    public DevToolException(String message, Throwable cause) {
        super(message, cause);
    }

    public DevToolException(Throwable cause) {
        super(cause);
    }

    protected DevToolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
