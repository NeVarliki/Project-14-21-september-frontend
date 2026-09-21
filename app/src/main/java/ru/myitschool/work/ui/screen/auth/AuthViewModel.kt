package ru.myitschool.work.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.source.ApiException
import ru.myitschool.work.domain.auth.LoginUseCase
import ru.myitschool.work.domain.auth.ValidateCredentialsUseCase
import ru.myitschool.work.ui.nav.HomeScreenDestination

class AuthViewModel : ViewModel() {
    private val validate = ValidateCredentialsUseCase()
    private val loginUseCase = LoginUseCase(AuthRepository)

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<AuthAction>()
    val actionFlow: SharedFlow<AuthAction> = _actionFlow

    private var lockJob: Job? = null

    init {
        viewModelScope.launch {
            startLock(AuthRepository.lockLeftMillis())
        }
    }

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.LoginInput -> _uiState.update {
                it.copy(
                    login = intent.text,
                    loginError = if (intent.text.isBlank()) null else validate.login(intent.text),
                    passwordError = if (it.password.isBlank()) null else validate.password(intent.text, it.password),
                    error = null
                )
            }
            is AuthIntent.PasswordInput -> _uiState.update {
                it.copy(
                    password = intent.text,
                    passwordError = if (intent.text.isBlank()) null else validate.password(it.login, intent.text),
                    error = null
                )
            }
            AuthIntent.Send -> send()
            AuthIntent.DismissAlert -> _uiState.update { it.copy(noInternet = false, showLockSheet = false) }
        }
    }

    private fun send() {
        val state = _uiState.value
        val loginError = validate.login(state.login)
        val passwordError = validate.password(state.login, state.password)
        if (loginError != null || passwordError != null) {
            _uiState.update { it.copy(loginError = loginError, passwordError = passwordError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            loginUseCase(state.login, state.password).fold(
                onSuccess = {
                    _uiState.update { it.copy(loading = false) }
                    _actionFlow.emit(AuthAction.Open(HomeScreenDestination))
                },
                onFailure = { error ->
                    when (error) {
                        is AuthRepository.LockedException -> {
                            _uiState.update { it.copy(loading = false, showLockSheet = true) }
                            startLock(error.leftMillis)
                        }
                        is ApiException.NoConnection -> _uiState.update {
                            it.copy(loading = false, noInternet = true, error = error.message)
                        }
                        else -> _uiState.update {
                            it.copy(loading = false, error = error.message ?: "Ошибка")
                        }
                    }
                }
            )
        }
    }

    private fun startLock(millis: Long) {
        if (millis <= 0) return
        lockJob?.cancel()
        lockJob = viewModelScope.launch {
            var left = ((millis + 999) / 1000).toInt()
            while (left > 0) {
                _uiState.update { it.copy(lockSecondsLeft = left, error = null) }
                delay(1000)
                left--
            }
            _uiState.update { it.copy(lockSecondsLeft = 0) }
        }
    }
}
