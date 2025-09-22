package com.sahabatquran.webapp.functional;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Custom log appender to monitor application logs for exceptions during tests.
 * This allows tests to detect server-side exceptions that would otherwise
 * only appear in logs without failing the test.
 */
@Slf4j
public class TestLogMonitor extends AppenderBase<ILoggingEvent> {

    private static final List<String> detectedExceptions = new CopyOnWriteArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
        // Only monitor WARN and ERROR level messages
        if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
            String message = event.getFormattedMessage();
            String loggerName = event.getLoggerName();

            // Look for LazyInitializationException patterns
            if (message != null && (
                message.contains("LazyInitializationException") ||
                message.contains("failed to lazily initialize") ||
                message.contains("could not initialize proxy") ||
                message.contains("Lazy initialization exception")
            )) {
                String exceptionInfo = String.format("LazyInitializationException detected in logger '%s': %s",
                    loggerName, message);
                detectedExceptions.add(exceptionInfo);
                log.debug("🔍 Test Log Monitor captured: {}", exceptionInfo);
            }

            // Look for other Hibernate exceptions
            if (message != null && (
                message.contains("HibernateException") ||
                message.contains("TransactionRequiredException") ||
                message.contains("No Session found for current thread")
            )) {
                String exceptionInfo = String.format("Hibernate exception detected in logger '%s': %s",
                    loggerName, message);
                detectedExceptions.add(exceptionInfo);
                log.debug("🔍 Test Log Monitor captured: {}", exceptionInfo);
            }
        }
    }

    /**
     * Get all detected exceptions since last reset
     */
    public static List<String> getDetectedExceptions() {
        return List.copyOf(detectedExceptions);
    }

    /**
     * Check if any exceptions were detected
     */
    public static boolean hasDetectedExceptions() {
        return !detectedExceptions.isEmpty();
    }

    /**
     * Reset detected exceptions (call at start of each test)
     */
    public static void reset() {
        detectedExceptions.clear();
    }
}