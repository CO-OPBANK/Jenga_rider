package ke.co.coopbank.financialliteracy.ui.activity.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.net.toFile
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.databinding.ActivityProfileBinding
import ke.co.coopbank.financialliteracy.ui.activity.auth.AuthActivity
import ke.co.coopbank.financialliteracy.ui.activity.main.adapter.OngoingCoursesAdapter
import ke.co.coopbank.financialliteracy.utilities.ACCESS_TOKEN
import ke.co.coopbank.financialliteracy.utilities.CURRENT_USER_PHONE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityProfileBinding
    private lateinit var ongoingCourseAdapter: OngoingCoursesAdapter
    private lateinit var phoneNumber: String
    private val viewModel: ProfileViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        phoneNumber = sharedPreferences.getString(CURRENT_USER_PHONE, null).toString()
        ongoingCourseAdapter = OngoingCoursesAdapter()
        binding.ongoingRecyclerView.adapter = ongoingCourseAdapter
        observeData()

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.changePhotoImageView.setOnClickListener { capturePhoto() }
        binding.imageView.setOnClickListener { capturePhoto() }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading profile photo. Please wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)

        viewModel.isSaccoTableEmpty().observe(this) {
            if (it == 0) {
                getSaccos()
            }
        }

        viewModel.isCountyTableEmpty().observe(this) {
            if (it == 0) {
                getCounties()
            }
        }

        getUser()
    }

    private fun capturePhoto() {
        ImagePicker.with(this)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .crop()
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            ).start()
    }

    private fun getSaccos() {
        viewModel.getSaccos()
        progressDialog?.setMessage("Fetching saccos. Please wait...")
        viewModel.saccosResource?.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    progressDialog!!.dismiss()
                }
                Status.ERROR -> {
                    progressDialog!!.dismiss()
                }
            }
        }
    }

    private fun getCounties() {
        viewModel.getCounties()
        progressDialog?.setMessage("Fetching counties. Please wait...")
        viewModel.countiesResource?.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    progressDialog!!.dismiss()
                }
                Status.ERROR -> {
                    progressDialog!!.dismiss()
                }
            }
        }
    }

    private fun observeData() {
        viewModel.user?.observe(this) {
            if (it != null) {
                val names = it.name!!.split("\\s".toRegex())
                binding.firstName = names.first()
                binding.lastName = names.last()
                binding.user = it

                it.saccoId?.let { it1 -> getSacco(it1) }
                it.countyId?.let { it1 -> getCounty(it1) }
            }
        }

        viewModel.onGoingCourses.observe(this) {
            if (it.isEmpty()) {
                binding.ongoingTv.visibility = View.GONE
                binding.ongoingDesc.visibility = View.GONE
                binding.ongoingRecyclerView.visibility = View.GONE
            } else {
                binding.ongoingTv.visibility = View.VISIBLE
                binding.ongoingDesc.visibility = View.VISIBLE
                binding.ongoingRecyclerView.visibility = View.VISIBLE
                ongoingCourseAdapter.submitList(it)
            }
        }
    }

    private fun getSacco(id: Int) {
        viewModel.findSacco(id).observe(this) {
            if (it != null) {
                binding.sacco = it
            }
        }
    }

    private fun getCounty(id: Int) {
        viewModel.findCounty(id).observe(this) {
            if (it != null) {
                binding.county = it
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_logout) {
            logout()
            true
        } else super.onOptionsItemSelected(item)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri: Uri = data?.data!!
                binding.imageView.setImageURI(fileUri)
                uploadPhoto(fileUri.toFile())
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadPhoto(file: File) {
        viewModel.uploadPhoto(file)
        viewModel.photoResource?.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    Toast.makeText(this, "Profile photo successfully", Toast.LENGTH_SHORT).show()
                    progressDialog!!.dismiss()
                    finish()
                }
                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getUser() {
        phoneNumber.let { viewModel.getUser(it) }
        viewModel.userResource?.observe(this) {

        }
    }
}