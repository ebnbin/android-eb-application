package com.ebnbin.ebapplication.net

import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import android.util.ArraySet
import com.ebnbin.eb.util.EBRuntimeException
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.model.EBModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException
import java.lang.reflect.ParameterizedType

/**
 * Helper for net.
 *
 * 网络请求帮助类. 使用 [NetHelper.get] 发起一次异步 get 请求, [NetModelCallback] 的回调规则为: 调用 [NetHelper.get] 后立即回调
 * [NetModelCallback.onBegin], 如果网络请求成功, 回调 [NetModelCallback.onSuccess], 如果网络请求失败或发生其他异常, 回调
 * [NetModelCallback.onFailure], 如果因 UI 销毁导致网络请求取消, 回调 [NetModelCallback.onCancel],
 * 以上三个方法在一次网络请求中必定且只会回调一次, 之后都会立即回调 [NetModelCallback.onEnd].
 */
class NetHelper private constructor() {
    //*****************************************************************************************************************
    // Calls.

    /**
     * Saved [Call]s.
     */
    private val calls = ArrayMap<Any, ArraySet<Call>>()

    /**
     * Add a [Call].
     */
    private fun addCall(call: Call) {
        val tag = call.request()?.tag() ?: return

        var callSet: ArraySet<Call>? = calls[tag]
        if (callSet == null) {
            callSet = ArraySet()
            calls.put(tag, callSet)
        }

        callSet.add(call)
    }

    /**
     * Removes a [Call].
     */
    private fun removeCall(call: Call) {
        val tag = call.request()?.tag() ?: return

        val callSet = calls[tag] ?: return

        callSet.remove(call)
    }

    /**
     * Whether contains the [Call].
     */
    private fun containsCall(call: Call) : Boolean {
        val tag = call.request()?.tag() ?: return false

        val callSet = calls[tag] ?: return false

        return callSet.contains(call)
    }

    /**
     * Cancels and removes [Call]s by tag.
     */
    fun cancelCalls(tag: Any) {
        val callSet = calls.remove(tag) ?: return

        callSet.forEach { call -> call.cancel() }
    }

    //*****************************************************************************************************************

    private val okHttpClient = OkHttpClient()

    private val handler = Handler(Looper.getMainLooper())

    /**
     * Gets a url async.
     *
     * @param tag A fragment tag.
     * @param callback Callback for getting a model.
     *
     * @return Current [Call].
     */
    fun <Model : EBModel> get(tag: Any, url: String, callback: NetModelCallback<Model>): Call {
        val request = Request
                .Builder()
                .tag(tag)
                .url(url)
                .build()
        val call = okHttpClient.newCall(request)

        callback.begin(call)

        addCall(call)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                postFailure(NetModelCallback.ERROR_CODE_FAILURE, e, null)
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (response == null) {
                    postFailure(NetModelCallback.ERROR_CODE_RESPONSE_NULL, null, response)

                    return
                }

                if (!response.isSuccessful) {
                    postFailure(NetModelCallback.ERROR_CODE_RESPONSE_UNSUCCESSFUL, null, response)

                    return
                }

                val responseBody = response.body()
                if (responseBody == null) {
                    postFailure(NetModelCallback.ERROR_CODE_RESPONSE_BODY_NULL, null, response)

                    return
                }

                val responseByteArray = responseBody.bytes() ?: byteArrayOf()

                val gson = Gson()
                val responseString = String(responseByteArray)
                val type = (callback.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

                val model: Model?
                try {
                    model = gson.fromJson<Model>(responseString, type)
                } catch (e: JsonSyntaxException) {
                    EBUtil.log(e)

                    postFailure(NetModelCallback.ERROR_CODE_JSON_SYNTAX_EXCEPTION, null, response)

                    return
                }

                if (model == null || !model.isValid) {
                    postFailure(NetModelCallback.ERROR_CODE_MODEL_INVALID, null, response)

                    return
                }

                postSuccess(model, response, responseByteArray)
            }

            /**
             * Posts success or cancel.
             */
            private fun postSuccess(model: Model, response: Response, byteArray: ByteArray) {
                handler.post {
                    if (!containsCall(call)) {
                        callback.cancel(call)
                        callback.end(call)

                        return@post
                    }

                    callback.success(call, model, response, byteArray)

                    removeCall(call)

                    callback.end(call)
                }
            }

            /**
             * Posts failure or cancel.
             */
            private fun postFailure(errorCode: Int, e: IOException?, response: Response?) {
                handler.post {
                    if (!containsCall(call)) {
                        callback.cancel(call)
                        callback.end(call)

                        return@post
                    }

                    callback.failure(call, errorCode, e, response)

                    removeCall(call)

                    callback.end(call)
                }
            }
        })

        return call
    }

    //*****************************************************************************************************************

    companion object {
        private var singleton: NetHelper? = null

        fun init() {
            if (singleton != null) {
                throw EBRuntimeException()
            }

            singleton = NetHelper()
        }

        val instance: NetHelper
            get() {
                if (singleton == null) {
                    throw EBRuntimeException()
                }

                return singleton!!
            }
    }
}
