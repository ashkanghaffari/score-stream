package org.ashkan.ghaffari.inferenceanalyzer.model;

import org.ashkan.ghaffari.common.dynamo.flaggedmessage.FlaggedMessage;

import java.util.List;

public record ConversationContext(
    FlaggedMessage flaggedMessage,
    List<ChatTurn> conversation,
    String chatId,
    long flaggedTimestamp,
    String analysisId
) {}
