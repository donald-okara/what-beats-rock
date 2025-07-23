package ke.don.core_datasource.remote.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.*
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import ke.don.core_datasource.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GoogleAuthClient(
    private val context: Activity,
) {
    private val auth= FirebaseAuth.getInstance()
    private val oneTapClient = Identity.getSignInClient(context)

    suspend fun signIn(): Result<IntentSenderRequest> = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthDebug:signIn", "Building sign-in request")
            val signInRequest = builder()
                .setGoogleIdTokenRequestOptions(
                    GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(false)
                .build()

            Log.d("AuthDebug:signIn", "Starting one tap sign-in")
            val result = oneTapClient.beginSignIn(signInRequest).await()
            Log.d("AuthDebug:signIn", "IntentSender obtained: ${result.pendingIntent}")

            Result.success(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
        } catch (e: Exception) {
            Log.e("AuthDebug:signIn", "Error during sign-in", e)
            Result.failure(e)
        }
    }

    suspend fun handleSignInResult(intent: Intent?): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            Log.d("AuthDebug:handleResult", "Extracting credential from intent")
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken

            if (googleIdToken == null) {
                Log.e("AuthDebug:handleResult", "No ID token found")
                return@withContext Result.failure(Exception("No ID token"))
            }

            Log.d("AuthDebug:handleResult", "Signing in with Firebase")
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()

            Log.d("AuthDebug:handleResult", "Firebase user signed in: ${authResult.user?.uid}")
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Log.e("AuthDebug:handleResult", "Error handling sign-in result", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
        oneTapClient.signOut()
    }

    fun getSignedInUser(): FirebaseUser? = auth.currentUser
}
