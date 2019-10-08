package br.ufpe.cin.android.podcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpe.cin.android.podcast.dao.ItemAudioDao
import br.ufpe.cin.android.podcast.models.ItemAudio

@Database(entities = arrayOf(ItemAudio::class), version = 1)
abstract class ItemAudioDB : RoomDatabase() {
    abstract fun itemAudioDao() : ItemAudioDao
    companion object {
        private var INSTANCE : ItemAudioDB? = null

        fun getDb(ctx: Context) : ItemAudioDB {
            if(INSTANCE == null) {
                synchronized(ItemAudioDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemAudioDB::class.java,
                        "items_audio.db"
                    ).build()
                }
            }

            return INSTANCE!!
        }
    }
}