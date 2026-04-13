package com.denser.june.presentation.screens.home.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denser.june.core.R
import com.denser.june.core.domain.model.Journal
import com.denser.june.core.utils.toDayOfMonth
import com.denser.june.core.utils.toFullDateTime
import com.denser.june.core.utils.toShortMonth
import java.util.Locale
import com.denser.june.presentation.components.JuneBadge
import com.denser.june.presentation.components.JuneMetadataRow
import java.util.Date

@Composable
fun JournalOptionsSheet(
    journal: Journal,
    onToggleBookmark: () -> Unit,
    onDeleteOrRestore: () -> Unit
) {
    val wordCount = remember(journal.content) {
        if (journal.content.isBlank()) 0
        else journal.content.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    }
    val year = remember(journal.dateTime) {
        SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(journal.dateTime))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = journal.dateTime.toDayOfMonth(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = journal.dateTime.toShortMonth().uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = year,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = journal.title.ifBlank { "Untitled" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    JuneBadge(show = journal.images.isNotEmpty(), icon = R.drawable.photo_24px, label = "${journal.images.size}")
                    JuneBadge(show = journal.songDetails != null, icon = R.drawable.music_note_24px)
                    JuneBadge(show = journal.location != null, icon = R.drawable.location_on_24px)
                    JuneBadge(show = journal.tags.isNotEmpty(), icon = R.drawable.sell_24px, label = "${journal.tags.size}")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionSquircle(
                    iconRes = if (journal.isBookmarked) R.drawable.bookmark_added_24px_fill else R.drawable.bookmark_24px,
                    contentDescription = if (journal.isBookmarked) "Remove Bookmark" else "Bookmark",
                    onClick = onToggleBookmark,
                    isActive = journal.isBookmarked,
                    activeColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.primary
                )
                ActionSquircle(
                    iconRes = if (journal.isDeleted) R.drawable.restore_from_trash_24px else R.drawable.delete_24px,
                    contentDescription = if (journal.isDeleted) "Restore" else "Delete",
                    onClick = onDeleteOrRestore,
                    tint = if (journal.isDeleted) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.error
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
            JuneMetadataRow(
                iconRes = R.drawable.article_24px,
                label = "Words",
                value = "$wordCount keywords"
            )
            JuneMetadataRow(
                iconRes = if (journal.cloudId != null) R.drawable.cloud_24px else R.drawable.cloud_off_24px,
                label = "Sync",
                value = if (journal.cloudId != null) "Cloud" else "Local only"
            )
            JuneMetadataRow(
                iconRes = R.drawable.cloud_sync_24px,
                label = "Synced",
                value = journal.syncedAt?.toFullDateTime() ?: "Not synced"
            )
            JuneMetadataRow(
                iconRes = R.drawable.today_24px,
                label = "Created",
                value = journal.createdAt.toFullDateTime()
            )
            JuneMetadataRow(
                iconRes = R.drawable.history_24px,
                label = "Updated",
                value = journal.updatedAt?.toFullDateTime() ?: "—"
            )
        }
    }
}

@Composable
private fun ActionSquircle(
    modifier: Modifier = Modifier,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    isActive: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.primaryContainer,
    activeContentColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) activeColor else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (isActive) activeContentColor else tint
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
