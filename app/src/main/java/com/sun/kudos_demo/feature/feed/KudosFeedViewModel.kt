package com.sun.kudos_demo.feature.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.KudosPreferences
import com.sun.kudos_demo.data.KudosRepository
import com.sun.kudos_demo.feature.auth.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** UI state for the Kudos Feed screen (mock-data driven). */
data class FeedUiState(
    val highlightKudos: List<Kudo> = emptyList(),    // top 5 by hearts, after filters
    val allKudos: List<Kudo> = emptyList(),          // full filtered feed
    val selectedHashtag: String? = null,
    val selectedDepartment: String? = null,
    val likedKudoIds: Set<String> = emptySet(),
    val stats: KudoStats = KudosMockData.stats,
    val giftRecipients: List<GiftRecipient> = KudosMockData.giftRecipients,
    val hashtags: List<String> = KudosMockData.hashtags,
    val departments: List<String> = KudosMockData.departments,
    val currentUserId: String = CURRENT_USER_ID,
    val language: AppLanguage = AppLanguage.VN,
    val unreadCount: Int = 3
) {
    /** A kudo's sender cannot like their own kudo (TC_FUN_008). */
    fun canLike(kudo: Kudo): Boolean = kudo.sender?.id != currentUserId
}

private const val CURRENT_USER_ID = "u1"

/**
 * State holder for the Kudos Feed. Derives the highlight carousel (top-5 by hearts),
 * applies the Hashtag AND Phòng ban filters to both the carousel and the full feed,
 * and persists the heart toggle via [KudosPreferences].
 */
class KudosFeedViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = KudosPreferences(app)

    private val selectedHashtag = MutableStateFlow<String?>(null)
    private val selectedDepartment = MutableStateFlow<String?>(null)
    private val language = MutableStateFlow(AppLanguage.VN)

    val uiState: StateFlow<FeedUiState> =
        combine(
            KudosRepository.kudos, selectedHashtag, selectedDepartment, prefs.likedKudoIds, language
        ) { kudos, tag, dept, liked, lang ->
            buildState(kudos, tag, dept, liked, lang)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FeedUiState())

    private fun buildState(
        kudos: List<Kudo>,
        tag: String?,
        dept: String?,
        liked: Set<String>,
        lang: AppLanguage
    ): FeedUiState {
        val filtered = filterKudos(kudos, tag, dept)
        val highlight = highlightKudos(filtered)
        return FeedUiState(
            highlightKudos = applyLikes(highlight, liked),
            allKudos = applyLikes(filtered, liked),
            selectedHashtag = tag,
            selectedDepartment = dept,
            likedKudoIds = liked,
            language = lang
        )
    }

    /** Heart toggle — persisted. Guarded so a sender can't like their own kudo. */
    fun toggleLike(kudoId: String) {
        val kudo = KudosRepository.kudoById(kudoId) ?: return
        if (kudo.sender?.id == CURRENT_USER_ID) return
        viewModelScope.launch { prefs.toggleLike(kudoId) }
    }

    /** Dropdown selection — re-selecting the active value clears it. */
    fun selectHashtag(tag: String?) {
        selectedHashtag.value = if (tag != null && tag == selectedHashtag.value) null else tag
    }

    fun selectDepartment(dept: String?) {
        selectedDepartment.value = if (dept != null && dept == selectedDepartment.value) null else dept
    }

    /** Tapping a hashtag chip on a card sets the filter to that tag (TC_FUN_016/031). */
    fun applyHashtag(tag: String) {
        selectedHashtag.value = tag
    }

    fun toggleLanguage() {
        language.value = if (language.value == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN
    }
}
