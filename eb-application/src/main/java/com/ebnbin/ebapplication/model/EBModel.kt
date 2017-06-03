package com.ebnbin.ebapplication.model

import com.google.gson.Gson
import java.io.Serializable

/**
 * Base model.
 */
abstract class EBModel : Serializable {
    /**
     * Checks and returns whether data of current model is valid.
     */
    abstract val isValid: Boolean

    /**
     * Parses current model to Json.
     *
     * @return Json string.
     */
    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}
