package ru.myitschool.work.ui.screen.auth

data class AuthState(
    val login: String = "",
    val password: String = "",
    val loginError: String? = null,
    val passwordError: String? = null,
    val error: String? = null,
    val loading: Boolean = false,
    val lockSecondsLeft: Int = 0,
    val noInternet: Boolean = false,
    val showLockSheet: Boolean = false,
) {
    val isLocked get() = lockSecondsLeft > 0
    val canSend get() = !loading && !isLocked && login.isNotBlank() && password.isNotBlank() &&
            loginError == null && passwordError == null
}
