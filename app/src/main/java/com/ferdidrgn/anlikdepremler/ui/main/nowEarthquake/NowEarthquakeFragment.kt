package com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake

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

        binding.includeEarthquakeList.apply {
            viewModel = this@NowEarthquakeFragment.viewModel
            tvHeader.text = getString(R.string.now_earthquake)
            nowEarthquakeAdapter = NowEarthquakeAdapter(this@NowEarthquakeFragment.viewModel, false)

            swipeRefreshLayout.setOnRefreshListener {
                this@NowEarthquakeFragment.viewModel.apply {
                    getNearEarthquakeList.postValue(null)
                    isNearPage.postValue(false)
                    getNowEarthquake()
                }
                swipeRefreshLayout.isRefreshing = false
            }

            llFilters.onClickThrottled {
                observeFilterIconClick()
            }
        }
    }

    private fun observeServiceData() {
        with(viewModel) {
            getNowEarthquake()

            clickableHeaderMenus.observe(viewLifecycleOwner) { clickable ->
                if (clickable) {
                    //Map icon Click
                    observeMapIconClick()
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

    private fun observeMapIconClick() {
        viewModel.clickMap.observe(viewLifecycleOwner) {
            NavHandler.instance.toMapsActivity(requireContext(), viewModel.filterNowList, false)
        }
    }

    private fun observeFilterIconClick() {
        viewModel.apply {
            location.value = ""
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
            viewModel.earthquakeBodyRequest.location = text.lowercase()
            observeServiceData()
        }
    }
}
