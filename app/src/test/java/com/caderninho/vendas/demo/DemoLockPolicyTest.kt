package com.caderninho.vendas.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class DemoLockPolicyTest {

    @Test
    fun disabledWhenDurationIsZero() {
        val policy = DemoLockPolicy(lockDuration = Duration.ZERO)
        assertFalse(policy.isEnabled())
        assertEquals(DemoLockState.Disabled, policy.evaluate(System.currentTimeMillis()))
    }

    @Test
    fun activeBeforeExpiration() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val now = installTime.plus(Duration.ofDays(3))
        val clock = Clock.fixed(now, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofDays(5), clock = clock)

        assertTrue(policy.isEnabled())
        assertEquals(DemoLockState.Active, policy.evaluate(installTime.toEpochMilli()))
    }

    @Test
    fun expiredAtExactLimit() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val expiresAt = installTime.plus(Duration.ofDays(5))
        val clock = Clock.fixed(expiresAt, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofDays(5), clock = clock)

        val state = policy.evaluate(installTime.toEpochMilli())
        assertTrue(state is DemoLockState.Expired)
        assertEquals(expiresAt, (state as DemoLockState.Expired).expiredAt)
    }

    @Test
    fun expiredAfterLimit() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val now = installTime.plus(Duration.ofDays(10))
        val clock = Clock.fixed(now, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofDays(5), clock = clock)

        val state = policy.evaluate(installTime.toEpochMilli())
        assertTrue(state is DemoLockState.Expired)
    }

    @Test
    fun expirationInstantCalculatedCorrectly() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val policy = DemoLockPolicy(lockDuration = Duration.ofDays(7))

        val expected = installTime.plus(Duration.ofDays(7))
        assertEquals(expected, policy.expirationInstant(installTime.toEpochMilli()))
    }

    @Test
    fun hoursLockWorksCorrectly() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val now = installTime.plus(Duration.ofHours(1))
        val clock = Clock.fixed(now, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofHours(2), clock = clock)

        assertTrue(policy.isEnabled())
        assertEquals(DemoLockState.Active, policy.evaluate(installTime.toEpochMilli()))
    }

    @Test
    fun hoursLockExpiresCorrectly() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val expiresAt = installTime.plus(Duration.ofHours(2))
        val clock = Clock.fixed(expiresAt, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofHours(2), clock = clock)

        val state = policy.evaluate(installTime.toEpochMilli())
        assertTrue(state is DemoLockState.Expired)
        assertEquals(expiresAt, (state as DemoLockState.Expired).expiredAt)
    }

    @Test
    fun minutesLockWorksCorrectly() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val now = installTime.plus(Duration.ofMinutes(15))
        val clock = Clock.fixed(now, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofMinutes(30), clock = clock)

        assertTrue(policy.isEnabled())
        assertEquals(DemoLockState.Active, policy.evaluate(installTime.toEpochMilli()))
    }

    @Test
    fun minutesLockExpiresCorrectly() {
        val installTime = Instant.parse("2026-06-01T10:00:00Z")
        val expiresAt = installTime.plus(Duration.ofMinutes(30))
        val clock = Clock.fixed(expiresAt, ZoneOffset.UTC)
        val policy = DemoLockPolicy(lockDuration = Duration.ofMinutes(30), clock = clock)

        val state = policy.evaluate(installTime.toEpochMilli())
        assertTrue(state is DemoLockState.Expired)
        assertEquals(expiresAt, (state as DemoLockState.Expired).expiredAt)
    }

    @Test
    fun millisConvertedCorrectly() {
        val installTime = Instant.parse("2026-06-01T00:00:00Z")
        val policy = DemoLockPolicy(lockDuration = Duration.ofMillis(3 * 60 * 60 * 1000L))

        val expectedMillis = installTime.toEpochMilli() + (3L * 60 * 60 * 1000)
        assertEquals(expectedMillis, policy.expirationInstant(installTime.toEpochMilli()).toEpochMilli())
    }
}
