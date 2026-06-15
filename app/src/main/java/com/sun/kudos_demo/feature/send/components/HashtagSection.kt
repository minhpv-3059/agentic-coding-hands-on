package com.sun.kudos_demo.feature.send.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sun.kudos_demo.feature.send.SendKudosMockData
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosBorder
import com.sun.kudos_demo.ui.theme.KudosContainer2
import com.sun.kudos_demo.ui.theme.KudosDarkText
import com.sun.kudos_demo.ui.theme.KudosDropdownHighlight
import com.sun.kudos_demo.ui.theme.KudosError
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray
import com.sun.kudos_demo.ui.theme.KudosWhite

private val ChipShape = RoundedCornerShape(50)
private val AddButtonShape = RoundedCornerShape(4.dp)
private val MenuShape = RoundedCornerShape(8.dp)

// A8: mockHashtagOptions re-export val removed — callers use SendKudosMockData.hashtagOptions directly.

/**
 * "Hashtag" section — shows selected hashtag chips (each with an × remove button),
 * a "+ Hashtag" trigger that opens a multi-select dropdown overlay.
 *
 * Max hashtags enforced by [SendKudosMockData.MAX_HASHTAGS].
 * B1: chips use white background + KudosBorder border + KudosBorder text (design node 6885:9951).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagSection(
    selectedHashtags: List<String>,
    options: List<String>,
    expanded: Boolean,
    hasError: Boolean,
    onToggle: (Boolean) -> Unit,
    onHashtagToggle: (String) -> Unit,
    onHashtagRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (hasError) KudosError else KudosBorder
    // A5: use named constant instead of magic 5
    val maxReached = selectedHashtags.size >= SendKudosMockData.MAX_HASHTAGS

    Column(modifier = modifier.fillMaxWidth()) {
        // Label row: "Hashtag *"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hashtag",
                style = MaterialTheme.typography.bodySmall,
                color = KudosDarkText
            )
            Text(
                text = "*",
                style = MaterialTheme.typography.bodySmall,
                color = KudosError
            )
        }
        Spacer(Modifier.height(4.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Selected chips — B1: white fill + KudosBorder border + KudosBorder text
            selectedHashtags.forEach { tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, KudosBorder, ChipShape)
                        .background(KudosWhite, ChipShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.labelSmall,
                        color = KudosBorder
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove $tag",
                        tint = KudosBorder,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(12.dp)
                            .clickable { onHashtagRemove(tag) }
                    )
                }
            }

            // A5: "+ Hashtag (Tối đa N)" — cap interpolated from constant; hidden when max reached
            if (!maxReached) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, borderColor, AddButtonShape)
                            .clickable { onToggle(!expanded) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = KudosGray,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = " Hashtag (Tối đa ${SendKudosMockData.MAX_HASHTAGS})",
                            style = MaterialTheme.typography.labelSmall,
                            color = KudosGray
                        )
                    }

                    // DARK dropdown per design node 6891:17706: background=#00070C, border=#998C5F
                    // Selected item: rgba(255,234,158,0.20) highlight (design node 6891:17707)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onToggle(false) },
                        modifier = Modifier
                            .widthIn(min = 220.dp)
                            .background(KudosContainer2, MenuShape)
                            .border(1.dp, KudosBorder, MenuShape)
                    ) {
                        options.forEach { tag ->
                            val isSelected = tag in selectedHashtags
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "#$tag",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KudosWhite
                                    )
                                },
                                trailingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            // Gold checkmark for selected state (design node 6891:17714)
                                            tint = KudosGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                modifier = Modifier.background(
                                    if (isSelected) KudosDropdownHighlight else KudosContainer2
                                ),
                                onClick = { onHashtagToggle(tag) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8E1, name = "Hashtag — filled")
@Composable
private fun HashtagSectionPreview() {
    KudosAppTheme {
        HashtagSection(
            selectedHashtags = listOf("BE OPTIMISTIC", "WASSHOI", "BE A TEAM"),
            options = SendKudosMockData.hashtagOptions,
            expanded = false,
            hasError = false,
            onToggle = {},
            onHashtagToggle = {},
            onHashtagRemove = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
