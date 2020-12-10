package io.suyong.mphago.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

object NetworkManager {
    const val SERVER_URL = "http://mphago.suyong.me"
    const val IMAGE_SERVER_URL = "http://mphago.suyong.me/images/"

    var id: String = ""
    var password: String = ""

    private var errorCallback: (any: Exception) -> Unit = {}
    private var queue: RequestQueue? = null

    enum class Event(val event: String) {
        ERROR("error")
    }

    fun request(method: Int, url: String, jsonObject: Any?, func: (response: Any?) -> Unit, error: (error: Exception) -> Unit, isArray: Boolean = false) {
        queue?.let {
            val request = when (isArray) {
                true -> JsonArrayRequest(
                    method,
                    "$SERVER_URL/$url",
                    jsonObject as JSONArray?,
                    func,
                    {
                        errorCallback(it)
                        error(it)
                    }
                )
                else -> JsonObjectRequest(
                    method,
                    "$SERVER_URL/$url",
                    jsonObject as JSONObject?,
                    func,
                    {
                        errorCallback(it)
                        error(it)
                    }
                )
            }

            it.add(request)
        }
    }

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun on(event: Event, callback: (any: Any) -> Unit) = on(event.event, callback)
    fun on(event: String, callback: (any: Any) -> Unit) = when (event) {
        Event.ERROR.event -> onError(callback)
        else -> Unit
    }

    fun onError(callback: (any: Exception) -> Unit) {
        errorCallback = callback
    }
}