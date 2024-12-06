package com.rpc.core.serialization;

import lombok.Getter;

public enum SerializationTypeEnum {
    HESSIAN((byte)0),
    JSON((byte)1);



    @Getter
    private byte type;

    private SerializationTypeEnum(byte type){
        this.type = type;
    }

    public static SerializationTypeEnum parseByName(String typeName){
        for(SerializationTypeEnum typeEnum : SerializationTypeEnum.values()){
            if(typeEnum.name().equalsIgnoreCase(typeName)){
                return typeEnum;
            }
        }
        return JSON;
    }

    public static SerializationTypeEnum parseByType(byte type){
        for(SerializationTypeEnum typeEnum : SerializationTypeEnum.values()){
            if(typeEnum.getType() == type){
                return typeEnum;
            }
        }
        return JSON;
    }
}
