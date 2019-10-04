package br.ufpe.cin.android.podcast.mapper

import br.ufpe.cin.android.podcast.dto.ItemFeedDto
import br.ufpe.cin.android.podcast.models.ItemFeed

object ItemFeedMapper {
    fun fromDto(item: ItemFeedDto): ItemFeed {
        return ItemFeed(item.title, item.link, item.pubDate, item.description, item.downloadLink)
    }

    fun toDto(item: ItemFeed): ItemFeedDto {
        return ItemFeedDto(item.title, item.link, item.pubDate, item.description, item.downloadLink)
    }
}