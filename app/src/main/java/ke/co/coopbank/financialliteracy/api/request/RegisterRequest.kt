package ke.co.coopbank.financialliteracy.api.request

data class RegisterRequest(
    val name: String,
    val gender: String,
    val dob: String?,
    val idNumber: String,
    val countyId: String?,
    val saccoId: String?,
    val saccoName: String?,
    val phoneNumber: String?
)