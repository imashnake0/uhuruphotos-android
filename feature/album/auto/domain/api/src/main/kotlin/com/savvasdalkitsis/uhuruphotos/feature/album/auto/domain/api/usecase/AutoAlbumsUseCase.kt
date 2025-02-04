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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import kotlinx.coroutines.flow.Flow

interface AutoAlbumsUseCase {

    fun observeAutoAlbum(albumId: Int): Flow<Pair<List<GetAutoAlbum>, List<GetPeopleForAutoAlbum>>>

    suspend fun getAutoAlbum(albumId: Int): Group<String, GetAutoAlbum>

    suspend fun refreshAutoAlbum(albumId: Int): Result<Unit>

}