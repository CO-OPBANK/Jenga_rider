package ke.co.coopbank.financialliteracy.utilities

import android.content.res.Resources
import com.google.gson.Gson
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.response.VerifyOtpResponse

object Bot {

    fun user(resources: Resources): VerifyOtpResponse {
        val jsonData = readRawJson(resources)
        return Gson().fromJson(jsonData, VerifyOtpResponse::class.java)
    }

    private fun readRawJson(resources: Resources): String {
        val bufferReader = resources.openRawResource(R.raw.bot).bufferedReader()
        return bufferReader.use {
            it.readText()
        }
    }
}