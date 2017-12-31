package com.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FunctionalInterface
public interface EmailSeparator {
    Map<Boolean, List<String>> getDividedEmails(Set<String> emails);
}
