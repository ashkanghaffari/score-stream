package org.ashkan.ghaffari.ingestor.dynamo.flaggedmessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.ingestor.ws.dto.FlaggedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlaggedMessageService {

    private static final Logger log = LoggerFactory.getLogger(FlaggedMessageService.class);
    private final FlaggedMessageRepository repository;
    private final ObjectMapper mapper;

    public FlaggedMessageService(FlaggedMessageRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void saveFlaggedMessage(FlaggedMessage flaggedMessage) {
        Map<String, Object> payloadMap =
            mapper.convertValue(flaggedMessage.payload(), new TypeReference<>() {});

        try {
            repository.save(
                new org.ashkan.ghaffari.ingestor.dynamo.flaggedmessage.FlaggedMessage(
                    flaggedMessage.chatId(),
                    flaggedMessage.timestamp().toEpochMilli(),
                    flaggedMessage.messageId().toString(),
                    flaggedMessage.senderId(),
                    payloadMap,
                    flaggedMessage.totalScore(),
                    flaggedMessage.decision(),
                    flaggedMessage.triggeredRules()
                )
            );
        } catch (UnsupportedOperationException ex) {
            log.error("Failed to save flagged message to DynamoDB: {}", ex.getMessage());
        }
    }
}
