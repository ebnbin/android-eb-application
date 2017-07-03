package com.ebnbin.ebapplication.net

import com.ebnbin.ebapplication.model.EBModel
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Net callback for getting a model.
 */
abstract class NetModelCallback<Model : EBModel> {
    /**
     * Called on begin.
     *
     * Count of [onBegin]
     * `=` count of [onSuccess] `+` count of [onFailure] `+` count of [onCancel]
     * `=` count of [onEnd].
     *
     * @see onSuccess
     * @see onFailure
     * @see onCancel
     * @see onEnd
     */
    open fun onBegin(call: Call) {}

    /**
     * Called on success.
     *
     * @param model Valid model.
     *
     * @see onBegin
     */
    open fun onSuccess(call: Call, model: Model, response: Response, byteArray: ByteArray) {}

    /**
     * Called on failure.
     *
     * @see onBegin
     */
    open fun onFailure(call: Call, errorCode: Int, e: IOException?, response: Response?) {}

    /**
     * Called on cancel.
     *
     * @see onBegin
     */
    open fun onCancel(call: Call) {}

    /**
     * Called on end.
     *
     * @see onBegin
     */
    open fun onEnd(call: Call) {}

    //*****************************************************************************************************************

    /**
     * Pre-callbacks.
     */
    val preCallbacks = mutableListOf<NetModelCallback<Model>>()
    /**
     * Post-callbacks.
     */
    val postCallbacks = mutableListOf<NetModelCallback<Model>>()

    /**
     * @see onBegin
     */
    internal fun begin(call: Call) {
        preCallbacks.forEach { preCallback -> preCallback.onBegin(call) }

        onBegin(call)

        postCallbacks.forEach { postCallback -> postCallback.onBegin(call) }
    }

    /**
     * @see onSuccess
     */
    internal fun success(call: Call, model: Model, response: Response, byteArray: ByteArray) {
        preCallbacks.forEach { preCallback -> preCallback.onSuccess(call, model, response, byteArray) }

        onSuccess(call, model, response, byteArray)

        postCallbacks.forEach { postCallback -> postCallback.onSuccess(call, model, response, byteArray) }
    }

    /**
     * @see onFailure
     */
    internal fun failure(call: Call, errorCode: Int, e: IOException?, response: Response?) {
        preCallbacks.forEach { preCallback -> preCallback.onFailure(call, errorCode, e, response) }

        onFailure(call, errorCode, e, response)

        postCallbacks.forEach { postCallback -> postCallback.onFailure(call, errorCode, e, response) }
    }

    /**
     * @see onCancel
     */
    internal fun cancel(call: Call) {
        preCallbacks.forEach { preCallback -> preCallback.onCancel(call) }

        onCancel(call)

        postCallbacks.forEach { postCallback -> postCallback.onCancel(call) }
    }

    /**
     * @see onEnd
     */
    internal fun end(call: Call) {
        preCallbacks.forEach { preCallback -> preCallback.onEnd(call) }

        onEnd(call)

        postCallbacks.forEach { postCallback -> postCallback.onEnd(call) }
    }

    //*****************************************************************************************************************

    companion object {
        //*************************************************************************************************************
        // Error codes.

        const val ERROR_CODE_WTF = 0
        const val ERROR_CODE_FAILURE = 1
        const val ERROR_CODE_RESPONSE_NULL = 2
        const val ERROR_CODE_RESPONSE_UNSUCCESSFUL = 3
        const val ERROR_CODE_RESPONSE_BODY_NULL = 4
        const val ERROR_CODE_JSON_SYNTAX_EXCEPTION = 5
        const val ERROR_CODE_CLASS_CAST_EXCEPTION = 6
        const val ERROR_CODE_MODEL_INVALID = 7
    }
}
