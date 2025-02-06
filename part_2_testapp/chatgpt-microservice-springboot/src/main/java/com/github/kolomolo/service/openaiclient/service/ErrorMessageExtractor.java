package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.common.ChatConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Slf4j
public class ErrorMessageExtractor {

    public static String extract(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            return ChatConstants.Error.UNKNOWN_ERROR;
        }

        if (errorMessage.contains(ChatConstants.Error.ERROR_MESSAGE_DELIMITER)) {
            try {
                return errorMessage.split(ChatConstants.Error.ERROR_MESSAGE_DELIMITER)[1].split("\"")[0];
            } catch (Exception ex) {
                log.warn("Error message extraction failed", ex);
                return errorMessage;
            }
        }
        return errorMessage;
    }
}