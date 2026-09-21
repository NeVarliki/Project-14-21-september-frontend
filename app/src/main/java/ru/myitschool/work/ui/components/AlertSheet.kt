package ru.myitschool.work.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.myitschool.work.R
import ru.myitschool.work.ui.theme.WorkTheme

sealed interface AlertKind {
    data class TooMany(val secondsLeft: Int) : AlertKind
    data class Success(val place: String) : AlertKind
    data object Cancel : AlertKind
    data object NoInternet : AlertKind
    data class Error(val message: String) : AlertKind
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertSheet(
    kind: AlertKind,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
) {
    val colors = WorkTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = colors.alertBackground,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        dragHandle = {
            Icon(
                painter = painterResource(R.drawable.ic_alert_chevron),
                contentDescription = null,
                tint = colors.onAlert.copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 8.dp)
                    .width(26.dp)
                    .height(9.dp)
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 25.dp)
                .testTag("alert_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(30.dp))
            Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                when (kind) {
                    is AlertKind.Success -> Icon(
                        painter = painterResource(R.drawable.ic_alert_check),
                        contentDescription = null,
                        tint = colors.onAlert.copy(alpha = 0.7f),
                        modifier = Modifier.size(width = 211.dp, height = 193.dp)
                    )
                    else -> Icon(
                        painter = painterResource(R.drawable.ic_alert_exclaim),
                        contentDescription = null,
                        tint = colors.onAlert.copy(alpha = 0.7f),
                        modifier = Modifier.size(width = 36.dp, height = 200.dp)
                    )
                }
            }
            Spacer(Modifier.height(30.dp))
            Text(
                text = when (kind) {
                    is AlertKind.TooMany -> "Много запросов"
                    is AlertKind.Success -> "Место ${kind.place} забронировано"
                    AlertKind.Cancel -> "Отменить?"
                    AlertKind.NoInternet -> "Нет соединения!"
                    is AlertKind.Error -> "Ошибка"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onAlert,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("alert_title")
            )
            val subtitle = when (kind) {
                is AlertKind.TooMany -> "Повторите через ${kind.secondsLeft} секунд"
                AlertKind.Cancel -> "Бронь отменится"
                is AlertKind.Error -> kind.message
                else -> null
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onAlert.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(if (subtitle == null) 55.dp else 55.dp))
            if (kind !is AlertKind.Success) {
                PillButton(
                    text = if (kind is AlertKind.Cancel) "Да" else "Повторить",
                    onClick = onAction,
                    enabled = kind !is AlertKind.TooMany,
                    container = colors.alertButton,
                    content = colors.onAlertButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("alert_button")
                )
            }
        }
    }
}
