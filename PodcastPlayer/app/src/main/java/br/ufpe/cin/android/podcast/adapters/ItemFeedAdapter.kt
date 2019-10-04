package br.ufpe.cin.android.podcast.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.activities.EpisodeDetailActivity
import br.ufpe.cin.android.podcast.activities.MainActivity
import br.ufpe.cin.android.podcast.dto.ItemFeedDto
import kotlinx.android.synthetic.main.itemlista.view.*

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
            Toast.makeText(main.applicationContext,
                "We are working to make this work",
                Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(main.applicationContext, EpisodeDetailActivity::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("description", item.description)
            intent.putExtra("link", item.link)

            main.startActivity(intent)
        }
    }

    class ViewHolder (item : View) : RecyclerView.ViewHolder(item) {
        val title = item.item_title
        val date = item.item_date
        val action = item.item_action
    }
}