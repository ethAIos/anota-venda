package com.caderninho.vendas.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        scope.launch {
            try {
                if (context.isDemoExpired()) {
                    A1Widget().updateAll(context)
                    A2Widget().updateAll(context)
                    A3Widget().updateAll(context)
                } else {
                    WidgetRefreshWorker.enqueue(context)
                    A1Widget().updateAll(context)
                    A2Widget().updateAll(context)
                    A3Widget().updateAll(context)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
