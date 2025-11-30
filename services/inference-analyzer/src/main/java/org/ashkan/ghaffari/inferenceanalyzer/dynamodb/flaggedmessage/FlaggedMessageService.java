package org.ashkan.ghaffari.inferenceanalyzer.dynamodb.flaggedmessage;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlaggedMessageService {
    private final FlaggedMessageRepository flaggedMessageRepository;

    public FlaggedMessageService (FlaggedMessageRepository flaggedMessageRepository) {
        this.flaggedMessageRepository = flaggedMessageRepository;
    }

    public List<FlaggedMessage> flaggedMessages() {
        return flaggedMessageRepository.findUnanalyzed();
    }

    public void updateWithAnalysis(FlaggedMessage flaggedMessage, String analysisId) {
        FlaggedMessage update = new FlaggedMessage(
            flaggedMessage.getChatId(),
            flaggedMessage.getTimestamp(),
            flaggedMessage.getMessageId(),
            flaggedMessage.getSenderId(),
            flaggedMessage.getPayload(),
            flaggedMessage.getTotalScore(),
            flaggedMessage.getDecision(),
            flaggedMessage.getTriggeredRules(),
            true,
            analysisId
        );

        flaggedMessageRepository.update(update);
    }
}
