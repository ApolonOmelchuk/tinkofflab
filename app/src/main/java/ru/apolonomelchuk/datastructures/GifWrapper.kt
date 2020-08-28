package ru.apolonomelchuk.datastructures

import org.json.JSONException
import org.json.JSONObject

class GifWrapper(content: String){
    val gifUrl : String
    val description: String

    init {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(content)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        gifUrl = jsonObject!!.getString("gifURL")
        description = jsonObject.getString("description")
    }
}