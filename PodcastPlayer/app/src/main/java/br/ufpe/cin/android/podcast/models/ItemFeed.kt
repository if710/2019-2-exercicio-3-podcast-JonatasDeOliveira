package br.ufpe.cin.android.podcast.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_feed")
data class ItemFeed (
    @PrimaryKey var title: String,
    var link: String,
    var pubDate: String,
    var description: String,
    var downloadLink: String
) {
    override fun toString(): String {
        return title
    }
}