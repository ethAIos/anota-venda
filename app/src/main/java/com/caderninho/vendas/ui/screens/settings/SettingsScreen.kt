package com.caderninho.vendas.ui.screens.settings

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caderninho.vendas.BuildConfig
import com.caderninho.vendas.ui.components.BrandMark
import com.caderninho.vendas.ui.components.FieldLabel
import com.caderninho.vendas.ui.components.PaperScaffold
import com.caderninho.vendas.ui.components.PaperTopBar
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.PrimaryColor
import com.caderninho.vendas.ui.components.TopBarLeading
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Rule
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
) {
    val template by vm.template.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    PaperScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            PaperTopBar(
                title = "Ajustes",
                leading = TopBarLeading.BACK,
                onLeading = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 22.dp),
        ) {
            Spacer(modifier = Modifier.size(12.dp))

            FieldLabel("Mensagem de cobrança")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaperAlt)
                    .border(1.dp, Rule, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = template,
                    color = Ink,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 23.sp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Paper)
                        .border(1.dp, Rule, RoundedCornerShape(8.dp))
                        .clickable { showEditDialog = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = InkSoft, modifier = Modifier.size(15.dp))
                }
            }
            Text(
                "usamos {nome}, {produto}, {valor} e {data} para preencher.",
                color = InkSoft,
                modifier = Modifier.padding(top = 8.dp),
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 18.sp),
            )

            Spacer(modifier = Modifier.size(26.dp))

            FieldLabel("Backup")
            SettingsRow(
                icon = Icons.Default.Download,
                title = "Exportar dados",
                subtitle = "CSV com clientes, pedidos e parcelas",
                onClick = {
                    scope.launch {
                        runCatching {
                            val file = vm.exportCsvFile(context)
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${BuildConfig.APPLICATION_ID}.fileprovider",
                                file,
                            )
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_SUBJECT, "Caderninho de Vendas")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Exportar dados"))
                        }.onFailure {
                            snackbarHostState.showSnackbar("Não consegui exportar agora")
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.size(26.dp))

            FieldLabel("Apagar dados")
            SettingsRow(
                icon = Icons.Default.DeleteSweep,
                title = "Limpar tudo",
                subtitle = "Remove todos os clientes, pedidos e parcelas",
                onClick = { showClearDialog = true },
            )

            Spacer(modifier = Modifier.size(26.dp))

            FieldLabel("Sobre o app")
            SettingsRow(
                icon = Icons.Default.Info,
                title = "Caderninho de Vendas",
                subtitle = "versão ${BuildConfig.VERSION_NAME}",
                onClick = { showAboutDialog = true },
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BrandMark(size = 26.sp, color = InkSoft)
                Text(
                    "feito com carinho · 2026",
                    color = InkSoft,
                    modifier = Modifier.padding(top = 2.dp),
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                )
            }
        }
    }

    if (showEditDialog) {
        var draft by remember(template) { mutableStateOf(template) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar mensagem") },
            text = {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.saveTemplate(draft)
                    showEditDialog = false
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
            },
            containerColor = Paper,
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Apagar tudo?") },
            text = {
                Text(
                    "Vai apagar todos os clientes, pedidos e parcelas. Não dá pra desfazer.",
                    color = Ink,
                )
            },
            confirmButton = {
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    PrimaryButton(
                        text = "Apagar tudo",
                        color = PrimaryColor.RED,
                        fillWidth = false,
                        onClick = {
                            vm.clearAllData {
                                scope.launch { snackbarHostState.showSnackbar("Tudo limpo") }
                            }
                            showClearDialog = false
                        },
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancelar") }
            },
            containerColor = Paper,
            textContentColor = Ink,
            titleContentColor = Ink,
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Caderninho de Vendas") },
            text = {
                Text(
                    "Versão ${BuildConfig.VERSION_NAME}\n\nApp local para anotar vendas fiadas, acompanhar parcelas e cobrar clientes pelo WhatsApp.",
                    color = Ink,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 22.sp),
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("OK") }
            },
            containerColor = Paper,
        )
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PaperAlt)
            .border(1.dp, Rule, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Paper)
                .border(1.dp, Rule, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = InkSoft, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = Ink,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 14.5.sp),
            )
            Text(
                subtitle,
                color = InkSoft,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp),
            )
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = InkSoft, modifier = Modifier.size(18.dp))
    }
}
