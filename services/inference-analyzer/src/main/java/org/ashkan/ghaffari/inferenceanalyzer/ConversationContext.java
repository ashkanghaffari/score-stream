package org.ashkan.ghaffari.inferenceanalyzer;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;

import java.util.List;

public record ConversationContext (
    FlaggedMessage trigger,
    List<ChatTurn> conversation,
    String chatId,
    long flaggedTimestamp,
    String analysisId
) {}