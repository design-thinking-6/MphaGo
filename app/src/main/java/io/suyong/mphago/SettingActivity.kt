package io.suyong.mphago

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.*
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.suyong.mphago.network.NetworkManager
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject
import kotlin.math.roundToInt


class SettingActivity : AppCompatActivity() {
    private val settingFragment = SettingsFragment()
    private var count = 0;

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
        supportActionBar?.setDisplayShowHomeEnabled(true)

        updateSettingInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
                    val shortMessage =
                        it.getJSONObject("profile").getString("shortMessage") ?: getString(
                            R.string.short_message
                        )
                    val url = it.getJSONObject("profile").getString("url")
                        ?: "https://dummyimage.com/100x100/000/fff"

                    init(nickname, password, shortMessage, url)
                }
            },
            {
                Log.d("error", it.toString())
                onBackPressed()
            }
        )
    }

    private fun adminPanel(isShow: Boolean = false) {
        val adminPreferences = settingFragment.findPreference<PreferenceCategory>("admin")
        adminPreferences?.isVisible = isShow
    }

    private fun init(nickname: String, password: String, shortMessage: String, url: String) {
        adminPanel()

        collapsingToolbar.title = nickname
        text_nickname.text = nickname
        text_short_message.text = shortMessage

        val nicknamePreference = settingFragment.findPreference<EditTextPreference>("nickname")
        val passwordPreference = settingFragment.findPreference<EditTextPreference>("password")
        val shortMessagePreference = settingFragment.findPreference<EditTextPreference>("short_message")
        val photoPreference = settingFragment.findPreference<EditTextPreference>("photo")
        val logoutPreference = settingFragment.findPreference<Preference>("logout")
        val informationPreference = settingFragment.findPreference<Preference>("information")
        val noticePreference = settingFragment.findPreference<Preference>("notice")
        val darkmodePreference = settingFragment.findPreference<DropDownPreference>("darkmode")

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
        logoutPreference?.setOnPreferenceClickListener {

            val prefernce = getSharedPreferences("io.suyong.mphago.preference", MODE_PRIVATE)
            prefernce.edit {
                this.remove("id")
                this.remove("password")
                NetworkManager.id = ""
                NetworkManager.password = ""

                this.commit()
            }
            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            true
        }

        noticePreference?.setOnPreferenceClickListener {
            val layout = LinearLayout(this)
            val titleEdit = EditText(this)
            val contentEdit = EditText(this)

            titleEdit.width = LinearLayout.LayoutParams.MATCH_PARENT
            titleEdit.hint = getString(R.string.title)
            contentEdit.width = LinearLayout.LayoutParams.MATCH_PARENT
            contentEdit.hint = getString(R.string.content)

            layout.orientation = LinearLayout.VERTICAL
            layout.addView(titleEdit)
            layout.addView(contentEdit)

            MaterialAlertDialogBuilder(this)
                .setTitle("공지사항")
                .setView(layout)
                .setPositiveButton(R.string.ok) { _, _ ->
                    val json = JSONObject()

                    json.put("title", titleEdit.text.toString())
                    json.put("content", contentEdit.text.toString())

                    NetworkManager.request(
                        Request.Method.POST,
                        "v1/notices/${NetworkManager.id}/${NetworkManager.password}",
                        json,
                        {
                            Toast.makeText(
                                this,
                                getString(R.string.upload_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        {}
                    )
                }.show()

            true
        }

        val preference = getSharedPreferences("io.suyong.mphago.preference", MODE_PRIVATE)

        if (preference.contains("darkMode")) {
            if (preference.getBoolean("darkMode", false)) {
                darkmodePreference?.setValueIndex(2)
            } else {
                darkmodePreference?.setValueIndex(1)
            }
        } else {
            darkmodePreference?.setValueIndex(0)
        }

        darkmodePreference?.setOnPreferenceChangeListener { _, value ->
            when(value) {
                getString(R.string.system_default) -> {
                    preference.edit {
                        this.remove("darkMode")
                        this.commit()
                    }
                }
                getString(R.string.light_mode) -> {
                    preference.edit {
                        this.putBoolean("darkMode", false)
                        this.commit()
                    }
                }
                getString(R.string.dark_mode) -> {
                    preference.edit {
                        this.putBoolean("darkMode", true)
                        this.commit()
                    }
                }
            }

            val intent = packageManager.getLaunchIntentForPackage(this.packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            startActivity(mainIntent)
            Runtime.getRuntime().exit(0)

            true
        }

        Glide
            .with(this)
            .load(url)
            .into(image_profile)

        NetworkManager.request(
            Request.Method.GET,
            "v1/users/${NetworkManager.id}/${NetworkManager.password}",
            null,
            {
                val json = it as JSONObject
                val isAdmin = json.getBoolean("isAdmin")

                adminPanel(isAdmin)
            },
            {

            }
        )
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

    private fun dp(dp: Int) = (dp * resources.displayMetrics.density).roundToInt()

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}