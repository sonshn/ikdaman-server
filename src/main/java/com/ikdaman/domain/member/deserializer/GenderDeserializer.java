package com.ikdaman.domain.member.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ikdaman.domain.member.entity.Member;

import java.io.IOException;

public class GenderDeserializer extends JsonDeserializer<Member.Gender> {
    @Override
    public Member.Gender deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        if (value == null) {
            // 실제 null인 경우
            return null;
        }
        if (value.trim().isEmpty()) {
            // 빈 문자열인 경우
            return Member.Gender.BLANK;
        }
        return Member.Gender.valueOf(value.toUpperCase());
    }
}