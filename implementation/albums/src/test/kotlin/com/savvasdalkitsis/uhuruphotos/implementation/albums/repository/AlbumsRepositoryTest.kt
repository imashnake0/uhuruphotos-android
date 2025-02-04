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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.repository

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.api.albums.service.AlbumsService
import com.savvasdalkitsis.uhuruphotos.api.db.TestDatabase
import com.savvasdalkitsis.uhuruphotos.api.db.albums.Albums
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.implementation.albums.ProgressUpdate
import com.savvasdalkitsis.uhuruphotos.implementation.albums.SERVER_ALBUM_LOCATION
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestAlbums.albums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestGetPhotoSummaries.photoSummariesForAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.album
import com.savvasdalkitsis.uhuruphotos.implementation.albums.albumId
import com.savvasdalkitsis.uhuruphotos.implementation.albums.completeAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.completes
import com.savvasdalkitsis.uhuruphotos.implementation.albums.entry
import com.savvasdalkitsis.uhuruphotos.implementation.albums.incompleteAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.photo
import com.savvasdalkitsis.uhuruphotos.implementation.albums.photoId
import com.savvasdalkitsis.uhuruphotos.implementation.albums.photoSummaryItem
import com.savvasdalkitsis.uhuruphotos.implementation.albums.respondsForAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.respondsWith
import com.savvasdalkitsis.uhuruphotos.implementation.albums.willRespondForAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.willRespondForPersonAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.willRespondForPersonWith
import com.savvasdalkitsis.uhuruphotos.implementation.albums.willRespondWith
import com.savvasdalkitsis.uhuruphotos.implementation.albums.withServerResponseData
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AlbumsRepositoryTest {

    private val db = TestDatabase.getDb()
    private val albumsService = mockk<AlbumsService>(relaxed = true)
    private val settingsUseCase = mockk<com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase>(relaxed = true)
    private val underTest = AlbumsRepository(
        db,
        albumsService,
        db.albumsQueries,
        db.autoAlbumsQueries,
        db.autoAlbumQueries,
        db.autoAlbumPhotosQueries,
        db.autoAlbumPeopleQueries,
        db.personQueries,
        db.peopleQueries,
        db.remoteMediaItemSummaryQueries,
        db.remoteMediaItemDetailsQueries,
        db.userAlbumsQueries,
        db.userAlbumQueries,
        db.userAlbumPhotosQueries,
        db.remoteMediaTrashQueries,
        settingsUseCase,
    )

    @Test
    fun `reports having albums if 1 or more exists`() = runBlocking {
        given(albums)

        assert(underTest.hasAlbums())
    }

    @Test
    fun `reports not having albums if none exists`() = runBlocking {
        db.albumsQueries.clearAll()

        assert(!underTest.hasAlbums())
    }

    @Test
    fun `gets albums by date`() = runBlocking {
        given(album(1), album(2))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 2),
        )

        underTest.getAlbumsByDate().assertSameAs(
            album(1,
                entry(photo(2)),
                entry(photo(1)),
            ),
            album(2,
                entry(photo(3)),
            ),
        )
    }

    @Test
    fun `observes albums by date and updates`() = runBlocking {
        given(album(1), album(2))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 2),
        )

        underTest.observeAlbumsByDate().test {
            awaitItem().assertSameAs(
                album(1,
                    entry(photo(2)),
                    entry(photo(1)),
                ),
                album(2,
                    entry(photo(3)),
                ),
            )

            insert(photo(4, inAlbum = 2))

            awaitItem().assertSameAs(
                album(1,
                    entry(photo(2)),
                    entry(photo(1)),
                ),
                album(2,
                    entry(photo(4)),
                    entry(photo(3)),
                ),
            )
        }
    }

    @Test
    fun `gets people albums`() = runBlocking {
        given(album(1))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 1),
        )
        given(person = 1).hasPhotos(1, 3)

        underTest.getPersonAlbums(1).assertSameAs(
            album(1,
                entry(personId = 1, photo(3)),
                entry(personId = 1, photo(1)),
            ),
        )
    }

    @Test
    fun `observes people albums and updates`() = runBlocking {
        given(album(1))
        given(
            photo(1, inAlbum = 1),
            photo(2, inAlbum = 1),
            photo(3, inAlbum = 1),
        )
        given(person = 1).hasPhotos(1, 3)

        underTest.observePersonAlbums(1).test {
            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(3)),
                    entry(personId = 1, photo(1)),
                )
            )

            insert(personId = 1, photoId(2))

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(3)),
                    entry(personId = 1, photo(2)),
                    entry(personId = 1, photo(1)),
                )
            )
        }
    }

    @Test
    fun `refreshes people albums when starting to observe`() = runBlocking {
        given(album(1))
        given(photo(1, inAlbum = 1))
        given(person = 1).hasPhotos(1)

        val albumsResponse = albumsService.willRespondForPersonWith(personId = 1,
            incompleteAlbum(1).copy(location = SERVER_ALBUM_LOCATION),
            incompleteAlbum(2).copy(location = SERVER_ALBUM_LOCATION),
        )
        val album1Response = albumsService.willRespondForPersonAlbum(personId = 1, albumId = 1,
            completeAlbum(1).copy(
                items = listOf(
                    photoSummaryItem(1),
                    photoSummaryItem(2),
                )
            )
        )
        val album2Response = albumsService.willRespondForPersonAlbum(personId = 1, albumId = 2,
            completeAlbum(2).copy(
                items = listOf(
                    photoSummaryItem(3),
                    photoSummaryItem(4),
                )
            )
        )

        underTest.observePersonAlbums(1).test {
            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(1)),
                )
            )

            albumsResponse.completes()

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(1)).copy(albumLocation = SERVER_ALBUM_LOCATION),
                )
            )

            album1Response.completes()

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(1)).withServerResponseData(),
                )
            )

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(2)).withServerResponseData(),
                    entry(personId = 1, photo(1)).withServerResponseData(),
                )
            )

            album2Response.completes()

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(2)).withServerResponseData(),
                    entry(personId = 1, photo(1)).withServerResponseData(),
                ),
                album(2,
                    entry(personId = 1, photo(3)).withServerResponseData(),
                ),
            )

            awaitItem().assertSameAs(
                album(1,
                    entry(personId = 1, photo(2)).withServerResponseData(),
                    entry(personId = 1, photo(1)).withServerResponseData(),
                ),
                album(2,
                    entry(personId = 1, photo(4)).withServerResponseData(),
                    entry(personId = 1, photo(3)).withServerResponseData(),
                ),
            )
        }
    }

    @Test
    fun `refreshes albums on demand`() = runBlocking {
        db.albumsQueries.clearAll()
        db.remoteMediaItemSummaryQueries.clearAll()
        albumsService.respondsWith(
            incompleteAlbum(1).copy(location = SERVER_ALBUM_LOCATION),
        )
        albumsService.respondsForAlbum(1,
            completeAlbum(1).copy(
                items = listOf(
                    photoSummaryItem(1),
                    photoSummaryItem(2),
                )
            )
        )

        underTest.refreshAlbums(shallow = false) {}

        val album1 = album(1)

        assertThat(db.albumsQueries.getAlbums(limit = -1).await(), sameBeanAs(listOf(
            entry(photo(2, inAlbum = 1))
                .copy(id = album1.id, albumDate = album1.date)
                .withServerResponseData(),
            entry(photo(1, inAlbum = 1))
                .copy(id = album1.id, albumDate = album1.date)
                .withServerResponseData(),
        )))

        assertThat(db.remoteMediaItemSummaryQueries.getPhotoSummariesForAlbum(album1.id).await(), sameBeanAs(listOf(
            photoSummariesForAlbum.copy(id = photoId(1)),
            photoSummariesForAlbum.copy(id = photoId(2)),
        )))
    }

    @Test
    fun `reports refresh progress`() = runBlocking {
        val progress = ProgressUpdate()

        val albumsResponse = albumsService.willRespondWith(
            incompleteAlbum(1),
            incompleteAlbum(2),
        )
        val album1Response = albumsService.willRespondForAlbum(1, completeAlbum(1))
        val album2Response = albumsService.willRespondForAlbum(2, completeAlbum(2))

        CoroutineScope(Dispatchers.Default).launch {
            underTest.refreshAlbums(shallow = false, progress)
        }

        progress.assertReceived(0)
        albumsResponse.completes()

        album1Response.completes()
        progress.assertReceived(50)

        album2Response.completes()
        progress.assertReceived(100)
    }

    @Test(timeout = 4000)
    fun `can perform shallow refresh when asked`() = runBlocking {
        val progress = ProgressUpdate()

        val albumsResponse = albumsService.willRespondWith(
            incompleteAlbum(1),
            incompleteAlbum(2),
            incompleteAlbum(3),
            incompleteAlbum(4),
        )
        val album1Response = albumsService.willRespondForAlbum(1, completeAlbum(1))
        val album2Response = albumsService.willRespondForAlbum(2, completeAlbum(2))
        val album3Response = albumsService.willRespondForAlbum(3, completeAlbum(3))
        albumsService.respondsForAlbum(4, completeAlbum(4))
        coEvery {  settingsUseCase.getFeedDaysToRefresh() } returns 3


        CoroutineScope(Dispatchers.Default).launch {
            underTest.refreshAlbums(shallow = true, progress)
        }

        progress.assertReceived(0)
        albumsResponse.completes()

        album1Response.completes()
        progress.assertReceived(33)

        album2Response.completes()
        progress.assertReceived(66)

        album3Response.completes()
        progress.assertReceived(100)

        coVerify(exactly = 0) { albumsService.getAlbum(albumId(4), 1) }
    }

    private fun given(vararg albums: Albums) = insert(*albums)

    private fun insert(vararg albums: Albums) = albums.forEach { db.albumsQueries.insert(it) }

    private fun given(vararg photoSummaries: DbRemoteMediaItemSummary) = insert(*photoSummaries)

    private fun insert(vararg photoSummaries: DbRemoteMediaItemSummary) =
        photoSummaries.forEach { db.remoteMediaItemSummaryQueries.insert(it) }

    @Suppress("SameParameterValue")
    private fun given(person: Int) = PersonContinuation(person)

    private inner class PersonContinuation(val personId: Int) {
        fun hasPhotos(vararg ids: Int) {
            ids.forEach {
                insert(personId, photoId(it))
            }
        }
    }

    private fun insert(personId: Int, photoId: String) {
        db.personQueries.insert(id = null, personId = personId, photoId = photoId )
    }

    private fun <T> Group<String, T>.assertSameAs(vararg pairs: Pair<String, List<T>>) =
        assertThat(this, sameBeanAs(Group(mapOf(*pairs))))
}
