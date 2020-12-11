package io.suyong.mphago

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.bumptech.glide.Glide
import io.suyong.mphago.adapter.MainAdapter
import io.suyong.mphago.adapter.RecommandType
import io.suyong.mphago.network.NetworkManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_notice.view.*
import kotlinx.android.synthetic.main.layout_recommand.view.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private var mainAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.setLogo(R.drawable.ic_ring_padding)

        mainAdapter = MainAdapter(this)
        mainRecyclerView.adapter = mainAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        init()

        button_search.setOnClickListener {
            val intent = Intent(this, SearchResultActivity::class.java)

            intent.putExtra("search", edit_text_search.text.toString())

            startActivity(intent)
        }
    }

    override fun onResume() {
        init()
        super.onResume()
    }

    private fun init() {
        mainAdapter?.list?.clear()

        val notice = RecommandType("공지사항")
        NetworkManager.request(
            Request.Method.GET,
            "v1/notices",
            null,
            {
                val array = it as JSONArray

                for (i in array.length() downTo 1) {
                    val title = (array[i - 1] as JSONObject).getString("title")
                    val content = (array[i - 1] as JSONObject).getString("content")

                    notice.layouts.add(createNotice(title, content))
                }

                mainAdapter?.notifyDataSetChanged()
            },
            {

            },
            true
        )
        mainAdapter?.list?.add(notice)
        mainAdapter?.notifyDataSetChanged()

        val result = RecommandType("추천 문제")
        NetworkManager.request(
            Request.Method.GET,
            "v1/questions/${NetworkManager.id}/${NetworkManager.password}/recommand",
            null,
            {
                val array = it as JSONArray

                for (i in 0 until array.length()) {
                    val code = (array[i] as JSONObject).get("code").toString()

                    result.layouts.add(createCodeByLayout(code))
                }

                mainAdapter?.notifyDataSetChanged()
            },
            {

            },
            true
        )
        mainAdapter?.list?.add(result)
        mainAdapter?.notifyDataSetChanged()
    }

    private fun createNotice(title: String, content: String): ViewGroup {
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_notice, null, false)

        layout.notice_title.text = title
        layout.notice_content.text = content

        return layout as ConstraintLayout
    }

    private fun createCodeByLayout(code: String): ViewGroup {
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_recommand, null, false)

        val year = code.substring(2, 6)
        val number = code.substring(8, 10).toInt()
        val type = code.substring(10, 11)

        Glide
            .with(this)
            .load("${NetworkManager.IMAGE_SERVER_URL}$year/$number$type.png")
            .override(dp(256))
            .into(layout.image_recommand)

        layout.text_recommand.text = "${year}학년도 대학수학능력평가 ${if (type == "A") "가" else "나"}형 ${number}번 문제"
        layout.button_recommand.text = "풀어보기"
        layout.button_recommand.setOnClickListener {
            val intent = Intent(this, StudyActivity::class.java)

            intent.putExtra("code", code)

            startActivity(intent)
        }

        return layout as ConstraintLayout
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

    private fun dp(dp: Int) = (dp * resources.displayMetrics.density).roundToInt()
}