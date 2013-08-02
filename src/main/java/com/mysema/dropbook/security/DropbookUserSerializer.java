package com.mysema.dropbook.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DropbookUserSerializer extends JsonSerializer<DropbookUser> {
    @Override
    public void serialize(DropbookUser dropbookUser, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", dropbookUser.getId());
        jsonGenerator.writeStringField("username", dropbookUser.getUsername());
        jsonGenerator.writeEndObject();
    }
}
