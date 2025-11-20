#!/bin/bash
set -euo pipefail

ENDPOINT_URL="http://localhost:8000"
REGION="us-west-2"

note() { echo "[dynamodb] $*"; }

note "Creating ChatMessage (chatId HASH, timestamp RANGE)..."
aws dynamodb create-table \
  --table-name ChatMessage \
  --attribute-definitions \
      AttributeName=chatId,AttributeType=S \
      AttributeName=timestamp,AttributeType=N \
  --key-schema \
      AttributeName=chatId,KeyType=HASH \
      AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  >/dev/null && note "Created ChatMessage" || note "ChatMessage may already exist"

note "Creating RuleConfig (ruleId HASH)..."
aws dynamodb create-table \
  --table-name RuleConfig \
  --attribute-definitions \
      AttributeName=ruleId,AttributeType=S \
  --key-schema \
      AttributeName=ruleId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  >/dev/null && note "Created RuleConfig" || note "RuleConfig may already exist"

note "Creating FlaggedMessage (chatId HASH, timestamp RANGE)..."
aws dynamodb create-table \
  --table-name FlaggedMessage \
  --attribute-definitions \
      AttributeName=chatId,AttributeType=S \
      AttributeName=timestamp,AttributeType=N \
  --key-schema \
      AttributeName=chatId,KeyType=HASH \
      AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT_URL" \
  --region "$REGION" \
  >/dev/null && note "Created FlaggedMessage" || note "FlaggedMessage may already exist"

note "All tables created (or already present)."
