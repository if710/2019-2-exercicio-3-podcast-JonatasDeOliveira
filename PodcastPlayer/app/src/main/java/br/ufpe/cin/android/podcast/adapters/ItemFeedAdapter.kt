package br.ufpe.cin.android.podcast.adapters

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.ufpe.cin.android.podcast.action_receiver.DownloadCompleteReceiver


class ItemFeedAdapter(private val items: List<ItemFeedDto>, private val main: MainActivity) : RecyclerView.Adapter<ItemFeedAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(main.applicationContext).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title?.text = item.title
        holder.date?.text = item.pubDate
        holder.action.setOnClickListener {
            holder.action.isEnabled = false
            val downloadService = Intent(main.applicationContext, DownloadItemService::class.java)
            downloadService.data = Uri.parse("https://www.jbox.com.br/wp/wp-content/uploads/2019/02/superonze2-destacada.jpg")
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