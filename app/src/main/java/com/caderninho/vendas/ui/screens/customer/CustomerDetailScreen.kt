package com.caderninho.vendas.ui.screens.customer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caderninho.vendas.data.model.OrderStatus
import com.caderninho.vendas.data.repo.CustomerOrderSummary
import com.caderninho.vendas.ui.components.DotTone
import com.caderninho.vendas.ui.components.FieldLabel
import com.caderninho.vendas.ui.components.GhostButton
import com.caderninho.vendas.ui.components.GhostColor
import com.caderninho.vendas.ui.components.InitialAvatar
import com.caderninho.vendas.ui.components.Money
import com.caderninho.vendas.ui.components.PaperDialogActions
import com.caderninho.vendas.ui.components.PaperIconButton
import com.caderninho.vendas.ui.components.PaperListRow
import com.caderninho.vendas.ui.components.PaperScaffold
import com.caderninho.vendas.ui.components.PaperTopBar
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.StatusDot
import com.caderninho.vendas.ui.components.TopBarLeading
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.components.paperScaffoldContentPadding
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.util.WhatsAppLauncher
import com.caderninho.vendas.util.describeDue
import com.caderninho.vendas.util.formatDayMonth
import kotlinx.coroutines.launch

@Composable
fun CustomerDetailScreen(
    onBack: () -> Unit,
    onOpenOrder: (Long) -> Unit,
    onNewSaleForCustomer: (Long) -> Unit,
    onCustomerDeleted: () -> Unit = onBack,
    vm: CustomerDetailViewModel = hiltViewModel(),
) {
    val summary by vm.summary.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is CustomerDetailViewModel.Event.Deleted -> onCustomerDeleted()
            }
        }
    }

    PaperScaffold(
        topBar = {
            PaperTopBar(
                title = summary?.customer?.name.orEmpty(),
                leading = TopBarLeading.BACK,
                onLeading = onBack,
                actions = {
                    PaperIconButton(Icons.Default.MoreVert, "Mais", { showMenu = true })
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        val s = summary
                        DropdownMenuItem(
                            text = { Text("Editar cliente") },
                            onClick = {
                                showMenu = false
                                showEditDialog = true
                            },
                            enabled = s != null,
                        )
                        DropdownMenuItem(
                            text = { Text("Novo pedido") },
                            onClick = {
                                showMenu = false
                                s?.let { onNewSaleForCustomer(it.customer.id) }
                            },
                            enabled = s != null,
                        )
                        DropdownMenuItem(
                            text = { Text("Cobrar no WhatsApp") },
                            onClick = {
                                showMenu = false
                                scope.launch {
                                    val msg = vm.chargeMessage()
                                    val customer = summary?.customer
                                    if (msg != null && customer != null) {
                                        WhatsAppLauncher.launch(context, customer.phone, msg)
                                    }
                                }
                            },
                            enabled = (s?.openBalanceCents ?: 0L) > 0L,
                        )
                        DropdownMenuItem(
                            text = { Text("Copiar telefone") },
                            onClick = {
                                showMenu = false
                                val phone = summary?.customer?.phone.orEmpty()
                                if (phone.isNotBlank()) {
                                    clipboard.setText(AnnotatedString(phone))
                                    Toast.makeText(context, "Telefone copiado", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !s?.customer?.phone.isNullOrBlank(),
                        )
                        DropdownMenuItem(
                            text = { Text("Excluir cliente", color = Red) },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            enabled = s != null,
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
        val s = summary
        if (s == null) {
            Spacer(modifier = Modifier.fillMaxSize())
            return@PaperScaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .paperScaffoldContentPadding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp, top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PaperAlt)
                        .border(1.dp, Rule, RoundedCornerShape(16.dp))
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    InitialAvatar(name = s.customer.name, size = 52.dp, tone = s.customer.avatarTone)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            s.customer.name,
                            color = Ink,
                            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp),
                        )
                        if (!s.customer.phone.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = InkSoft, modifier = Modifier.size(13.dp))
                                Text(
                                    s.customer.phone!!,
                                    color = InkSoft,
                                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "SALDO EM ABERTO",
                        color = InkSoft,
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.6.sp),
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Money(s.openBalanceCents, color = Red, size = 42.sp)
                    Text(
                        text = "de ${s.openOrderCount} pedido${if (s.openOrderCount == 1) "" else "s"}" +
                            if (s.overdueOrderCount > 0) " · ${s.overdueOrderCount} atrasado${if (s.overdueOrderCount == 1) "" else "s"}" else "",
                        color = InkSoft,
                        modifier = Modifier.padding(top = 2.dp),
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PrimaryButton(
                            text = "Novo pedido",
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Paper, modifier = Modifier.size(18.dp)) },
                            onClick = { onNewSaleForCustomer(s.customer.id) },
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PrimaryButton(
                            text = "Recebi",
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = Paper, modifier = Modifier.size(18.dp)) },
                            onClick = { showSheet = true },
                        )
                    }
                }
            }

            item {
                GhostButton(
                    text = "Mandar cobrança no WhatsApp",
                    fillWidth = true,
                    color = GhostColor.PAPER,
                    onClick = {
                        scope.launch {
                            val msg = vm.chargeMessage()
                            if (msg != null) {
                                WhatsAppLauncher.launch(context, s.customer.phone, msg)
                            }
                        }
                    },
                )
            }

            item { FieldLabel("Pedidos") }

            items(s.orders, key = { it.order.id }) { os ->
                OrderRow(os = os, onClick = { onOpenOrder(os.order.id) })
            }
        }

        if (showSheet) {
            val next = vm.nextOpenInstallment()
            val nextOrder = s.orders.firstOrNull { o -> o.installments.any { it.id == next?.id } }
            if (next != null && nextOrder != null) {
                ReceivePaymentSheet(
                    customerName = s.customer.name,
                    productLabel = nextOrder.order.what,
                    dueDate = next.dueDate,
                    amountCents = next.amountCents,
                    onConfirm = {
                        vm.markPaid(next.id)
                        showSheet = false
                    },
                    onDismiss = { showSheet = false },
                )
            } else {
                showSheet = false
            }
        }

        if (showEditDialog) {
            EditCustomerDialog(
                name = s.customer.name,
                phone = s.customer.phone,
                tone = s.customer.avatarTone,
                onDismiss = { showEditDialog = false },
                onSave = { name, phone, tone ->
                    vm.updateCustomer(name, phone, tone)
                    showEditDialog = false
                },
            )
        }

        if (showDeleteDialog) {
            DeleteCustomerDialog(
                customerName = s.customer.name,
                orderCount = s.orders.size,
                openBalanceCents = s.openBalanceCents,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    vm.deleteCustomer()
                },
            )
        }
    }
}

