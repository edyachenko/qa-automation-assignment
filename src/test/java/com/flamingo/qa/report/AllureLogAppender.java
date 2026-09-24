package com.flamingo.qa.report;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AllureLogAppender extends AppenderBase<ILoggingEvent> {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    private static final Map<Long, StringBuilder> BUFFERS = new ConcurrentHashMap<>();

    @Override
    protected void append(ILoggingEvent event) {
        String logger = event.getLoggerName().substring(event.getLoggerName().lastIndexOf('.') + 1);
        BUFFERS.computeIfAbsent(Thread.currentThread().getId(), key -> new StringBuilder())
                .append(TIME.format(Instant.ofEpochMilli(event.getTimeStamp())))
                .append(' ').append(event.getLevel())
                .append(' ').append(logger)
                .append(" - ").append(event.getFormattedMessage())
                .append(System.lineSeparator());
    }

    public static String drain() {
        StringBuilder buffer = BUFFERS.remove(Thread.currentThread().getId());
        return buffer == null ? "" : buffer.toString();
    }
}
