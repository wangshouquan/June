package com.denser.june.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.data.JournalRepository
import com.denser.june.core.domain.AppPreferences
import com.denser.june.core.domain.backup.ExportRepo
import com.denser.june.core.domain.backup.ExportState
import com.denser.june.core.domain.backup.RestoreRepo
import com.denser.june.core.domain.backup.RestoreResult
import com.denser.june.core.domain.backup.RestoreState
import com.denser.june.core.domain.enums.ThemeMode
import com.denser.june.core.domain.enums.Fonts
import com.denser.june.core.domain.enums.LockType
import com.denser.june.core.utils.FileUtils
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsVM(
    private val repo: JournalRepository,
    private val prefs: AppPreferences,
    private val exportRepo: ExportRepo,
    private val restoreRepo: RestoreRepo
) : ViewModel() {

    private val _localState = MutableStateFlow(SettingsState())

    val state = combine<Any?, SettingsState>(
        listOf(
            _localState,
            prefs.getSeedColorFlow(),
            prefs.getAppThemePrefFlow(),
            prefs.getAmoledPrefFlow(),
            prefs.getPaletteStyle(),
            prefs.getMaterialYouFlow(),
            prefs.getFontFlow(),
            prefs.getOnboardingDoneFlow(),
            prefs.getAppLockFlow(),
            prefs.getLockTypeFlow(),
            prefs.getPinHashFlow()
        )
    ) { array ->
        val local = array[0] as SettingsState

        local.copy(
            onBoardingDone = array[7] as Boolean,
            isAppLockEnabled = array[8] as Boolean,
            lockType = array[9] as LockType,
            pinHash = array[10] as String?,

            appTheme = local.appTheme.copy(
                seedColor = array[1] as Int,
                themeMode = array[2] as ThemeMode,
                withAmoled = array[3] as Boolean,
                style = array[4] as PaletteStyle,
                materialTheme = array[5] as Boolean,
                font = array[6] as Fonts,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState()
    )

    fun onAction(action: SettingsAction) {
        viewModelScope.launch {
            when (action) {
                SettingsAction.OnDeleteJournals -> {
                    withContext(Dispatchers.IO) {
                        val allJournals = repo.getAllJournals()
                        allJournals.flatMap { it.images }.forEach { FileUtils.deleteMedia(it) }
                        repo.deleteAllJournals()
                    }
                }

                is SettingsAction.OnExportJournals -> {
                    _localState.update { it.copy(exportState = ExportState.Exporting) }
                    val result = exportRepo.exportData(includeMedia = action.includeMedia)
                    _localState.update {
                        it.copy(exportState = if (result != null) ExportState.ExportReady(result) else ExportState.Error)
                    }
                }

                is SettingsAction.OnRestoreJournals -> {
                    _localState.update { it.copy(restoreState = RestoreState.Restoring) }
                    when (val res = restoreRepo.restoreData(action.path)) {
                        is RestoreResult.Failure -> _localState.update {
                            it.copy(
                                restoreState = RestoreState.Failure(
                                    res.exceptionType
                                )
                            )
                        }

                        RestoreResult.Success -> _localState.update { it.copy(restoreState = RestoreState.Restored) }
                    }
                }
                SettingsAction.ResetBackup -> _localState.update {
                    it.copy(
                        restoreState = RestoreState.Idle,
                        exportState = ExportState.Idle
                    )
                }
                is SettingsAction.OnUpdateOnboardingDone -> prefs.updateOnboardingDone(action.done)
                is SettingsAction.OnSeedColorChange -> prefs.updateSeedColor(action.color)
                is SettingsAction.OnAmoledSwitch -> prefs.updateAmoledPref(action.amoled)
                is SettingsAction.OnThemeSwitch -> prefs.updateAppThemePref(action.themeMode)
                is SettingsAction.OnPaletteChange -> prefs.updatePaletteStyle(action.style)
                is SettingsAction.OnFontChange -> prefs.updateFont(action.fonts)
                is SettingsAction.OnMaterialThemeToggle -> prefs.updateMaterialTheme(action.pref)
                is SettingsAction.OnAppLockToggle -> prefs.updateAppLock(action.enabled)
                is SettingsAction.UpdateLockType -> prefs.updateLockType(action.type)
                is SettingsAction.UpdatePinHash -> prefs.updatePinHash(action.hash)
            }
        }
    }
}