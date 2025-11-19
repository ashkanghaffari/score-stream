package org.ashkan.ghaffari.common.dynamo.flaggedmessage;

import org.ashkan.ghaffari.common.ruleengine.RuleResult;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuleResultListConverter implements AttributeConverter<List<RuleResult>> {

    @Override
    public AttributeValue transformFrom(List<RuleResult> input) {
        if (input == null || input.isEmpty()) {
            return AttributeValue.fromL(List.of());
        }

        List<AttributeValue> serialized = new ArrayList<>(input.size());
        for (RuleResult result : input) {
            Map<String, AttributeValue> map = new LinkedHashMap<>();
            map.put("name", AttributeValue.fromS(result.name()));
            map.put("matched", AttributeValue.fromBool(result.matched()));
            map.put("score", AttributeValue.fromN(Integer.toString(result.score())));
            map.put("reason", AttributeValue.fromS(result.reason() != null ? result.reason() : ""));
            serialized.add(AttributeValue.fromM(map));
        }
        return AttributeValue.fromL(serialized);
    }

    @Override
    public List<RuleResult> transformTo(AttributeValue attributeValue) {
        if (attributeValue == null || attributeValue.l() == null) {
            return List.of();
        }

        List<RuleResult> deserialized = new ArrayList<>();
        for (AttributeValue value : attributeValue.l()) {
            Map<String, AttributeValue> map = value.m();
            if (map == null) {
                continue;
            }
            deserialized.add(new RuleResult(
                map.getOrDefault("name", AttributeValue.fromS("unknown")).s(),
                map.getOrDefault("matched", AttributeValue.fromBool(false)).bool(),
                Integer.parseInt(map.getOrDefault("score", AttributeValue.fromN("0")).n()),
                map.getOrDefault("reason", AttributeValue.fromS("")).s()
            ));
        }
        return deserialized;
    }

    @Override
    public EnhancedType<List<RuleResult>> type() {
        return EnhancedType.listOf(RuleResult.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }
}
