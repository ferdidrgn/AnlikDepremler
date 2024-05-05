package com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake

import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseStaggeredAdapter
import com.ferdidrgn.anlikdepremler.databinding.ItemNowEarthquakeListBinding
import com.ferdidrgn.anlikdepremler.databinding.ItemNowEarthquakeMapsBinding
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake

class NowEarthquakeAdapter(
    private val nowEarthQuakeAdapterListener: NowEarthQuakeAdapterListener,
    private val comeFromMap: Boolean,
) : BaseStaggeredAdapter<ItemNowEarthquakeListBinding, ItemNowEarthquakeMapsBinding, Earthquake>() {

    override val comeMapData: Boolean = comeFromMap

    override val layoutId: Int = R.layout.item_now_earthquake_list

    override fun bind(binding: ItemNowEarthquakeListBinding, item: Earthquake, position: Int) {
        binding.apply {
            earthquake = item
            index = position
            listener = nowEarthQuakeAdapterListener
            executePendingBindings()

            //ViewBinding

            //hesaplama yap mock data
            //tvDistanceToLocation.text = item?.distance

        }
    }

    override val staggeredLayoutId: Int = R.layout.item_now_earthquake_maps

    override fun staggeredBind(
        binding: ItemNowEarthquakeMapsBinding,
        item: Earthquake,
        position: Int
    ) {
        binding.apply {
            earthquake = item
            index = position
            listener = nowEarthQuakeAdapterListener
            executePendingBindings()

            //ViewBinding

            //hesaplama yap mock data
            //tvDistanceToLocation.text = item?.distance
        }

        addMarkerListener?.let { listener -> listener(item) }
    }


    private var addMarkerListener: ((Earthquake) -> Unit)? = null

    fun addMarker(listener: (Earthquake) -> Unit) {
        addMarkerListener = listener
    }
}

interface NowEarthQuakeAdapterListener {
    fun onNowEarthquakeItemClicked(position: Int) {}
}