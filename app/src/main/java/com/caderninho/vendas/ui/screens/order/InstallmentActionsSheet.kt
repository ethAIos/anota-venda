package com.caderninho.vendas.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import com.caderninho.vendas.ui.components.GhostButton
import com.caderninho.vendas.ui.components.GhostColor
import com.caderninho.vendas.ui.components.MoneyVisualTransformation
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.centsToDigits
import com.caderninho.vendas.ui.components.digitsToCents
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.util.describeDue
import com.caderninho.vendas.util.formatDayMonth
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentActionsSheet(
    inst: InstallmentEntity,
    onDismiss: () -> Unit,
    onPostpone: (LocalDate) -> Unit,
    onUpdateAmount: (Long) -> Unit,
    onUnmarkPaid: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDatePicker by remember { mutableStateOf(false) }
    var editingAmount by remember { mutableStateOf(false) }
    var amountText by remember(inst.id) {
        mutableStateOf(centsToDigits(inst.amountCents))
    }
    val paid = inst.paidAt != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Paper,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    "PARCELA ${inst.number}/${inst.ofTotal}",
                    color = InkSoft,
                    style = TextStyle(
                        fontFamily = NunitoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.6.sp,
                    ),
                )
                Text(
                    formatBrl(inst.amountCents),
                    color = Ink,
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(
                        fontFamily = NunitoFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = (-0.3).sp,
                    ),
                )
                val statusText = if (paid) "pago em ${formatDayMonth(inst.paidAt!!)}"
                    else "vence ${formatDayMonth(inst.dueDate)} · ${describeDue(inst.dueDate)}"
                Text(
                    statusText,
                    color = InkSoft,
                    modifier = Modifier.padding(top = 2.dp),
                    style = TextStyle(
                        fontFamily = NunitoFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    ),
                )
            }

            if (editingAmount) {
                Column {
                    Text(
                        "NOVO VALOR",
                        color = InkSoft,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.6.sp,
                        ),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { v ->
                            amountText = v.filter { it.isDigit() }.take(12)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        prefix = { Text("R$ ", color = InkSoft) },
                        visualTransformation = MoneyVisualTransformation(),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            GhostButton(
                                text = "Cancelar",
                                color = GhostColor.PAPER,
                                fillWidth = true,
                                onClick = {
                                    editingAmount = false
                                    amountText = centsToDigits(inst.amountCents)
                                },
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PrimaryButton(
                                text = "Salvar",
                                onClick = {
                                    val cents = digitsToCents(amountText)
                                    if (cents > 0L) {
                                        onUpdateAmount(cents)
                                        editingAmount = false
                                    }
                                },
                            )
                        }
                    }
                }
            } else {
                Column {
                    if (!paid) {
                        ActionRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Adiar vencimento",
                            color = Ink,
                            onClick = { showDatePicker = true },
                        )
                        Divider()
                        ActionRow(
                            icon = Icons.Default.Edit,
                            label = "Editar valor",
                            color = Ink,
                            onClick = { editingAmount = true },
                        )
                    }
                    if (paid) {
                        ActionRow(
                            icon = Icons.AutoMirrored.Filled.Undo,
                            label = "Desmarcar como pago",
                            color = Red,
                            onClick = {
                                onUnmarkPaid()
                                onDismiss()
                            },
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = inst.dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onPostpone(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
            colors = androidx.compose.material3.DatePickerDefaults.colors(
                containerColor = Paper,
            ),
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Text(
            label,
            color = color,
            style = TextStyle(
                fontFamily = NunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Rule),
    )
}

