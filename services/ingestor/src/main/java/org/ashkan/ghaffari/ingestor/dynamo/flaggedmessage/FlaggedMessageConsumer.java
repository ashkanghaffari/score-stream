package org.ashkan.ghaffari.ingestor.dynamo.flaggedmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashkan.ghaffari.common.ws.dto.FlaggedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FlaggedMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(FlaggedMessageConsumer.class);
    private static final String FLAGGED_TOPIC = "text-flagged";
    private static final String FLAGGED_MESSAGE_CONSUMER_GROUP_ID = "flagged-message-consumer";

    private final ObjectMapper mapper;
    private final FlaggedMessageService flaggedMessageService;

    public FlaggedMessageConsumer(ObjectMapper mapper, FlaggedMessageService flaggedMessageService) {
        this.mapper = mapper;
        this.flaggedMessageService = flaggedMessageService;
    }

    @KafkaListener(topics = FLAGGED_TOPIC, groupId = FLAGGED_MESSAGE_CONSUMER_GROUP_ID)
    public void onMessage(byte[] data) throws IOException {
        FlaggedMessage flaggedMessage = mapper.readValue(data, FlaggedMessage.class);
        flaggedMessageService.saveFlaggedMessage(flaggedMessage);
    }
}
