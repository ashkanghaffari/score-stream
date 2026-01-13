package org.ashkan.ghaffari.adminconsole.exception;

public class InvalidExternalClaimException extends RuntimeException {

    public static final String MISSING_NAME = "Invalid claim: name is missing or blank";
    public static final String MISSING_EMAIL = "Invalid claim: email is missing or blank";
    public static final String MISSING_EXTERNAL_ID = "Invalid claim: externalId is missing or blank";
    public static final String INVALID_GOOGLE_TOKEN_RES = "Invalid Google token response: ";

    public InvalidExternalClaimException(String message) {
        super(message);
    }
}
