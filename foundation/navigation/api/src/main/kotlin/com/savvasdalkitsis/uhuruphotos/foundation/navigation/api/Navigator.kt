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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api

import android.content.Intent
import androidx.navigation.NavHostController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject internal constructor(
    private val intentLauncher: IntentLauncher,
) {
    lateinit var navController: NavHostController

    fun navigateTo(intent: Intent) {
        intentLauncher.launch(intent)
    }

    fun navigateTo(route: String) {
        navController.navigate(route)
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun clearBackStack() {
        navController.backQueue.clear()
    }
}