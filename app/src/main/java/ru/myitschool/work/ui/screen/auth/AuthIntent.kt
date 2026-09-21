package ru.myitschool.work.ui.screen.auth

sealed interface AuthIntent {
    data class LoginInput(val text: String) : AuthIntent
    data class PasswordInput(val text: String) : AuthIntent
    data object Send : AuthIntent
    data object DismissAlert : AuthIntent
}
