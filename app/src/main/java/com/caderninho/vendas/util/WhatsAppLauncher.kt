package com.caderninho.vendas.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.caderninho.vendas.ui.components.formatBrl
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

object WhatsAppLauncher {

    fun fillTemplate(
        template: String,
        customerName: String,
        product: String,
        amountCents: Long,
        dueDate: LocalDate,
    ): String = template
        .replace("{nome}", customerName)
        .replace("{produto}", product)
        .replace("{valor}", formatBrl(amountCents))
        .replace("{data}", formatDayMonth(dueDate))

    fun launch(context: Context, phone: String?, message: String) {
        val phoneSanitized = phone?.filter { it.isDigit() }.orEmpty()
        val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
        val uri = if (phoneSanitized.isNotBlank()) {
            Uri.parse("https://wa.me/$phoneSanitized?text=$encoded")
        } else {
            Uri.parse("https://wa.me/?text=$encoded")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Instale o WhatsApp para enviar a cobrança", Toast.LENGTH_LONG).show()
        }
    }
}
