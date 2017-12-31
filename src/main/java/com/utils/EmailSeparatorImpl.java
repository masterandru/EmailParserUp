package com.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class EmailSeparatorImpl implements EmailSeparator {

    private Logger logger = LogManager.getLogger(this.getClass());

    public Map<Boolean, List<String>> getDividedEmails(Set<String> emails) {
        //Map<Boolean, List<String>> separatedEmails;
        String currentMethod = new Object() {
        }.getClass().getEnclosingMethod().getName();
        logger.trace("Call " + currentMethod + " BEGIN");

        // Делим адреса на две части
        Map<Boolean, List<String>> gmailAndOtherEmails = emails
                .stream()
                .sorted()
                .collect(Collectors.partitioningBy((s) -> s.contains("@gmail.com")));

        logger.trace("Call " + currentMethod + " END");
        return gmailAndOtherEmails;
    }

}
