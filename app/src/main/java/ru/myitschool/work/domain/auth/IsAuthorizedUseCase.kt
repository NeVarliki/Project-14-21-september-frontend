package ru.myitschool.work.domain.auth

import ru.myitschool.work.data.repo.AuthRepository

class IsAuthorizedUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean = repository.isAuthorized()
}
