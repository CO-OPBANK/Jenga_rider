package ke.co.coopbank.financialliteracy.ui.activity.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.api.ViewState
import ke.co.coopbank.financialliteracy.databinding.ActivityMainBinding
import ke.co.coopbank.financialliteracy.databinding.NavHeaderMainBinding
import ke.co.coopbank.financialliteracy.model.User
import ke.co.coopbank.financialliteracy.ui.activity.AboutActivity
import ke.co.coopbank.financialliteracy.ui.activity.auth.AuthActivity
import ke.co.coopbank.financialliteracy.ui.activity.main.adapter.CourseAdapter
import ke.co.coopbank.financialliteracy.ui.activity.main.adapter.OngoingCoursesAdapter
import ke.co.coopbank.financialliteracy.ui.activity.notification.NotificationActivity
import ke.co.coopbank.financialliteracy.ui.activity.profile.ProfileActivity
import ke.co.coopbank.financialliteracy.utilities.ACCESS_TOKEN
import ke.co.coopbank.financialliteracy.utilities.CURRENT_USER_PHONE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var ongoingCourseAdapter: OngoingCoursesAdapter
    private lateinit var pagerSnapHelper: PagerSnapHelper
    private val viewModel: MainViewModel by viewModels()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navigationView = binding.navView
        navHeaderMainBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.nav_header_main,
            binding.navView,
            true
        )

        val drawer = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        courseAdapter = CourseAdapter()
        binding.upcomingRecyclerView.adapter = courseAdapter

        ongoingCourseAdapter = OngoingCoursesAdapter()
        binding.ongoingRecyclerView.adapter = ongoingCourseAdapter

        pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.ongoingRecyclerView)

        observeData()
        fetchCourses()
    }

    private fun observeData() {
        viewModel.user?.observe(this) {
            this.user = it
            navHeaderMainBinding.user = user
            binding.user = user
            binding.complete = user.completedCourses == user.totalCourses
            binding.executePendingBindings()
            fetchTrophy()
        }

        viewModel.courses.observe(this) { courses ->
            courseAdapter.submitList(courses)
        }

        viewModel.onGoingCourses.observe(this) {
            if (it.isEmpty()) {
                binding.ongoingTV.visibility = View.GONE
                binding.indicator.visibility = View.GONE
                binding.ongoingRecyclerView.visibility = View.GONE
            } else {
                binding.ongoingTV.visibility = View.VISIBLE
                binding.indicator.visibility = View.VISIBLE
                binding.ongoingRecyclerView.visibility = View.VISIBLE
                ongoingCourseAdapter.submitList(it)
                binding.indicator.attachToRecyclerView(binding.ongoingRecyclerView, pagerSnapHelper)
            }
        }
    }

    private fun fetchCourses() {
        viewModel.queryCourses()
        viewModel.coursesList.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {

                }
                is ViewState.Success -> {
                    fetchCoursesProgress()
                }
                is ViewState.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchCoursesProgress() {
        viewModel.getCoursesProgress(userId = user.id.toString())
        viewModel.coursesProgressResource?.observe(this) { }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_logout -> {
                logout()
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        return true
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Log out")
            .setMessage("Exit app?")
            .setPositiveButton("No") { dialog, _ ->
                dialog.dismiss()
            }.setNegativeButton("Yes") { dialog, _ ->
                sharedPreferences.edit {
                    putString(ACCESS_TOKEN, null)
                    putString(CURRENT_USER_PHONE, null)
                    apply()
                }
                startActivity(Intent(this, AuthActivity::class.java))
                finishAffinity()
                dialog.dismiss()
            }.show()
    }

    private fun fetchTrophy() {
        viewModel.getTrophy(userId = user.id.toString())
        viewModel.trophyResource?.observe(this) {
            if (it?.status == Status.SUCCESS) {
                val user = it.data

                var percentageProgress = 0

                if (user?.completedCourses != null) {
                    val progress = user.completedCourses / user.totalCourses!! * 100
                    percentageProgress = progress
                }

                binding.user = user
                binding.percentageProgress = percentageProgress
                binding.complete = user!!.completedCourses == user.totalCourses
                binding.executePendingBindings()
            }
        }
    }
}