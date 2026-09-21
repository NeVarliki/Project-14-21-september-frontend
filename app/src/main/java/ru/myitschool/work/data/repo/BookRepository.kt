package ru.myitschool.work.data.repo

import ru.myitschool.work.data.source.ApiException
import ru.myitschool.work.data.source.CacheDataSource
import ru.myitschool.work.data.source.NetworkDataSource
import ru.myitschool.work.domain.book.entities.BookingData
import ru.myitschool.work.domain.book.entities.Cached
import ru.myitschool.work.domain.main.entities.MainInfoEntity

object BookRepository {

    suspend fun getInfo(): Result<Cached<MainInfoEntity>> = runCatching {
        val dto = authorized { NetworkDataSource.getInfo(it) }
        CacheDataSource.saveInfo(dto)
        Cached(dto.toEntity(), fromCache = false)
    }.recoverCatching { error ->
        if (error is ApiException.NoConnection) {
            val cached = CacheDataSource.readInfo() ?: throw error
            Cached(cached.toEntity(), fromCache = true)
        } else throw error
    }

    suspend fun getFreePlaces(): Result<Cached<List<BookingData>>> = runCatching {
        val dto = authorized { NetworkDataSource.getBooking(it) }
        CacheDataSource.saveBooking(dto)
        Cached(dto.toEntity(), fromCache = false)
    }.recoverCatching { error ->
        if (error is ApiException.NoConnection) {
            val cached = CacheDataSource.readBooking() ?: throw error
            Cached(cached.toEntity(), fromCache = true)
        } else throw error
    }

    suspend fun book(date: String, placeId: Long): Result<Unit> = runCatching {
        authorized { NetworkDataSource.book(it, date, placeId) }
    }

    suspend fun bookRoomToday(placeId: Long): Result<Unit> = runCatching {
        authorized { NetworkDataSource.bookRoomToday(it, placeId) }
    }

    suspend fun free(): Result<Unit> = runCatching {
        authorized { NetworkDataSource.free(it) }
    }

    private suspend fun <T> authorized(block: suspend (token: String) -> T): T {
        val token = AuthRepository.accessToken() ?: throw ApiException.Unauthorized()
        return try {
            block(token)
        } catch (e: ApiException.Unauthorized) {
            if (AuthRepository.refreshTokens()) {
                block(AuthRepository.accessToken() ?: throw e)
            } else throw e
        }
    }

    private fun ru.myitschool.work.data.dto.UserDto.toEntity() = MainInfoEntity(
        name = name.orEmpty(),
        photoUrl = photoUrl.orEmpty(),
        book = booking?.mapNotNull { (date, place) ->
            MainInfoEntity.Book(
                date = date,
                place = place.place ?: return@mapNotNull null
            )
        } ?: emptyList()
    )

    private fun Map<String, List<ru.myitschool.work.data.dto.PlaceDto>>.toEntity() = map { (date, places) ->
        BookingData(
            date = date,
            places = places.mapNotNull { place ->
                BookingData.Place(
                    id = place.id ?: return@mapNotNull null,
                    name = place.place ?: return@mapNotNull null
                )
            }
        )
    }
}
