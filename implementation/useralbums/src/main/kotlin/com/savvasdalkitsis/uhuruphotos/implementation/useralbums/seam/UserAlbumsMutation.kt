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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam

import com.savvasdalkitsis.uhuruphotos.api.useralbums.view.state.UserAlbum
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class UserAlbumsMutation(
    mutation: Mutation<UserAlbumsState>
) : Mutation<UserAlbumsState> by mutation {

    data class DisplayAlbums(val albums: List<UserAlbum>) : UserAlbumsMutation({
        it.copy(albums = albums)
    })

    data class Loading(val loading: Boolean) : UserAlbumsMutation({
        it.copy(isLoading = loading)
    })
}
