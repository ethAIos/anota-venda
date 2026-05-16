package com.caderninho.vendas.ui.screens.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import com.caderninho.vendas.ui.components.FieldLabel
import com.caderninho.vendas.ui.components.GhostButton
import com.caderninho.vendas.ui.components.GhostColor
import com.caderninho.vendas.ui.components.Money
import com.caderninho.vendas.ui.components.MoneyVisualTransformation
import com.caderninho.vendas.ui.components.PaperScaffold
import com.caderninho.vendas.ui.components.PaperTopBar
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.PrimaryColor
import com.caderninho.vendas.ui.components.TopBarLeading
import com.caderninho.vendas.ui.components.centsToDigits
import com.caderninho.vendas.ui.components.digitsToCents
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.ui.theme.RuleStrong
import com.caderninho.vendas.util.describeDue
import com.caderninho.vendas.util.formatDayMonth
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun OrderDetailScreen(
    onBack: () -> Unit,
    vm: OrderDetailViewModel = hiltViewModel(),
) {
    val detail by vm.detail.collectAsStateWithLifecycle()
    val uiError by vm.uiError.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedInstallment by remember { mutableStateOf<InstallmentEntity?>(null) }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is OrderDetailViewModel.Event.Closed -> onBack()
            }
        }
    }

    PaperScaffold(
        topBar = {
            PaperTopBar(
                title = "Pedido",
                leading = TopBarLeading.BACK,
                onLeading = onBack,
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Mais", tint = Ink)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar pedido") },
                            onClick = {
                                showMenu = false
                                showEditDialog = true
                            },
                            enabled = detail != null,
                        )
                        DropdownMenuItem(
                            text = { Text("Copiar resumo") },
                            onClick = {
                                showMenu = false
                                detail?.let { d ->
                                    clipboard.setText(AnnotatedString(d.summaryText()))
                                    Toast.makeText(context, "Resumo copiado", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = detail != null,
                        )
                        DropdownMenuItem(
                            text = { Text("Excluir pedido", color = Red) },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            enabled = detail != null,
                            colors = MenuDefaults.itemColors(
                                textColor = Red,
                                leadingIconColor = Red,
                                trailingIconColor = Red,
                            ),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val d = detail ?: run {
            Spacer(modifier = Modifier.fillMaxSize())
            return@PaperScaffold
        }
        val total = d.order.totalCents
        val left = d.installments.filter { it.paidAt == null }.sumOf { it.amountCents }
        val installmentLabel = if (d.installments.size > 1) "${d.installments.size}x de ${moneyText(d.installments.first().amountCents)}" else moneyText(total)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Column {
                    Text(
                        d.customer.name,
                        color = InkSoft,
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    )
                    Text(
                        d.order.what,
                        color = Ink,
                        modifier = Modifier.padding(top = 2.dp),
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = (-0.4).sp),
                    )
                    Text(
                        "anotado em ${formatDayMonth(d.order.createdAt)} · $installmentLabel",
                        color = InkSoft,
                        modifier = Modifier.padding(top = 4.dp),
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PaperAlt)
                        .border(1.dp, Rule, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TOTAL", color = InkSoft, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.6.sp))
                        Money(total, color = Ink, size = 20.sp)
                    }
                    Box(modifier = Modifier.size(width = 1.dp, height = 32.dp).background(RuleStrong))
                    Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                        Text("FALTA", color = InkSoft, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.6.sp))
                        Money(left, color = if (left > 0) Red else InkSoft, size = 20.sp)
                    }
                }
            }

            item { FieldLabel("Parcelas") }

            items(d.installments, key = { it.id }) { inst ->
                InstallmentRow(
                    inst = inst,
                    onClick = { selectedInstallment = inst },
                    onMarkPaid = { vm.markPaid(inst.id) },
                )
            }

            if (!d.order.observation.isNullOrBlank()) {
                item { FieldLabel("Observação") }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Paper)
                            .drawBehind {
                                val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
                                )
                                drawRoundRect(
                                    color = RuleStrong,
                                    topLeft = Offset(0f, 0f),
                                    size = size,
                                    style = stroke,
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Text(
                            "\"${d.order.observation}\"",
                            color = Ink,
                            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, fontStyle = FontStyle.Italic, lineHeight = 21.sp),
                        )
                    }
                }
            }

            item {
                GhostButton(
                    text = "Editar pedido",
                    fillWidth = true,
                    color = GhostColor.PAPER,
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Ink, modifier = Modifier.size(16.dp)) },
                    onClick = { showEditDialog = true },
                )
            }
        }

        if (showEditDialog) {
            val paidSum = d.installments.filter { it.paidAt != null }.sumOf { it.amountCents }
            val hasOpen = d.installments.any { it.paidAt == null }
            EditOrderDialog(
                what = d.order.what,
                observation = d.order.observation.orEmpty(),
                createdAt = d.order.createdAt,
                totalCents = d.order.totalCents,
                paidSumCents = paidSum,
                hasOpenInstallment = hasOpen,
                errorMessage = uiError,
                onClearError = { vm.clearError() },
                onDismiss = {
                    showEditDialog = false
                    vm.clearError()
                },
                onSave = { what, observation, createdAt, totalCents ->
                    vm.updateOrder(what, observation, createdAt, totalCents)
                    showEditDialog = false
                },
            )
        }

        if (showDeleteDialog) {
            DeleteOrderDialog(
                installmentCount = d.installments.size,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    vm.deleteOrder()
                },
            )
        }

        selectedInstallment?.let { inst ->
            // Pull live version from current detail to keep state in sync
            val live = d.installments.firstOrNull { it.id == inst.id } ?: inst
            InstallmentActionsSheet(
                inst = live,
                onDismiss = { selectedInstallment = null },
                onPostpone = { newDate ->
                    vm.postponeInstallment(live.id, newDate)
                    selectedInstallment = null
                },
                onUpdateAmount = { cents ->
                    vm.updateInstallmentAmount(live.id, cents)
                    selectedInstallment = null
                },
                onUnmarkPaid = {
                    vm.unmarkPaid(live.id)
                    selectedInstallment = null
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditOrderDialog(
    what: String,
    observation: String,
    createdAt: LocalDate,
    totalCents: Long,
    paidSumCents: Long,
    hasOpenInstallment: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (what: String, observation: String, createdAt: LocalDate, totalCents: Long) -> Unit,
) {
    var draftWhat by remember(what) { mutableStateOf(what) }
    var draftObservation by remember(observation) { mutableStateOf(observation) }
    var draftDate by remember(createdAt) { mutableStateOf(createdAt) }
    var draftTotalText by remember(totalCents) { mutableStateOf(centsToDigits(totalCents)) }
    var showDatePicker by remember { mutableStateOf(false) }

    val parsedTotal = digitsToCents(draftTotalText)
    val totalChanged = parsedTotal != totalCents
    val canEditTotal = hasOpenInstallment
    val totalBelowPaid = paidSumCents > 0L && parsedTotal in 1 until paidSumCents
    val totalInvalid = parsedTotal <= 0L
    val saveEnabled = draftWhat.isNotBlank() &&
        !totalInvalid &&
        !totalBelowPaid &&
        (!totalChanged || canEditTotal)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar pedido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = draftWhat,
                    onValueChange = { draftWhat = it },
                    label = { Text("Produto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = draftTotalText,
                    onValueChange = { v ->
                        draftTotalText = v.filter { it.isDigit() }.take(12)
                        if (errorMessage != null) onClearError()
                    },
                    label = { Text("Valor total") },
                    singleLine = true,
                    enabled = canEditTotal,
                    prefix = { Text("R$ ", color = InkSoft) },
                    visualTransformation = MoneyVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = totalBelowPaid || totalInvalid,
                )
                if (!canEditTotal) {
                    Text(
                        "todas as parcelas estão pagas — não dá pra alterar o total",
                        color = InkSoft,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                } else if (totalBelowPaid) {
                    Text(
                        "já recebido ${formatBrl(paidSumCents)} — não pode reduzir abaixo disso",
                        color = Red,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                } else if (totalChanged) {
                    Text(
                        "isso vai redividir as parcelas em aberto",
                        color = InkSoft,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Rule, RoundedCornerShape(8.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Data do pedido",
                        color = InkSoft,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        ),
                    )
                    Text(
                        formatDayMonth(draftDate),
                        color = Ink,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        ),
                    )
                }
                OutlinedTextField(
                    value = draftObservation,
                    onValueChange = { draftObservation = it },
                    label = { Text("Observação") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (errorMessage != null) {
                    Text(
                        errorMessage,
                        color = Red,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        draftWhat,
                        draftObservation,
                        draftDate,
                        if (canEditTotal) parsedTotal else totalCents,
                    )
                },
                enabled = saveEnabled,
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        containerColor = Paper,
        textContentColor = Ink,
        titleContentColor = Ink,
    )

    if (showDatePicker) {
        val initialMillis = draftDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        draftDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
            colors = DatePickerDefaults.colors(containerColor = Paper),
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun DeleteOrderDialog(
    installmentCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir pedido?") },
        text = {
            Text(
                "Isso vai apagar este pedido e suas $installmentCount " +
                    if (installmentCount == 1) "parcela. Não dá pra desfazer." else "parcelas. Não dá pra desfazer.",
            )
        },
        confirmButton = {
            Box(modifier = Modifier.padding(end = 8.dp)) {
                PrimaryButton(
                    text = "Excluir",
                    color = PrimaryColor.RED,
                    fillWidth = false,
                    onClick = onConfirm,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        containerColor = Paper,
        textContentColor = Ink,
        titleContentColor = Ink,
    )
}

@Composable
private fun InstallmentRow(
    inst: InstallmentEntity,
    onClick: () -> Unit,
    onMarkPaid: () -> Unit,
) {
    val paid = inst.paidAt != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Paper)
            .border(1.dp, Rule, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .let { if (paid) it.alpha(0.7f) else it },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (paid) GreenSoft else PaperAlt)
                .border(if (paid) 0.dp else 1.dp, Rule, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (paid) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Green, modifier = Modifier.size(17.dp))
            } else {
                Text(
                    inst.number.toString(),
                    color = InkSoft,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Parcela ${inst.number}/${inst.ofTotal}",
                color = Ink,
                style = TextStyle(
                    fontFamily = NunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    textDecoration = if (paid) TextDecoration.LineThrough else TextDecoration.None,
                ),
            )
            val statusText = if (paid) "pago em ${formatDayMonth(inst.paidAt!!)}" else describeDue(inst.dueDate)
            Text(
                "vence ${formatDayMonth(inst.dueDate)} · $statusText",
                color = InkSoft,
                modifier = Modifier.padding(top = 1.dp),
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            )
        }
        Money(
            inst.amountCents,
            color = if (paid) InkSoft else Ink,
            size = 15.sp,
        )
        if (!paid) {
            Spacer(modifier = Modifier.size(8.dp))
            FilledTonalButton(
                onClick = onMarkPaid,
                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                    containerColor = GreenSoft,
                    contentColor = Green,
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    "Recebi",
                    color = Green,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
                )
            }
        }
    }
}

private fun moneyText(cents: Long): String = formatBrl(cents)

private fun com.caderninho.vendas.data.repo.OrderDetail.summaryText(): String {
    val paid = installments.filter { it.paidAt != null }.sumOf { it.amountCents }
    val left = installments.filter { it.paidAt == null }.sumOf { it.amountCents }
    return buildString {
        appendLine(customer.name)
        appendLine(order.what)
        appendLine("Total: ${moneyText(order.totalCents)}")
        appendLine("Pago: ${moneyText(paid)}")
        appendLine("Falta: ${moneyText(left)}")
        installments.forEach { inst ->
            appendLine("Parcela ${inst.number}/${inst.ofTotal}: ${moneyText(inst.amountCents)} - ${if (inst.paidAt == null) describeDue(inst.dueDate) else "pago em ${formatDayMonth(inst.paidAt!!)}"}")
        }
        if (!order.observation.isNullOrBlank()) appendLine("Obs: ${order.observation}")
    }.trim()
}
