package ke.co.coopbank.financialliteracy.api.response

import com.google.gson.annotations.SerializedName
import ke.co.coopbank.financialliteracy.model.User

data class VerifyOtpResponse(
    @field:SerializedName("existingUserData")
    val user: User?,
    val message: String?,
    val status: Int?,
    val userExists: Boolean?,
    @field:SerializedName("access_token")
    val accessToken: String?
)