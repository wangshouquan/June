package com.denser.june.presentation.screens.settings.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.denser.june.core.R
import com.denser.june.presentation.navigation.AppNavigator
import com.denser.june.presentation.components.JuneTopAppBar
import com.denser.june.presentation.screens.settings.components.SettingsItem
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLibrariesScreen() {
    val navigator = koinInject<AppNavigator>()
    val libsState = produceLibraries()
    val uriHandler = LocalUriHandler.current
    var selectedLibraryForLicense by remember { mutableStateOf<Library?>(null) }

    Scaffold(
        modifier = Modifier.widthIn(max = 1000.dp),
        topBar = {
            JuneTopAppBar(
                title = { Text(stringResource(R.string.about_libraries)) },
                navigationIcon = {
                    FilledIconButton(
                        onClick = { navigator.navigateBack() },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        ),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        val libs = libsState.value
        val libraryList = libs?.libraries ?: emptyList()

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(libraryList, key = { it.uniqueId }) { library ->
                val firstLicense = library.licenses.firstOrNull()
                val licenseName = firstLicense?.name ?: "Unknown License"
                val url = library.website ?: library.scm?.url
                val versionText = library.artifactVersion?.let { "v$it" } ?: "Unknown version"

                SettingsItem(
                    title = library.name,
                    subtitle = versionText,
                    onClick = { selectedLibraryForLicense = library },
                    trailingContent = if (!url.isNullOrBlank()) {
                        {
                            IconButton(
                                onClick = { uriHandler.openUri(url) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.open_in_new_24px),
                                    contentDescription = "Open Website",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else null
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = licenseName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    selectedLibraryForLicense?.let { library ->
        val firstLicense = library.licenses.firstOrNull()
        val licenseName = firstLicense?.name ?: "Unknown License"
        val licenseContent = firstLicense?.licenseContent

        LibraryLicenseBottomSheet(
            libraryName = library.name,
            licenseName = licenseName,
            licenseContent = licenseContent,
            onDismiss = { selectedLibraryForLicense = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryLicenseBottomSheet(
    libraryName: String,
    licenseName: String,
    licenseContent: String?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = libraryName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = licenseName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Text(
                            text = licenseContent ?: "No license text available.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}