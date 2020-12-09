package io.suyong.mphago.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.suyong.mphago.R
import kotlinx.android.synthetic.main.item_hint.view.*

class HintAdapter(val context: Context) : RecyclerView.Adapter<HintViewHolder>() {
    var list = mutableListOf<HintType>()

    private var listener: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HintViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.item_hint, parent, false)

        return HintViewHolder(layout)
    }

    override fun onBindViewHolder(holder: HintViewHolder, position: Int) {
        val item = list[position]

        holder.timeView.maxTime = item.lockTime
        holder.timeView.setOnTimeListener { time, _, _ ->
            if (time == 0) listener(position)
        }

        if (position == 0) holder.timeView.start()

        holder.hintTitle.text = item.title
        holder.hintText.text = item.text
    }

    override fun getItemCount(): Int = list.size

    fun onTimeEndListener(func: (Int) -> Unit) {
        listener = func
    }
}

class HintViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val timeView = root.hint_time
    val hintTitle = root.hint_title
    val hintText = root.hint_text
}