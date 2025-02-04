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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.MediaRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.MediaRowSlot.MediaSlot
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaItemSelected
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode

@Composable
internal fun StaggeredCollage(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    showSelectionHeader: Boolean = false,
    showAlbumRefreshButton: Boolean = false,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    columnCount: Int,
    shouldAddEmptyPhotosInRows: Boolean,
    listState: LazyListState = rememberLazyListState(),
    collageHeader: @Composable (LazyItemScope.() -> Unit)? = null,
    onMediaItemSelected: MediaItemSelected,
    onMediaItemLongPressed: (MediaItem) -> Unit,
    onAlbumRefreshClicked: (Album) -> Unit,
    onAlbumSelectionClicked: (Album) -> Unit,
) {
    Box {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
        ) {
            collageHeader?.let { header ->
                item("collageHeader", "collageHeader") {
                    header(this)
                }
            }
            albums.forEach { album ->
                if ((album.displayTitle + album.location.orEmpty()).isNotEmpty()) {
                    item(album.id, "header") {
                        CollageGroupHeader(
                            modifier = Modifier.animateItemPlacement(),
                            album = album,
                            showSelectionHeader = showSelectionHeader,
                            showRefreshButton = showAlbumRefreshButton,
                            onSelectionHeaderClicked = {
                                onAlbumSelectionClicked(album)
                            },
                            onRefreshClicked = {
                                onAlbumRefreshClicked(album)
                            }
                        )
                    }
                }
                val (slots, rows) = if (shouldAddEmptyPhotosInRows) {
                    val emptyPhotos = (columnCount - album.photos.size % columnCount) % columnCount
                    val paddedSlots = album.photos.map(::MediaSlot) + List(emptyPhotos) { EmptySlot }
                    paddedSlots to paddedSlots.size / columnCount
                } else {
                    val evenRows = album.photos.size / columnCount
                    album.photos.map(::MediaSlot) to evenRows + if (album.photos.size % columnCount == 0) 0 else 1
                }
                for (row in 0 until rows) {
                    val slotsInRow = (0 until columnCount).mapNotNull { column ->
                        slots.getOrNull(row * columnCount + column)
                    }.toTypedArray()
                    item(
                        slotsInRow.firstNotNullOf { it as? MediaSlot }.mediaItem.id.value,
                        "photoRow"
                    ) {
                        MediaRow(
                            modifier = Modifier
                                .animateContentSize()
                                .animateItemPlacement(),
                            miniIcons = miniIcons,
                            maintainAspectRatio = maintainAspectRatio,
                            onMediaItemSelected = onMediaItemSelected,
                            onMediaItemLongPressed = onMediaItemLongPressed,
                            slots = slotsInRow
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier
            .padding(contentPadding)
        ) {
            LazyColumnScrollbar(
                listState = listState,
                thickness = 8.dp,
                selectionMode = ScrollbarSelectionMode.Thumb,
                thumbColor = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                thumbSelectedColor = MaterialTheme.colors.primary,
            )
        }
    }
}