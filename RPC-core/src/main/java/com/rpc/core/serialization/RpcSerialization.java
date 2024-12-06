package com.rpc.core.serialization;

import java.io.IOException;

public interface RpcSerialization {
    <T> byte[] serialize(T obj) throws IOException ;

    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException ;

}
