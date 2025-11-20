#!/usr/bin/env bash
set -euo pipefail

ENDPOINT_URL="http://localhost:8000"
REGION="us-west-2"

CHAT_TABLE="ChatMessage"
FLAGGED_TABLE="FlaggedMessage"
CHAT_ID="chat-123"

note() { echo "[seed] $*"; }

note "Creating tables if they do not exist..."
aws dynamodb create-table \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  --table-name "$CHAT_TABLE" \
  --attribute-definitions \
    AttributeName=chatId,AttributeType=S \
    AttributeName=timestamp,AttributeType=N \
  --key-schema \
    AttributeName=chatId,KeyType=HASH \
    AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  >/dev/null && note "Created $CHAT_TABLE" || note "$CHAT_TABLE may already exist"

aws dynamodb create-table \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  --table-name "$FLAGGED_TABLE" \
  --attribute-definitions \
    AttributeName=chatId,AttributeType=S \
    AttributeName=timestamp,AttributeType=N \
  --key-schema \
    AttributeName=chatId,KeyType=HASH \
    AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  >/dev/null && note "Created $FLAGGED_TABLE" || note "$FLAGGED_TABLE may already exist"

note "Seeding chat messages..."
aws dynamodb batch-write-item \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  --request-items "{
    \"$CHAT_TABLE\": [
      {\"PutRequest\": {\"Item\": {
        \"chatId\": {\"S\": \"$CHAT_ID\"},
        \"timestamp\": {\"N\": \"1700000000000\"},
        \"messageId\": {\"S\": \"m-1\"},
        \"senderId\": {\"S\": \"alice\"},
        \"payload\": {\"M\": {\"text\": {\"S\": \"Hello team\"}}}
      }}},
      {\"PutRequest\": {\"Item\": {
        \"chatId\": {\"S\": \"$CHAT_ID\"},
        \"timestamp\": {\"N\": \"1700000005000\"},
        \"messageId\": {\"S\": \"m-2\"},
        \"senderId\": {\"S\": \"bob\"},
        \"payload\": {\"M\": {\"text\": {\"S\": \"Need this urgently\"}}}
      }}},
      {\"PutRequest\": {\"Item\": {
        \"chatId\": {\"S\": \"$CHAT_ID\"},
        \"timestamp\": {\"N\": \"1700000010000\"},
        \"messageId\": {\"S\": \"m-3\"},
        \"senderId\": {\"S\": \"alice\"},
        \"payload\": {\"M\": {\"text\": {\"S\": \"Send me a gift card\"}}}
      }}},
      {\"PutRequest\": {\"Item\": {
        \"chatId\": {\"S\": \"$CHAT_ID\"},
        \"timestamp\": {\"N\": \"1700000015000\"},
        \"messageId\": {\"S\": \"m-4\"},
        \"senderId\": {\"S\": \"bob\"},
        \"payload\": {\"M\": {\"text\": {\"S\": \"Why a gift card?\"}}}
      }}}
    ]
  }"

note "Seeding flagged message..."
aws dynamodb put-item \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  --table-name "$FLAGGED_TABLE" \
  --item "{
    \"chatId\": {\"S\": \"$CHAT_ID\"},
    \"timestamp\": {\"N\": \"1700000010000\"},
    \"messageId\": {\"S\": \"m-3\"},
    \"senderId\": {\"S\": \"alice\"},
    \"payload\": {\"M\": {\"text\": {\"S\": \"Send me a gift card\"}}},
    \"totalScore\": {\"N\": \"85\"},
    \"decision\": {\"S\": \"FLAG\"},
    \"triggeredRules\": {\"L\": [
      {\"M\": {\"name\": {\"S\": \"gift_card_request\"}, \"matched\": {\"BOOL\": true}, \"score\": {\"N\": \"40\"}, \"reason\": {\"S\": \"Gift card pattern\"}}},
      {\"M\": {\"name\": {\"S\": \"urgent_language\"}, \"matched\": {\"BOOL\": true}, \"score\": {\"N\": \"20\"}, \"reason\": {\"S\": \"Urgent phrasing\"}}}
    ]},
    \"analyzed\": {\"BOOL\": false},
    \"analysisId\": {\"NULL\": true}
  }"

note "Done. Run inference-analyzer against DynamoDB Local (endpoint $ENDPOINT_URL)."
