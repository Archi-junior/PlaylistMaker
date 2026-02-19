package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.practicum.playlistmaker.mediaLibrary.data.db.converters.LongListConverter

@Database(
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(LongListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        private const val DATABASE_NAME = "playlist_maker.db"

        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}