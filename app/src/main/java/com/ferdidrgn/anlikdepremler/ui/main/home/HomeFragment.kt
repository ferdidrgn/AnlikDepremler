package com.ferdidrgn.anlikdepremler.ui.main.home

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.ferdidrgn.anlikdepremler.base.BaseFragment
import com.ferdidrgn.anlikdepremler.base.Err
import com.ferdidrgn.anlikdepremler.databinding.FragmentHomeBinding
import com.ferdidrgn.anlikdepremler.model.HomeSliderData
import com.ferdidrgn.anlikdepremler.tools.builderADS
import com.ferdidrgn.anlikdepremler.tools.getPositionAndSendHandler2
import com.ferdidrgn.anlikdepremler.tools.helpers.MainSliderHandler
import com.ferdidrgn.anlikdepremler.tools.ioScope
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.ferdidrgn.anlikdepremler.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<MainViewModel, FragmentHomeBinding>() {

    private lateinit var handler: MainSliderHandler
    override fun getVM(): Lazy<MainViewModel> = activityViewModels()
    override fun getDataBinding(): FragmentHomeBinding =
        FragmentHomeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        builderADS(requireContext(), binding.adView)
        binding.viewModel = viewModel
        binding.homeSliderAdapter = HomeSliderHorizontalAdapter(viewModel)

        handler = MainSliderHandler()

        observe()

    }

    private fun observe() {
        viewModel.apply {
            getHomeSlider()

            homeSliderList.observe(viewLifecycleOwner) { homeSliderList ->
                ioScope {
                    binding.apply {
                        homeSliderAdapter?.differ?.submitList(homeSliderList as List<HomeSliderData?>?)
                        if (homeSliderList != null) {
                            indicator.count = homeSliderList.size
                        }
                        vpSlider.getPositionAndSendHandler2(homeSliderList, handler) {
                            indicator.selection = it
                        }
                    }
                }
            }

            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                error.observe(viewLifecycleOwner) { Err ->
                    Err?.message?.let { showToast(it) }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        binding.indicator.selection = 0
        handler.addChanging()
        viewModel.error.removeObserver(Observer<Err?> { value ->
            // Observer işlemleri
        })
    }

    override fun onPause() {
        super.onPause()
        handler.removeChanging()
    }

}