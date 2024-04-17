package com.ferdidrgn.anlikdepremler.presentation.mapsEarthquake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.domain.model.Earthquake

class EarthQuakeMapAdapter(private val earthquakeList: List<Earthquake?>) :
    RecyclerView.Adapter<EarthQuakeMapAdapter.MapHolder>() {

    class MapHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvMl: TextView = itemView.findViewById(R.id.tvMl)
        val tvDepth: TextView = itemView.findViewById(R.id.tvDepth)
        val tvRevize: TextView = itemView.findViewById(R.id.tvRevize)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvDate)
        val tvDistanceToLocation: TextView = itemView.findViewById(R.id.tvDistanceToLocation)
    }

    override fun onBindViewHolder(holder: MapHolder, position: Int) {
        val item = earthquakeList[position]
        holder.apply {
            tvLocation.text = item?.location
            tvMl.text = item?.ml
            tvDepth.text = item?.depth
            tvRevize.text = item?.revize
            tvDate.text = item?.date
            tvTime.text = item?.time

            //Myself hespalama olacak
            //tvDistanceToLocation.text = item?.lokasyon

            addMarkerListener?.let { listener -> item?.let { listener(item) } }
        }
    }

    private var addMarkerListener: ((Earthquake) -> Unit)? = null

    fun addMarker(listener: (Earthquake) -> Unit) {
        addMarkerListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_now_earthquke_maps, parent, false)
        return MapHolder(view)
    }

    override fun getItemCount(): Int {
        return earthquakeList.size
    }
}