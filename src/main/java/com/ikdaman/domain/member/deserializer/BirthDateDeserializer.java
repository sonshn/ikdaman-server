package com.ikdaman.domain.member.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;

public class BirthDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String value = p.getValueAsString();
        if (value == null) {
            // 실제 null인 경우
            return null;
        }
        if (value.trim().isEmpty()) {
            // 빈 문자열인 경우
            return LocalDate.MIN;
        }
        return LocalDate.parse(value);
    }
}