@Composable
private fun DeleteCustomerDialog(
    customerName: String,
    orderCount: Int,
    openBalanceCents: Long,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir cliente?") },
        text = {
            val orderText = if (orderCount == 1) "1 pedido" else "$orderCount pedidos"
            val balanceText = if (openBalanceCents > 0L)
                " e o saldo em aberto de ${formatBrl(openBalanceCents)}"
            else ""
            Text(
                "Isso vai apagar $customerName, $orderText$balanceText. Não dá pra desfazer.",
            )
        },
        confirmButton = {
            PaperDialogActions(
                onDismiss = onDismiss,
                confirmText = "Excluir",
                destructive = true,
                onConfirm = onConfirm,
            )
        },
        containerColor = Paper,
        textContentColor = Ink,
        titleContentColor = Ink,
    )
}

@Composable
private fun OrderRow(os: CustomerOrderSummary, onClick: () -> Unit) {
    val tone = when (os.status) {
        OrderStatus.OVERDUE -> DotTone.RED
        OrderStatus.DUE_TODAY -> DotTone.AMBER
        OrderStatus.PAID -> DotTone.GREEN
        OrderStatus.OPEN -> DotTone.AMBER
    }
    val descriptor = when (os.status) {
        OrderStatus.PAID -> "pago"
        else -> describeDue(os.installments.firstOrNull { it.paidAt == null }?.dueDate ?: os.order.createdAt)
    }
    PaperListRow(
        title = os.order.what,
        subtitle = "${formatDayMonth(os.order.createdAt)} · $descriptor",
        onClick = onClick,
        leading = { StatusDot(tone = tone) },
        trailing = {
            Money(
                os.order.totalCents,
                color = if (os.status == OrderStatus.PAID) InkSoft else Ink,
                size = 15.sp,
            )
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = InkSoft, modifier = Modifier.size(20.dp))
        },
    )
}
