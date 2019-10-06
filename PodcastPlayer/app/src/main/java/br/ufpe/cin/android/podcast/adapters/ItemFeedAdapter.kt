package br.ufpe.cin.android.podcast.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.activities.EpisodeDetailActivity
import br.ufpe.cin.android.podcast.activities.MainActivity
import br.ufpe.cin.android.podcast.dto.ItemFeedDto
import br.ufpe.cin.android.podcast.services.DownloadItemService
import kotlinx.android.synthetic.main.itemlista.view.*
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.ufpe.cin.android.podcast.action_receiver.DownloadCompleteReceiver
import br.ufpe.cin.android.podcast.database.ItemPathDB
import br.ufpe.cin.android.podcast.services.PodcastPlayerService
import kotlinx.android.synthetic.main.itemlista.view.playAndPause
import org.jetbrains.anko.doAsync


class ItemFeedAdapter(private val main: MainActivity) : RecyclerView.Adapter<ItemFeedAdapter.ViewHolder>() {

    var items: List<ItemFeedDto> = emptyList()
    var podcastPlayerService : PodcastPlayerService? = null
    var isPodcastServiceBound : Boolean = false
    var isPaused : Boolean = true
    var lastItemTitlePlayed : String? = null

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(main.applicationContext).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pathDb = ItemPathDB.getDb(main.applicationContext)

        val item = items[position]
        holder.title?.text = item.title
        holder.date?.text = item.pubDate
        holder.action.setOnClickListener {
            holder.action.isEnabled = false
            val downloadService = Intent(main.applicationContext, DownloadItemService::class.java)
            downloadService.data = Uri.parse("https://dc607.4shared.com/img/NJMNidv6gm/27b3e950/dlink__2Fdownload_2FNJMNidv6gm_2FTijolo_5FJorge_5FMatheus_5F_5F1_5F.mp3_3Fsbsr_3D26982c2e7943ee1cd5b652bab23c0601a24_26bip_3DMTcwLjIzOC4xMzguMzc_26lgfp_3D52_26bip_3DMTcwLjIzOC4xMzguMzc/preview.mp3")
            downloadService.putExtra("item_title", item.title)
            main.startService(downloadService)
            configureReceiver(holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(main.applicationContext, EpisodeDetailActivity::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("description", item.description)
            intent.putExtra("link", item.link)

            main.startActivity(intent)
        }

        holder.itemView.playAndPause.setOnClickListener {
            doAsync {
                val item = pathDb.itemPathDao().findItemPath(holder.title.text.toString())

                if (isPodcastServiceBound) {
                    if(lastItemTitlePlayed == null || item.title != lastItemTitlePlayed!!){
                        podcastPlayerService?.setPodcastToPlay(item.path)
                        lastItemTitlePlayed = item.title
                    }

                    isPaused = if(!isPaused) {
                        podcastPlayerService?.pauseMusic()
                        holder.itemView.playAndPause.setImageResource(R.drawable.play_icon)
                        true
                    } else {
                        podcastPlayerService?.playMusic(item.title)
                        holder.itemView.playAndPause.setImageResource(R.drawable.pause_icon)
                        false
                    }
                }
            }
        }
    }

    private fun configureReceiver(holder: ViewHolder) {
        val filter = IntentFilter()
        filter.addAction(DownloadItemService.ACTION_DOWNLOAD)
        val receiver = DownloadCompleteReceiver(holder)
        LocalBroadcastManager.getInstance(main).registerReceiver(receiver, filter)
    }

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        val title = item.item_title
        val date = item.item_date
        val action = item.item_action
    }
}