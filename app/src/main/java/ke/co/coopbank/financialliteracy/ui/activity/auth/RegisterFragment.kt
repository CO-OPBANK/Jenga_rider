package ke.co.coopbank.financialliteracy.ui.activity.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.databinding.FragmentRegisterBinding
import ke.co.coopbank.financialliteracy.ui.activity.main.MainActivity
import ke.co.coopbank.financialliteracy.utilities.BOT_PHONE
import ke.co.coopbank.financialliteracy.utilities.Bot
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private var progressDialog: ProgressDialog? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(view.context)
        progressDialog!!.setMessage("Sending OTP. Please wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)

        binding.buttonSignUp.setOnClickListener {
            it.hideKeyboard()
            validatePhone()
        }
        binding.sigInLayout.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }

        val indicator = binding.indicator
        indicator.createIndicators(3, 0)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun validatePhone() {
        if (TextUtils.isEmpty(binding.phoneEditText.text)) {
            binding.phoneEditText.error = "Phone number is required"
            Snackbar.make(binding.container, "Phone number is required", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        val phoneNumber = binding.phoneEditText.text.toString()

        if (phoneNumber == BOT_PHONE) {
            val user = Bot.user(resources)
            viewModel.setBotAccount(user)

            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }

        if (phoneNumber.length < 10 || phoneNumber.length > 10) {
            binding.phoneEditText.error = "Phone length is invalid"
            Snackbar.make(binding.container, "Phone length is invalid", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            binding.phoneEditText.error = "Phone number is invalid"
            Snackbar.make(binding.container, "Phone number is invalid", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        viewModel.sendOtp(phoneNumber)
        viewModel.otpResource?.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }

                Status.SUCCESS -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
                    if (it.data?.status == 0) {
                        val bundle = bundleOf(
                            "phoneNumber" to phoneNumber,
                            "otp" to it.data.otp
                        )

                        NavHostFragment.findNavController(this)
                            .navigate(R.id.action_RegisterFragment_to_VerifyPhoneFragment, bundle)
                    }
                }

                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}