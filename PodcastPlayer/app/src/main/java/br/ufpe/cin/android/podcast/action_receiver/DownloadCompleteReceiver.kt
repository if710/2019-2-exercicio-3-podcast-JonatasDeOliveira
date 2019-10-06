package br.ufpe.cin.android.podcast.action_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import br.ufpe.cin.android.podcast.database.ItemPathDB
import br.ufpe.cin.android.podcast.models.ItemPath
import org.jetbrains.anko.doAsync

class DownloadCompleteReceiver(holder: ItemFeedAdapter.ViewHolder) : BroadcastReceiver() {

    private val itemAction = holder.action

    override fun onReceive(context: Context, intent: Intent) {
        itemAction.isEnabled = true
        Toast.makeText(context,"Download completed!!!",Toast.LENGTH_LONG).show()
    }
}
