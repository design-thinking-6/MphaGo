package io.suyong.mphago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import io.suyong.mphago.adapter.HintAdapter
import io.suyong.mphago.adapter.HintType
import io.suyong.mphago.adapter.HintViewHolder
import kotlinx.android.synthetic.main.activity_study.*

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