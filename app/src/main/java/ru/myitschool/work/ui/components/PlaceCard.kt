package ru.myitschool.work.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.theme.WorkTheme

@Composable
fun PlaceCard(
    hint: String,
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = WorkTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(colors.card)
            .padding(horizontal = 17.dp, vertical = 19.dp)
    ) {
        Text(
            text = hint,
            style = MaterialTheme.typography.bodySmall,
            color = colors.cardHint,
            modifier = Modifier.testTag(TestIds.Main.ITEM_DATE)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onCard,
            modifier = Modifier.testTag(TestIds.Main.ITEM_PLACE)
        )
        if (action != null) {
            Spacer(Modifier.height(15.dp))
            action()
        }
    }
}

@Composable
fun ActionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = WorkTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(colors.card)
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp, vertical = 19.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onCard
        )
    }
}
