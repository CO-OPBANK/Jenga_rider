package ke.co.coopbank.financialliteracy.ui.activity.auth

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.databinding.FragmentVerifyOtpBinding
import ke.co.coopbank.financialliteracy.ui.activity.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class VerifyOtpFragment : Fragment() {

    private lateinit var binding: FragmentVerifyOtpBinding
    private val args: VerifyOtpFragmentArgs by navArgs()
    private val viewModel: AuthViewModel by viewModels()
    private var progressDialog: ProgressDialog? = null
    private var phoneNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneNumber = args.phoneNumber
        binding.phoneNumber = phoneNumber

        progressDialog = ProgressDialog(view.context)
        progressDialog!!.setCanceledOnTouchOutside(false)

        binding.wrongNumberTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.buttonVerify.setOnClickListener { verifyOtp() }
        binding.resendTextView.setOnClickListener { resendOtp() }

        binding.optEditText1.addTextChangedListener(GenericTextWatcher(binding.optEditText1))
        binding.optEditText2.addTextChangedListener(GenericTextWatcher(binding.optEditText2))
        binding.optEditText3.addTextChangedListener(GenericTextWatcher(binding.optEditText3))
        binding.optEditText4.addTextChangedListener(GenericTextWatcher(binding.optEditText4))
    }

    private fun resendOtp() {
        phoneNumber?.let { viewModel.sendOtp(it) }
        viewModel.otpResource?.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog?.setMessage("Resending OTP. Please wait...")
                    progressDialog!!.show()
                }

                Status.SUCCESS -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
                }

                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun verifyOtp() {
        val one = binding.optEditText1.text.toString()
        val two = binding.optEditText2.text.toString()
        val three = binding.optEditText3.text.toString()
        val four = binding.optEditText4.text.toString()
        val otp = "$one$two$three$four"

        if (otp.isEmpty()) {
            Snackbar.make(binding.container, "OTP is required", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (otp.length < 4) {
            Snackbar.make(binding.container, "Invalid OTP", Snackbar.LENGTH_SHORT).show()
            return
        }

        phoneNumber?.let { viewModel.verifyOtp(it, otp) }
        viewModel.verifyOtpResource?.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog?.setMessage("Verifying OTP. Please wait...")
                    progressDialog!!.show()
                }

                Status.SUCCESS -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
                    if (it.data?.status == 0) {
                        if (it.data.userExists == true) {
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            val bundle = bundleOf("phoneNumber" to phoneNumber)
                            NavHostFragment.findNavController(this)
                                .navigate(
                                    R.id.action_VerifyOtpFragment_to_CompleteProfileFragment,
                                    bundle
                                )
                        }
                    }
                }

                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    inner class GenericTextWatcher internal constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        @SuppressLint("NonConstantResourceId")
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.optEditText1 -> if (text.length == 1) binding.optEditText2.requestFocus()
                R.id.optEditText2 -> if (text.length == 1) binding.optEditText3.requestFocus() else if (text.isEmpty()) binding.optEditText1.requestFocus()
                R.id.optEditText3 -> if (text.length == 1) binding.optEditText4.requestFocus() else if (text.isEmpty()) binding.optEditText2.requestFocus()
                R.id.optEditText4 -> if (text.isEmpty()) binding.optEditText3.requestFocus()
            }
        }
    }
}