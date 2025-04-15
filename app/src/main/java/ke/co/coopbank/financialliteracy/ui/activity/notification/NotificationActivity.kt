package ke.co.coopbank.financialliteracy.ui.activity.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.api.ViewState
import ke.co.coopbank.financialliteracy.databinding.ActivityNotificationBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        notificationAdapter = NotificationAdapter()
        binding.recyclerView.adapter = notificationAdapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchNotifications()
        }

        observeData()
        fetchNotifications()
    }

    private fun observeData() {
        viewModel.notifications.observe(this) {
            notificationAdapter.submitList(it)
        }
    }

    private fun fetchNotifications() {
        viewModel.queryNotifications()
        viewModel.notificationList.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                is ViewState.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (response.value?.data?.notifications?.isEmpty() == true) {
                        binding.notificationTextView.visibility = View.VISIBLE
                    } else {
                        binding.notificationTextView.visibility = View.GONE
                    }
                }
                is ViewState.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}