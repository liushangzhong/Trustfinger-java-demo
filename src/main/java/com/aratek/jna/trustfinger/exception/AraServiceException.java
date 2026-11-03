package com.aratek.jna.trustfinger.exception;

/**
 * @author shijinlong
 * @date 2018-11-28
 */
public class AraServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;

    public AraServiceException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public AraServiceException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public AraServiceException(int code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public AraServiceException(int code, String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
