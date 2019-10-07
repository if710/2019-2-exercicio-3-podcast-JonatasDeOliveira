package br.ufpe.cin.android.podcast.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import kotlinx.android.synthetic.main.itemlista.view.*
import java.io.FileInputStream

class PodcastPlayerService : Service() {

    private val TAG = "MusicPlayerWithBindingService"
    private var mPlayer: MediaPlayer? = null

    private val mBinder = MusicBinder()

    private var isPaused : Boolean = true
    private var currentTitle : String? = null
    private var currentHolder : ItemFeedAdapter.ViewHolder? = null

    override fun onCreate() {
        super.onCreate()
        mPlayer = MediaPlayer()

        mPlayer?.isLooping = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val playFilter = IntentFilter(PLAY_ACTION)
        val pauseFilter = IntentFilter(PAUSE_ACTION)
        registerReceiver(playReceiver, playFilter)
        registerReceiver(pauseReceiver, pauseFilter)

        return Service.START_STICKY
    }

    override fun onDestroy() {
        mPlayer?.release()
        super.onDestroy()
    }

    fun controlMusic(title: String, holder: ItemFeedAdapter.ViewHolder) {
        if(currentTitle == null || title != currentTitle) {
            currentTitle = title
        }
        if(currentHolder != holder) {
            currentHolder = holder
        }
        isPaused = if(isPaused) {
            playMusic(title)
            false
        } else {
            pauseMusic(title)
            true
        }
    }

    fun playMusic(title: String) {
        if (!mPlayer!!.isPlaying) {
            mPlayer?.start()
            currentHolder!!.itemView.playAndPause.setImageResource(R.drawable.pause_icon)

            setNotification(title, PAUSE_ACTION)
        }
    }

    fun pauseMusic(title: String) {
        if (mPlayer!!.isPlaying) {
            mPlayer?.pause()
            currentHolder!!.itemView.playAndPause.setImageResource(R.drawable.play_icon)

            setNotification(title, PLAY_ACTION)
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

    fun setNotification(t: String, action: String) {
        val imageAction = if(action == PAUSE_ACTION) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        val actionName = if(action == PAUSE_ACTION) {
            "Pause"
        } else {
            "Play"
        }

        val actionIntent = Intent(action)
        actionIntent.putExtra("item_title", t)
        val actionPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, actionIntent, 0)

        val notificationIntent = Intent(applicationContext, PodcastPlayerService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        createChannel()

        val notification = NotificationCompat.Builder(
            applicationContext,"1")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .addAction(NotificationCompat.Action(imageAction, actionName, actionPendingIntent))
            .setOngoing(true).setContentTitle("Você está escutando")
            .setContentText(t)
            .setContentIntent(pendingIntent).build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        startForeground(NOTIFICATION_ID, notification)
    }

    private val playReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            controlMusic(currentTitle!!, currentHolder!!)
        }
    }

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            controlMusic(currentTitle!!, currentHolder!!)
        }
    }

    companion object {
        private val NOTIFICATION_ID = 2
        const val PLAY_ACTION = "br.ufpe.cin.android.podcast.services.PLAY_ACTION"
        const val PAUSE_ACTION = "br.ufpe.cin.android.podcast.services.PAUSE_ACTION"
    }
}
