package com.caderninho.vendas.demo

import java.time.Clock
import java.time.Duration
import java.time.Instant

sealed interface DemoLockState {
    data object Disabled : DemoLockState
    data object Active : DemoLockState
    data class Expired(val expiredAt: Instant) : DemoLockState
}

class DemoLockPolicy(
    private val lockDuration: Duration,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    fun isEnabled(): Boolean = !lockDuration.isZero && !lockDuration.isNegative

    fun expirationInstant(firstInstallTime: Long): Instant =
        Instant.ofEpochMilli(firstInstallTime).plus(lockDuration)

    fun evaluate(firstInstallTime: Long): DemoLockState {
        if (!isEnabled()) return DemoLockState.Disabled
        val expiresAt = expirationInstant(firstInstallTime)
        val now = clock.instant()
        return if (now >= expiresAt) {
            DemoLockState.Expired(expiresAt)
        } else {
            DemoLockState.Active
        }
    }
}
