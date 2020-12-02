package io.suyong.mphago.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.suyong.mphago.R
import kotlinx.android.synthetic.main.item_problem.view.*

class MainAdapter(private val context: Context) : RecyclerView.Adapter<MainHolder>() {
    public val list = mutableListOf<RecommandType>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.item_problem, parent, false)
        val holder = MainHolder(layout)

        return holder
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.title
        holder.acceptButton.text = item.accept ?: ""

        when {
            item.accept == null -> holder.acceptButton.visibility = View.GONE
            item.layout != null -> holder.layout = item.layout
        }
    }

    override fun getItemCount(): Int {
        return list.size;
    }
}

class MainHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    val title: TextView = root.titleTextView
    val acceptButton: Button = root.acceptButton
    var layout: ConstraintLayout = root.problemLayout
}