package com.utils;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface EmailFormatter {
    String getFormattedEmails(Map<Boolean, List<String>> emails);
}
