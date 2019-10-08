package br.ufpe.cin.android.podcast.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.dto.ItemFeedDto
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import br.ufpe.cin.android.podcast.parser.Parser
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.database.ItemFeedDB
import br.ufpe.cin.android.podcast.mapper.ItemFeedMapper
import br.ufpe.cin.android.podcast.services.PodcastPlayerService
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    internal var podcastPlayerService: PodcastPlayerService? = null
    internal var isBound = false

    lateinit var adapter : ItemFeedAdapter

    private val sConn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            podcastPlayerService = null
            isBound = false
            adapter.isPodcastServiceBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as PodcastPlayerService.MusicBinder
            podcastPlayerService = binder.service
            adapter.podcastPlayerService = podcastPlayerService
            isBound = true
            adapter.isPodcastServiceBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ItemFeedAdapter(this)

        list_item_feed.layoutManager = LinearLayoutManager(this)

        val url = "https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml?fbclid=IwAR3X1VxOU4OFdxG-2m0IKHLwDXHFRavdx1ZndZ1T53OLRQk_XQlE168N1bI"
        var items: List<ItemFeedDto>

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
                adapter.items = items
                list_item_feed.adapter = adapter
            }

            items.forEach {
                val item = ItemFeedMapper.fromDto(it)
                db.itemFeedDao().insertItemFeed(item)
            }
        }

        list_item_feed.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

        val musicServiceIntent = Intent(applicationContext, PodcastPlayerService::class.java)
        startService(musicServiceIntent)
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val bindIntent = Intent(this, PodcastPlayerService::class.java)
            isBound = bindService(bindIntent,sConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        isBound = false
        unbindService(sConn)
        super.onStop()
    }
}
