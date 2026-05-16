package com.caderninho.vendas.ui.screens.customer

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.components.AvatarTone
import com.caderninho.vendas.ui.components.InitialAvatar
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.RuleStrong

@Composable
fun EditCustomerDialog(
    name: String,
    phone: String?,
    tone: AvatarTone,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String?, tone: AvatarTone) -> Unit,
) {
    var draftName by remember(name) { mutableStateOf(name) }
    var draftPhone by remember(phone) { mutableStateOf(phone.orEmpty()) }
    var draftTone by remember(tone) { mutableStateOf(tone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = draftPhone,
                    onValueChange = { draftPhone = it },
                    label = { Text("Telefone") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "TOM DO AVATAR",
                    color = InkSoft,
                    style = TextStyle(
                        fontFamily = NunitoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.6.sp,
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    AvatarTone.values().forEach { t ->
                        val borderColor = if (t == draftTone) Ink else RuleStrong
                        val borderWidth = if (t == draftTone) 2.dp else 1.dp
                        Row(
                            modifier = Modifier
                                .size(44.dp)
                                .selectable(
                                    selected = t == draftTone,
                                    onClick = { draftTone = t },
                                )
                                .border(borderWidth, borderColor, CircleShape)
                                .padding(4.dp),
                        ) {
                            InitialAvatar(
                                name = draftName.ifBlank { "?" },
                                size = 32.dp,
                                tone = t,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        draftName.trim(),
                        draftPhone.trim().takeIf { it.isNotBlank() },
                        draftTone,
                    )
                },
                enabled = draftName.isNotBlank(),
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        containerColor = Paper,
        textContentColor = Ink,
        titleContentColor = Ink,
        iconContentColor = Color.Unspecified,
    )
}
