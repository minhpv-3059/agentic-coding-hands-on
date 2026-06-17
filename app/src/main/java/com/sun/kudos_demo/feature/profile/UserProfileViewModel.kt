package com.sun.kudos_demo.feature.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.KudosPreferences
import com.sun.kudos_demo.feature.auth.AppLanguage
import com.sun.kudos_demo.feature.feed.Kudo
import com.sun.kudos_demo.feature.feed.KudoUser
import com.sun.kudos_demo.navigation.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** UI state for "Profile người khác" — read-only, shows the viewed user's received kudos. */
data class UserProfileUiState(
    val user: KudoUser = ProfileMockData.currentUser,
    val badges: List<AwardBadge> = ProfileMockData.awardBadges,
    val receivedKudos: List<Kudo> = emptyList(),
    val likedIds: Set<String> = emptySet(),
    val language: AppLanguage = AppLanguage.VN,
    val unreadCount: Int = 3
) {
    val receivedCount: Int get() = receivedKudos.size

    /** The design always shows an achievement badge; fall back to Rising Hero when unset. */
    val badge: String get() = user.badge ?: "Rising Hero"
}

/**
 * State holder for another Sunner's profile. Resolves the user from the [NavRoutes.ARG_USER_ID]
 * route arg via [SavedStateHandle], shows their received kudos, and persists heart toggles
 * through [KudosPreferences] (consistent with the feed).
 */
class UserProfileViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {

    private val prefs = KudosPreferences(app)
    private val userId: String = savedStateHandle.get<String>(NavRoutes.ARG_USER_ID).orEmpty()
    private val user: KudoUser = ProfileMockData.userById(userId)
    private val receivedKudos: List<Kudo> = ProfileMockData.receivedKudosFor(user)
    private val language = MutableStateFlow(AppLanguage.VN)

    val uiState: StateFlow<UserProfileUiState> =
        combine(prefs.likedKudoIds, language) { liked, lang ->
            UserProfileUiState(
                user = user,
                receivedKudos = receivedKudos,
                likedIds = liked,
                language = lang
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UserProfileUiState(user = user, receivedKudos = receivedKudos)
        )

    /** Recipient never sends their own received kudos, so every card here is likeable. */
    fun toggleLike(kudoId: String) {
        viewModelScope.launch { prefs.toggleLike(kudoId) }
    }

    fun toggleLanguage() {
        language.value = if (language.value == AppLanguage.VN) AppLanguage.EN else AppLanguage.VN
    }
}
