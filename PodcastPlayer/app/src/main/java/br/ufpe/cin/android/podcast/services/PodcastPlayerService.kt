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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.adapters.ItemFeedAdapter
import br.ufpe.cin.android.podcast.database.ItemAudioDB
import br.ufpe.cin.android.podcast.models.ItemAudio
import kotlinx.android.synthetic.main.itemlista.view.*
import org.jetbrains.anko.doAsync
import java.io.FileInputStream
import br.ufpe.cin.android.podcast.database.ItemPathDB
import br.ufpe.cin.android.podcast.models.ItemPath
import java.io.File
import java.io.FileNotFoundException


class PodcastPlayerService : Service() {

    private var mPlayer: MediaPlayer? = null

    private val mBinder = MusicBinder()
    private val channelId = "1"

    private var isPaused : Boolean = true
    private var currentTitle : String? = null
    private var currentHolder : ItemFeedAdapter.ViewHolder? = null

    private var itemAudioDB: ItemAudioDB? = null
    private var itemPathDB: ItemPathDB? = null

    override fun onCreate() {
        super.onCreate()
        itemAudioDB = ItemAudioDB.getDb(this)
        itemPathDB = ItemPathDB.getDb(this)
        mPlayer = MediaPlayer()

        mPlayer?.isLooping = true
        mPlayer?.setOnCompletionListener {
            doAsync {

                val itemPath = itemPathDB!!.itemPathDao().findItemPath(currentTitle!!)
                itemPathDB!!.itemPathDao().insertItemPath(ItemPath(currentTitle!!, ""))
                itemAudioDB!!.itemAudioDao().insertItemAudio(ItemAudio(currentTitle!!, 0))

                currentHolder!!.itemView.playAndPause.setImageResource(R.drawable.play_icon)
                currentHolder!!.itemView.playAndPause.isEnabled = false

                val podcastFile = File(itemPath.path)
                if (podcastFile.exists()) {
                    podcastFile.delete()
                }
            }
        }
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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
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
            doAsync {
                val itemAudio = itemAudioDB!!.itemAudioDao().findItemAudio(title)
                val itemPosition = if(itemAudio == null) {
                    0
                } else {
                    itemAudio.currentPosition
                }

                mPlayer!!.seekTo(itemPosition)

                mPlayer?.start()
                currentHolder!!.itemView.playAndPause.setImageResource(R.drawable.pause_icon)

                setNotification(title, PAUSE_ACTION)
            }
        }
    }

    fun pauseMusic(title: String) {
        if (mPlayer!!.isPlaying) {
            mPlayer?.pause()
            currentHolder!!.itemView.playAndPause.setImageResource(R.drawable.play_icon)

            doAsync {
                itemAudioDB!!.itemAudioDao().insertItemAudio(ItemAudio(title, mPlayer!!.currentPosition))
            }
            setNotification(title, PLAY_ACTION)
        }
    }

    fun setPodcastToPlay(path: String, title: String) {
        if(title != currentTitle && !isPaused) {
            pauseMusic(title)
        }

        try {
            val fis = FileInputStream(path)

            mPlayer?.reset()
            mPlayer?.setDataSource(fis.fd)
            mPlayer?.prepare()
        } catch (ex: FileNotFoundException) {
            Toast.makeText(this,"Arquivo foi apagado ou não existe", Toast.LENGTH_LONG).show()
        }
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
            val mChannel = NotificationChannel(channelId, "Canal de Notificacoes", NotificationManager.IMPORTANCE_DEFAULT)
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
            applicationContext,channelId)
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
