package com.example.data.local.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room TypeConverters for persisting complex objects such as Date,
 * List<String>, and custom enum/collection types in SQLite.
 */
class Converters {

    // DATE <-> LONG TIMESTAMP
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // LIST<STRING> <-> STRING (delimited by ';;')
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        return value.joinToString(";;")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(";;").filter { it.isNotEmpty() }
    }
}
