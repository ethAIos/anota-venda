package com.caderninho.vendas.ui.screens.payingtoday

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caderninho.vendas.data.repo.PayingTodayRow
import com.caderninho.vendas.ui.components.AvatarTone
import com.caderninho.vendas.ui.components.InitialAvatar
import com.caderninho.vendas.ui.components.Money
import com.caderninho.vendas.ui.components.PaperIconButton
import com.caderninho.vendas.ui.components.PaperInteractiveSurface
import com.caderninho.vendas.ui.components.PaperScaffold
import com.caderninho.vendas.ui.components.PaperTopBar
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.TopBarLeading
import com.caderninho.vendas.ui.components.paperScaffoldContentPadding
import com.caderninho.vendas.ui.screens.customer.ReceivePaymentSheet
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.LocalCaveatStyle
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.RedSoft
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.util.WhatsAppLauncher
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun PayingTodayScreen(
    onOpenCustomer: (Long) -> Unit,
    onOpenNewSale: () -> Unit,
    onOpenSettings: () -> Unit,
    initialReceiveInstallmentId: Long? = null,
    vm: PayingTodayViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var receiveInstallmentId by rememberSaveable(initialReceiveInstallmentId) {
        mutableStateOf(initialReceiveInstallmentId)
    }
    val receiveRowOverride by vm.receiveRowForDeepLink.collectAsStateWithLifecycle()
    LaunchedEffect(initialReceiveInstallmentId) {
        val id = initialReceiveInstallmentId ?: return@LaunchedEffect
        receiveInstallmentId = id
        vm.openReceiveInstallment(id)
    }
    val receiveRow = state.rows.firstOrNull { it.installment.id == receiveInstallmentId }
        ?: receiveRowOverride?.takeIf { it.installment.id == receiveInstallmentId }
    val overdueDays = ChronoUnit.DAYS.between(state.selectedDate, state.today)
    val showSearchEmpty = state.query.isNotBlank() && state.filteredRows.isEmpty()

    PaperScaffold(
        topBar = {
            PaperTopBar(
                title = "Cobranças",
                leading = TopBarLeading.NONE,
                actions = {
                    PaperIconButton(
                        icon = if (state.searchOpen) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (state.searchOpen) "Fechar busca" else "Buscar",
                        onClick = vm::toggleSearch,
                    )
                    PaperIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        onClick = onOpenSettings,
                    )
                },
            )
        },
        floatingActionButton = {
            PrimaryButton(
                text = "Anotar venda",
                onClick = onOpenNewSale,
                fillWidth = false,
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Paper, modifier = Modifier.size(18.dp)) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paperScaffoldContentPadding(padding),
        ) {
            DateControls(
                state = state,
                onTogglePicker = vm::togglePicker,
                onPreviousWeek = vm::previousWeek,
                onNextWeek = vm::nextWeek,
                onSelectDate = vm::selectDate,
                onQueryChange = vm::updateQuery,
            )

            if (state.filteredRows.isEmpty()) {
                EmptyDateState(
                    searchEmpty = showSearchEmpty,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 6.dp, bottom = 96.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (overdueDays > 0) {
                        item(key = "overdue-heading") {
                            OverdueHeading(days = overdueDays)
                        }
                    }
                    items(state.filteredRows, key = { it.installment.id }) { row ->
                        PayingRowCard(
                            row = row,
                            overdue = row.installment.dueDate.isBefore(state.today),
                            onTap = { onOpenCustomer(row.customer.id) },
                            onReceive = { receiveInstallmentId = row.installment.id },
                            onWhatsApp = {
                                scope.launch {
                                    val msg = vm.chargeMessage(row)
                                    WhatsAppLauncher.launch(context, row.customer.phone, msg)
                                }
                            },
                            onPostpone = { vm.postpone(row.installment.id) },
                        )
                    }
                }
            }
        }

        if (receiveRow != null) {
            ReceivePaymentSheet(
                customerName = receiveRow.customer.name,
                productLabel = receiveRow.order.what,
                dueDate = receiveRow.installment.dueDate,
                amountCents = receiveRow.installment.amountCents,
                onConfirm = {
                    vm.markPaid(receiveRow.installment.id)
                    receiveInstallmentId = null
                    vm.clearReceiveInstallment()
                },
                onDismiss = {
                    receiveInstallmentId = null
                    vm.clearReceiveInstallment()
                },
            )
        }
    }
}

