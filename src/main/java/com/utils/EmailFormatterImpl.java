package com.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


@Service
public class EmailFormatterImpl implements EmailFormatter {

    private Logger logger = LogManager.getLogger(this.getClass());
    private final Integer LINE_BRAKE = 100 - 1; // т.к. индексация с 0

    public String getFormattedEmails(Map<Boolean, List<String>> gmailAndOtherEmails) {
        String currentMethod = new Object() {
        }.getClass().getEnclosingMethod().getName();
        logger.trace("Call " + currentMethod + " BEGIN");


        StringBuilder emailsList = new StringBuilder();
/*
        gmailAndOtherEmails.get(true).forEach((emailAddress) -> {                        // .forEach неявно  пеобразует в stream().forEach
            logger.trace(emailAddress + ", ");
            emailsList.append(emailAddress + ",\n");
        });
*/

        IntStream.range(0, gmailAndOtherEmails.get(true).size()).forEach(idx -> {
            emailsList.append(gmailAndOtherEmails.get(true).get(idx) + ",\n");
            logger.trace(gmailAndOtherEmails.get(true).get(idx));
            if ((idx % LINE_BRAKE == 0) && (idx != 0)) emailsList.append("\n");
        });
        emailsList.append("GMail EmailsList: Count:" + gmailAndOtherEmails.get(true).size() + "\n\n");
        logger.info("GMail EmailsList: Count:" + gmailAndOtherEmails.get(true).size());

/*
        gmailAndOtherEmails.get(false).forEach((emailAddress) -> {
            logger.trace(emailAddress);
            emailsList.append(emailAddress + ",\n");

        });
*/
        IntStream.range(0, gmailAndOtherEmails.get(false).size()).forEach(idx -> {
            emailsList.append(gmailAndOtherEmails.get(false).get(idx) + ",\n");
            if ((idx % LINE_BRAKE == 0) && (idx != 0)) emailsList.append("\n");
            logger.trace(gmailAndOtherEmails.get(false).get(idx));

        });
        emailsList.append("\nOther EmailsList: Count:" + gmailAndOtherEmails.get(false).size());
        logger.info("\nOther EmailsList: Count:" + gmailAndOtherEmails.get(false).size());

        logger.trace("Call " + currentMethod + " END");
        return emailsList.toString();
    }

}
