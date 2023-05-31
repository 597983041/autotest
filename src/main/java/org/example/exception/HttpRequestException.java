package org.example.exception;

import java.io.Serializable;

/**
 * cf-opt-platform的工具包
 */
public class HttpRequestException extends Exception implements Serializable {
    public static int SUCCESS = 0;
    public static int FAIL = 1;
    public static int PARAMETER_INVALID = -1;
    public static int EXCEPTION = 500;

    private String errMsg;
    private int errCode;

    public HttpRequestException() {
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public HttpRequestException(int errCode, String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public HttpRequestException(String message, String errMsg, int errCode) {
        super(message);
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public HttpRequestException(String message, Throwable throwable, String errMsg, int errCode) {
        super(message, throwable);
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public HttpRequestException(Throwable throwable, String errMsg, int errCode) {
        super(throwable);
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public HttpRequestException(Exception e) {
        super(e);
        this.errCode = EXCEPTION;
        this.errMsg = e.getMessage();
    }

    @Override
    public String getMessage() {
        return errMsg;
    }
}
