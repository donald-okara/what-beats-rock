package ke.don.feature_leaderboard.models

sealed class LeaderboardIntentHandler {
    class NavigateToProfile(val id: String): LeaderboardIntentHandler()
    object NavigateMyProfile: LeaderboardIntentHandler()
    object FetchLeaderboard: LeaderboardIntentHandler()
    object RefreshLeaderboard: LeaderboardIntentHandler()
    object NavigateToChat: LeaderboardIntentHandler()
}