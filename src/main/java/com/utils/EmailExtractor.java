package com.utils;

import java.io.InputStream;
import java.util.Set;

@FunctionalInterface
public interface EmailExtractor {
    Set<String> getEmails(InputStream emailsStream);
}
