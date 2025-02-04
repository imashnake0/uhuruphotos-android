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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class AutoAlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
) : AutoAlbumsUseCase {

    override fun observeAutoAlbum(albumId: Int): Flow<Pair<List<GetAutoAlbum>, List<GetPeopleForAutoAlbum>>> =
        combine(
            albumsRepository.observeAutoAlbum(albumId),
            albumsRepository.observeAutoAlbumPeople(albumId),
        ) { album, people ->
            album to people
        }

    override suspend fun getAutoAlbum(albumId: Int): Group<String, GetAutoAlbum> =
        albumsRepository.getAutoAlbum(albumId)

    override suspend fun refreshAutoAlbum(albumId: Int) =
        albumsRepository.refreshAutoAlbum(albumId)

}

