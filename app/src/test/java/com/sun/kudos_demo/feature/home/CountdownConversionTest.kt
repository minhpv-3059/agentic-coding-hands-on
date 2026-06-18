package com.sun.kudos_demo.feature.home

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Unit tests for countdown time conversion logic.
 * Validates that [Long.toCountdown] correctly splits milliseconds into days/hours/minutes.
 */
class CountdownConversionTest {

    @Test
    fun `test zero milliseconds converts to 0d 0h 0m`() {
        val result = 0L.toCountdown()
        assertEquals(0, result.days)
        assertEquals(0, result.hours)
        assertEquals(0, result.minutes)
    }

    @Test
    fun `test exact 20 days 20 hours 20 minutes`() {
        // 20 days = 20 * 24 * 60 minutes = 28,800 minutes
        // 20 hours = 20 * 60 minutes = 1,200 minutes
        // 20 minutes = 20 minutes
        // Total = 30,020 minutes = 1,801,200,000 ms
        val twentyDaysMs = (20L * 24 * 60 + 20 * 60 + 20) * 60 * 1000
        val result = twentyDaysMs.toCountdown()
        assertEquals(20, result.days)
        assertEquals(20, result.hours)
        assertEquals(20, result.minutes)
    }

    @Test
    fun `test 1 day in milliseconds`() {
        val oneDayMs = 24L * 60 * 60 * 1000
        val result = oneDayMs.toCountdown()
        assertEquals(1, result.days)
        assertEquals(0, result.hours)
        assertEquals(0, result.minutes)
    }

    @Test
    fun `test 1 hour in milliseconds`() {
        val oneHourMs = 60L * 60 * 1000
        val result = oneHourMs.toCountdown()
        assertEquals(0, result.days)
        assertEquals(1, result.hours)
        assertEquals(0, result.minutes)
    }

    @Test
    fun `test 1 minute in milliseconds`() {
        val oneMinuteMs = 60L * 1000
        val result = oneMinuteMs.toCountdown()
        assertEquals(0, result.days)
        assertEquals(0, result.hours)
        assertEquals(1, result.minutes)
    }

    @Test
    fun `test 1 day 5 hours 30 minutes`() {
        val totalMs = (1L * 24 * 60 + 5 * 60 + 30) * 60 * 1000
        val result = totalMs.toCountdown()
        assertEquals(1, result.days)
        assertEquals(5, result.hours)
        assertEquals(30, result.minutes)
    }

    @Test
    fun `test 5 days 12 hours 45 minutes`() {
        val totalMs = (5L * 24 * 60 + 12 * 60 + 45) * 60 * 1000
        val result = totalMs.toCountdown()
        assertEquals(5, result.days)
        assertEquals(12, result.hours)
        assertEquals(45, result.minutes)
    }

    @Test
    fun `test milliseconds below one minute are floored to zero`() {
        val halfMinuteMs = 30_000L
        val result = halfMinuteMs.toCountdown()
        assertEquals(0, result.days)
        assertEquals(0, result.hours)
        assertEquals(0, result.minutes)
    }

    @Test
    fun `test 59 seconds 999 milliseconds floors to zero`() {
        val almostOneMinute = 59_999L
        val result = almostOneMinute.toCountdown()
        assertEquals(0, result.days)
        assertEquals(0, result.hours)
        assertEquals(0, result.minutes)
    }

    @Test
    fun `test hour boundary does not leak into days`() {
        val ms = (2L * 24 * 60 + 23 * 60 + 59) * 60 * 1000
        val result = ms.toCountdown()
        assertEquals(2, result.days)
        assertEquals(23, result.hours)
        assertEquals(59, result.minutes)
    }
}
