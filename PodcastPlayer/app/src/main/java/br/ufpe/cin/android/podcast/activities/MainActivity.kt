package br.ufpe.cin.android.podcast.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.dto.ItemFeedDto
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import br.ufpe.cin.android.podcast.parser.Parser
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.database.ItemFeedDB
import br.ufpe.cin.android.podcast.mapper.ItemFeedMapper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_item_feed.layoutManager = LinearLayoutManager(this)

        val url = "https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml?fbclid=IwAR3X1VxOU4OFdxG-2m0IKHLwDXHFRavdx1ZndZ1T53OLRQk_XQlE168N1bI"
        var items: List<ItemFeedDto> = emptyList()

        val db = ItemFeedDB.getDb(applicationContext)
        doAsync {
            items = try {
                val xmlPodcast = URL(url).readText()
                Parser.parse(xmlPodcast)
            } catch (ex: Throwable) {
                Log.e("error", ex.message)
                db.itemFeedDao().findAllItemsFeed().map { itemFeed ->
                    ItemFeedMapper.toDto(itemFeed)
                }
            }

            uiThread {
                list_item_feed.adapter =
                    ItemFeedAdapter(items, this.weakRef.get()!!)
            }

            items.forEach {
                val item = ItemFeedMapper.fromDto(it)
                db.itemFeedDao().insertItemFeed(item)
            }
        }

        list_item_feed.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
    }
}
