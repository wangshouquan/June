package com.denser.june.presentation.screens.home.journals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.data.JournalRepository
import com.denser.june.core.domain.data_classes.Journal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JournalsVM(
    private val journalRepo: JournalRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(JournalListTab.Journals)
    val selectedTab = _selectedTab.asStateFlow()

    val journals: StateFlow<List<Journal>> = journalRepo.getJournals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val nonDraftJournals = journals.map { list ->
        list.filter { !it.isDraft }.sortedByDescending { it.dateTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedJournals = journals.map { list ->
        list.filter { it.isBookmarked }.sortedByDescending { it.dateTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftJournals = journals.map { list ->
        list.filter { it.isDraft }.sortedByDescending { it.dateTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onTabSelected(tab: JournalListTab) {
        _selectedTab.value = tab
    }

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