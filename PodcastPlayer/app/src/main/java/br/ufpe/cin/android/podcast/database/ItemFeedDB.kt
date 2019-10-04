package br.ufpe.cin.android.podcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpe.cin.android.podcast.dao.ItemFeedDao
import br.ufpe.cin.android.podcast.models.ItemFeed

@Database(entities = arrayOf(ItemFeed::class), version = 1)
abstract class ItemFeedDB : RoomDatabase() {
    abstract fun itemFeedDao() : ItemFeedDao
    companion object {
        private var INSTANCE : ItemFeedDB? = null

        fun getDb(ctx: Context) : ItemFeedDB {
            if(INSTANCE == null) {
                synchronized(ItemFeedDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemFeedDB::class.java,
                        "items_feed.db"
                    ).build()
                }
            }

            return INSTANCE!!
        }
    }
}