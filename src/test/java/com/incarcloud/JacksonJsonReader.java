package com.incar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incar.base.anno.ICSComponent;
import com.incar.base.handler.dynamicrequest.json.JsonReader;

@ICSComponent
public class JacksonJsonReader implements JsonReader{
    @Override
    public String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
