package com.denser.june.core.data.database.journal

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "journal_tag_cross_ref",
    primaryKeys = ["id", "tagName"],
    indices = [Index("tagName")]
)
data class JournalTagCrossRef(
    val id: String,
    val tagName: String
)