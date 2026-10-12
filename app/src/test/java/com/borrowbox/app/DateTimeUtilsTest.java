package com.borrowbox.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.borrowbox.app.utils.DateTimeUtils;

import org.junit.Test;

public class DateTimeUtilsTest {

    @Test
    public void testFormatTimeAgo_justNow() {
        long now = System.currentTimeMillis();
        String result = DateTimeUtils.formatTimeAgo(now - 10_000); // 10 seconds ago
        assertEquals("Just now", result);
    }

    @Test
    public void testFormatTimeAgo_minutesAgo() {
        long now = System.currentTimeMillis();
        String result = DateTimeUtils.formatTimeAgo(now - 5 * 60_000); // 5 minutes ago
        assertEquals("5m ago", result);
    }

    @Test
    public void testFormatTimeAgo_hoursAgo() {
        long now = System.currentTimeMillis();
        String result = DateTimeUtils.formatTimeAgo(now - 3 * 3600_000); // 3 hours ago
        assertEquals("3h ago", result);
    }

    @Test
    public void testCalculateDueTimestamp_andDaysRemaining() {
        long now = System.currentTimeMillis();
        int days = 5;
        long dueAt = DateTimeUtils.calculateDueTimestamp(now, days);

        int remaining = DateTimeUtils.getDaysRemaining(dueAt);
        assertEquals(5, remaining);
        assertFalse("Future due date should not be marked overdue", DateTimeUtils.isOverdue(dueAt));
    }

    @Test
    public void testOverdueCalculation() {
        long now = System.currentTimeMillis();
        long pastDueDate = now - (2 * 86400_000L + 3600_000L); // ~2 days in past

        assertTrue("Past due date must be marked overdue", DateTimeUtils.isOverdue(pastDueDate));
        int overdueDays = DateTimeUtils.getDaysOverdue(pastDueDate);
        assertTrue("Overdue days should be >= 2", overdueDays >= 2);
    }

    @Test
    public void testTimeOfDayGreeting_returnsValidGreeting() {
        String greeting = DateTimeUtils.getTimeOfDayGreeting();
        assertNotNull(greeting);
        assertTrue("Greeting must contain good", greeting.toLowerCase().contains("good"));
    }
}
