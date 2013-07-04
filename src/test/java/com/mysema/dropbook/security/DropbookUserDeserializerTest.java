package com.mysema.dropbook.security;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DropbookUserDeserializerTest {

    private final String userJson = "{\"username\": \"testuser\", \"password\":\"foobar\" }";

    @Test
    public void deserializeJson() throws IOException {
        DropbookUserDeserializer deserializer = new DropbookUserDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(userJson);

        DropbookUser user = deserializer.deserialize(parser, Mockito.mock(DeserializationContext.class));
        assertEquals("testuser", user.getUsername());
        assertEquals("foobar", user.getPassword());
    }
}
