package com.sun.kudos_demo.feature.home.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosAppTheme

/** Award data extracted directly from Figma design — mock only. */
data class AwardItem(
    val id: String,
    val name: String,
    val description: String,
    @DrawableRes val image: Int? = null
)

/**
 * Mock award list — text from Figma nodes I6885:9033, I6885:9034, I6885:9035.
 * Only Top Talent has a real trophy asset for now; the others fall back to a styled
 * placeholder until their images are exported in Phase 10 (see phase-10-awards.md).
 */
val mockAwards = listOf(
    AwardItem(
        id = "top_talent",
        name = "Top Talent",
        description = "Giải thưởng Top Talent vinh danh những cá nhân xuất sắc trên mọi phương diện",
        image = R.drawable.img_award_top_talent
    ),
    AwardItem(
        id = "top_project",
        name = "Top Project",
        description = "Giải thưởng Top Project vinh danh các tập thể dự án xuất sắc nhất năm",
        image = R.drawable.img_award_top_project
    ),
    AwardItem(
        id = "top_project_leader",
        name = "Top Project Leader",
        description = "Giải thưởng Top Project Leader vinh danh những nhà lãnh đạo dự án xuất sắc",
        image = R.drawable.img_award_top_project_leader
    )
)

/**
 * Awards section: header ("Sun* Annual Awards 2025" / "Hệ thống giải thưởng")
 * + horizontal LazyRow of award cards (160×298 dp each, 16 dp gap).
 * Design: mms_4_awards (6885:9030) — starts at horizontal padding 20 dp.
 */
@Composable
fun HomeAwardsSection(
    awards: List<AwardItem> = mockAwards,
    onAwardDetail: (awardId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            eyebrow = stringResource(R.string.home_awards_eyebrow),
            title = stringResource(R.string.home_awards_title),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Horizontal scrolling — starts flush with the 20 dp page margin
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(awards, key = { it.id }) { award ->
                AwardCard(
                    award = award,
                    onDetailClick = { onAwardDetail(award.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00101A)
@Composable
private fun HomeAwardsSectionPreview() {
    KudosAppTheme {
        HomeAwardsSection()
    }
}
