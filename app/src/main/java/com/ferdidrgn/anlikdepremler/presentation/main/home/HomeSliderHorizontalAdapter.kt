package com.ferdidrgn.anlikdepremler.presentation.main.home

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseAdapter
import com.ferdidrgn.anlikdepremler.databinding.ItemHomeSliderBinding
import com.ferdidrgn.anlikdepremler.data.remote.dto.HomeSliderDto

class HomeSliderHorizontalAdapter(
    private val sliderDetailsAdapterListener: SliderDetailsAdapterListener
) : BaseAdapter<ItemHomeSliderBinding, HomeSliderDto>() {

    override val layoutId: Int = R.layout.item_home_slider

    override fun bind(binding: ItemHomeSliderBinding, item: HomeSliderDto, position: Int) {
        binding.apply {
            homeSliderData = item
            index = position
            listener = sliderDetailsAdapterListener
            executePendingBindings()
        }
    }
}

interface SliderDetailsAdapterListener {
    fun onSliderDetailsAdapterListener(homeSliderDto: HomeSliderDto){}
}