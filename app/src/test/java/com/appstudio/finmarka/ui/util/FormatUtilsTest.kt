package com.appstudio.finmarka.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun `formatCurrency uses known symbols and fallback code`() {
        assertEquals("₹ 123.40", formatCurrency(123.4, "INR"))
        assertEquals("$ 123.40", formatCurrency(123.4, "USD"))
        assertEquals("JPY 123.40", formatCurrency(123.4, "JPY"))
    }

    @Test
    fun `formatDate methods return non-empty strings`() {
        val timestamp = 1_700_000_000_000L
        assertTrue(formatDate(timestamp).isNotBlank())
        assertTrue(formatDateShort(timestamp).isNotBlank())
        assertTrue(formatDateTime(timestamp).isNotBlank())
        assertTrue(formatDateTimeTravel(timestamp).contains("·"))
    }
}
