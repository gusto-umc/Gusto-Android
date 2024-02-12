package com.gst.gusto.Util

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.RoutesBottomsheetdialogBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter


class DiaLogFragment(val itemClick: (Int) -> Unit, val layout : Int, val gustoViewModel: GustoViewModel,val activity : MainActivity) :
    BottomSheetDialogFragment() {

    lateinit var binding1: RoutesBottomsheetdialogBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if(layout == R.layout.routes_bottomsheetdialog) {
            Log.d("sfasfd","fdsafsa2")
            binding1 = RoutesBottomsheetdialogBinding.inflate(inflater, container, false)
            return binding1.root
        } else {
            return inflater.inflate(layout, container, false)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(layout == R.layout.routes_bottomsheetdialog) {
            val itemList = gustoViewModel.markerListLiveData.value as ArrayList

            val boardAdapter = MapRoutesAdapter(itemList,binding1.lyAddRoute,activity)
            boardAdapter.notifyDataSetChanged()

            binding1.rvRoutes.adapter = boardAdapter
            binding1.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            binding1.btnPlus.setOnClickListener {
                itemList.add(
                    mapUtil.Companion.MarkerItem(
                        0,
                        0,
                        0,
                        37.6219001,
                        127.0743010,
                        binding1.tvRestName.text.toString(),
                        "",
                        false
                    )
                )
                gustoViewModel.markerListLiveData.value = itemList
                boardAdapter.notifyItemInserted(itemList.size-1)
                if(itemList.size==6) {
                    binding1.lyAddRoute.visibility = View.INVISIBLE
                }
            }
            binding1.btnSave.setOnClickListener {
                itemClick(1)
                dialog?.dismiss()
            }
        }

    }
}