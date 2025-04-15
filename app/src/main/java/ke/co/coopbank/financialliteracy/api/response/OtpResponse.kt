package ke.co.coopbank.financialliteracy.api.response

data class OtpResponse(
    val status: Int?,
    val message: String?,
    val otp: String?
)