@Composable
private fun DateControls(
    state: PayingTodayState,
    onTogglePicker: () -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DatePill(
            label = datePillLabel(state.selectedDate, state.today),
            open = state.pickerOpen,
            totalCents = state.totalCents,
            count = state.rows.distinctBy { it.customer.id }.size,
            onClick = onTogglePicker,
        )
        if (state.pickerOpen) {
            WeekPicker(
                weekStart = state.weekStart,
                selectedDate = state.selectedDate,
                today = state.today,
                summaries = state.weekSummaries,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                onSelectDate = onSelectDate,
            )
        }
        if (state.searchOpen) {
            SearchField(
                query = state.query,
                onQueryChange = onQueryChange,
            )
        }
    }
}

@Composable
private fun DatePill(
    label: String,
    open: Boolean,
    totalCents: Long,
    count: Int,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    PaperInteractiveSurface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        selected = open,
        containerColor = PaperAlt,
        selectedContainerColor = Paper,
        borderColor = if (open) Ink else Rule,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Ink, modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
            )
            Row(
                modifier = Modifier.padding(top = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = if (count == 0) "sem cobranças" else "$count pessoa${if (count == 1) "" else "s"}",
                    color = InkSoft,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                )
                if (totalCents > 0) {
                    Text(
                        text = "·",
                        color = InkSoft,
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp),
                    )
                    Money(totalCents, color = Green, size = 12.sp, weight = FontWeight.Bold)
                }
            }
        }
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = InkSoft,
            modifier = Modifier
                .size(20.dp)
                .rotate(if (open) 180f else 0f),
        )
    }
}

@Composable
private fun WeekPicker(
    weekStart: LocalDate,
    selectedDate: LocalDate,
    today: LocalDate,
    summaries: List<WeekSummary>,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val summariesByDate = summaries.associateBy { it.date }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PaperAlt)
            .border(1.dp, Rule, shape)
            .padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            WeekArrow(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Semana anterior", onPreviousWeek)
            Text(
                text = monthLabel(weekStart),
                color = Ink,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
            )
            WeekArrow(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Próxima semana", onNextWeek)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            (0L..6L).forEach { offset ->
                val date = weekStart.plusDays(offset)
                val summary = summariesByDate[date] ?: WeekSummary(date)
                WeekDayCell(
                    date = date,
                    summary = summary,
                    selected = date == selectedDate,
                    today = date == today,
                    overdue = date.isBefore(today) && summary.count > 0,
                    onClick = { onSelectDate(date) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WeekArrow(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    PaperIconButton(icon = icon, contentDescription = contentDescription, onClick = onClick, tint = InkSoft)
}

@Composable
private fun WeekDayCell(
    date: LocalDate,
    summary: WeekSummary,
    selected: Boolean,
    today: Boolean,
    overdue: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    val bg = if (selected) Ink else Color.Transparent
    val primary = if (selected) Paper else Ink
    val secondary = if (selected) Paper.copy(alpha = 0.72f) else InkSoft
    val indicator = when {
        selected && overdue -> RedSoft
        selected -> Paper.copy(alpha = 0.86f)
        overdue -> Red
        summary.totalCents > 0 -> Green
        else -> InkSoft
    }
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.5.dp,
                color = if (today && !selected) Green else Color.Transparent,
                shape = shape,
            ),
        shape = shape,
        color = bg,
        contentColor = primary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = WEEKDAY_LABELS[date.dayOfWeek.value - 1],
                color = secondary,
                maxLines = 1,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp),
            )
            Text(
                text = date.dayOfMonth.toString(),
                color = primary,
                maxLines = 1,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, lineHeight = 17.sp),
            )
            Text(
                text = if (summary.totalCents > 0) compactMoney(summary.totalCents) else "-",
                color = indicator,
                maxLines = 1,
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp),
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Paper)
            .border(1.dp, Rule, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = InkSoft, modifier = Modifier.size(18.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = NunitoFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Ink,
            ),
            cursorBrush = SolidColor(Green),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            "buscar cliente ou produto",
                            color = InkSoft,
                            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                        )
                    }
                    inner()
                }
            },
        )
        if (query.isNotBlank()) {
            PaperIconButton(
                icon = Icons.Default.Close,
                contentDescription = "Limpar busca",
                onClick = { onQueryChange("") },
                tint = InkSoft,
            )
        }
    }
}

