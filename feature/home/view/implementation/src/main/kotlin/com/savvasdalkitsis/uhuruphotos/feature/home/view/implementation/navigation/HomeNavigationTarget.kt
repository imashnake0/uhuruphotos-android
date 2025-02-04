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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeAction
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffect
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.Home
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.state.HomeState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.viewmodel.HomeViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import javax.inject.Inject

internal class HomeNavigationTarget @Inject constructor(
    private val effectsHandler: HomeEffectsHandler,
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) =
        navigationTarget<HomeState, HomeEffect, HomeAction, HomeViewModel>(
            name = HomeNavigationTarget.registrationName,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { _, actions -> actions(HomeAction.Load) },
            createModel = { hiltViewModel() }
        ) { state, action ->
            Home(state, action)
        }
}
