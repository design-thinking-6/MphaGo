package io.suyong.mphago

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.suyong.mphago.adapter.MainAdapter
import io.suyong.mphago.adapter.RecommandType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.setLogo(R.drawable.ic_ring_padding)

        val mainAdapter = MainAdapter(this)
        mainRecyclerView.adapter = mainAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        mainAdapter.list.add(RecommandType("test", "수락"))
        mainAdapter.list.add(RecommandType("gd"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {
                val intent = Intent(this, SettingActivity::class.java)

                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}