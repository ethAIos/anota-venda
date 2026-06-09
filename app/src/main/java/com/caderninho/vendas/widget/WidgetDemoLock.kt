package com.caderninho.vendas.widget

import android.content.Context
import com.caderninho.vendas.demo.DemoLockState

internal fun Context.isDemoExpired(): Boolean {
    val policy = widgetDemoLockPolicy()
    if (!policy.isEnabled()) return false
    return runCatching {
        val pi = packageManager.getPackageInfo(packageName, 0)
        policy.evaluate(pi.firstInstallTime) is DemoLockState.Expired
    }.getOrElse { false }
}
