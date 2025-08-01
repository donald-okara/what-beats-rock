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
package ke.don.feature_onboarding.models

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

sealed class OnBoardingIntentHandler {
    data class FetchActivity(val activity: Activity) : OnBoardingIntentHandler()
    data class Start(val steps: List<OnboardingStep>) : OnBoardingIntentHandler()
    data class LaunchSignIn(val launcher: ActivityResultLauncher<IntentSenderRequest>?) : OnBoardingIntentHandler()
    data class HandleActivityResult(val intent: Intent?, val navigateToMain: () -> Unit) : OnBoardingIntentHandler()
    data object ShowNextStep : OnBoardingIntentHandler()
    data object SkipToLast : OnBoardingIntentHandler()
    data object NavigateToMain : OnBoardingIntentHandler()
}
