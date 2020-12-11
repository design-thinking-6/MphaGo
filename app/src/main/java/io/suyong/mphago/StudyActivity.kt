package io.suyong.mphago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.suyong.mphago.adapter.HintAdapter
import io.suyong.mphago.adapter.HintType
import io.suyong.mphago.adapter.HintViewHolder
import io.suyong.mphago.network.NetworkManager
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.layout_answer.view.*
import kotlinx.android.synthetic.main.layout_choice_answer.view.*
import kotlinx.android.synthetic.main.layout_subjective.*
import kotlinx.android.synthetic.main.layout_subjective.view.*
import org.json.JSONArray
import org.json.JSONObject

class StudyActivity : AppCompatActivity() {
    private var adapter: HintAdapter? = null
    private var isCorrect = 0
    private var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        adapter = HintAdapter(this)
        adapter?.onTimeEndListener {
            val holder = hintRecyclerView.findViewHolderForAdapterPosition(it + 1) as HintViewHolder?
            holder?.timeView?.start()

            if (holder == null) {

            }
        }

        button_start.setOnClickListener {
            button_start.visibility = View.GONE
            problem_hide_view.visibility = View.GONE

            hintRecyclerView.findViewHolderForAdapterPosition(0)?.let {
                if (it is HintViewHolder) it.timeView.start()
            }
        }

        code = intent.extras?.get("code") as String

        NetworkManager.request(
            Request.Method.GET,
            "v1/questions/$code",
            null,
            {
                it as JSONObject
                val hints = it.get("hints") as JSONArray

                init(hints, code)
                initBottomSheet(
                    if (it.get("type") == "1") AnswerType.SUBJECTIVE else AnswerType.OBJECTIVE,
                    it.getString("answer")
                )
            },
            {

            }
        )

        hintRecyclerView.adapter = adapter
        hintRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun sendData() {
        if (isCorrect > 0) {
            NetworkManager.request(
                Request.Method.PUT,
                "v1/users/${NetworkManager.id}/${NetworkManager.password}/questions/${code}/correct",
                null,
                { },
                { }
            )
        } else if (isCorrect < 0) {
            NetworkManager.request(
                Request.Method.PUT,
                "v1/users/${NetworkManager.id}/${NetworkManager.password}/questions/${code}/mistake",
                null,
                { },
                { }
            )
        }
    }

    private fun failed() {
        Toast.makeText(this, R.string.wrong_answer, Toast.LENGTH_SHORT).show()

        isCorrect -= 1
    }

    private fun correct() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.correct_answer)
            .setMessage("메인 화면으로 나가시겠습니까?")
            .setPositiveButton(R.string.ok) { _, _ ->
                isCorrect += 1

                sendData()
                finish()
            }.setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

    private fun initBottomSheet(type: AnswerType, answer: String) {
        val child = LayoutInflater.from(this).inflate(
            if (type == AnswerType.OBJECTIVE) R.layout.layout_choice_answer
            else R.layout.layout_subjective,
            answer_layout as ConstraintLayout,
            false
        )

        if (type == AnswerType.OBJECTIVE) {
            val list = mutableListOf(
                child.button_one,
                child.button_two,
                child.button_three,
                child.button_four,
                child.button_five)

            for (button in list) {
                button.setOnClickListener {
                    failed()
                }
            }

            list[answer.toInt() - 1].setOnClickListener {
                correct()
            }
        } else {
            child.button_answer.setOnClickListener {
                when (child.edit_answer.text.toString()) {
                    answer -> correct()
                    else -> {
                        child.edit_answer.setText("")
                        failed()
                    }
                }
            }
        }

        answer_layout.bottom_sheet_child_layout.addView(child)
    }

    private fun init(hints: JSONArray, code: String) {
        for (i in 0 until hints.length()) {
            val hint = HintType(
                "${i + 1}번째 힌트",
                (hints[i] as JSONObject).get("value").toString(),
                (hints[i] as JSONObject).get("time").toString().toInt()
            )

            adapter?.list?.add(hint)
        }
        adapter?.notifyDataSetChanged()

        val year = code.substring(2, 6)
        val number = code.substring(8, 10).toInt()
        val type = code.substring(10, 11)

        Glide
            .with(this)
            .load("${NetworkManager.IMAGE_SERVER_URL}$year/$number$type.png")
            .error(R.drawable.ic_launcher_background)
            .into(problemImageView as AppCompatImageView)
    }
}