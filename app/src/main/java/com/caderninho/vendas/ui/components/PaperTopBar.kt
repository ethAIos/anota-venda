package com.caderninho.vendas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.NunitoFamily

enum class TopBarLeading { CLOSE, BACK, NONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperTopBar(
    title: String,
    leading: TopBarLeading = TopBarLeading.CLOSE,
    onLeading: () -> Unit = {},
    actions: @Composable () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Ink,
                style = TextStyle(
                    fontFamily = NunitoFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    letterSpacing = 0.sp,
                ),
            )
        },
        navigationIcon = {
            when (leading) {
                TopBarLeading.CLOSE -> PaperIconButton(Icons.Default.Close, "Fechar", onLeading)
                TopBarLeading.BACK -> PaperIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", onLeading)
                TopBarLeading.NONE -> Unit
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            scrolledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            titleContentColor = Ink,
        ),
    )
}
