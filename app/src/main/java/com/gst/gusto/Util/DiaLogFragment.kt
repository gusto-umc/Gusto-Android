package com.gst.gusto.Util

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.BottomsheetdialogCreateBinding
import com.gst.gusto.databinding.BottomsheetdialogInviteBinding
import com.gst.gusto.databinding.BottomsheetdialogRoutesBinding
import com.gst.gusto.list.adapter.LisAdapter
import com.gst.gusto.list.adapter.MapRoutesAdapter


class DiaLogFragment(val itemClick: (Int) -> Unit, val layout : Int, val gustoViewModel: GustoViewModel,val activity : MainActivity) :
    BottomSheetDialogFragment() {

    lateinit var binding1: BottomsheetdialogRoutesBinding
    lateinit var binding2: BottomsheetdialogInviteBinding
    lateinit var binding3: BottomsheetdialogCreateBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if(layout == R.layout.bottomsheetdialog_routes) {
            binding1 = BottomsheetdialogRoutesBinding.inflate(inflater, container, false)
            return binding1.root
        } else if(layout == R.layout.bottomsheetdialog_invite) {
            binding2 = BottomsheetdialogInviteBinding.inflate(inflater, container, false)
            return binding2.root
        }  else if(layout == R.layout.bottomsheetdialog_create) {
            binding3 = BottomsheetdialogCreateBinding.inflate(inflater, container, false)
            return binding3.root
        }  else {
            return inflater.inflate(layout, container, false)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(layout == R.layout.bottomsheetdialog_routes) {
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
        else if(layout == R.layout.bottomsheetdialog_invite) {
            binding2.btnExit.setOnClickListener {
                dialog?.dismiss()
            }
            binding2.btnEnter.setOnClickListener {
                gustoViewModel.joinGroup(12,binding2.etCode.text.toString()) {result ->
                    when(result) {
                        1 -> {
                            dialog?.dismiss()
                        }
                    }
                }
            }
        }
        else if(layout == R.layout.bottomsheetdialog_create) {
            binding3.btnExit.setOnClickListener {
                dialog?.dismiss()
            }
            binding3.btnEnter.setOnClickListener {
                dialog?.dismiss()
            }
            binding3.etGroupName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 이벤트 발생 전에 수행할 작업
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 수행할 작업
                    binding3.tvGroupNameSize.text = "(${binding3.etGroupName.text.length}/10)"
                }

                override fun afterTextChanged(s: Editable?) {
                    // 이벤트 발생 후에 수행할 작업
                }
            })
            binding3.etComent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 이벤트 발생 전에 수행할 작업
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 수행할 작업
                    binding3.tvComentSize.text = "(${binding3.etComent.text.length}/30)"
                }

                override fun afterTextChanged(s: Editable?) {
                    // 이벤트 발생 후에 수행할 작업
                }
            })
            binding3.btnEnter.setOnClickListener {
                gustoViewModel.createGroup(binding3.etGroupName.text.toString(),binding3.etComent.text.toString()) {result ->
                    when(result) {
                        1 -> {
                            dialog?.dismiss()
                        }
                    }
                }
            }
        }
    }
}