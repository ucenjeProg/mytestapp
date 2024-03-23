package com.mycustomappapply.wotttoo.data.local.jsonlocal

import android.content.Context
import java.io.IOException

fun Context.readJsonFromAssets(
    fileName: String
): String? {

    val jsonString: String
    try {
        jsonString = this.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }

    return jsonString
}
