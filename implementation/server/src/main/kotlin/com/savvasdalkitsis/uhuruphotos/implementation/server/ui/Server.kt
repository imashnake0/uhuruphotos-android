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
package com.savvasdalkitsis.uhuruphotos.implementation.server.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction
import com.savvasdalkitsis.uhuruphotos.implementation.server.ui.ServerState.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.server.ui.ServerState.ServerUrl
import com.savvasdalkitsis.uhuruphotos.implementation.server.ui.ServerState.UserCredentials

@Composable
internal fun Server(
    state: ServerState,
    action: (ServerAction) -> Unit = {},
) {
    CommonScaffold(
        modifier = Modifier
            .imeNestedScroll()
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            when (state) {
                is Loading ->
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                is ServerUrl -> ServerUrlPage(state, action)
                is UserCredentials -> UserCredentialsPage(action, state)
            }
        }
    }
}