/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rpc.core.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.core.util.Fields;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class JsonSerialization implements RpcSerialization {

    private static final ObjectMapper MAPPER;

    static {
        // ALWAYS: 默认值，所有属性都参与序列化
        MAPPER = generateMapper(JsonInclude.Include.ALWAYS);
    }

    private static ObjectMapper generateMapper(JsonInclude.Include include) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(include);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        return mapper;
    }


    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        Field[] fields=obj.getClass().getFields();
        for(Field field:fields){
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type.isArray()) {
                try{
                    Object o=field.get(obj);
                    //Class<?> clz=o.getClass().getComponentType();
                    strBuilder.append(Arrays.toString((Object[])o));
                }catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                strBuilder.append(field);
            }

        }
        return obj instanceof String ? ((String )obj).getBytes() : strBuilder.toString().getBytes();

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return MAPPER.readValue(Arrays.toString(data), clazz);
    }

}
