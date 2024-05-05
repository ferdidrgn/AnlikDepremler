package com.ferdidrgn.anlikdepremler.presentation.main.home

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.ferdidrgn.anlikdepremler.util.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentHomeBinding
import com.ferdidrgn.anlikdepremler.util.helpers.ToMain
import com.ferdidrgn.anlikdepremler.data.remote.dto.HomeSliderDto
import com.ferdidrgn.anlikdepremler.util.handler.NavHandler
import com.ferdidrgn.anlikdepremler.util.helpers.getPositionAndSendHandler2
import com.ferdidrgn.anlikdepremler.util.helpers.ioScope
import com.ferdidrgn.anlikdepremler.presentation.main.MainActivity
import com.ferdidrgn.anlikdepremler.presentation.main.MainViewModel
import com.ferdidrgn.anlikdepremler.util.handler.MainSliderHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<MainViewModel, FragmentHomeBinding>() {

    private lateinit var handler: MainSliderHandler

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()
    override fun getDataBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        setAds(binding.adView)
        binding.viewModel = viewModel
        binding.homeSliderAdapter = HomeSliderHorizontalAdapter(viewModel)
        binding.topTenEarthquakeAdapter = TopTenEarthquakeAdapter(viewModel)
        binding.topTenLocationEarthquakeAdapter = TopTenLocationEarthquakeAdapter(viewModel)

        handler = MainSliderHandler()
        observe()
    }

    private fun observe() {
        viewModel.apply {
            getHomePage()

            homeSliderList.observe(viewLifecycleOwner) { homeSliderList ->
                ioScope {
                    binding.apply {
                        homeSliderAdapter?.differ?.submitList(homeSliderList as List<HomeSliderDto?>?)
                        if (homeSliderList != null)
                            indicator.count = homeSliderList.size

                        vpSlider.getPositionAndSendHandler2(homeSliderList, handler) {
                            indicator.selection = it
                        }
                    }
                }
            }

            clickSeeAllNowEarthquake.observe(viewLifecycleOwner) {
                NavHandler.instance.toMainActivity(
                    requireActivity() as MainActivity, ToMain.NowEarthquake
                )
            }

            clickSeeAllLocationEarthquake.observe(viewLifecycleOwner) {
                NavHandler.instance.toMainActivity(
                    requireActivity() as MainActivity, ToMain.NowEarthquake
                )
            }

            binding.csChooseCityEarthquake.changeableText { isTrue ->
                if (isTrue)
                    getTopTenLocationEarthquake()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setAds(binding.adView)
        binding.indicator.selection = 0
        handler.addChanging()
    }

    override fun onPause() {
        super.onPause()
        handler.removeChanging()
    }
}