/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.albums.usecase

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetTrash
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapValues
import com.savvasdalkitsis.uhuruphotos.implementation.albums.worker.AlbumWorkScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val dateDisplayer: DateDisplayer,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val albumWorkScheduler: AlbumWorkScheduler,
    private val userUseCase: UserUseCase,
) : AlbumsUseCase {

    override fun observePersonAlbums(personId: Int): Flow<List<Album>> = albumsRepository.observePersonAlbums(personId)
        .map {
            it.mapValues {
                getPersonAlbums -> getPersonAlbums.toDbAlbums()
            }
        }
        .map { it.mapToAlbums() }
        .initialize()

    override fun observeAlbums(): Flow<List<Album>> = albumsRepository.observeAlbumsByDate()
        .map {
            it.mapValues {
                    getAlbums -> getAlbums.toDbAlbums()
            }
        }
        .map { it.mapToAlbums() }
        .initialize()

    override fun observeTrash(): Flow<List<Album>> = albumsRepository.observeTrash()
        .map {
            it.mapValues {
                    getTrash -> getTrash.toDbAlbums()
            }
        }
        .map { it.mapToAlbums() }

    override suspend fun refreshTrash() = albumsRepository.refreshTrash()

    override suspend fun getPersonAlbums(personId: Int): List<Album> = albumsRepository.getPersonAlbums(personId)
        .mapValues { it.toDbAlbums() }
        .mapToAlbums()

    override suspend fun getAlbums(): List<Album> = albumsRepository.getAlbumsByDate()
        .mapValues { it.toDbAlbums() }
        .mapToAlbums()

    override suspend fun getTrash(): List<Album> = albumsRepository.getTrash()
        .mapValues { it.toDbAlbums() }
        .mapToAlbums()

    override suspend fun hasTrash(): Boolean = albumsRepository.hasTrash()

    override suspend fun getAutoAlbum(albumId: Int): List<Album> = albumsRepository.getAutoAlbum(albumId)
        .mapValues { it.toDbAlbums() }
        .mapToAlbums()

    override suspend fun getUserAlbum(albumId: Int): List<Album> = albumsRepository.getUserAlbum(albumId)
        .mapValues { it.toDbAlbums() }
        .mapToAlbums()

    private fun Flow<List<Album>>.initialize(): Flow<List<Album>> = distinctUntilChanged()
        .safelyOnStartIgnoring {
            if (!albumsRepository.hasAlbums()) {
                startRefreshAlbumsWork(shallow = false)
            }
        }

    private suspend fun Group<String, DbAlbums>.mapToAlbums(): List<Album> {
        val favouriteThreshold = userUseCase.getUserOrRefresh()
            .mapCatching { it.favoriteMinRating!! }
        return items.map { (id, photos) ->
            val albumDate = photos.firstOrNull()?.albumDate
            val albumLocation = photos.firstOrNull()?.albumLocation

            val date = dateDisplayer.dateString(albumDate)
            Album(
                id = id,
                displayTitle = date,
                unformattedDate = albumDate,
                location = albumLocation ?: "",
                photos = photos.mapNotNull { item ->
                    when {
                        item.photoId.isNullOrBlank() -> null
                        else -> {
                            val photoId = item.photoId
                            MediaItem(
                                id = MediaId.Remote(photoId),
                                mediaHash = photoId,
                                thumbnailUri = with(remoteMediaUseCase) {
                                    photoId.toThumbnailUrlFromId()
                                },
                                fullResUri = with(remoteMediaUseCase) {
                                    photoId.toFullSizeUrlFromId(item.isVideo)
                                },
                                fallbackColor = item.dominantColor,
                                displayDayDate = date,
                                isFavourite = favouriteThreshold
                                    .map {
                                        (item.rating ?: 0) >= it
                                    }
                                    .getOrElse { false },
                                ratio = item.aspectRatio ?: 1.0f,
                                isVideo = item.isVideo,
                            )
                        }
                    }
                }
            )
        }.filter { it.photos.isNotEmpty() }
    }

    override fun startRefreshAlbumsWork(shallow: Boolean) {
        albumWorkScheduler.scheduleAlbumsRefreshNow(shallow)
    }

    override suspend fun refreshAlbum(albumId: String) {
        albumsRepository.refreshAlbum(albumId)
    }
}

private data class DbAlbums(
    val id: String,
    val albumDate: String?,
    val albumLocation: String?,
    val photoId: String?,
    val dominantColor: String?,
    val rating: Int?,
    val aspectRatio: Float?,
    val isVideo: Boolean,
)

private fun GetPersonAlbums.toDbAlbums() = DbAlbums(
    id = id,
    albumDate = albumDate,
    albumLocation = albumLocation,
    photoId = photoId,
    dominantColor = dominantColor,
    rating = rating,
    aspectRatio = aspectRatio,
    isVideo = isVideo,
)
private fun GetAlbums.toDbAlbums() = DbAlbums(
    id = id,
    albumDate = albumDate,
    albumLocation = albumLocation,
    photoId = photoId,
    dominantColor = dominantColor,
    rating = rating,
    aspectRatio = aspectRatio,
    isVideo = isVideo,
)
private fun GetTrash.toDbAlbums() = DbAlbums(
    id = id,
    albumDate = albumDate,
    albumLocation = albumLocation,
    photoId = photoId,
    dominantColor = dominantColor,
    rating = rating,
    aspectRatio = aspectRatio,
    isVideo = isVideo,
)
private fun GetAutoAlbum.toDbAlbums() = DbAlbums(
    id = id,
    albumDate = albumTimestamp,
    albumLocation = null,
    photoId = photoId,
    dominantColor = null,
    rating = rating,
    aspectRatio = 1f,
    isVideo = video == true,
)
private fun GetUserAlbum.toDbAlbums() = DbAlbums(
    id = id,
    albumDate = date,
    albumLocation = location,
    photoId = photoId,
    dominantColor = dominantColor,
    rating = rating,
    aspectRatio = aspectRatio,
    isVideo = isVideo,
)