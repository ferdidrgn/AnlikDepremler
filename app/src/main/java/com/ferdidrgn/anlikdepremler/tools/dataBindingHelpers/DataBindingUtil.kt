package com.ferdidrgn.anlikdepremler.tools.dataBindingHelpers

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ferdidrgn.anlikdepremler.base.BaseAdapter
import com.ferdidrgn.anlikdepremler.base.BaseStaggeredAdapter
import com.ferdidrgn.anlikdepremler.base.ListAdapterItem
import com.ferdidrgn.anlikdepremler.tools.changeDataShameFormat
import com.ferdidrgn.anlikdepremler.tools.components.CustomToolbar
import com.ferdidrgn.anlikdepremler.tools.downloadFromUrl

object DataBindingUtil {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun downloadImage(view: ImageView, url: String?) {
        view.downloadFromUrl(url)
    }

    @JvmStatic
    @BindingAdapter(
        value = ["changeText", "isTextClickable"],
        requireAll = false
    )
    fun changeText(
        view: TextView,
        text: String?,
        boolean: Boolean?
    ) {
        if (boolean != null) {
            view.isClickable = boolean
        }
        view.text = text
    }

    @JvmStatic
    @BindingAdapter(
        value = ["cst_tb_back_icon_visibility"],
        requireAll = false
    )
    fun changeVisibility(
        view: CustomToolbar,
        boolean: Boolean?
    ) {
        if (boolean != null) {
            view.visibilityOfBackIcon(boolean)
        }
    }

    //RECYCLERVIEW
    @BindingAdapter("setAdapter")
    @JvmStatic
    fun setAdapter(
        recyclerView: RecyclerView,
        adapter: BaseAdapter<ViewDataBinding, ListAdapterItem>?
    ) {
        adapter?.let { adapter ->
            recyclerView.adapter = adapter
        }
    }

    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("submitList")
    @JvmStatic
    fun submitList(recyclerView: RecyclerView, list: List<ListAdapterItem>?) {
        val adapter = recyclerView.adapter as? BaseAdapter<ViewDataBinding, ListAdapterItem>?
        adapter?.differ?.submitList(list)
    }

    //RECYCLERVIEW BaseStaggeredAdapter

    @BindingAdapter("setAdapter")
    @JvmStatic
    fun setAdapter(
        recyclerView: RecyclerView,
        adapter: BaseStaggeredAdapter<ViewDataBinding, ViewDataBinding, ListAdapterItem>?
    ) {
        adapter?.let {
            recyclerView.adapter = it
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter("submitListSet")
    fun submitListSet(recyclerView: RecyclerView, list: List<ListAdapterItem>?) {
        val adapter =
            recyclerView.adapter as? BaseStaggeredAdapter<ViewDataBinding, ViewDataBinding, ListAdapterItem>?
        adapter?.differ?.submitList(list)
    }

    //VIEWPAGER
    @BindingAdapter("setAdapter")
    @JvmStatic
    fun setAdapter(
        vp: ViewPager2,
        adapter: BaseAdapter<ViewDataBinding, ListAdapterItem>?
    ) {
        adapter?.let {
            vp.adapter = it
        }
    }

    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("submitList")
    @JvmStatic
    fun submitList(vp: ViewPager2, list: List<ListAdapterItem>?) {
        val adapter = vp.adapter as? BaseAdapter<ViewDataBinding, ListAdapterItem>?
        adapter?.differ?.submitList(list)
    }

    @JvmStatic
    @BindingAdapter("dateFormat")
    fun setDateFormat(textView: TextView, date: String?) {
        if (!date.isNullOrEmpty()) {
            textView.text = changeDataShameFormat(date)
        }
    }
}