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
package com.savvasdalkitsis.uhuruphotos.foundation.seam.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Seam<S : Any, E : Any, A : Any, M : Mutation<S>> {
    val state: StateFlow<S>
    val effects: Flow<E>
    suspend fun action(action: A)
}

fun <S : Any, E : Any, A : Any, M : Mutation<S>> handler(
    handler: ActionHandler<S, E, A, M>,
    initialState: S,
): Seam<S, E, A, M> = SeamViaHandler(handler, initialState)