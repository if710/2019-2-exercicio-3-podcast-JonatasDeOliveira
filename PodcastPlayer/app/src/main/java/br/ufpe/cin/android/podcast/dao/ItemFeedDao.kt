package br.ufpe.cin.android.podcast.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.ufpe.cin.android.podcast.models.ItemFeed

@Dao
interface ItemFeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemFeed(vararg item: ItemFeed)

    @Query("SELECT * FROM items_feed")
    fun findAllItemsFeed() : Array<ItemFeed>
}