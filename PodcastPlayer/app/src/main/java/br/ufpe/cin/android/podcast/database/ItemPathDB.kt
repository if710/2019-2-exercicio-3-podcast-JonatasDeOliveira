package br.ufpe.cin.android.podcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpe.cin.android.podcast.dao.ItemPathDao
import br.ufpe.cin.android.podcast.models.ItemPath

@Database(entities = arrayOf(ItemPath::class), version = 1)
abstract class ItemPathDB : RoomDatabase() {
    abstract fun itemPathDao() : ItemPathDao
    companion object {
        private var INSTANCE : ItemPathDB? = null

        fun getDb(ctx: Context) : ItemPathDB {
            if(INSTANCE == null) {
                synchronized(ItemPathDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemPathDB::class.java,
                        "items_path.db"
                    ).build()
                }
            }

            return INSTANCE!!
        }
    }
}