package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sun.kudos_demo.R
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosWhite

// Violation criteria string resource IDs — sourced verbatim from spec node 6885:10852 (design node text)
private val VIOLATION_CRITERIA_RES_IDS = listOf(
    R.string.send_community_criterion_1,
    R.string.send_community_criterion_2,
    R.string.send_community_criterion_3,
    R.string.send_community_criterion_4,
    R.string.send_community_criterion_5,
    R.string.send_community_criterion_6,
    R.string.send_community_criterion_7,
    R.string.send_community_criterion_8,
    R.string.send_community_criterion_9,
    R.string.send_community_criterion_10,
)

// Design typography constants (from node queries)
// Titles (6885:10849, 6885:10855): fontSize=18px, fontWeight=700, lineHeight=24px
// Bold intro (6885:10851): fontSize=14px, fontWeight=700, lineHeight=20px, letterSpacing=0.25px
// Body text (6885:10852, 6885:10857): fontSize=14px, fontWeight=400, lineHeight=20px, letterSpacing=0.25px
// Contact (6885:10859): fontSize=14px, fontWeight=700, lineHeight=20px, letterSpacing=0.25px

/**
 * Section B — Tiêu chuẩn cộng đồng.
 * Spec node 6885:10848. Static content, sourced verbatim from design.
 *
 * Issue 4 fix: typography aligned to design node values:
 *   - Section title: 18sp/Bold, KudosGold
 *   - Intro paragraph: 14sp/Bold, KudosGold (node 6885:10851)
 *   - Criteria intro: 14sp/Regular, KudosWhite (node 6885:10852 first line)
 *   - Numbered criteria: 14sp, gold number + white text, gap=16dp between items
 */
@Composable
internal fun CommunityStandardsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Title — node 6885:10849: 18sp/Bold, gold
        Text(
            text = stringResource(R.string.send_community_section_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Intro bold paragraph — node 6885:10851: 14sp/Bold, gold, letterSpacing=0.25sp
        Text(
            text = stringResource(R.string.send_community_intro_bold),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Criteria intro — node 6885:10852: 14sp/Regular, white
        Text(
            text = stringResource(R.string.send_community_criteria_intro),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosWhite
        )
        Spacer(Modifier.height(16.dp))
        // Violation criteria list — gap 16dp between items per design (node 6885:10850: gap=16dp)
        VIOLATION_CRITERIA_RES_IDS.forEachIndexed { index, resId ->
            ViolationItem(number = index + 1, text = stringResource(resId))
            if (index < VIOLATION_CRITERIA_RES_IDS.lastIndex) Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ViolationItem(number: Int, text: String) {
    // Node 6885:10852 body: 14sp/Regular, white; gold number prefix Bold
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = KudosGold, fontWeight = FontWeight.Bold)) {
                append("$number. ")
            }
            withStyle(SpanStyle(color = KudosWhite, fontWeight = FontWeight.Normal)) {
                append(text)
            }
        },
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
}

/**
 * Section C — Tiêu chuẩn bảo mật.
 * Spec node 6885:10854. Static content, sourced verbatim from design.
 *
 * Issue 4 fix: typography aligned to design node values — same scale as Section B.
 * Security sub-items use a darker container (KudosDivider) with 8dp radius, padding 12/10.
 */
@Composable
internal fun SecurityStandardsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Title — node 6885:10855: 18sp/Bold, gold
        Text(
            text = stringResource(R.string.send_security_section_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(16.dp))
        // Intro — node 6885:10857 first sentence: 14sp/Regular, white
        Text(
            text = stringResource(R.string.send_security_intro),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosWhite
        )
        Spacer(Modifier.height(16.dp))
        // Infor frame (node 6885:10856): gap=4dp between sub-items
        SecuritySubItem(
            label = stringResource(R.string.send_security_item_a_label),
            body = stringResource(R.string.send_security_item_a_body)
        )
        Spacer(Modifier.height(4.dp))
        SecuritySubItem(
            label = stringResource(R.string.send_security_item_b_label),
            body = stringResource(R.string.send_security_item_b_body)
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = KudosDivider, thickness = 1.dp)
        Spacer(Modifier.height(16.dp))
        // Contact support block — node 6885:10859: 14sp/Bold, gold label + white body
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = KudosGold, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.send_security_contact_label))
                }
                withStyle(SpanStyle(color = KudosWhite, fontWeight = FontWeight.Normal)) {
                    append(stringResource(R.string.send_security_contact_body))
                }
            },
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
    }
}

@Composable
private fun SecuritySubItem(label: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = KudosDivider,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            color = KudosGold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
            color = KudosWhite
        )
    }
}
