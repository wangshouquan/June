package com.denser.june.core.data.repository

import com.denser.june.core.data.database.journal.JournalDao
import com.denser.june.core.data.database.journal.TagCount
import com.denser.june.core.data.mappers.asDomain
import com.denser.june.core.data.mappers.asEntity
import com.denser.june.core.domain.repository.JournalRepository
import com.denser.june.core.domain.model.Journal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JournalRepositoryImpl(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun getJournals(
        query: String?,
        isBookmarked: Boolean?,
        isDraft: Boolean?,
        hasLocation: Boolean?,
        hasSong: Boolean?,
        hasMedia: Boolean?
    ): Flow<List<Journal>> {
        return journalDao.getJournals(
            query = query,
            isBookmarked = isBookmarked,
            isDraft = isDraft,
            hasLocation = hasLocation,
            hasSong = hasSong,
            hasMedia = hasMedia
        ).map { it.asDomain() }
    }

    override suspend fun getAllJournals(): List<Journal> {
        return journalDao.getAllJournalsSync().asDomain()
    }

    override suspend fun getJournalById(id: Long): Journal? {
        return journalDao.getJournalById(id)?.asDomain()
    }

    override suspend fun getLatestJournal(): Journal? {
        return journalDao.getLatestJournal()?.asDomain()
    }

    override fun getJournalsByDateRange(startDate: Long, endDate: Long): Flow<List<Journal>> {
        return journalDao.getJournalsByDateRange(startDate, endDate).map { it.asDomain() }
    }

    override fun getJournalsByMultipleTags(tags: List<String>): Flow<List<Journal>> {
        return journalDao.getJournalsWithAllTags(tags = tags, tagCount = tags.size).map { it.asDomain() }
    }

    override suspend fun insertJournal(journal: Journal): Long {
        return journalDao.insertJournal(journal.asEntity())
    }

    override suspend fun deleteJournal(id: Long) {
        journalDao.deleteJournal(id)
    }

    override suspend fun deleteAllJournals() {
        journalDao.deleteAllJournals()
    }

    override suspend fun updateJournal(journal: Journal) {
        journalDao.updateJournal(journal.asEntity())
    }

    override fun getTagSuggestions(query: String): Flow<List<String>> {
        return journalDao.getTagSuggestions(query)
    }

    override fun getUniqueTags(): Flow<List<String>> {
        return journalDao.getAllUniqueTags()
    }

    override fun getTagCounts(): Flow<Map<String, Int>> {
        return journalDao.getAllTagCounts().map { list ->
            list.associate { it.name to it.count }
        }
    }

    override suspend fun renameTag(oldName: String, newName: String) {
        journalDao.updateTagName(oldName, newName)
    }

    override suspend fun deleteTag(tagName: String) {
        journalDao.deleteTag(tagName)
    }
}
