package com.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class EmailExtractorImpl implements EmailExtractor {

    private Logger logger = LogManager.getLogger(this.getClass());

    public Set<String> getEmails(InputStream emailsStream) {
        Set<String> emails;

        logger.trace("Call getEmails BEGIN");
        //logger.info(" - " + file.getClass().getName());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(emailsStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            Pattern pattern = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b", Pattern.CASE_INSENSITIVE);
            emails = new HashSet<>();
            br.lines().forEach(line -> {
                        //logger.info("2 - " + line);
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            emails.add(matcher.group());
                        }
                    }
            );
            return emails;
        } catch (IOException pe) {
            logger.error("IOException ->" + pe.getStackTrace());
        }
        logger.trace("Call getEmails END");
        return null;
    }

}
