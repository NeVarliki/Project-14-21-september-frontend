package ru.myitschool.work.domain.main

import ru.myitschool.work.data.repo.BookRepository
import ru.myitschool.work.domain.book.entities.Cached
import ru.myitschool.work.domain.main.entities.MainInfoEntity

class GetMainDataUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<Cached<MainInfoEntity>> = repository.getInfo()
}
