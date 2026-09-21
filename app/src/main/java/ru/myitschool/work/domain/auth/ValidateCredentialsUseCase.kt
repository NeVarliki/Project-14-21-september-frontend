package ru.myitschool.work.domain.auth

class ValidateCredentialsUseCase {
    private val loginRegex = Regex("^[.a-zA-Z0-9]+$")

    fun login(login: String): String? = when {
        login.isBlank() -> "Введите логин"
        !loginRegex.matches(login) -> "Логин: только a-z, A-Z, 0-9 и точка"
        else -> null
    }

    fun password(login: String, password: String): String? {
        if (password.isBlank()) return "Введите пароль"
        if (password.length < 8) return "Пароль не короче 8 символов"
        if (password.none { !it.isLetterOrDigit() }) return "Нужен хотя бы один спецсимвол"
        var run = 1
        for (i in 1 until password.length) {
            run = if (password[i] == password[i - 1]) run + 1 else 1
            if (run >= 3) return "Символ не может повторяться 3 раза подряд"
        }
        val lower = password.lowercase()
        val loginLower = login.lowercase()
        if (loginLower.length >= 3) {
            for (i in 0..loginLower.length - 3) {
                if (lower.contains(loginLower.substring(i, i + 3))) {
                    return "Пароль не должен содержать части логина"
                }
            }
        }
        return null
    }
}
