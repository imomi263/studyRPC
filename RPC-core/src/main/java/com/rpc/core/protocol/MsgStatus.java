package com.rpc.core.protocol;

import lombok.Getter;

public enum MsgStatus {
    SUCCESS((byte)0),
    FAIL((byte)1);

    @Getter
    private final byte code;

    MsgStatus(byte code) {
        this.code=code;
    }

    public static boolean isSuccess(byte code) {
        return MsgStatus.SUCCESS.code == code;
    }
}
