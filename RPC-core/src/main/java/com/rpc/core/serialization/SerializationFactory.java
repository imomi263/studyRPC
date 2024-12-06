package com.rpc.core.serialization;

public class SerializationFactory {
    public static RpcSerialization getRpcSerialization(SerializationTypeEnum typeEnum){
        return new HessianSerialization() ;
//        switch(typeEnum){
//            case JSON:
//                return new JsonSerialization();
//            case HESSIAN:
//                return new HessianSerialization();
//            default:
//                throw new IllegalArgumentException("serialization type is illegal");
//        }
    }
}
