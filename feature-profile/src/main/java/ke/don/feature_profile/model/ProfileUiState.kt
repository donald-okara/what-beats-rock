/*
 * Copyright © 2025 Donald O. Isoe (isoedonald@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ke.don.feature_profile.model

import ke.don.core_datasource.domain.models.Profile

data class ProfileUiState(
    val profile: Profile = Profile(),
    val isMyProfile: Boolean = false,
    val showSheet: Boolean = false,
    val showSignOutDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isSignoutError: Boolean = false,
    val isDeleteError: Boolean = false,
    val errorMessage: String? = null,
    val signoutErrorMessage: String? = null,
    val deleteErrorMessage: String? = null,
)
