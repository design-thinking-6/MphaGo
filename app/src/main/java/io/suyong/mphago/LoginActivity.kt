package io.suyong.mphago

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import io.suyong.mphago.network.NetworkManager
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private val LOGIN = "login"
    private val REGISTER = "register"

    var mode = LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        NetworkManager.init(this)
        NetworkManager.onError {
            Log.d("test", it.toString())
        }

        button_register.setOnClickListener {
            mode = if (mode == LOGIN) REGISTER else LOGIN

            if (mode == LOGIN) {
                edit_text_nickname_layout.visibility = View.GONE

                button_register.text = getString(R.string.register)
                button_login.text = getString(R.string.login)
            } else {
                edit_text_nickname_layout.visibility = View.VISIBLE

                button_register.text = getString(R.string.login)
                button_login.text = getString(R.string.register)
            }
        }

        button_login.setOnClickListener {
            val id = edit_text_id.text.toString()
            val password = edit_text_password.text.toString()
            val nickname = edit_text_nickname.text.toString()

            if (id == "") edit_text_id.error = getString(R.string.not_be_null)
            if (password == "") edit_text_password.error = getString(R.string.not_be_null)

            if (mode == LOGIN) {
                login(id, password)
            } else {
                if (nickname == "") edit_text_nickname.error = getString(R.string.not_be_null)

                if (id != "" && password != "" && nickname != "") register(id, password, nickname)
            }
        }
    }

    fun login(id: String, password: String) {
        NetworkManager.request(
            Request.Method.GET,
            "v1/users/$id/$password",
            null,
            {
                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("id", id)
                intent.putExtra("password", password)

                startActivity(intent)
            },
            {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun register(id: String, password: String, nickname: String) {
        val json = JSONObject()

        json.put("id", id)
        json.put("password", password)
        json.put("nickname", nickname)

        NetworkManager.request(
            Request.Method.POST,
            "v1/users",
            json,
            {
                login(id, password)
            },
            {
                Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
            }
        )
    }
}