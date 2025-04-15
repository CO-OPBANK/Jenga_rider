package ke.co.coopbank.financialliteracy.ui.activity.auth

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.api.request.RegisterRequest
import ke.co.coopbank.financialliteracy.databinding.FragmentCompleteProfileBinding
import ke.co.coopbank.financialliteracy.model.County
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.ui.activity.main.MainActivity
import ke.co.coopbank.financialliteracy.ui.dialog.CountyDialog
import ke.co.coopbank.financialliteracy.ui.dialog.SaccoDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CompleteProfileFragment : Fragment(), SaccoDialog.OnSaccoListener,
    CountyDialog.OnCountyListener {

    private lateinit var binding: FragmentCompleteProfileBinding

    private val args: CompleteProfileFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null
    private var phoneNumber: String? = null
    private var genders = arrayOf("Male", "Female")
    private var gender: String? = null
    private var dob: String? = null
    private var saccoId: String? = null
    private var saccoName: String? = null
    private var countyId: String? = null
    private var saccos: List<Sacco>? = null
    private var counties: List<County>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(view.context)
        progressDialog!!.setMessage("Creating account. Please wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)

        val indicator = binding.indicator
        indicator.createIndicators(3, 2)

        phoneNumber = args.phoneNumber

        binding.buttonComplete.setOnClickListener {
            it.hideKeyboard()
            registerUser()
        }

        binding.arrowGenderImageView.setOnClickListener {
            showGenderPicker(it)
        }
        binding.genderLayout.setOnClickListener {
            showGenderPicker(it)
        }

        binding.ageImageView.setOnClickListener {
            showDatePicker(it)
        }
        binding.dobLayout.setOnClickListener {
            showDatePicker(it)
        }

        binding.arrowSaccoImageView.setOnClickListener {
            openSaccoPicker(it)
        }
        binding.saccosLayout.setOnClickListener {
            openSaccoPicker(it)
        }

        binding.arrowCountyImageView.setOnClickListener {
            openCountyPicker(it)
        }
        binding.countyLayout.setOnClickListener {
            openCountyPicker(it)
        }

        getSaccos()
        getCounties()
    }

    private fun showGenderPicker(it: View) {
        it.hideKeyboard()
        showGenderDialogPicker()
    }

    private fun showDatePicker(it: View) {
        it.hideKeyboard()
        showDobPickerDialog()
    }

    private fun openCountyPicker(it: View) {
        it.hideKeyboard()
        CountyDialog.newInstance(counties).show(childFragmentManager, CountyDialog.TAG)
    }

    private fun openSaccoPicker(it: View) {
        it.hideKeyboard()
        SaccoDialog.newInstance(saccos).show(childFragmentManager, SaccoDialog.TAG)
    }

    private fun registerUser() {
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

        if (!TextUtils.isEmpty(binding.saccoNameEditText.text)) {
            saccoName = binding.saccoNameEditText.text.toString();
        }

        viewModel.register(
            RegisterRequest(
                name = binding.nameEditText.text.toString(),
                gender = gender?.first().toString(),
                dob = dob,
                idNumber = binding.idNumberEditText.text.toString(),
                countyId = countyId,
                saccoId = saccoId,
                saccoName = saccoName,
                phoneNumber = phoneNumber
            )
        )

        viewModel.registerResource?.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "Account created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog!!.dismiss()
                    getUser()
                }
                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getUser() {
        phoneNumber?.let { viewModel.getUser(it) }
        viewModel.userResource?.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.setMessage("Please wait...")
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    progressDialog!!.dismiss()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getCounties() {
        viewModel.getCounties()
        viewModel.countiesResource?.observe(viewLifecycleOwner) { counties = it.data }
    }

    private fun getSaccos() {
        viewModel.getSaccos()
        viewModel.saccosResource?.observe(viewLifecycleOwner) { saccos = it.data }
    }

    private fun showGenderDialogPicker() {
        val builder = AlertDialog.Builder(requireContext())
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
            requireContext(), { _, year, month, day ->
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
        this.saccoId = sacco?.id.toString()
        binding.saccoTextView.text = sacco?.name ?: ""
    }

    override fun onCounty(county: County?) {
        this.countyId = county?.id.toString()
        binding.countyTextView.text = county?.name ?: ""
    }
}