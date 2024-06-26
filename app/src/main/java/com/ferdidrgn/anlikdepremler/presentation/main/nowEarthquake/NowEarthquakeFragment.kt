package com.ferdidrgn.anlikdepremler.presentation.main.nowEarthquake

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentNowEarthquakeBinding
import com.ferdidrgn.anlikdepremler.presentation.main.MainViewModel
import com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake.NowEarthquakeAdapter
import com.ferdidrgn.anlikdepremler.util.handler.NavHandler
import com.ferdidrgn.anlikdepremler.util.helpers.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class NowEarthquakeFragment : BaseFragment<MainViewModel, FragmentNowEarthquakeBinding>() {

    private var job: Job? = null

    override fun getVM(): Lazy<MainViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentNowEarthquakeBinding =
        FragmentNowEarthquakeBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        setAds(binding.adView)

        binding.includeEarthquakeList.apply {
            viewModel = this@NowEarthquakeFragment.viewModel
            tvHeader.text = getString(R.string.now_earthquake)
            nowEarthquakeAdapter = NowEarthquakeAdapter(this@NowEarthquakeFragment.viewModel, false)

            swipeRefreshLayout.setOnRefreshListener {
                this@NowEarthquakeFragment.viewModel.apply {
                    getNearEarthquakeList.postValue(null)
                    isNearPage.postValue(true)
                    getNowEarthquake()
                }
                swipeRefreshLayout.isRefreshing = false
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
                    observeFilterIconClick()
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
        viewModel.apply {
            clickMap.observe(viewLifecycleOwner) {
                NavHandler.instance.toMapsActivity(requireContext(), isNearPage.value)
            }
        }
    }

    private fun observeFilterIconClick() {
        viewModel.apply {
            clickFilter.observe(viewLifecycleOwner) {
                viewModel.isNearPage.postValue(null)
                NavHandler.instance.toFilterActivity(requireContext())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setAds(binding.adView)
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
            binding.includeEarthquakeList.rvEarthquake.layoutManager = linearLayoutManager
        }, 3000)
    }

    private fun updateUiAfterSearch(text: String, isValid: Boolean) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            viewModel.location.emit(text.lowercase())
            observeServiceData()
        }
    }
}
