package com.github.zhaoli.rpc.common.exception;

import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.util.PlaceHolderUtil;

/**
 * @author zhaoli
 * @date 2018/7/7
 */
public class RPCException extends RuntimeException {
    private ErrorEnum errorEnum;

    public RPCException(ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderUtil.replace(message, args));
        this.errorEnum = errorEnum;
    }

    public RPCException(Throwable cause, ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderUtil.replace(message, args), cause);
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
