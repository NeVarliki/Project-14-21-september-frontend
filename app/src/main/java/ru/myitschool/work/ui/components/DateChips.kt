package ru.myitschool.work.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.theme.WorkTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val chipFormat = DateTimeFormatter.ofPattern("dd.MM")

@Composable
fun DateChips(
    dates: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabledIndices: Set<Int>? = null,
) {
    val colors = WorkTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(colors.chipBar)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dates.forEachIndexed { index, date ->
                val isSelected = index == selected
                val enabled = enabledIndices?.contains(index) ?: true
                Box(
                    modifier = Modifier
                        .testTag(TestIds.Book.getIdDateItemByPosition(index))
                        .clip(CircleShape)
                        .background(if (isSelected) colors.chipSelected else colors.chipBar)
                        .clickable(enabled = enabled) { onSelect(index) }
                        .padding(10.dp)
                ) {
                    Text(
                        text = runCatching { LocalDate.parse(date).format(chipFormat) }.getOrDefault(date),
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            isSelected -> colors.onChipSelected
                            enabled -> colors.chipText
                            else -> colors.chipText.copy(alpha = 0.35f)
                        },
                        modifier = Modifier.testTag(TestIds.Book.ITEM_DATE)
                    )
                }
            }
        }
    }
}
