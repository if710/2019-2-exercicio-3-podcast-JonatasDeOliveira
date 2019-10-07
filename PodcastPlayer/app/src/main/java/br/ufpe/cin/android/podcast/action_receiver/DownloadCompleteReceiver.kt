package br.ufpe.cin.android.podcast.action_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import br.ufpe.cin.android.podcast.database.ItemPathDB
import br.ufpe.cin.android.podcast.models.ItemPath
import kotlinx.android.synthetic.main.itemlista.view.*
import org.jetbrains.anko.doAsync

class DownloadCompleteReceiver(holder: ItemFeedAdapter.ViewHolder) : BroadcastReceiver() {

    private val itemAction = holder.action
    private val title: String = holder.title.text.toString()
    private val playAndPauseButton = holder.itemView.playAndPause

    override fun onReceive(context: Context, intent: Intent) {
        val intentTitle = intent.getStringExtra("item_title")
        if(title == intentTitle) {
            itemAction.isEnabled = true
            playAndPauseButton.isEnabled = true
        }
        Toast.makeText(context,"Download completed!!!",Toast.LENGTH_LONG).show()
    }
}
