package org.yaroslaavl.notificationservice.dto;

import java.util.List;

public record PageShortDto<T>(
        List<T> allContent,
        long totalElements,
        long totalPages,
        int page,
        int size
) { }
