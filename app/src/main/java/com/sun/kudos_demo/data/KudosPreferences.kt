package com.sun.kudos_demo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.kudosDataStore by preferencesDataStore(name = "kudos_prefs")

/**
 * Persists local-only Kudos Feed state across sessions (Phase 05 — mock app, no backend):
 *  - liked kudo IDs (the heart toggle)
 *  - recent Sunner-search user IDs, most-recent first
 *
 * The user chose "full interactivity + persistence" in clarifications, so these survive
 * process death via DataStore Preferences. Recent searches are stored newline-joined to
 * preserve order (a Preferences string set is unordered).
 */
class KudosPreferences(private val context: Context) {

    private val likedKey = stringSetPreferencesKey("liked_kudo_ids")
    private val recentKey = stringPreferencesKey("recent_search_user_ids")
    private val currentUserKey = stringPreferencesKey("current_user_id")

    val likedKudoIds: Flow<Set<String>> =
        context.kudosDataStore.data.map { it[likedKey] ?: emptySet() }

    /** Id of the Sunner whose session is active (set at login); null before first login. */
    val currentUserId: Flow<String?> =
        context.kudosDataStore.data.map { it[currentUserKey] }

    val recentSearchIds: Flow<List<String>> =
        context.kudosDataStore.data.map { prefs -> prefs[recentKey].toIdList() }

    /** Persist the signed-in Sunner's id — "creates" the local session at login. */
    suspend fun setCurrentUser(id: String) {
        context.kudosDataStore.edit { prefs -> prefs[currentUserKey] = id }
    }

    /** Toggle a kudo's liked state (add if absent, remove if present). */
    suspend fun toggleLike(kudoId: String) {
        context.kudosDataStore.edit { prefs ->
            val current = (prefs[likedKey] ?: emptySet()).toMutableSet()
            if (!current.add(kudoId)) current.remove(kudoId)
            prefs[likedKey] = current
        }
    }

    /** Record a search selection at the front of the recent list (deduped, capped at [max]). */
    suspend fun addRecentSearch(userId: String, max: Int = 10) {
        context.kudosDataStore.edit { prefs ->
            val updated = prefs[recentKey].toIdList().toMutableList().apply {
                remove(userId)
                add(0, userId)
            }
            prefs[recentKey] = updated.take(max).joinToString("\n")
        }
    }

    /** Remove a single recent-search entry (the X button). */
    suspend fun removeRecentSearch(userId: String) {
        context.kudosDataStore.edit { prefs ->
            val updated = prefs[recentKey].toIdList().toMutableList().apply { remove(userId) }
            prefs[recentKey] = updated.joinToString("\n")
        }
    }
}

private fun String?.toIdList(): List<String> =
    this?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
