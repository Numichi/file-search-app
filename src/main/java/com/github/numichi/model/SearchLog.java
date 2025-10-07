package com.github.numichi.model;

import java.time.Instant;
import java.util.List;

/**
 * Record representing a search log entry.
 *
 * @param user      the user who performed the search
 * @param path      the path that was searched
 * @param ext       the file extension that was searched for
 * @param result    the list of results found
 * @param timestamp the time when the search was performed, it can be null
 */
public record SearchLog(String user, String path, String ext, List<String> result, Instant timestamp) {
}
