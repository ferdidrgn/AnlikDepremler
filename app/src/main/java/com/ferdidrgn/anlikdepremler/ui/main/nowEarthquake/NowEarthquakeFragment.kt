package com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentNowEarthquakeBinding
import com.ferdidrgn.anlikdepremler.tools.*
import com.ferdidrgn.anlikdepremler.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class NowEarthquakeFragment : BaseFragment<MainViewModel, FragmentNowEarthquakeBinding>() {

    private var job: Job? = null

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentNowEarthquakeBinding =
        FragmentNowEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        builderADS(requireContext(), binding.adView)

        binding.includeEarthquakeList.viewModel = viewModel
        binding.includeEarthquakeList.tvHeader.text = getString(R.string.now_earthquake)
        binding.includeEarthquakeList.nowEarthquakeAdapter = NowEarthquakeAdapter(viewModel, false)

    }

    private fun observeServiceData() {
        with(viewModel) {

            //Swipe Refresh
            binding.includeEarthquakeList.swipeRefreshLayout.setOnRefreshListener {
                getNearEarthquakeList.postValue(null)
                isNearPage.postValue(false)
                getNowEarthquake()
                binding.includeEarthquakeList.swipeRefreshLayout.isRefreshing = false
            }

            clickableHeaderMenus.observe(viewLifecycleOwner) {
                if (it) {
                    //Map icon Click
                    clickMap.observe(viewLifecycleOwner) {
                        NavHandler.instance.toMapsActivity(requireContext(), filterNowList, false)
                    }

                    //Filter icon Click
                    binding.includeEarthquakeList.llFilters.onClickThrottled {
                        FilterBottomSheet { lng ->
                            earthquakeBodyRequest.userLat = lng?.latitude
                            earthquakeBodyRequest.userLong = lng?.longitude

                            if (clickFilterClear.value == true) {
                                getNowEarthquake()
                            } else {
                                getFilters()
                                clickFilterClear.postValue(false)
                            }
                        }.show(parentFragmentManager, "filterBottomSheet")
                    }
                }
            }

            getNowEarthquakeList.observe(viewLifecycleOwner) {
                scrollToTop()
            }

            getNearEarthquakeList.observe(viewLifecycleOwner) {
                scrollToTop()
            }

            error.observe(viewLifecycleOwner) { Err ->
                Err?.message?.let { showToast(it) }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        observeServiceData()
    }
    override fun onPause() {
        super.onPause()
        viewModel.getNowEarthquakeList.postValue(null)
    }

    private fun scrollToTop() {
        Handler(Looper.getMainLooper()).postDelayed({
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.scrollToPositionWithOffset(0, 0)
            binding.includeEarthquakeList.rvEarthquake.layoutManager =
                linearLayoutManager
        }, 4000)
    }

    private fun updateUiAfterSearch(text: String, isValid: Boolean) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            viewModel.earthquakeBodyRequest.search = text.lowercase()
            observeServiceData()
        }
    }
}
