package com.github.numichi.model;

import java.time.Instant;
import java.util.List;

public record SearchLog(String user, String path, String ext, List<String> result, Instant timestamp) {
}
