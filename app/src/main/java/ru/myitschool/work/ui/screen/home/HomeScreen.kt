package ru.myitschool.work.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.components.ActionCard
import ru.myitschool.work.ui.components.AlertKind
import ru.myitschool.work.ui.components.AlertSheet
import ru.myitschool.work.ui.components.DateChips
import ru.myitschool.work.ui.components.HoldButton
import ru.myitschool.work.ui.components.HomeTab
import ru.myitschool.work.ui.components.PlaceCard
import ru.myitschool.work.ui.components.SideBar
import ru.myitschool.work.ui.theme.WorkTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val cardDate = DateTimeFormatter.ofPattern("dd.MM.yy")

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val colors = WorkTheme.colors

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is HomeAction.Open -> navController.navigate(action.destination) { popUpTo(0) }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Header(state, viewModel)
            Text(
                text = when (state.tab) {
                    HomeTab.Main -> "Привет, ${state.name.substringBefore(' ').ifBlank { "гость" }}!"
                    HomeTab.Places -> "Забронировать"
                    HomeTab.Rooms -> "Переговорки"
                },
                style = MaterialTheme.typography.headlineLarge,
                color = colors.text,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(colors.sheet)
            ) {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                        color = colors.chipBar
                    )
                } else {
                    when (state.tab) {
                        HomeTab.Main -> MainTab(state, viewModel)
                        HomeTab.Places -> BookTab(state, viewModel, rooms = false)
                        HomeTab.Rooms -> BookTab(state, viewModel, rooms = true)
                    }
                }
                if (state.offline) {
                    Text(
                        text = "Нет соединения — показаны сохранённые данные",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onChipSelected,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .clip(CircleShape)
                            .background(colors.chipSelected)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("offline_banner")
                    )
                }
            }
        }
        SideBar(
            selected = state.tab,
            onSelect = { viewModel.onIntent(HomeIntent.SelectTab(it)) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }

    state.alert?.let { alert ->
        AlertSheet(
            kind = alert,
            onDismiss = { viewModel.onIntent(HomeIntent.DismissAlert) },
            onAction = {
                when (alert) {
                    AlertKind.Cancel -> viewModel.onIntent(HomeIntent.ConfirmFree)
                    AlertKind.NoInternet -> {
                        viewModel.onIntent(HomeIntent.DismissAlert)
                        viewModel.onIntent(HomeIntent.Refresh)
                    }
                    else -> viewModel.onIntent(HomeIntent.DismissAlert)
                }
            }
        )
    }
}

@Composable
private fun Header(state: HomeState, viewModel: HomeViewModel) {
    val colors = WorkTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(state.photoUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .testTag(TestIds.Main.PROFILE_IMAGE)
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.field)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = state.name,
            style = MaterialTheme.typography.bodySmall,
            color = colors.text,
            modifier = Modifier
                .width(90.dp)
                .testTag(TestIds.Main.PROFILE_NAME)
        )
        Spacer(Modifier.weight(1f))
        RoundIcon(
            onClick = { viewModel.onIntent(HomeIntent.Refresh) },
            tag = TestIds.Main.REFRESH_BUTTON
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Обновить", tint = colors.onField)
        }
        Spacer(Modifier.width(10.dp))
        RoundIcon(
            onClick = { viewModel.onIntent(HomeIntent.Logout) },
            tag = TestIds.Main.LOGOUT_BUTTON
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Выйти", tint = colors.onField)
        }
    }
}

@Composable
private fun RoundIcon(onClick: () -> Unit, tag: String, content: @Composable () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .testTag(tag)
            .size(40.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = WorkTheme.colors.field,
            contentColor = WorkTheme.colors.onField
        )
    ) { content() }
}

private fun gridPadding(offline: Boolean) =
    PaddingValues(start = 20.dp, end = 20.dp, top = if (offline) 48.dp else 20.dp, bottom = 120.dp)

@Composable
private fun MainTab(state: HomeState, viewModel: HomeViewModel) {
    val colors = WorkTheme.colors
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        contentPadding = gridPadding(state.offline),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text("Места для работы", style = MaterialTheme.typography.headlineMedium, color = colors.text)
        }
        itemsIndexed(state.myBookings) { index, booking ->
            PlaceCard(
                hint = "К " + runCatching { LocalDate.parse(booking.date).format(cardDate) }.getOrDefault(booking.date),
                title = "Место ${booking.place}",
                modifier = Modifier.testTag(TestIds.Main.getIdItemByPosition(index)),
                action = {
                    HoldButton(
                        text = "Освободить",
                        onConfirm = { viewModel.onIntent(HomeIntent.AskFree) },
                        enabled = !state.busy && !state.offline,
                        modifier = Modifier.testTag("free_button_$index")
                    )
                }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row {
                ActionCard(
                    text = "Забронировать",
                    onClick = { viewModel.onIntent(HomeIntent.SelectTab(HomeTab.Places)) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(TestIds.Main.ADD_BUTTON)
                )
                Spacer(Modifier.weight(1f))
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "Комната для переговоров",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.text,
                modifier = Modifier.padding(top = 30.dp)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row {
                ActionCard(
                    text = "Забронировать",
                    onClick = { viewModel.onIntent(HomeIntent.SelectTab(HomeTab.Rooms)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.weight(1f))
            }
        }
        state.error?.let { error ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    error,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.error,
                    modifier = Modifier.testTag(TestIds.Main.ERROR)
                )
            }
        }
    }
}

@Composable
private fun BookTab(state: HomeState, viewModel: HomeViewModel, rooms: Boolean) {
    val colors = WorkTheme.colors
    val dayIndex = if (rooms) 0 else state.selectedDay
    val day = state.days.getOrNull(dayIndex)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        contentPadding = gridPadding(state.offline),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            DateChips(
                dates = state.days.map { it.date },
                selected = dayIndex,
                onSelect = { viewModel.onIntent(HomeIntent.SelectDay(it)) },
                enabledIndices = if (rooms) setOf(0) else null,
                modifier = Modifier.padding(bottom = 26.dp)
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                if (rooms) "Комната для переговоров" else "Места для работы",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.text
            )
        }
        if (day == null || day.places.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Всё забронировано",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted,
                    modifier = Modifier.testTag(TestIds.Book.EMPTY)
                )
            }
        } else {
            itemsIndexed(day.places) { index, place ->
                PlaceCard(
                    hint = "К " + runCatching { LocalDate.parse(day.date).format(cardDate) }.getOrDefault(day.date),
                    title = "Место ${place.name}",
                    modifier = Modifier.testTag(TestIds.Book.getIdPlaceItemByPosition(index)),
                    action = {
                        HoldButton(
                            text = "Забронировать",
                            onConfirm = {
                                viewModel.onIntent(
                                    if (rooms) HomeIntent.BookRoom(place.id, place.name)
                                    else HomeIntent.BookPlace(place.id, place.name)
                                )
                            },
                            enabled = !state.busy && !state.offline,
                            modifier = Modifier.testTag(TestIds.Book.BOOK_BUTTON)
                        )
                    }
                )
            }
        }
    }
}
