package com.github.kolomolo.service.openaiclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ErrorMessageExtractor {
    private static final String ERROR_MESSAGE_DELIMITER = "message\": \"";
    private static final Logger logger = LoggerFactory.getLogger(ErrorMessageExtractor.class);

    public static String extract(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            return "Unknown error occurred";
        }

        if (errorMessage.contains(ERROR_MESSAGE_DELIMITER)) {
            try {
                return errorMessage.split(ERROR_MESSAGE_DELIMITER)[1].split("\"")[0];
            } catch (Exception ex) {
                logger.warn("Error message extraction failed", ex);
                return errorMessage;
            }
        }
        return errorMessage;
    }
}