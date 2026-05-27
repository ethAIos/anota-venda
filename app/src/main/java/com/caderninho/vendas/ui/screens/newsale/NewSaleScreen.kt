package com.caderninho.vendas.ui.screens.newsale

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caderninho.vendas.data.model.PaymentMode
import com.caderninho.vendas.ui.components.BigToggle
import com.caderninho.vendas.ui.components.ChipRow
import com.caderninho.vendas.ui.components.FieldLabel
import com.caderninho.vendas.ui.components.GhostButton
import com.caderninho.vendas.ui.components.GhostColor
import com.caderninho.vendas.ui.components.Money
import com.caderninho.vendas.ui.components.MoneyVisualTransformation
import com.caderninho.vendas.ui.components.PaperDialogActions
import com.caderninho.vendas.ui.components.PaperInput
import com.caderninho.vendas.ui.components.PaperInteractiveSurface
import com.caderninho.vendas.ui.components.PaperScaffold
import com.caderninho.vendas.ui.components.PaperTopBar
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.Stepper
import com.caderninho.vendas.ui.components.ToggleOption
import com.caderninho.vendas.ui.components.TopBarLeading
import com.caderninho.vendas.ui.components.paperScaffoldContentPadding
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.LocalCaveatStyle
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.util.toDatePickerLocalDate
import com.caderninho.vendas.util.toDatePickerMillis
import com.caderninho.vendas.util.formatDayMonth
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleScreen(
    onClose: () -> Unit,
    onOpenCustomer: (Long) -> Unit,
    vm: NewSaleViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()

    PaperScaffold(
        topBar = {
            PaperTopBar(
                title = "Nova venda",
                leading = TopBarLeading.CLOSE,
                onLeading = onClose,
            )
        },
    ) { padding ->
        if (s.savedOrderId != null) {
            ConfirmationContent(
                customerName = s.savedCustomerName.orEmpty(),
                dueDate = s.savedDueDate ?: LocalDate.now(),
                amountCents = s.savedAmountCents ?: 0L,
                onAnotarOutra = { vm.reset() },
                onVerCliente = {
                    val cid = s.savedCustomerId
                    if (cid != null) onOpenCustomer(cid)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .paperScaffoldContentPadding(padding),
            )
        } else {
            FormContent(
                state = s,
                vm = vm,
                modifier = Modifier
                    .fillMaxSize()
                    .paperScaffoldContentPadding(padding),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormContent(
    state: NewSaleState,
    vm: NewSaleViewModel,
    modifier: Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                FieldLabel("Cliente")
                PaperInput(
                    value = state.customerName,
                    onValueChange = vm::onCustomerName,
                    placeholder = "Nome do cliente",
                )
                if (state.suggestions.isNotEmpty()) {
                    Box(modifier = Modifier.padding(top = 10.dp)) {
                        ChipRow(
                            chips = state.suggestions.map { it.name },
                            selected = state.selectedSuggestion?.name,
                            onClick = { name -> state.suggestions.firstOrNull { it.name == name }?.let(vm::selectSuggestion) },
                        )
                    }
                }
            }

            Column {
                FieldLabel("Número")
                PaperInput(
                    value = state.customerPhone,
                    onValueChange = vm::onCustomerPhone,
                    placeholder = "(11) 99999-9999",
                    keyboard = KeyboardType.Phone,
                )
            }
        }

        Column {
            FieldLabel("O quê")
            PaperInput(
                value = state.what,
                onValueChange = vm::onWhat,
                placeholder = "ex: Batom, perfume, panela…",
            )
        }

        Column {
            FieldLabel("Valor")
            PaperInput(
                value = state.amountText,
                onValueChange = vm::onAmount,
                placeholder = "0,00",
                big = true,
                prefix = "R$",
                keyboard = KeyboardType.Number,
                visualTransformation = MoneyVisualTransformation(),
            )
        }

        Column {
            FieldLabel("Como vai pagar?")
            BigToggle(
                options = listOf(
                    ToggleOption(PaymentMode.AVISTA, "À vista", "recebido hoje"),
                    ToggleOption(PaymentMode.FIADO, "Fiado", "me paga depois"),
                ),
                selected = state.paymentMode,
                onSelect = vm::onPaymentMode,
            )
        }

        if (state.paymentMode == PaymentMode.FIADO) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(PaperAlt)
                    .border(BorderStroke(1.dp, Rule), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    PaperInteractiveSurface(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Paper,
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Ink, modifier = Modifier.size(18.dp))
                        Text(
                            "Quando paga?",
                            color = Ink,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                        )
                        Text(
                            formatDayMonth(state.dueDate),
                            color = Ink,
                            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                        )
                    }
                }
                Column {
                    Text(
                        "Em quantas vezes?",
                        color = InkSoft,
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Stepper(value = state.installments, onValueChange = vm::onInstallments)
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        PrimaryButton(
            text = "Anotar",
            onClick = { vm.save(onSaved = {}) },
        )

        Spacer(modifier = Modifier.size(16.dp))
    }

    if (showDatePicker) {
        val initial = state.dueDate.toDatePickerMillis()
        val picker = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                PaperDialogActions(
                    onDismiss = { showDatePicker = false },
                    confirmText = "OK",
                    onConfirm = {
                        picker.selectedDateMillis?.let { millis ->
                            vm.onDueDate(millis.toDatePickerLocalDate())
                        }
                        showDatePicker = false
                    },
                )
            },
        ) {
            DatePicker(state = picker)
        }
    }
}

@Composable
private fun ConfirmationContent(
    customerName: String,
    dueDate: LocalDate,
    amountCents: Long,
    onAnotarOutra: () -> Unit,
    onVerCliente: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(GreenSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Green, modifier = Modifier.size(36.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Anotado!",
                color = Green,
                style = LocalCaveatStyle.current.copy(fontSize = 38.sp, lineHeight = 38.sp),
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${customerName.substringBefore(' ')} deve ",
                    color = Ink,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                )
                Money(amountCents, color = Ink, size = 16.sp)
                Text(
                    " até ${formatDayMonth(dueDate)}.",
                    color = Ink,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                )
            }
            Text(
                "avisamos no widget no dia.",
                color = InkSoft,
                modifier = Modifier.padding(top = 6.dp),
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GhostButton(text = "Anotar outra", color = GhostColor.PAPER, onClick = onAnotarOutra)
            PrimaryButton(text = "Ver cliente", fillWidth = false, onClick = onVerCliente)
        }
    }
}
