package io.suyong.mphago.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

object NetworkManager {
    const val SERVER_URL = "http://10.0.2.2:3000"
    const val IMAGE_SERVER_URL = "http://mphago.suyong.me/images/"

    private var errorCallback: (any: Exception) -> Unit = {}
    private var queue: RequestQueue? = null

    private var id: String = ""
    private var password: String = ""

    enum class Event(val event: String) {
        ERROR("error")
    }

    fun request(method: Int, url: String, jsonObject: JSONObject?, func: (response: Any?) -> Unit, error: (error: Exception) -> Unit) {
        queue?.let {
            val request = when (method) {
                Request.Method.POST -> JsonObjectRequest(
                    "$SERVER_URL/$url",
                    jsonObject,
                    func,
                    {
                        errorCallback(it)
                        error(it)
                    }
                )
                else -> StringRequest(
                    method,
                    "$SERVER_URL/$url",
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