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
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import ke.don.core_datasource.BuildConfig
import ke.don.core_datasource.domain.models.Profile
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

            val result = oneTapClient.beginSignIn(signInRequest).await()

            Result.success(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun handleSignInResult(intent: Intent?): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
                ?: return@withContext Result.failure(Exception("No ID token"))

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user ?: return@withContext Result.failure(Exception("No user returned"))

            val uid = user.uid
            val profileRef = Firebase.firestore.collection("profiles").document(uid)

            val snapshot = profileRef.get().await()
            if (!snapshot.exists()) {
                val profile = Profile(
                    uid = uid,
                    displayName = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = Timestamp.now().toDate().toInstant().toString(),
                    highScore = 0,
                    onboarded = false,
                    lastPlayed = null
                )
                profileRef.set(profile).await()
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
        oneTapClient.signOut()
    }

    fun getSignedInUser(): FirebaseUser? = auth.currentUser
}
