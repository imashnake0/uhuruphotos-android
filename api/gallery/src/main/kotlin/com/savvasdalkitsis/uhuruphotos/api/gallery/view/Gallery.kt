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
package com.savvasdalkitsis.uhuruphotos.api.gallery.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.view.MediaItemSelected
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.view.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalWindowSize

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: GalleryState,
    showSelectionHeader: Boolean = false,
    showGroupRefreshButton: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    galleryHeader: @Composable (LazyItemScope.() -> Unit)? = null,
    emptyContent: @Composable () -> Unit = { NoContent(string.no_photos) },
    onMediaItemSelected: MediaItemSelected = { _, _, _ -> },
    onChangeDisplay: ((GalleryDisplay) -> Unit) = {},
    onPhotoLongPressed: (MediaItem) -> Unit = {},
    onGroupRefreshClicked: (Album) -> Unit = {},
    onGroupSelectionClicked: (Album) -> Unit = {},
) = when {
    state.isLoading || (!state.isEmpty && state.albums.isEmpty()) -> FullProgressBar()
    state.isEmpty && state.albums.isEmpty() -> emptyContent()
    else -> {
        val galleryDisplay = state.galleryDisplay
        StaggeredGallery(
            modifier = modifier
                .let {
                    when {
                        galleryDisplay.allowsPinchGestures -> it.pinchToChange(
                            galleryDisplay,
                            onChangeDisplay,
                        )
                        else -> it
                    }
                },
            contentPadding = contentPadding,
            albums = state.albums,
            showSelectionHeader = showSelectionHeader,
            showAlbumRefreshButton = showGroupRefreshButton,
            maintainAspectRatio = galleryDisplay.maintainAspectRatio,
            miniIcons = galleryDisplay.miniIcons,
            listState = listState,
            galleryHeader = galleryHeader,
            columnCount = galleryDisplay.columnCount(
                widthSizeClass = LocalWindowSize.current.widthSizeClass,
                landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            ),
            shouldAddEmptyPhotosInRows = galleryDisplay.shouldAddEmptyPhotosInRows,
            onMediaItemSelected = onMediaItemSelected,
            onPhotoLongPressed = onPhotoLongPressed,
            onAlbumSelectionClicked = onGroupSelectionClicked,
            onAlbumRefreshClicked = onGroupRefreshClicked,
        )
    }
}

private fun GalleryDisplay.columnCount(
    widthSizeClass: WindowWidthSizeClass,
    landscape: Boolean,
) = when {
    landscape -> when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumnsLandscape
        else -> wideColumnsLandscape
    }
    else -> when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumnsPortrait
        else -> wideColumnsPortrait
    }
}