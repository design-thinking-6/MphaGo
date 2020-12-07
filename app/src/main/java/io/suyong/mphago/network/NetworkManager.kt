package io.suyong.mphago.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.lang.Exception

object NetworkManager {
    private const val SERVER_URL = ""
    private var errorCallback: (any: Exception) -> Unit = {}
    private var queue: RequestQueue? = null

    enum class Event(val event: String) {
        ERROR("error")
    }

    enum class Operation(val method: Int, val url: String) {
        CREATE_USER(Request.Method.POST, "/v1/users"),
        READ_USER(Request.Method.GET, "/v1/users"),
        UPDATE_USER(Request.Method.PATCH, "/v1/users"),
        DELETE_USER(Request.Method.DELETE, "/v1/users")
    }

    fun request(operation: Operation, func: (response: String?) -> Unit) {
        queue?.let {
            val request = StringRequest(
                operation.method,
                "$SERVER_URL${operation.url}",
                func,
                errorCallback
            )

            it.add(request)
        }
    }

    fun init(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun on(event: Event, callback: (any: Any) -> Unit) = on(event.event, callback)
    fun on(event: String, callback: (any: Any) -> Unit) = when(event) {
        Event.ERROR.event -> onError(callback)
        else -> Unit
    }

    fun onError(callback: (any: Exception) -> Unit) {
        errorCallback = callback
    }
}