package ke.co.coopbank.financialliteracy.ui.activity.notification

import androidx.lifecycle.*
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.coopbank.financialliteracy.NotificationsQuery
import ke.co.coopbank.financialliteracy.api.ViewState
import ke.co.coopbank.financialliteracy.db.dao.NotificationDao
import ke.co.coopbank.financialliteracy.model.Notification
import ke.co.coopbank.financialliteracy.repository.CourseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class NotificationViewModel @Inject internal constructor(
    private val courseRepository: CourseRepository,
    private val notificationDao: NotificationDao,
) : ViewModel() {

    val notifications: LiveData<List<Notification>> =
        notificationDao.getNotifications().asLiveData()

    private val _notificationList by lazy { MutableLiveData<ViewState<Response<NotificationsQuery.Data>>>() }
    val notificationList: LiveData<ViewState<Response<NotificationsQuery.Data>>>
        get() = _notificationList

    fun queryNotifications() = viewModelScope.launch {
        _notificationList.postValue(ViewState.Loading())
        try {
            val response = courseRepository.queryNotifications()
            notificationDao.delete()
            response.data?.notifications?.forEach {
                notificationDao.insert(
                    Notification(
                        id = 0,
                        title = it?.title,
                        content = it?.content,
                        createdAt = it?.created_at.toString(),
                    )
                )
            }
            _notificationList.postValue(ViewState.Success(response))
        } catch (e: ApolloException) {
            Timber.d(e, "Failure")
            _notificationList.postValue(ViewState.Error("Error fetching notifications"))
        }
    }
}