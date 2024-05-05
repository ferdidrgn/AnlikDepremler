package com.ferdidrgn.anlikdepremler.util.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<BINDING : ViewDataBinding>(val binder: BINDING) :
    RecyclerView.ViewHolder(binder.root)

class SingleViewHolder<BINDING : ViewDataBinding>(val binder: BINDING) :
    RecyclerView.ViewHolder(binder.root)

class DoubleViewHolder<WIDEBINDING : ViewDataBinding>(val binder: WIDEBINDING) :
    RecyclerView.ViewHolder(binder.root)