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
package com.savvasdalkitsis.uhuruphotos.implementation.library.seam

import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryAutoAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryUserAlbum

sealed class LibraryAction {
    data class ChangeAutoAlbumsSorting(val sorting: AlbumSorting) : LibraryAction()
    data class ChangeUserAlbumsSorting(val sorting: AlbumSorting) : LibraryAction()
    data class AutoAlbumSelected(val album: LibraryAutoAlbum) : LibraryAction()
    data class UserAlbumSelected(val album: LibraryUserAlbum) : LibraryAction()
    object Load : LibraryAction()
    object RefreshAutoAlbums : LibraryAction()
    object RefreshUserAlbums : LibraryAction()
}
