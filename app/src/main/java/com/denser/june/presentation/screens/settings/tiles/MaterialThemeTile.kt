package com.denser.june.presentation.screens.settings.tiles

import android.os.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.denser.june.core.R
import com.denser.june.presentation.screens.settings.SettingsAction
import com.denser.june.presentation.screens.settings.SettingsVM
import com.denser.june.presentation.screens.settings.components.SettingsItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MaterialThemeTile() {
    val viewModel: SettingsVM = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SettingsItem(
            title = stringResource(R.string.material_theme),
            subtitle = stringResource(R.string.material_theme_desc),
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.imagesearch_roller_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            trailingContent = {
                Switch(
                    checked = state.appTheme.materialTheme,
                    onCheckedChange = { viewModel.onAction(SettingsAction.OnMaterialThemeToggle(it)) }
                )
            },
            onClick = { viewModel.onAction(SettingsAction.OnMaterialThemeToggle(!state.appTheme.materialTheme)) }
        )
    }
}
