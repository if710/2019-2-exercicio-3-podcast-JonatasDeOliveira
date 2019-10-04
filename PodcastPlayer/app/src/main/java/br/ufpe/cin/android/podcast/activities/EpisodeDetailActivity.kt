package br.ufpe.cin.android.podcast.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import br.ufpe.cin.android.podcast.R
import kotlinx.android.synthetic.main.activity_episode_detail.*
import kotlinx.android.synthetic.main.itemlista.*

class EpisodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        item_desc_title.text = intent.getStringExtra("title")
        item_desc_link.text = intent.getStringExtra("link")
        item_desc_description.text = intent.getStringExtra("description")

        item_desc_description.movementMethod = ScrollingMovementMethod()
    }
}
