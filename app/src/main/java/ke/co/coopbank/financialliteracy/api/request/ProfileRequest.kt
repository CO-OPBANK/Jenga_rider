package ke.co.coopbank.financialliteracy.api.request

data class ProfileRequest(
    val countyId: Int,
    val dob: String,
    val gender: String,
    val idNumber: String,
    val name: String,
    val phoneNumber: String,
    val saccoId: Int
)