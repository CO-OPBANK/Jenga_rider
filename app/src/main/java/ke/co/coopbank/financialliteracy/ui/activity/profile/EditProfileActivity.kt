package ke.co.coopbank.financialliteracy.ui.activity.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.api.request.ProfileRequest
import ke.co.coopbank.financialliteracy.databinding.ActivityEditProfileBinding
import ke.co.coopbank.financialliteracy.model.County
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.ui.dialog.CountyDialog
import ke.co.coopbank.financialliteracy.ui.dialog.SaccoDialog
import ke.co.coopbank.financialliteracy.utilities.CURRENT_USER_PHONE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class EditProfileActivity : AppCompatActivity(), SaccoDialog.OnSaccoListener,
    CountyDialog.OnCountyListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var phoneNumber: String
    private val viewModel: ProfileViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null
    private var genders = arrayOf("Male", "Female")
    private var gender: String? = null
    private var dob: String? = null
    private var saccoId: Int? = null
    private var countyId: Int? = null
    private var saccos: List<Sacco>? = null
    private var counties: List<County>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        phoneNumber = sharedPreferences.getString(CURRENT_USER_PHONE, null).toString()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Updating personal details. Please wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)

        binding.buttonComplete.setOnClickListener {
            it.hideKeyboard()
            updateUser()
        }


        binding.arrowGenderImageView.setOnClickListener {
            showGenderPicker(it)
        }
        binding.genderLayout.setOnClickListener {
            showGenderPicker(it)
        }

        binding.arrowAgeImageView.setOnClickListener {
            showDobPicker(it)
        }
        binding.dobLayout.setOnClickListener {
            showDobPicker(it)
        }

        binding.arrowSaccoImageView.setOnClickListener {
            showCountyPicker(it)
        }
        binding.saccosLayout.setOnClickListener {
            showCountyPicker(it)
        }

        binding.arrowCountyImageView.setOnClickListener {
            openCountyPicker(it)
        }
        binding.countyLayout.setOnClickListener {
            openCountyPicker(it)
        }

        observeData()
        getSaccos()
        getCounties()
    }

    private fun showGenderPicker(it: View) {
        it.hideKeyboard()
        showGenderDialogPicker()
    }

    private fun showDobPicker(it: View) {
        it.hideKeyboard()
        showDobPickerDialog()
    }

    fun showCountyPicker(it: View) {
        it.hideKeyboard()
        SaccoDialog.newInstance(saccos).show(supportFragmentManager, SaccoDialog.TAG)
    }

    private fun openCountyPicker(it: View) {
        it.hideKeyboard()
        CountyDialog.newInstance(counties).show(supportFragmentManager, CountyDialog.TAG)
    }

    private fun observeData() {
        viewModel.user?.observe(this) {
            if (it != null) {
                binding.user = it
                gender = it.gender
                dob = it.dob
                saccoId = it.saccoId
                countyId = it.countyId
            }
        }
    }

    private fun updateUser() {
        if (TextUtils.isEmpty(binding.nameEditText.text)) {
            binding.nameEditText.error = "Name is required"
            return
        }

        if (TextUtils.isEmpty(gender)) {
            Snackbar.make(binding.container, "Gender is required", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(dob)) {
            Snackbar.make(binding.container, "Date of Birth is required", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        if (TextUtils.isEmpty(binding.idNumberEditText.text)) {
            binding.idNumberEditText.error = "National ID is required"
            return
        }

        if (countyId == null) {
            Snackbar.make(binding.container, "Please select your county", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        viewModel.updateProfile(
            ProfileRequest(
                name = binding.nameEditText.text.toString(),
                gender = gender?.first().toString(),
                dob = dob ?: "",
                idNumber = binding.idNumberEditText.text.toString(),
                countyId = countyId ?: 0,
                saccoId = saccoId ?: 0,
                phoneNumber = phoneNumber
            )
        )

        viewModel.profileResource?.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
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

    private fun getCounties() {
        viewModel.getCounties()
        viewModel.countiesResource?.observe(this) {
            counties = it.data
            val county: County? = counties?.find { county -> county.id == countyId }
            binding.countyTextView.text = county?.name ?: ""
        }
    }

    private fun getSaccos() {
        viewModel.getSaccos()
        viewModel.saccosResource?.observe(this) {
            saccos = it.data
            val sacco: Sacco? = saccos?.find { sacco -> sacco.id == saccoId }
            binding.saccoTextView.text = sacco?.name ?: ""
        }
    }

    private fun showGenderDialogPicker() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Choose Gender")
            .setItems(R.array.genders) { dialog, which ->
                gender = genders[which]
                binding.genderTextView.text = gender
                dialog.dismiss()
            }
        builder.create()
        builder.show()
    }

    private fun showDobPickerDialog() {
        val dobDialog = DatePickerDialog(
            this, { _, year, month, day ->
                dob = String.format("%02d-%02d-%02d", year, (month + 1), day)
                binding.dobTextView.text = dob
            }, 1990, 0, 1
        )
        dobDialog.show()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onSacco(sacco: Sacco?) {
        this.saccoId = sacco?.id
        binding.saccoTextView.text = sacco?.name ?: ""
    }

    override fun onCounty(county: County?) {
        this.countyId = county?.id
        binding.countyTextView.text = county?.name ?: ""
    }
}