package com.ferdidrgn.anlikdepremler.ui.main.home

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseAdapter
import com.ferdidrgn.anlikdepremler.databinding.ItemTopTenEarthquakeBinding
import com.ferdidrgn.anlikdepremler.model.Earthquake

class TopTenEarthquakeAdapter(
    private val topTenEarthquakeAdapterListener: TopTenEarthquakeAdapterListener
) : BaseAdapter<ItemTopTenEarthquakeBinding, Earthquake>() {

    override val layoutId: Int = R.layout.item_top_ten_earthquake

    override fun bind(binding: ItemTopTenEarthquakeBinding, item: Earthquake, position: Int) {
        binding.apply {
            earthquake = item
            index = position
            listener = topTenEarthquakeAdapterListener
            executePendingBindings()
        }
    }
}

interface TopTenEarthquakeAdapterListener {
    fun onTopTenEarthquakeAdapterListener(earthquake: Earthquake)
}