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
package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.api.map.Locations
import com.savvasdalkitsis.uhuruphotos.api.map.view.GoogleMapView
import com.savvasdalkitsis.uhuruphotos.api.map.view.rememberGoogleMapViewState
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsTop
import kotlinx.coroutines.withContext

@Composable
fun HeatMapContent(
    modifier: Modifier = Modifier,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState,
    state: HeatMapState
) {
    val mapViewState = rememberGoogleMapViewState(
        initialPosition = Locations.TRAFALGAR_SQUARE,
        zoom = 2f,
    )
    var startedMoving by remember { mutableStateOf(false) }
    if (mapViewState.isMoving) {
        startedMoving = true
    }
    val scope = rememberCoroutineScope()
    if (startedMoving && !mapViewState.isMoving) {
        @Suppress("UNUSED_VALUE")
        startedMoving = false
        action(HeatMapAction.CameraViewPortChanged {  latLng ->
            withContext(scope.coroutineContext) {
                mapViewState.contains(latLng)
            }
        })
    }

    val showLocationButton = locationPermissionState.status.isGranted

    GoogleMapView(
        modifier = modifier,
        mapViewState = mapViewState,
        mapOptions = {
            copy(
                scrollGesturesEnabled = true,
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                myLocationButtonEnabled = showLocationButton,
            )
        },
        contentPadding = PaddingValues(top = insetsTop() + 56.dp)
    ) {
        HeatMap(state.pointsToDisplay)
    }
}