package io.suyong.mphago.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.suyong.mphago.R
import io.suyong.mphago.StudyActivity
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchResultAdapter(val activity: Activity) : RecyclerView.Adapter<SearchResultViewHolder>() {
    var list = mutableListOf<SearchResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val layout = LayoutInflater.from(activity).inflate(R.layout.item_search_result, parent, false)

        return SearchResultViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val item = list[position]

        holder.root.setOnClickListener {
            val intent = Intent(activity, StudyActivity::class.java)

            intent.putExtra("code", item.code)

            activity.startActivity(intent)
        }
        holder.titleView.text = item.title
        holder.difficultyView.text = activity.getString(R.string.difficulty, item.difficult.toString())
    }

    override fun getItemCount() = list.size
}

class SearchResultViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val titleView = root.item_title
    val difficultyView = root.item_difficult
}