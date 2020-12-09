package io.suyong.mphago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.suyong.mphago.adapter.HintAdapter
import io.suyong.mphago.adapter.HintType
import io.suyong.mphago.adapter.HintViewHolder
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.layout_answer.view.*

class StudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val adapter = HintAdapter(this)
        adapter.onTimeEndListener {
            val holder = hintRecyclerView.findViewHolderForAdapterPosition(it + 1) as HintViewHolder?
            holder?.timeView?.start()

            if (holder == null) {

            }
        }

        val child = LayoutInflater.from(this).inflate(R.layout.layout_choice_answer, answer_layout as ConstraintLayout, false)
        answer_layout.bottom_sheet_child_layout.addView(child)

        hintRecyclerView.adapter = adapter
        hintRecyclerView.layoutManager = LinearLayoutManager(this)

        val url = "http://blog.suyong.me/images/2021/N320201114A.png"
        adapter.list.add(HintType("테스트", "아무 텍스트나 적어야지", 2))
        adapter.list.add(HintType("대충 2번째 힌트", "ㅁㄴㅇㄹ", 3))
        adapter.list.add(HintType("마지막 힌트", "ㅁㄴㅇㄹ", 4))

        Glide
            .with(this)
            .load(url)
            .error(R.drawable.ic_launcher_background)
            .into(problemImageView as AppCompatImageView)

    }
}