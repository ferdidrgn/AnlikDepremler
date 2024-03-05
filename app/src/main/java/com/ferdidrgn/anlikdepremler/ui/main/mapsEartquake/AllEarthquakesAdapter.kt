package com.ferdidrgn.anlikdepremler.ui.main.nowEarthquake

import androidx.recyclerview.widget.RecyclerView
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseStaggeredAdapter
import com.ferdidrgn.anlikdepremler.databinding.ItemNowEarthquakeListBinding
import com.ferdidrgn.anlikdepremler.databinding.ItemNowEarthqukeMapsBinding
import com.ferdidrgn.anlikdepremler.model.Earthquake

class NowEarthquakeAdapter(
    private val nowEarthQuakeAdapterListener: NowEarthQuakeAdapterListener,
    private val comeFromMap: Boolean,
) : BaseStaggeredAdapter<ItemNowEarthquakeListBinding, ItemNowEarthqukeMapsBinding, Earthquake>() {

    override val comeMapData: Boolean = comeFromMap

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun selectItem(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

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

    override val staggeredLayoutId: Int = R.layout.item_now_earthquke_maps

    override fun staggeredBind(
        binding: ItemNowEarthqukeMapsBinding,
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
    fun onNowEarthquakeItemClicked(position: Int)
}