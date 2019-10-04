package br.ufpe.cin.android.podcast.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_path")
class ItemPath (
    @PrimaryKey
    var title: String,
    var path: String
) {
    override fun toString(): String {
        return title
    }
}