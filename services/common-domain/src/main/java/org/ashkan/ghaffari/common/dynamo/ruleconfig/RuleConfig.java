package org.ashkan.ghaffari.common.dynamo.ruleconfig;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
public class RuleConfig {

    private String ruleId;
    private String name;
    private String type;
    private List<String> patterns;
    private String logic;
    private int score;
    private boolean enabled;
    private String description;

    @DynamoDbPartitionKey
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getPatterns() { return patterns; }
    public void setPatterns(List<String> patterns) { this.patterns = patterns; }

    public String getLogic() { return logic; }
    public void setLogic(String logic) { this.logic = logic; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
