package ru.myitschool.work.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import ru.myitschool.work.ui.theme.WorkTheme

enum class HomeTab(val title: String, val tag: String) {
    Main("Главная", "tab_main"),
    Places("Рабочие места", "tab_places"),
    Rooms("Переговорки", "tab_rooms"),
}

@Composable
fun SideBar(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = WorkTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(colors.chipBar)
            .padding(15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.entries.forEach { tab ->
                val isSelected = tab == selected
                Box(
                    modifier = Modifier
                        .testTag(tab.tag)
                        .clip(CircleShape)
                        .background(if (isSelected) colors.chipSelected else colors.chipBar)
                        .clickable { onSelect(tab) }
                        .padding(10.dp)
                ) {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) colors.onChipSelected else colors.chipText
                    )
                }
            }
        }
    }
}
