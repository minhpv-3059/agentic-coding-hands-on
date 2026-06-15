package com.sun.kudos_demo.feature.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.KudosPreferences
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** UI state for "Profile của tôi" (mock-data driven). */
data class MyProfileUiState(
    val user: KudoUser = ProfileMockData.currentUser,
    val stats: ProfileStats = ProfileMockData.currentUserStats,
    val filter: ProfileKudosTab = ProfileKudosTab.SENT,   // design pill defaults to "Đã gửi (5)"
    val likedIds: Set<String> = emptySet(),
    val language: AppLanguage = AppLanguage.VN,
    val unreadCount: Int = 3
) {
    val receivedKudos: List<Kudo> get() = ProfileMockData.myReceivedKudos
    val sentKudos: List<Kudo> get() = ProfileMockData.mySentKudos
    val receivedCount: Int get() = receivedKudos.size
    val sentCount: Int get() = sentKudos.size

    /** Cards shown under the current filter selection. */
    val visibleKudos: List<Kudo>
        get() = if (filter == ProfileKudosTab.RECEIVED) receivedKudos else sentKudos
}

/**
 * State holder for the own-profile screen. Toggles the Đã nhận / Đã gửi filter and persists
 * the heart toggle via [KudosPreferences] so likes stay consistent with the feed.
 */
class MyProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = KudosPreferences(app)
    private val filter = MutableStateFlow(ProfileKudosTab.SENT)
    private val language = MutableStateFlow(AppLanguage.VN)

    val uiState: StateFlow<MyProfileUiState> =
        combine(filter, prefs.likedKudoIds, language) { f, liked, lang ->
            MyProfileUiState(filter = f, likedIds = liked, language = lang)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MyProfileUiState())

    fun setFilter(tab: ProfileKudosTab) { filter.value = tab }

    /** A user cannot like a kudo they sent (TC_FUN_008 parity with the feed). */
    fun toggleLike(kudoId: String) {
        val kudo = (ProfileMockData.myReceivedKudos + ProfileMockData.mySentKudos)
            .firstOrNull { it.id == kudoId } ?: return
        if (kudo.sender?.id == ProfileMockData.CURRENT_USER_ID) return
        viewModelScope.launch { prefs.toggleLike(kudoId) }
    }

    fun toggleLanguage() {
        language.value = if (language.value == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN
    }
}
