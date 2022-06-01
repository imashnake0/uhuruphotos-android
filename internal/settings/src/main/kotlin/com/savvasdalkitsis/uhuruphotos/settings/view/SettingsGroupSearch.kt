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
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeSearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ClearRecentSearches
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.icons.R as Icons

@Composable
internal fun SettingsGroupSearch(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
    collapsed: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    SettingsGroup(
        title = stringResource(R.string.settings),
        collapsed = collapsed,
    ) {
        val checked = state.searchSuggestionsEnabled
        SettingsCheckBox(
            text = stringResource(R.string.enable_suggestions),
            icon = when {
                checked -> Icons.drawable.ic_lightbulb
                else -> Icons.drawable.ic_lightbulb_off
            },
            isChecked = checked,
            onCheckedChange = { action(ChangeSearchSuggestionsEnabled(it)) }
        )
        SettingsOutlineButtonRow(
            buttonText = stringResource(R.string.clear_recent_searches),
            icon = Icons.drawable.ic_clear_all,
        ) {
            action(ClearRecentSearches)
        }
    }
}