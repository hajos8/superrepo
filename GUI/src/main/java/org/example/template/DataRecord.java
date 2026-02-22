package org.example.template;

import java.time.LocalDate;

/**
 * Generic row model for headered CSV tasks (JavaFX side).
 * Rename fields/types to match your CSV columns.
 */
public class DataRecord {
    private final String primary;
    private final String secondary;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int metric;

    public DataRecord(String primary, String secondary, LocalDate startDate, LocalDate endDate, int metric) {
        this.primary = primary;
        this.secondary = secondary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metric = metric;
    }

    public String getPrimary() {
        return primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getMetric() {
        return metric;
    }

    public int getSpanDays() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
}
