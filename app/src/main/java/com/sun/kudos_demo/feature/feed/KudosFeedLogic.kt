package com.sun.kudos_demo.feature.feed

/**
 * Pure, UI-independent derivations for the Kudos Feed. Kept free of Android/DataStore so the
 * filter, ranking and search rules can be unit-tested directly on the JVM.
 */

/** Apply the Hashtag AND Phòng-ban filters (TC_FUN_004): a null filter matches everything. */
fun filterKudos(kudos: List<Kudo>, hashtag: String?, department: String?): List<Kudo> =
    kudos.filter { kudo ->
        (hashtag == null || hashtag in kudo.hashtags) &&
            (department == null || kudo.department == department)
    }

/** Highlight carousel = the top [limit] kudos by heart count, descending (TC_FUN_001). */
fun highlightKudos(kudos: List<Kudo>, limit: Int = 5): List<Kudo> =
    kudos.sortedByDescending { it.heartCount }.take(limit)

/** Add the signed-in user's heart to kudos they have liked (display-only increment). */
fun applyLikes(kudos: List<Kudo>, likedIds: Set<String>): List<Kudo> =
    kudos.map { if (it.id in likedIds) it.copy(heartCount = it.heartCount + 1) else it }

/** Live search over the Sunner pool by name or code; a blank query yields no results. */
fun searchUsers(users: List<KudoUser>, query: String): List<KudoUser> {
    val q = query.trim()
    return if (q.isBlank()) emptyList()
    else users.filter { it.name.contains(q, ignoreCase = true) || it.code.contains(q, ignoreCase = true) }
}
