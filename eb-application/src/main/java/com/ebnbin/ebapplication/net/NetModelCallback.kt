package com.ebnbin.ebapplication.net

import com.ebnbin.ebapplication.model.EBModel
import okhttp3.Call

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
    open fun onSuccess(call: Call, model: Model) {}

    /**
     * Called on failure.
     *
     * @see onBegin
     */
    open fun onFailure(call: Call) {}

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
    internal fun success(call: Call, model: Model) {
        preCallbacks.forEach { preCallback -> preCallback.onSuccess(call, model) }

        onSuccess(call, model)

        postCallbacks.forEach { postCallback -> postCallback.onSuccess(call, model) }
    }

    /**
     * @see onFailure
     */
    internal fun failure(call: Call) {
        preCallbacks.forEach { preCallback -> preCallback.onFailure(call) }

        onFailure(call)

        postCallbacks.forEach { postCallback -> postCallback.onFailure(call) }
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
}
