package com.denser.june.core.domain.repository

import com.denser.june.core.domain.model.Journal
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getJournals(
        query: String? = null,
        isBookmarked: Boolean? = null,
        isDraft: Boolean? = null,
        hasLocation: Boolean? = null,
        hasSong: Boolean? = null,
        hasMedia: Boolean? = null
    ): Flow<List<Journal>>

    suspend fun getAllJournals(): List<Journal>
    suspend fun getJournalById(id: Long): Journal?
    suspend fun getLatestJournal(): Journal?
    fun getJournalsByDateRange(startDate: Long, endDate: Long): Flow<List<Journal>>
    fun getJournalsByMultipleTags(tags: List<String>): Flow<List<Journal>>
    
    suspend fun insertJournal(journal: Journal): Long
    suspend fun deleteJournal(id: Long)
    suspend fun deleteAllJournals()
    suspend fun updateJournal(journal: Journal)

    fun getTagSuggestions(query: String): Flow<List<String>>
    fun getUniqueTags(): Flow<List<String>>
    fun getTagCounts(): Flow<Map<String, Int>>
    suspend fun renameTag(oldName: String, newName: String)
    suspend fun deleteTag(tagName: String)
}
