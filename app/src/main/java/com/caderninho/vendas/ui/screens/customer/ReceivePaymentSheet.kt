package com.caderninho.vendas.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.components.PaperDialogActions
import com.caderninho.vendas.ui.components.PaperSheetContent
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.util.describeDue
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivePaymentSheet(
    customerName: String,
    productLabel: String,
    dueDate: LocalDate,
    amountCents: Long,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Paper,
    ) {
        PaperSheetContent {
            Column {
                Text(
                    "RECEBI DE",
                    color = InkSoft,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.6.sp),
                )
                Text(
                    customerName,
                    color = Ink,
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = 0.sp),
                )
                Text(
                    "$productLabel · ${describeDue(dueDate)}",
                    color = InkSoft,
                    modifier = Modifier.padding(top = 2.dp),
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                )
            }

            Column {
                Text(
                    "QUANTO?",
                    color = InkSoft,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.6.sp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = Green,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx(),
                            )
                        }
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "R$",
                        color = InkSoft,
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    )
                    Text(
                        formatBrl(amountCents).removePrefix("R$ "),
                        color = Green,
                        style = TextStyle(
                            fontFamily = NunitoFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 44.sp,
                            letterSpacing = 0.sp,
                            fontFeatureSettings = "tnum",
                        ),
                    )
                }
                Text(
                    "valor cheio do pedido",
                    color = InkSoft,
                    modifier = Modifier.padding(top = 6.dp),
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                )
            }

            PaperDialogActions(
                onDismiss = onDismiss,
                confirmText = "Confirmar",
                onConfirm = onConfirm,
            )
        }
    }
}
