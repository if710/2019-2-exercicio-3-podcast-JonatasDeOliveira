package br.ufpe.cin.android.podcast.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_audio")
data class ItemAudio(
    @PrimaryKey var title: String,
    var currentPosition: Int
) {
    override fun toString(): String {
        return title
    }
}