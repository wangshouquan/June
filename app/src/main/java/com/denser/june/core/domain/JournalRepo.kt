package com.denser.june.core.domain

import com.denser.june.core.domain.data_classes.Journal
import kotlinx.coroutines.flow.Flow

interface JournalRepo {
    suspend fun insertJournal(journal: Journal): Long
    suspend fun updateJournal(journal: Journal)
    suspend fun deleteJournal(id: Long)
    suspend fun deleteAllJournals()

    fun getJournals(): Flow<List<Journal>>
    suspend fun getAllJournals(): List<Journal>
    suspend fun getJournalById(id: Long): Journal?
    suspend fun getLatestJournal(): Journal?

    fun searchJournals(query: String): Flow<List<Journal>>
    fun getJournalsByTag(tagName: String): Flow<List<Journal>>
    fun getJournalsByDateRange(startDate: Long, endDate: Long): Flow<List<Journal>>
    fun getFilteredJournals(
        query: String = "",
        isBookmarked: Boolean? = null,
        isDraft: Boolean? = null,
        hasLocation: Boolean? = null,
        hasSong: Boolean? = null
    ): Flow<List<Journal>>

    fun getUniqueTags(): Flow<List<String>>
    fun getTagSuggestions(query: String): Flow<List<String>>
}