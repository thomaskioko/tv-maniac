package com.thomaskioko.tvmaniac.data.watchproviders.implementation

/**
 * Collapse TMDB watch-provider variants under a single canonical brand entry.
 *
 * TMDB lists the same brand many ways: a canonical service ("MGM Plus", "Amazon
 * Prime Video"), channel variants ("MGM+ Amazon Channel", "MGM Plus Roku Premium
 * Channel"), and tier variants ("Amazon Prime Video with Ads"). Across all of these,
 * the canonical brand is the shortest word-sequence prefix — every variant extends
 * that prefix with one or more extra words.
 *
 * For each entry, if any OTHER entry's normalized words form a strict prefix of this
 * one's words, this entry is a variant of that shorter brand and is dropped. The
 * shorter entry is kept. When two entries normalize to the same words (e.g. "MGM+"
 * and "MGM Plus", since `+` normalizes to ` Plus`), the first occurrence wins to
 * preserve input order.
 *
 * Word-prefix matching is dynamic: no hardcoded suffix list, so new TMDB variants
 * (e.g. "Hulu with Live TV", "Paramount+ with Showtime") collapse automatically as
 * long as the canonical brand is present in the same payload.
 */
internal inline fun <T> List<T>.dedupedByBrand(name: (T) -> String?): List<T> {
    if (isEmpty()) return this
    val words: List<List<String>> = map { name(it).orEmpty().toBrandWords() }
    val keep = BooleanArray(size) { true }

    for (i in indices) {
        val iWords = words[i]
        if (iWords.isEmpty()) {
            keep[i] = false
            continue
        }
        for (j in indices) {
            if (i == j) continue
            val jWords = words[j]
            if (jWords.size < iWords.size && jWords.isWordPrefixOf(iWords)) {
                keep[i] = false
                break
            }
        }
    }

    val seen = mutableSetOf<List<String>>()
    return filterIndexed { i, _ -> keep[i] && seen.add(words[i]) }
}

private fun String.toBrandWords(): List<String> =
    replace("+", " Plus")
        .lowercase()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

private fun List<String>.isWordPrefixOf(other: List<String>): Boolean {
    if (size > other.size) return false
    for (i in indices) {
        if (this[i] != other[i]) return false
    }
    return true
}
