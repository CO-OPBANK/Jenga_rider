package ke.co.coopbank.financialliteracy.model

import com.google.gson.annotations.SerializedName

data class Token(
    @field:SerializedName("access_token")
    val accessToken: String
)