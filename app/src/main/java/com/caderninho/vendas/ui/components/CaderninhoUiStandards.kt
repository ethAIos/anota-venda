package com.caderninho.vendas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.RedSoft
import com.caderninho.vendas.ui.theme.Rule

object CaderninhoSpacing {
    val screenHorizontal = 22.dp
    val compactScreenHorizontal = 16.dp
    val screenVertical = 8.dp
    val cardPadding = 14.dp
    val rowGap = 10.dp
    val contentMaxWidth = 560.dp
}

object CaderninhoInteraction {
    val minTouchTarget = 48.dp
    val iconTargetSize = 48.dp
    val iconSize = 20.dp
    val rowMinHeight = 56.dp
    val controlRadius = 12.dp
}

fun Modifier.caderninhoContentWidth(): Modifier =
    this
        .fillMaxWidth()
        .widthIn(max = CaderninhoSpacing.contentMaxWidth)

@Composable
fun PaperIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Ink,
    containerColor: Color = Color.Transparent,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(CaderninhoInteraction.iconTargetSize),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = tint,
            disabledContentColor = InkSoft.copy(alpha = 0.45f),
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(CaderninhoInteraction.iconSize),
        )
    }
}

@Composable
fun PaperInteractiveSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = if (selected) Paper else PaperAlt,
    selectedContainerColor: Color = Paper,
    borderColor: Color = if (selected) Ink else Rule,
    shape: RoundedCornerShape = RoundedCornerShape(CaderninhoInteraction.controlRadius),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = CaderninhoInteraction.minTouchTarget)
            .clip(shape),
        enabled = enabled,
        shape = shape,
        color = if (selected) selectedContainerColor else containerColor,
        contentColor = Ink,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
fun PaperListRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    enabled: Boolean = true,
) {
    PaperInteractiveSurface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        containerColor = Paper,
    ) {
        if (leading != null) leading()
        androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 14.5.sp),
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = InkSoft,
                    modifier = Modifier.padding(top = 1.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp),
                )
            }
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = InkSoft,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun PaperChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(99.dp)
    Surface(
        modifier = modifier
            .defaultMinSize(
                minWidth = CaderninhoInteraction.minTouchTarget,
                minHeight = CaderninhoInteraction.minTouchTarget,
            )
            .clip(shape)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        shape = shape,
        color = if (selected) Ink else PaperAlt,
        contentColor = if (selected) Paper else Ink,
        border = if (selected) null else BorderStroke(1.dp, Rule),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp),
        )
    }
}

@Composable
fun <T> PaperSegmentedChoice(
    options: List<ToggleOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { opt ->
            val isSelected = opt.value == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = CaderninhoInteraction.minTouchTarget)
                    .clip(RoundedCornerShape(CaderninhoInteraction.controlRadius))
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelect(opt.value) },
                        role = Role.RadioButton,
                    ),
                shape = RoundedCornerShape(CaderninhoInteraction.controlRadius),
                color = if (isSelected) Ink else PaperAlt,
                contentColor = if (isSelected) Paper else Ink,
                border = if (isSelected) null else BorderStroke(1.dp, Rule),
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = opt.label,
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                    )
                    if (opt.sub != null) {
                        Text(
                            text = opt.sub,
                            color = if (isSelected) Paper.copy(alpha = 0.72f) else InkSoft,
                            style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaperStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 6,
    min: Int = 1,
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        (min..max).forEach { n ->
            val isSelected = n == value
            Surface(
                modifier = Modifier
                    .size(CaderninhoInteraction.minTouchTarget)
                    .clip(RoundedCornerShape(12.dp))
                    .selectable(
                        selected = isSelected,
                        onClick = { onValueChange(n) },
                        role = Role.RadioButton,
                    ),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Ink else PaperAlt,
                contentColor = if (isSelected) Paper else Ink,
                border = if (isSelected) null else BorderStroke(1.dp, Rule),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = n.toString(),
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                    )
                }
            }
        }
    }
}

@Composable
fun PaperDialogActions(
    onDismiss: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String = "Cancelar",
    destructive: Boolean = false,
    confirmEnabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        GhostButton(
            text = dismissText,
            color = GhostColor.PAPER,
            fillWidth = true,
            modifier = Modifier.weight(1f),
            onClick = onDismiss,
        )
        PrimaryButton(
            text = confirmText,
            color = if (destructive) PrimaryColor.RED else PrimaryColor.GREEN,
            fillWidth = true,
            enabled = confirmEnabled,
            modifier = Modifier.weight(1f),
            onClick = onConfirm,
        )
    }
}

@Composable
fun PaperBottomSheetActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Ink,
    destructive: Boolean = false,
) {
    PaperInteractiveSurface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        containerColor = if (destructive) RedSoft else PaperAlt,
        borderColor = if (destructive) RedSoft else Rule,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Text(
            label,
            color = color,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
        )
    }
}

@Composable
fun PaperActionRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable
fun PaperSheetContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CaderninhoSpacing.screenHorizontal, vertical = 12.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        content = content,
    )
}
