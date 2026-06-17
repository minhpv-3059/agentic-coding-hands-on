package com.sun.kudos_demo.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette — sourced from MoMorph design tokens (Color variable collection)
val KudosBackground = Color(0xFF00101A)
val KudosContainer = Color(0xFF101417)
val KudosContainer2 = Color(0xFF00070C)
val KudosBgUpdate = Color(0xFF1E2D39)
val KudosBorder = Color(0xFF998C5F)
val KudosDivider = Color(0xFF2E3940)
val KudosError = Color(0xFFB3261E)

// Text
val KudosGold = Color(0xFFFFEA9E)       // Primary accent — headlines, active states
val KudosDarkText = Color(0xFF00101A)   // Text on gold/light surfaces
val KudosWhite = Color(0xFFFFFFFF)
val KudosGray = Color(0xFF999999)

// Button states
val KudosPrimaryButtonHover = Color(0xFFFFF8E1)
val KudosSecondaryButtonNormal = Color(0x1AFFEA9E)  // rgba(255,234,158, 0.10)
val KudosSecondaryButtonHover = Color(0x66FFEA9E)   // rgba(255,234,158, 0.40)

// Kudos card (cream surface) accents — Phase 05
val KudosAccentRed = Color(0xFFD4271D)   // active heart + hashtag accent (design node 6885:10175)
val KudosCardMuted = Color(0xFF555555)   // secondary text on cream card (codes, links)
val KudosCardFaint = Color(0xFF888888)   // tertiary text on cream card (timestamps, sub-labels)

// Send Kudos form — Phase 06
val KudosFormCream = Color(0xFFFFF8E1)   // cream form card background (design node 6885:9903)
// Dropdown overlay — DARK per design node 6891:17450 (KudosContainer2 already defined above)
// Selected item highlight in dark dropdown: rgba(255,234,158,0.20) (design node 6891:17451/17707)
val KudosDropdownHighlight = Color(0x33FFEA9E)   // rgba(255,234,158,0.20) ≈ 0x33

// Community Standards link color — design node 6885:9933: rgba(228,96,96,1)
val KudosLinkRed = Color(0xFFE46060)

// Notification icons are now real Figma SVG exports (res/drawable/ic_*.xml) with their fill
// colors baked in — rendered with tint = Color.Unspecified, so no per-type Compose tints here.
