package com.ferdidrgn.anlikdepremler.ui.main.home

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseAdapter
import com.ferdidrgn.anlikdepremler.databinding.ItemTopTenLocationEarthquakeBinding
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake

class TopTenLocationEarthquakeAdapter(
    private val topTenLocationEarthquakeAdapterListener: TopTenLocationEarthquakeAdapterListener
) : BaseAdapter<ItemTopTenLocationEarthquakeBinding, Earthquake>() {

    override val layoutId: Int = R.layout.item_top_ten_location_earthquake

    override fun bind(binding: ItemTopTenLocationEarthquakeBinding, item: Earthquake, position: Int) {
        binding.apply {
            earthquake = item
            index = position
            listener = topTenLocationEarthquakeAdapterListener
            executePendingBindings()
        }
    }
}

interface TopTenLocationEarthquakeAdapterListener {
    fun onTopTenLocationEarthquakeAdapterListener(earthquake: Earthquake)
}