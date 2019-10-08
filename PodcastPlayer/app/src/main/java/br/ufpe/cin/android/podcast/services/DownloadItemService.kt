package br.ufpe.cin.android.podcast.services

import android.app.IntentService
import android.content.Intent
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.ufpe.cin.android.podcast.database.ItemPathDB
import br.ufpe.cin.android.podcast.models.ItemPath
import org.jetbrains.anko.doAsync

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL



class DownloadItemService : IntentService("DownloadItemService") {

    companion object {
        const val ACTION_DOWNLOAD = "br.ufpe.cin.android.podcast.services.action.DOWNLOAD_COMPLETE"
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            val title = intent!!.getStringExtra("item_title")
            val root = getExternalFilesDir(DIRECTORY_DOWNLOADS)
            root?.mkdirs()
            val output = File(root, intent!!.data!!.lastPathSegment)
            if (output.exists()) {
                output.delete()
            }
            val url = URL(intent?.data!!.toString())
            val c = url.openConnection() as HttpURLConnection
            val fos = FileOutputStream(output.path)
            val out = BufferedOutputStream(fos)
            try {
                val `in` = c.inputStream
                val buffer = ByteArray(16000)
                var len = `in`.read(buffer)
                while (len >= 0) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                    Log.i(javaClass.name, "baixando... $len")
                }
                out.flush()
            } finally {
                fos.fd.sync()
                out.close()
                c.disconnect()
            }
            val actionIntent = Intent(ACTION_DOWNLOAD)
            actionIntent.putExtra("downloadPath", output.path)
            actionIntent.putExtra("item_title", title)

            val db = ItemPathDB.getDb(applicationContext)

            doAsync {
                db.itemPathDao().insertItemPath(ItemPath(title, output.path))
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(actionIntent)

        } catch (e2: IOException) {
            Log.e(javaClass.getName(), "Download could not be completed", e2)
        }
    }
}
