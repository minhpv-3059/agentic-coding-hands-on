package com.sun.kudos_demo.feature.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sun.kudos_demo.data.KudosPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * State holder for the Sunner search screen. Live-filters the mock user pool by name/code
 * and keeps a persisted "Recent" list. Before the user has interacted, the design's default
 * recent entries are shown; the first mutation seeds DataStore so removals persist.
 */
class KudosSearchViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = KudosPreferences(app)
    private val allUsers = KudosMockData.searchableUsers
    private val defaults = KudosMockData.recentSearches

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<List<KudoUser>> =
        _query.map { q -> searchUsers(allUsers, q) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recent: StateFlow<List<KudoUser>> =
        prefs.recentSearchIds.map { ids ->
            if (ids.isEmpty()) defaults
            else ids.mapNotNull { id -> allUsers.firstOrNull { it.id == id } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaults)

    fun updateQuery(value: String) {
        _query.value = value.take(100)   // MaxLength 100 (TC_FUN_034)
    }

    /** Record a tapped result at the top of Recent (navigation is handled by the screen). */
    fun selectResult(user: KudoUser) {
        viewModelScope.launch {
            ensureSeeded()
            prefs.addRecentSearch(user.id)
        }
    }

    fun removeRecent(user: KudoUser) {
        viewModelScope.launch {
            ensureSeeded()
            prefs.removeRecentSearch(user.id)
        }
    }

    /** Persist the default recent list the first time the user mutates it. */
    private suspend fun ensureSeeded() {
        if (prefs.recentSearchIds.first().isEmpty()) {
            defaults.asReversed().forEach { prefs.addRecentSearch(it.id) }
        }
    }
}
