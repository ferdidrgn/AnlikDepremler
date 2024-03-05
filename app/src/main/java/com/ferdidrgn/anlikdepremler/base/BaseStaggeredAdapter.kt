package com.ferdidrgn.anlikdepremler.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseStaggeredAdapter<BINDING : ViewDataBinding, WIDEBINDING : ViewDataBinding, T : ListAdapterItem>(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @get:LayoutRes
    abstract val layoutId: Int

    @get:LayoutRes
    abstract val staggeredLayoutId: Int

    abstract val comeMapData: Boolean

    abstract fun bind(binding: BINDING, item: T, position: Int)

    abstract fun staggeredBind(binding: WIDEBINDING, item: T, position: Int)

    private val differCallback = object : DiffUtil.ItemCallback<T?>() {
        override fun areItemsTheSame(
            oldItem: T,
            newItem: T
        ): Boolean {
            return oldItem.mId == newItem.mId
        }

        override fun areContentsTheSame(
            oldItem: T,
            newItem: T
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun updateData(list: List<T>) {
        //  this.data = list
        differ.submitList(list)
        notifyDataSetChanged()
        notifyItemRangeInserted(0, list.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (!comeMapData) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binder = if (viewType == 0) {
            DataBindingUtil.inflate<BINDING>(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false
            )
        } else {
            DataBindingUtil.inflate<WIDEBINDING>(
                LayoutInflater.from(parent.context),
                staggeredLayoutId,
                parent,
                false
            )
        }

        return if (viewType == 0) {
            SingleViewHolder(binder)
        } else {
            DoubleViewHolder(binder)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = differ.currentList[position] ?: return

        when (holder) {
            is SingleViewHolder<*> -> bind(holder.binder as BINDING, currentItem, position)
            is DoubleViewHolder<*> -> staggeredBind(
                holder.binder as WIDEBINDING, currentItem, position
            )
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}