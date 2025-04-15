package ke.co.coopbank.financialliteracy.ui.activity.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.coopbank.financialliteracy.api.Resource
import ke.co.coopbank.financialliteracy.api.request.ProfileRequest
import ke.co.coopbank.financialliteracy.api.response.PhotoUploadResponse
import ke.co.coopbank.financialliteracy.db.dao.CountyDao
import ke.co.coopbank.financialliteracy.db.dao.CourseDao
import ke.co.coopbank.financialliteracy.db.dao.SaccoDao
import ke.co.coopbank.financialliteracy.model.*
import ke.co.coopbank.financialliteracy.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProfileViewModel @Inject internal constructor(
    courseDao: CourseDao,
    private val saccoDao: SaccoDao,
    private val countyDao: CountyDao,
    private val authRepository: AuthRepository
) : ViewModel() {
    val user: LiveData<User>? = authRepository.getLoggedInUser()
    var userResource: LiveData<Resource<User>>? = null
    var saccosResource: LiveData<Resource<List<Sacco>>>? = null
    var countiesResource: LiveData<Resource<List<County>>>? = null
    var profileResource: LiveData<Resource<Token>>? = null
    var photoResource: LiveData<Resource<PhotoUploadResponse>>? = null
    val onGoingCourses: LiveData<List<Course>> = courseDao.getOngoingCourses().asLiveData()

    fun findSacco(id: Int): LiveData<Sacco> {
        return saccoDao.find(id)
    }

    fun isSaccoTableEmpty(): LiveData<Int> {
        return saccoDao.isTableEmpty()
    }

    fun findCounty(id: Int): LiveData<County> {
        return countyDao.find(id)
    }

    fun isCountyTableEmpty(): LiveData<Int> {
        return countyDao.isTableEmpty()
    }

    fun getUser(phoneNumber: String) {
        userResource = authRepository.getUser(phoneNumber)
    }

    fun getSaccos() {
        saccosResource = authRepository.getSaccos()
    }

    fun getCounties() {
        countiesResource = authRepository.getCounties()
    }

    fun updateProfile(profileRequest: ProfileRequest) {
        profileResource = authRepository.updateProfile(profileRequest)
    }

    fun uploadPhoto(file: File) {
        photoResource = authRepository.uploadPhoto(file)
    }
}