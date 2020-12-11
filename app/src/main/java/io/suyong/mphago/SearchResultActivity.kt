package io.suyong.mphago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import io.suyong.mphago.adapter.SearchResult
import io.suyong.mphago.adapter.SearchResultAdapter
import io.suyong.mphago.network.NetworkManager
import kotlinx.android.synthetic.main.activity_search_result.*
import org.json.JSONArray
import org.json.JSONObject

class SearchResultActivity : AppCompatActivity() {
    private var adapter: SearchResultAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        adapter = SearchResultAdapter(this)

        search_result_recycler_view.adapter = adapter
        search_result_recycler_view.layoutManager = LinearLayoutManager(this)

        showSearchResult(intent.extras?.getString("search", "") ?: "")

        button_search.setOnClickListener {
            showSearchResult(edit_text_search.text.toString())
        }
    }

    private fun showSearchResult(search: String) {
        edit_text_search.setText(search)
        NetworkManager.request(
            Request.Method.GET,
            "v1/questions",
            null,
            {
                it as JSONArray

                adapter?.list?.removeAll { true }
                for (i in 0 until it.length()) {
                    val obj = it[i] as JSONObject

                    val code = obj.getString("code")

                    val year = code.substring(2, 6)
                    val number = code.substring(8, 10).toInt()
                    val type = code.substring(10, 11)

                    if (search.contains(year) || search.contains(number.toString()) || search.contains(type) ||
                        "${year}학년도 대학수학능력평가 ${if (type == "A") "가" else "나"}형 ${number}번 문제".contains(search)) {
                        adapter?.list?.add(
                            SearchResult(
                                obj.getString("code"),
                                "${year}학년도 대학수학능력평가 ${if (type == "A") "가" else "나"}형 ${number}번 문제",
                                obj.getInt("difficult")
                            )
                        )
                    }
                }

                adapter?.notifyDataSetChanged()
            },
            {},
            true
        )
    }
}