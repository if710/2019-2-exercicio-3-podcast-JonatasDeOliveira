package br.ufpe.cin.android.podcast.dto

data class ItemFeedDto(val title: String, val link: String, val pubDate: String, val description: String, val downloadLink: String) {

    override fun toString(): String {
        return title
    }
}
