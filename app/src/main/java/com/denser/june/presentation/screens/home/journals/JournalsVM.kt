package com.denser.june.presentation.screens.home.journals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.data.JournalRepository
import com.denser.june.core.domain.data_classes.Journal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalsVM(
    private val journalRepo: JournalRepository
) : ViewModel() {

    val journals: StateFlow<List<Journal>> = journalRepo.getJournals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteJournal(id: Long) {
        viewModelScope.launch { journalRepo.deleteJournal(id) }
    }

    fun toggleBookmark(id: Long) {
        viewModelScope.launch {
            val journal = journalRepo.getJournalById(id)
            journal?.let {
                journalRepo.updateJournal(it.copy(isBookmarked = !it.isBookmarked))
            }
        }
    }
}