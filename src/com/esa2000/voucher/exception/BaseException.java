package com.esa2000.voucher.exception;

/**
 * Created by Administrator on 2015/6/4.
 */
public class BaseException extends RuntimeException {
    private String code;
    private String desc;

    public BaseException() {
    }

    public BaseException(String code, String desc) {
        super("Code[" + code + "], Desc[" + desc + "]");
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
