package org.ashkan.ghaffari.inferenceanalyzer.dynamodb;

import org.ashkan.ghaffari.ingestor.dynamo.flaggedmessage.FlaggedMessage;
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
}
