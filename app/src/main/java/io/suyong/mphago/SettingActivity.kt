package io.suyong.mphago

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.suyong.mphago.network.NetworkManager
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject

class SettingActivity : AppCompatActivity() {
    val settingFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.layout_setting, settingFragment)
                    .commit()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        updateSettingInfo()
    }

    private fun updateSettingInfo() {
        NetworkManager.request(
            Request.Method.GET,
            "v1/users/${NetworkManager.id}/${NetworkManager.password}",
            null,
            { json ->
                json?.let {
                    it as JSONObject
                    val nickname = it.getString("nickname") ?: getString(R.string.nickname)
                    val password = it.getString("password") ?: getString(R.string.password)
                    val shortMessage = it.getJSONObject("profile").getString("shortMessage") ?: getString(R.string.short_message)
                    val url = it.getJSONObject("profile").getString("url") ?: "https://dummyimage.com/100x100/000/fff"

                    init(nickname, password, shortMessage, url)
                }
            },
            {
                Log.d("error", it.toString())
                onBackPressed()
            }
        )
    }

    private fun init(nickname: String, password: String, shortMessage: String, url: String) {
        collapsingToolbar.title = nickname
        text_nickname.text = nickname
        text_short_message.text = shortMessage

        val nicknamePreference = settingFragment.findPreference<EditTextPreference>("nickname")
        val passwordPreference = settingFragment.findPreference<EditTextPreference>("password")
        val shortMessagePreference = settingFragment.findPreference<EditTextPreference>("short_message")
        val photoPreference = settingFragment.findPreference<EditTextPreference>("photo")

        nicknamePreference?.text = nickname
        passwordPreference?.text = password
        shortMessagePreference?.text = shortMessage
        photoPreference?.text = url

        nicknamePreference?.setOnPreferenceChangeListener { _, newValue ->
            requestEditSetting("nickname", newValue.toString())
            true
        }
        passwordPreference?.setOnPreferenceChangeListener { _, newValue ->
            requestEditSetting("password", newValue.toString()) {
                NetworkManager.password = newValue.toString()
            }
            true
        }
        shortMessagePreference?.setOnPreferenceChangeListener { _, newValue ->
            requestEditSetting("short_message", newValue.toString())
            true
        }
        photoPreference?.setOnPreferenceChangeListener { _, newValue ->
            requestEditSetting("url", newValue.toString())
            true
        }

        Glide
            .with(this)
            .load(url)
            .into(image_profile)
    }

    private fun requestEditSetting(key: String, value: String, func: (() -> Unit)? = null) {
        val json = JSONObject()

        json.put(key, value)

        NetworkManager.request(
            Request.Method.PATCH,
            "v1/users/${NetworkManager.id}/${NetworkManager.password}",
            json,
            {
                updateSettingInfo()
                func?.invoke()
            },
            {
                Log.d("error", it.toString())
            }
        )
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}