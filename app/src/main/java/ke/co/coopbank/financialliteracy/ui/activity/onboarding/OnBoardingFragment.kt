package ke.co.coopbank.financialliteracy.ui.activity.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.databinding.FragmentOnboardingBinding

class OnBoardingFragment : Fragment() {
    private var binding: FragmentOnboardingBinding? = null
    private var position = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titles = resources.getStringArray(R.array.illustration_titles)
        val descriptions = resources.getStringArray(R.array.illustration_descriptions)
        val images = intArrayOf(
            R.drawable.ic_loans_illustration,
            R.drawable.ic_learning_illustration,
            R.drawable.ic_unlock_illustration
        )
        val args = arguments
        if (args != null) {
            position = args.getInt(ARG_POSITION)
        }
        binding!!.imageView.setImageResource(images[position])
        binding!!.titleTv.text = titles[position]
        binding!!.descriptionTv.text = descriptions[position]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val ARG_POSITION = "position"
    }
}