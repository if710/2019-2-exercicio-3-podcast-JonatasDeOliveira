package br.ufpe.cin.android.podcast.action_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import br.ufpe.cin.android.podcast.database.ItemPathDB
import br.ufpe.cin.android.podcast.models.ItemPath
import org.jetbrains.anko.doAsync

class DownloadCompleteReceiver(holder: ItemFeedAdapter.ViewHolder) : BroadcastReceiver() {

    private val itemAction = holder.action
    private val itemTitle = holder.title

    override fun onReceive(context: Context, intent: Intent) {
        itemAction.isEnabled = true
        Toast.makeText(context,"Download completed!!!",Toast.LENGTH_LONG).show()

        val itemPath = intent.getStringExtra("downloadPath")
        val db = ItemPathDB.getDb(context)

        doAsync {
            db.itemPathDao().insertItemPath(ItemPath(itemTitle.toString(), itemPath!!))
        }
    }
}
