package br.ufpe.cin.android.podcast.services

import android.app.IntentService
import android.content.Intent
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

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
                val buffer = ByteArray(8192)
                var len = `in`.read(buffer)
                while (len >= 0) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()
            } finally {
                fos.fd.sync()
                out.close()
                c.disconnect()
            }
            val actionIntent = Intent(ACTION_DOWNLOAD)
            actionIntent.putExtra("downloadPath", output.path)
            LocalBroadcastManager.getInstance(this).sendBroadcast(actionIntent)

        } catch (e2: IOException) {
            Log.e(javaClass.getName(), "Download could not be completed", e2)
        }
    }
}
