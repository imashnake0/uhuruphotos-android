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
package com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.logging

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

internal class TimberLogger @Inject constructor() : HttpLoggingInterceptor.Logger {

    override fun log(message: String) = log("OkHttp") { message }
}
