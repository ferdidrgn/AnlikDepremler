package com.ferdidrgn.anlikdepremler.ui.main.home

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseAdapter
import com.ferdidrgn.anlikdepremler.databinding.ItemHomeSliderBinding
import com.ferdidrgn.anlikdepremler.model.HomeSliderData

class HomeSliderHorizontalAdapter(
    private val sliderDetailsAdapterListener: SliderDetailsAdapterListener
) : BaseAdapter<ItemHomeSliderBinding, HomeSliderData>() {

    override val layoutId: Int = R.layout.item_home_slider

    override fun bind(binding: ItemHomeSliderBinding, item: HomeSliderData, position: Int) {
        binding.apply {
            homeSliderData = item
            index = position
            listener = sliderDetailsAdapterListener
            executePendingBindings()
        }
    }
}

interface SliderDetailsAdapterListener {
    fun onSliderDetailsAdapterListener(homeSliderData: HomeSliderData)
}