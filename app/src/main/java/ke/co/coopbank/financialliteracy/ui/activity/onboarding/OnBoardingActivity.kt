package ke.co.coopbank.financialliteracy.ui.activity.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.databinding.ActivityOnboardingBinding
import ke.co.coopbank.financialliteracy.ui.activity.auth.AuthActivity
import ke.co.coopbank.financialliteracy.utilities.USER_ONBOARDED
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity(), OnPageChangeListener {
    private var binding: ActivityOnboardingBinding? = null
    private var position = 0
    private var totalScreens = 0

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        val viewPager = binding!!.pager
        val adapter = OnBoardingAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        viewPager.adapter = adapter
        viewPager.setOnPageChangeListener(this)
        binding!!.dotsIndicator.setViewPager(viewPager)
        totalScreens = adapter.count
        binding!!.buttonNext.setOnClickListener {
            if (totalScreens - 1 == position) {
                goToNext()
            } else {
                viewPager.currentItem = position + 1
            }
        }
        binding!!.buttonSkip.setOnClickListener { goToNext() }
    }

    private fun goToNext() {
        sharedPreferences.edit {
            putBoolean(USER_ONBOARDED, true)
            apply()
        }

        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        this.position = position
        if (position + 1 == totalScreens) {
            binding!!.buttonNext.text = getString(R.string.get_started)
        } else {
            binding!!.buttonNext.text = getString(R.string.next)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
    class OnBoardingAdapter(fm: FragmentManager, behavior: Int) :
        FragmentStatePagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            val fragment: Fragment = OnBoardingFragment()
            val args = Bundle()
            args.putInt(OnBoardingFragment.ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }

        override fun getCount(): Int {
            return 3
        }
    }
}