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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class AlbumDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val albumsRepository: AlbumsRepository,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val shallow = params.inputData.getBoolean(KEY_SHALLOW, false)
        val result = albumsRepository.refreshAlbums(shallow) { progress ->
            setProgress(workDataOf(Progress to progress))
            createForegroundInfo(progress)
        }
        when {
            result.isSuccess -> Result.success()
            else -> Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = createForegroundInfo(null)

    private fun createForegroundInfo(progress: Int?) = foregroundInfoBuilder.build(
        applicationContext,
        string.refreshing_albums,
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID,
        progress,
    )
    companion object {
        const val Progress = "Progress"
        const val WORK_NAME = "refreshAlbums"
        const val KEY_SHALLOW = "shallow"
        private const val NOTIFICATION_ID = 1273
    }
}