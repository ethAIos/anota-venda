package com.caderninho.vendas.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.components.BrandMark
import com.caderninho.vendas.ui.components.GhostButton
import com.caderninho.vendas.ui.components.GhostColor
import com.caderninho.vendas.ui.components.PaperScaffold
import com.caderninho.vendas.ui.components.PrimaryButton
import com.caderninho.vendas.ui.components.paperScaffoldContentPadding
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.LocalCaveatStyle
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.ui.theme.RuleStrong
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    PaperScaffold { padding ->
        val pagerState = rememberPagerState(pageCount = { 2 })
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paperScaffoldContentPadding(padding)
                .padding(horizontal = 22.dp, vertical = 20.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                GhostButton(
                    text = "pular",
                    color = GhostColor.PAPER,
                    onClick = onFinish,
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (page == 0) OnboardingPaperIllustration() else OnboardingHomeIllustration()
                    }
                    OnboardingCopy(page)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(2) { i ->
                    val active = i == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(if (active) 24.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (active) Green else RuleStrong),
                    )
                }
            }

            PrimaryButton(
                text = if (pagerState.currentPage == 0) "Próximo" else "Bora começar",
                onClick = {
                    if (pagerState.currentPage == 0) {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    } else {
                        onFinish()
                    }
                },
            )
        }
    }
}

@Composable
private fun OnboardingCopy(page: Int) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (page == 0) "Vamos te lembrar de quem te deve."
                   else "Adiciona o widget na sua tela.",
            color = Ink,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = NunitoFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (page == 0) 30.sp else 28.sp,
                letterSpacing = 0.sp,
                lineHeight = 36.sp,
            ),
        )
        Text(
            text = if (page == 0) "Sem planilha, sem complicação. Você anota, a gente avisa."
                   else "Segura num espaço vazio da home → \"Widgets\" → arrasta o caderninho.",
            color = InkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp),
            style = TextStyle(
                fontFamily = NunitoFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
        )
    }
}

@Composable
private fun OnboardingPaperIllustration() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .rotate(-3f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(PaperAlt)
                .border(BorderStroke(1.dp, Rule), RoundedCornerShape(14.dp)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // ruled lines
                var y = 34.dp.toPx()
                while (y < size.height) {
                    drawLine(
                        color = Rule,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                    )
                    y += 34.dp.toPx()
                }
                // red margin
                drawLine(
                    color = Red.copy(alpha = 0.4f),
                    start = Offset(16.dp.toPx(), 12.dp.toPx()),
                    end = Offset(16.dp.toPx(), size.height - 12.dp.toPx()),
                    strokeWidth = 1f,
                )
            }
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 24.dp, end = 22.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val handStyle = LocalCaveatStyle.current.copy(fontSize = 24.sp, color = Ink)
                Text("Maria · 45", style = handStyle.copy(textDecoration = TextDecoration.LineThrough))
                Text("Joana · 30", style = handStyle)
                Text("Bia · 60", style = handStyle)
                Text("Carla · 22", style = handStyle)
            }
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .align(Alignment.TopEnd)
                .padding(top = 0.dp)
                .clip(CircleShape)
                .background(Green),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                val p = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.55f)
                    lineTo(size.width * 0.42f, size.height * 0.78f)
                    lineTo(size.width * 0.85f, size.height * 0.25f)
                }
                drawPath(p, color = Color.White, style = Stroke(width = 3.dp.toPx()))
            }
        }
    }
}

@Composable
private fun OnboardingHomeIllustration() {
    Box(
        modifier = Modifier
            .size(width = 260.dp, height = 240.dp),
    ) {
        // tiny phone outline
        Box(
            modifier = Modifier
                .padding(start = 60.dp)
                .size(width = 140.dp, height = 220.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFB59873))
                .border(BorderStroke(6.dp, Ink), RoundedCornerShape(18.dp))
                .padding(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Paper)
                        .padding(8.dp),
                ) {
                    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Pagam hoje", color = Ink, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp))
                            BrandMark(size = 10.sp, color = InkSoft)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Maria", color = Ink, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 8.sp))
                            Text("R$ 45", color = Green, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 8.sp))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Joana", color = Ink, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 8.sp))
                            Text("R$ 30", color = Green, style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 8.sp))
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)), RoundedCornerShape(6.dp)),
                        )
                    }
                }
            }
        }
        // hand-drawn arrow
        Canvas(modifier = Modifier.size(width = 80.dp, height = 100.dp).padding(top = 16.dp)) {
            val path = Path().apply {
                moveTo(5.dp.toPx(), 80.dp.toPx())
                quadraticBezierTo(10.dp.toPx(), 30.dp.toPx(), 60.dp.toPx(), 28.dp.toPx())
            }
            drawPath(path, color = Green, style = Stroke(width = 2.5.dp.toPx()))
            val arrow = Path().apply {
                moveTo(50.dp.toPx(), 22.dp.toPx())
                lineTo(62.dp.toPx(), 28.dp.toPx())
                lineTo(52.dp.toPx(), 36.dp.toPx())
            }
            drawPath(arrow, color = Green, style = Stroke(width = 2.5.dp.toPx()))
        }
        Text(
            text = "aqui!",
            color = Green,
            modifier = Modifier
                .padding(start = 0.dp, top = 86.dp)
                .rotate(-6f),
            style = LocalCaveatStyle.current.copy(fontSize = 22.sp),
        )
    }
}
