package io.suyong.mphago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import io.suyong.mphago.adapter.MainAdapter
import io.suyong.mphago.adapter.RecommandType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val mainAdapter = MainAdapter(this)
        mainRecyclerView.adapter = mainAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        mainAdapter.list.add(RecommandType("test", "수락"))
        mainAdapter.list.add(RecommandType("gd"))
    }
}