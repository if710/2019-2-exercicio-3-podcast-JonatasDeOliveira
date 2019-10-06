package br.ufpe.cin.android.podcast.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileInputStream

class PodcastPlayerService : Service() {

    private val TAG = "MusicPlayerWithBindingService"
    private var mPlayer: MediaPlayer? = null
    private val mStartID: Int = 0

    private val mBinder = MusicBinder()

    override fun onCreate() {
        super.onCreate()
        mPlayer = MediaPlayer()

        mPlayer?.isLooping = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        mPlayer?.release()
        super.onDestroy()
    }

    fun playMusic(text: String) {
        if (!mPlayer!!.isPlaying) {
            mPlayer?.start()

            setNotification(text)
        }
    }

    fun pauseMusic() {
        if (mPlayer!!.isPlaying) {
            mPlayer?.pause()

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.deleteNotificationChannel("1")
            }
        }
    }

    fun setPodcastToPlay(path: String) {
        val fis = FileInputStream(path)

        mPlayer?.reset()
        mPlayer?.setDataSource(fis.fd)
        mPlayer?.prepare()
    }

    inner class MusicBinder : Binder() {
        internal val service: PodcastPlayerService
            get() = this@PodcastPlayerService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val mChannel = NotificationChannel("1", "Canal de Notificacoes", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "PodcastPlayer"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun setNotification(text: String) {
        val notificationIntent = Intent(applicationContext, PodcastPlayerService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        createChannel()

        val notification = NotificationCompat.Builder(
            applicationContext,"1")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true).setContentTitle("Você está escutando")
            .setContentText(text)
            .setContentIntent(pendingIntent).build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        startForeground(NOTIFICATION_ID, notification)
    }

    companion object {
        private val NOTIFICATION_ID = 2
    }
}
