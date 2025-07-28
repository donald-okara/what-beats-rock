package ke.don.feature_leaderboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.material_theme.components.AvatarImage
import ke.don.core_designsystem.material_theme.components.Images
import ke.don.core_designsystem.material_theme.components.shimmerBackground
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.core_datasource.domain.models.PodiumProfile

@Composable
fun LeaderboardItem(
    modifier: Modifier = Modifier,
    profile: PodiumProfile,
    onClick: (String) -> Unit = {}
){
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clickable(onClick = { onClick(profile.id) })
                .fillMaxWidth()
        ){
            Text(
                text = profile.position.toString(),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            ListItem(
                modifier = modifier,
                headlineContent = {
                    Text(
                        text = profile.userName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                trailingContent = {
                    Text(
                        text = profile.score.toString() + " ⭐",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingContent = {
                    AvatarImage(
                        size = 48.dp,
                        profileUrl = profile.profileUrl,
                        fallback = painterResource(Images.appLogo)
                    )
                }
            )
        }

        HorizontalDivider()
    }

}

@Composable
fun LeaderboardItemShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ListItem(
            modifier = Modifier.fillMaxWidth(),
            headlineContent = {
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth(0.8f)
                        .shimmerBackground(MaterialTheme.shapes.medium)
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shimmerBackground(CircleShape)
                )
            }
        )
    }
    HorizontalDivider()
}



@Preview
@Composable
fun LeaderboardItemPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean
){
    val profile =
        PodiumProfile(
            position = 3,
            score = 75,
            profileUrl = null,
            userName = "Dwayne Johnson",
            id = ""
        )
    ThemedPreviewTemplate(isDark) {
        LeaderboardItem(
            profile = profile
        )
    }
}

@Preview
@Composable
fun LeaderboardItemShimmerPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean
){
    val profile =
        PodiumProfile(
            position = 3,
            score = 75,
            profileUrl = null,
            userName = "Dwayne Johnson",
            id = ""
        )
    ThemedPreviewTemplate(isDark) {
        LeaderboardItemShimmer()
    }
}