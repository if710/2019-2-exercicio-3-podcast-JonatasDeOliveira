package br.ufpe.cin.android.podcast.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.ufpe.cin.android.podcast.models.ItemAudio

@Dao
interface ItemAudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemAudio(vararg item: ItemAudio)

    @Query("SELECT * FROM items_audio WHERE title = :title")
    fun findItemAudio(title: String) : ItemAudio

    @Query("SELECT * FROM items_audio")
    fun findAllItemsAudio() : Array<ItemAudio>
}