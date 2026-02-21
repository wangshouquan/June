package com.denser.june

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.domain.AppPreferences
import com.denser.june.core.domain.data_classes.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AppState(
    val appTheme: AppTheme = AppTheme(),
    val isAppLockEnabled: Boolean = false,
    val isLoading: Boolean = true
)

class MainVM(
    prefs: AppPreferences
) : ViewModel() {

    private val themeFlow = combine(
        prefs.getSeedColorFlow(),
        prefs.getAppThemePrefFlow(),
        prefs.getAmoledPrefFlow(),
        prefs.getPaletteStyle(),
        prefs.getMaterialYouFlow()
    ) { seed, themeMode, amoled, style, matYou ->
        AppTheme(
            seedColor = seed,
            themeMode = themeMode,
            withAmoled = amoled,
            style = style,
            materialTheme = matYou
        )
    }

    val state = combine(
        themeFlow,
        prefs.getFontFlow(),
        prefs.getAppLockFlow()
    ) { baseTheme, font, appLock ->
        AppState(
            appTheme = baseTheme.copy(font = font),
            isAppLockEnabled = appLock,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppState()
    )
}