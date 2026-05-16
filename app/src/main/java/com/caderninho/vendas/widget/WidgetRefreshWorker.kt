package com.caderninho.vendas.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class WidgetRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        A1Widget().updateAll(applicationContext)
        A2Widget().updateAll(applicationContext)
        A3Widget().updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "widget_refresh_daily"
        private val RefreshTime = LocalTime.of(0, 1)

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(millisUntilNextRefresh(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        internal fun millisUntilNextRefresh(now: ZonedDateTime = ZonedDateTime.now()): Long {
            var next = now.toLocalDate().atTime(RefreshTime).atZone(now.zone)
            if (!next.isAfter(now)) next = next.plusDays(1)
            return Duration.between(now, next).toMillis().coerceAtLeast(0L)
        }
    }
}
