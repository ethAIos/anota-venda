package com.caderninho.vendas.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.caderninho.vendas.MainActivity

object WidgetIntents {

    fun openApp(context: Context, path: String): PendingIntent {
        val uri = Uri.parse("caderninho://$path")
        val intent = Intent(Intent.ACTION_VIEW, uri, context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        return PendingIntent.getActivity(
            context,
            path.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    fun openNewSale(context: Context): PendingIntent = openApp(context, "newsale")
    fun openPayingToday(context: Context): PendingIntent = openApp(context, "payingtoday")
    fun openCustomer(context: Context, id: Long): PendingIntent = openApp(context, "customer/$id")
}
