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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAction.FingerPrintActionPressed
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAction.Load
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosEffect.NavigateToAppSettings
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosMutation.DisplayFingerPrintAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class HiddenPhotosActionHandler @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val biometricsUseCase: BiometricsUseCase,
): ActionHandler<HiddenPhotosState, HiddenPhotosEffect, HiddenPhotosAction, HiddenPhotosMutation> {

    override fun handleAction(
        state: HiddenPhotosState,
        action: HiddenPhotosAction,
        effect: suspend (HiddenPhotosEffect) -> Unit
    ): Flow<HiddenPhotosMutation> = when (action) {
        Load -> settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess()
            .map { required ->
                DisplayFingerPrintAction(!required && biometricsUseCase.getBiometrics().isSupported)
            }
        FingerPrintActionPressed -> flow {
            effect(NavigateToAppSettings)
        }
    }
}
