package com.denser.june.core.domain.preferences

import com.denser.june.core.domain.model.enums.ThemeMode
import com.denser.june.core.domain.model.enums.TimeFormat
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow

interface JournalPreferences {
    fun isAutoTimeEnabled(): Flow<Boolean>
    suspend fun setAutoTimeEnabled(enabled: Boolean)

    fun startOfWeek(): Flow<DayOfWeek>
    suspend fun setStartOfWeek(dayOfWeek: DayOfWeek)

    fun isReminderEnabled(): Flow<Boolean>
    suspend fun setReminderEnabled(enabled: Boolean)

    fun reminderTime(): Flow<String>
    suspend fun setReminderTime(time: String)

    fun reminderDays(): Flow<Set<DayOfWeek>>
    suspend fun setReminderDays(days: Set<DayOfWeek>)

    fun timeFormat(): Flow<TimeFormat>
    suspend fun setTimeFormat(format: TimeFormat)

    fun mapTheme(): Flow<ThemeMode>
    suspend fun setMapTheme(theme: ThemeMode)

    fun isMarkdownEnabled(): Flow<Boolean>
    suspend fun setMarkdownEnabled(enabled: Boolean)
}
