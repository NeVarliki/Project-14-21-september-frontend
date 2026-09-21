package ru.myitschool.work.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.components.AlertKind
import ru.myitschool.work.ui.components.AlertSheet
import ru.myitschool.work.ui.components.PillButton
import ru.myitschool.work.ui.components.PillTextField
import ru.myitschool.work.ui.theme.WorkTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val colors = WorkTheme.colors

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is AuthAction.Open -> navController.navigate(action.destination) { popUpTo(0) }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Добро пожаловать!",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.text,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Введи логин и пароль",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(120.dp))
            Column(
                modifier = Modifier.widthIn(max = 420.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PillTextField(
                    value = state.login,
                    onValueChange = { viewModel.onIntent(AuthIntent.LoginInput(it)) },
                    placeholder = "Логин",
                    enabled = !state.loading && !state.isLocked,
                    isError = state.loginError != null,
                    modifier = Modifier.testTag(TestIds.Auth.CODE_INPUT)
                )
                Spacer(Modifier.height(10.dp))
                PillTextField(
                    value = state.password,
                    onValueChange = { viewModel.onIntent(AuthIntent.PasswordInput(it)) },
                    placeholder = "Пароль",
                    password = true,
                    enabled = !state.loading && !state.isLocked,
                    isError = state.passwordError != null,
                    imeAction = ImeAction.Done,
                    onDone = { if (state.canSend) viewModel.onIntent(AuthIntent.Send) },
                    modifier = Modifier.testTag("auth_password_input")
                )
                Spacer(Modifier.height(14.dp))
                val message = when {
                    state.isLocked -> "Много запросов. Повторите через ${state.lockSecondsLeft} с"
                    state.loginError != null -> state.loginError
                    state.passwordError != null -> state.passwordError
                    state.error != null -> state.error
                    else -> null
                }
                Text(
                    text = message ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (message == null) colors.textMuted else colors.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(32.dp)
                        .testTag(TestIds.Auth.ERROR)
                )
                Spacer(Modifier.height(46.dp))
                PillButton(
                    text = "Войти",
                    onClick = { viewModel.onIntent(AuthIntent.Send) },
                    enabled = state.canSend,
                    loading = state.loading,
                    modifier = Modifier
                        .width(153.dp)
                        .testTag(TestIds.Auth.SIGN_BUTTON)
                )
            }
        }
    }

    if (state.noInternet) {
        AlertSheet(
            kind = AlertKind.NoInternet,
            onDismiss = { viewModel.onIntent(AuthIntent.DismissAlert) },
            onAction = {
                viewModel.onIntent(AuthIntent.DismissAlert)
                viewModel.onIntent(AuthIntent.Send)
            }
        )
    }
    if (state.showLockSheet && state.isLocked) {
        AlertSheet(
            kind = AlertKind.TooMany(state.lockSecondsLeft),
            onDismiss = { viewModel.onIntent(AuthIntent.DismissAlert) },
            onAction = {}
        )
    }
}
