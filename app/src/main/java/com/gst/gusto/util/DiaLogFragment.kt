package com.gst.gusto.util

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.BottomsheetdialogCreateBinding
import com.gst.gusto.databinding.BottomsheetdialogInviteBinding
import com.gst.gusto.databinding.BottomsheetdialogJoinBinding
import com.gst.gusto.databinding.BottomsheetdialogRoutesBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter


class DiaLogFragment(val itemClick: (Int) -> Unit, val layout : Int, val gustoViewModel: GustoViewModel,val activity : MainActivity) :
    BottomSheetDialogFragment() {

    lateinit var binding1: BottomsheetdialogRoutesBinding
    lateinit var binding2: BottomsheetdialogJoinBinding
    lateinit var binding3: BottomsheetdialogCreateBinding
    lateinit var binding4: BottomsheetdialogInviteBinding
    private val colorStateOnList = ColorStateList.valueOf(Color.parseColor("#F27781"))
    private val colorStateOffList = ColorStateList.valueOf(Color.parseColor("#ECECEC"))
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if(layout == R.layout.bottomsheetdialog_routes) {
            binding1 = BottomsheetdialogRoutesBinding.inflate(inflater, container, false)
            return binding1.root
        } else if(layout == R.layout.bottomsheetdialog_join) {
            binding2 = BottomsheetdialogJoinBinding.inflate(inflater, container, false)
            return binding2.root
        }  else if(layout == R.layout.bottomsheetdialog_create) {
            binding3 = BottomsheetdialogCreateBinding.inflate(inflater, container, false)
            return binding3.root
        } else if(layout == R.layout.bottomsheetdialog_invite) {
            binding4 = BottomsheetdialogInviteBinding.inflate(inflater, container, false)
            return binding4.root
        }  else {
            return inflater.inflate(layout, container, false)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        //behavior.state = BottomSheetBehavior.STATE_EXPANDED
        if(layout == R.layout.bottomsheetdialog_routes) {
            val itemList = gustoViewModel.markerListLiveData.value as ArrayList

            if(itemList.size==6) {
                binding1.lyAddRoute.visibility = View.INVISIBLE
            }

            val boardAdapter = MapRoutesAdapter(itemList,binding1.lyAddRoute,activity,0)
            boardAdapter.notifyDataSetChanged()

            binding1.rvRoutes.adapter = boardAdapter
            binding1.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding1.btnPlus.setOnClickListener {
                activity.getCon().navigate(R.id.action_groupMRoutMapFragment_to_routeSearchFragment)
                dismiss()

            }
            binding1.btnSave.setOnClickListener {
                itemClick(1)
                dialog?.dismiss()
            }
        }
        else if(layout == R.layout.bottomsheetdialog_join) {
            binding2.btnExit.setOnClickListener {
                dialog?.dismiss()
            }
            binding2.btnEnter.setOnClickListener {
                gustoViewModel.joinGroup(binding2.etCode.text.toString()) {result ->
                    when(result) {
                        1 -> {
                            itemClick(1)
                            dialog?.dismiss()
                        }
                        else -> {
                            binding2.bgCode.visibility = View.VISIBLE
                            binding2.tvCode.visibility = View.VISIBLE
                        }
                    }
                }
            }
            binding2.etCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 이벤트 발생 전에 수행할 작업
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 수행할 작업
                    if(binding2.etCode.text.length==12) {
                        binding2.btnEnter.backgroundTintList = colorStateOnList
                        gustoViewModel.checkGroup(binding2.etCode.text.toString()) { result, data ->
                            when (result) {
                                1 -> {
                                    binding2.bgCode.visibility = View.GONE
                                    binding2.tvCode.visibility = View.GONE
                                    binding2.tvCodeCorrect.visibility = View.VISIBLE
                                    binding2.lyCheckGroup.visibility = View.VISIBLE
                                    binding2.ivGusto.visibility = View.INVISIBLE

                                    if (data != null) {
                                        binding2.tvTitleGroup.text = data.groupName
                                        binding2.tvMemberNum.text = "${data.numMembers}명"
                                        binding2.tvPeople.text = "${data.groupMembers[0]} 님 외 ${data.numMembers-1}"
                                        binding2.btnEnter.backgroundTintList = null
                                        binding2.btnEnter.setBackgroundResource(R.drawable.gradient_background_orange_horizontal)
                                        binding2.btnEnter.setTextColor(Color.parseColor("#FFFFFF"))
                                    }
                                }
                                else -> {
                                    binding2.ivGusto.visibility = View.VISIBLE
                                    binding2.bgCode.visibility = View.VISIBLE
                                    binding2.tvCode.visibility = View.VISIBLE
                                    binding2.tvCodeCorrect.visibility = View.GONE
                                    binding2.lyCheckGroup.visibility = View.GONE
                                    binding2.btnEnter.setTextColor(Color.parseColor("#FFFFFF"))
                                }
                            }
                        }
                    } else {
                        binding2.ivGusto.visibility = View.VISIBLE
                        binding2.btnEnter.backgroundTintList = colorStateOffList
                        binding2.lyCheckGroup.visibility = View.GONE
                        binding2.btnEnter.setTextColor(Color.parseColor("#717171"))
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                    // 이벤트 발생 후에 수행할 작업
                }
            })
        }
        else if(layout == R.layout.bottomsheetdialog_create) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding3.btnExit.setOnClickListener {
                dialog?.dismiss()
            }
            binding3.etGroupName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 이벤트 발생 전에 수행할 작업
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트가 변경될 때 수행할 작업
                    binding3.tvGroupNameSize.text = "(${binding3.etGroupName.text.length}/10)"
                    if(binding3.etComent.text.length>0 && binding3.etGroupName.text.length>0) {
                        binding3.btnEnter.setTextColor(Color.parseColor("#FFFFFF"))
                        binding3.btnEnter.backgroundTintList = colorStateOnList
                    }
                    else {
                        binding3.btnEnter.setTextColor(Color.parseColor("#717171"))
                        binding3.btnEnter.backgroundTintList = colorStateOffList
                    }
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
                    if(binding3.etComent.text.length>0 && binding3.etGroupName.text.length>0) {
                        binding3.btnEnter.setTextColor(Color.parseColor("#FFFFFF"))
                        binding3.btnEnter.backgroundTintList = colorStateOnList
                    }
                    else {
                        binding3.btnEnter.setTextColor(Color.parseColor("#717171"))
                        binding3.btnEnter.backgroundTintList = colorStateOffList
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // 이벤트 발생 후에 수행할 작업
                }
            })
            binding3.btnEnter.setOnClickListener {
                if(binding3.etComent.text.length>0 && binding3.etGroupName.text.length>0) {
                    gustoViewModel.createGroup(
                        binding3.etGroupName.text.toString(),
                        binding3.etComent.text.toString()
                    ) { result ->
                        when (result) {
                            1 -> {
                                itemClick(1)
                                dialog?.dismiss()
                            }
                        }
                    }
                }
            }
        }
        else if(layout == R.layout.bottomsheetdialog_invite) {
            binding4.tvCode
            var tmpCode = ""
            gustoViewModel.getGroupInvitationCode {result, data ->
                when(result) {
                    1 -> {
                        if(data!=null) {
                            tmpCode = data
                            binding4.tvCode.text = data
                        }
                    }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
            binding4.btnExit.setOnClickListener {
                dialog?.dismiss()
            }
            binding4.btnEnter.setOnClickListener {
                dialog?.dismiss()
            }
            binding4.btnCopy.setOnClickListener {
                itemClick(1)
                dialog?.dismiss()
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/html"
                val text = gustoViewModel.currentGroupName+tmpCode
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
                //sharingIntent.`package` = "com.kakao.talk"
                startActivity(sharingIntent)
            }

        }
    }
}