@Composable
private fun OverdueHeading(days: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RedSoft)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Red),
        )
        Text(
            text = "atrasado · $days dia${if (days == 1L) "" else "s"}",
            color = Red,
            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
        )
    }
}

@Composable
private fun EmptyDateState(searchEmpty: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PaperAlt)
                .border(1.dp, Rule, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (searchEmpty) Icons.Default.Search else Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = InkSoft,
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            text = if (searchEmpty) "Nada encontrado" else "dia tranquilo!",
            color = Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
            style = if (searchEmpty) {
                TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            } else {
                LocalCaveatStyle.current.copy(fontSize = 30.sp, lineHeight = 30.sp)
            },
        )
        Text(
            text = if (searchEmpty) "tente outro nome ou produto." else "ninguém tem que pagar nesse dia.",
            color = InkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 6.dp)
                .widthIn(max = 240.dp),
            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 19.sp),
        )
    }
}

@Composable
private fun PayingRowCard(
    row: PayingTodayRow,
    overdue: Boolean,
    onTap: () -> Unit,
    onReceive: () -> Unit,
    onWhatsApp: () -> Unit,
    onPostpone: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Paper)
            .border(1.dp, if (overdue) RedSoft else Rule, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PaperInteractiveSurface(
            onClick = onTap,
            modifier = Modifier.fillMaxWidth(),
            containerColor = PaperAlt,
            borderColor = Rule,
        ) {
            InitialAvatar(
                name = row.customer.name,
                size = 40.dp,
                tone = if (overdue) AvatarTone.RED else AvatarTone.AMBER,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    row.customer.name,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 15.5.sp, lineHeight = 17.sp),
                )
                Text(
                    row.order.what,
                    color = InkSoft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp),
                )
            }
            Money(row.installment.amountCents, color = if (overdue) Red else Ink, size = 17.sp)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = InkSoft, modifier = Modifier.size(20.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ActionButton(
                modifier = Modifier.weight(1.4f),
                bg = Green,
                fg = Paper,
                icon = Icons.Default.Check,
                label = "Recebi",
                onClick = onReceive,
            )
            ActionButton(
                modifier = Modifier.weight(1f),
                bg = PaperAlt,
                fg = Ink,
                border = true,
                icon = Icons.AutoMirrored.Filled.Send,
                label = "Cobrar",
                onClick = onWhatsApp,
            )
            ActionButton(
                modifier = Modifier.weight(1f),
                bg = PaperAlt,
                fg = InkSoft,
                border = true,
                icon = Icons.Default.CalendarMonth,
                label = "Adiar",
                onClick = onPostpone,
            )
        }
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier,
    bg: Color,
    fg: Color,
    icon: ImageVector,
    label: String,
    border: Boolean = false,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(shape),
        shape = shape,
        color = bg,
        contentColor = fg,
        border = if (border) androidx.compose.foundation.BorderStroke(1.dp, Rule) else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            Text(
                label,
                color = fg,
                maxLines = 1,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.5.sp),
            )
        }
    }
}

private val PT_BR = Locale("pt", "BR")
private val DAY_MONTH = DateTimeFormatter.ofPattern("d 'de' MMMM", PT_BR)
private val MONTH_YEAR = DateTimeFormatter.ofPattern("LLLL · yyyy", PT_BR)
private val WEEKDAY_LABELS = listOf("seg", "ter", "qua", "qui", "sex", "sáb", "dom")

private fun datePillLabel(date: LocalDate, today: LocalDate): String {
    val dateLabel = date.format(DAY_MONTH).lowercase(PT_BR)
    return when (date) {
        today -> "hoje, $dateLabel"
        today.minusDays(1) -> "ontem, $dateLabel"
        today.plusDays(1) -> "amanhã, $dateLabel"
        else -> "${WEEKDAY_LABELS[date.dayOfWeek.value - 1]}, $dateLabel"
    }
}

private fun monthLabel(weekStart: LocalDate): String =
    weekStart.plusDays(3).format(MONTH_YEAR).lowercase(PT_BR)

private val COMPACT_INT = NumberFormat.getIntegerInstance(PT_BR)

private fun compactMoney(cents: Long): String {
    val reais = cents / 100
    val fmt = COMPACT_INT.format(reais)
    return if (cents % 100 == 0L) "R$ $fmt" else {
        val centavos = (cents % 100).toString().padStart(2, '0')
        "R$ $fmt,$centavos"
    }
}
