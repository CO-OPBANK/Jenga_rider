package ke.co.coopbank.financialliteracy.api.response

data class PhotoUploadResponse(
    val message: String,
    val profilePicture: String,
    val status: Int
)