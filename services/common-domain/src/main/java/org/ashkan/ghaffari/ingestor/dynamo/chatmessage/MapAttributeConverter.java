package org.ashkan.ghaffari.ingestor.dynamo.chatmessage;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapAttributeConverter implements AttributeConverter<Map<String, Object>> {

    @Override
    public AttributeValue transformFrom(Map<String, Object> map) {
        if (map == null) {
            return AttributeValue.fromNul(true);
        }
        return AttributeValue.fromM(convertMap(map));
    }

    @Override
    public Map<String, Object> transformTo(AttributeValue attributeValue) {
        if (attributeValue == null || attributeValue.m() == null) {
            return null;
        }
        return convertAttributeMap(attributeValue.m());
    }

    @Override
    public EnhancedType<Map<String, Object>> type() {
        return EnhancedType.mapOf(String.class, Object.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }

    // --- Helpers ---

    private Map<String, AttributeValue> convertMap(Map<String, Object> src) {
        Map<String, AttributeValue> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : src.entrySet()) {
            result.put(entry.getKey(), toAttributeValue(entry.getValue()));
        }
        return result;
    }

    private AttributeValue toAttributeValue(Object value) {
        if (value == null) {
            return AttributeValue.fromNul(true);
        }
        if (value instanceof String s) {
            return AttributeValue.fromS(s);
        }
        if (value instanceof Number n) {
            return AttributeValue.fromN(n.toString());
        }
        if (value instanceof Boolean b) {
            return AttributeValue.fromBool(b);
        }
        if (value instanceof Map<?, ?> m) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) m;
            return AttributeValue.fromM(convertMap(casted));
        }
        if (value instanceof List<?> list) {
            return AttributeValue.fromL(
                list.stream()
                    .map(this::toAttributeValue)
                    .collect(Collectors.toList())
            );
        }

        // Fallback: Convert everything else to String
        return AttributeValue.fromS(value.toString());
    }

    private Map<String, Object> convertAttributeMap(Map<String, AttributeValue> src) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, AttributeValue> entry : src.entrySet()) {
            result.put(entry.getKey(), fromAttributeValue(entry.getValue()));
        }
        return result;
    }

    private Object fromAttributeValue(AttributeValue value) {
        if (value.s() != null) return value.s();
        if (value.n() != null) return Double.valueOf(value.n());
        if (value.bool() != null) return value.bool();
        if (value.m() != null) return convertAttributeMap(value.m());
        if (value.l() != null) {
            return value.l().stream()
                .map(this::fromAttributeValue)
                .collect(Collectors.toList());
        }
        if (Boolean.TRUE.equals(value.nul())) return null;

        return null;
    }
}
