package ru.myitschool.work.domain.auth

import ru.myitschool.work.data.repo.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String): Result<Unit> {
        return repository.login(login, password)
    }
}
