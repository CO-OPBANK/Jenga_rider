package ke.co.coopbank.financialliteracy.api.request

data class VerifyOtpRequest(
    val phoneNumber: String,
    val otp: String
)