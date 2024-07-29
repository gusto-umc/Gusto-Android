package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.clock.Fragment.ListRouteFragment
import com.gst.gusto.R
import com.gst.gusto.util.mapUtil.Companion.MarkerItem
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RequestCreateRoute
import com.gst.gusto.databinding.FragmentListRouteStoresBinding
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.util.util

class RouteStoresFragment : Fragment() {

    lateinit var binding: FragmentListRouteStoresBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var itemList = ArrayList<MarkerItem>()
    private var remove = true // save = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRouteStoresBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabEdit.setOnClickListener {
            gustoViewModel.getRouteMap() { result ->
                when (result) {
                    1 -> {
                        gustoViewModel.editMode = true
                        findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment)
                    }
                    else -> {
                        Toast.makeText(context,"서버와의 연결 불안정", Toast.LENGTH_SHORT ).show()
                    }
                }
            }
        }

        binding.fabMap.setOnClickListener {
            gustoViewModel.getRouteMap() { result ->
                when (result) {
                    1 -> {
                        findNavController().navigate(R.id.action_routeStoresFragment_to_groupMRMFragment)
                    }
                    else -> {
                        Toast.makeText(context,"서버와의 연결 불안정", Toast.LENGTH_SHORT ).show()
                    }
                }
            }

        }
        binding.btnRemoveOrSave.setOnClickListener {
            if(remove) {
                util.setPopupTwo(requireContext(),"${gustoViewModel.routeName}을\n내 루트에서 삭제하시겠습니까?","",1) { yesOrNo ->
                    gustoViewModel.deleteRoute(gustoViewModel.currentRouteId) { result ->
                        when (result) {
                            1 -> {
                                findNavController().popBackStack()
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "서버와의 연결 불안정",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            } else {
                gustoViewModel.editRoute(gustoViewModel.currentRouteId, binding.tvRouteName.text.toString(), null) {result ->
                    when(result) {
                        1 -> {
                            remove = true
                            binding.btnRemoveOrSave.text = "삭제"
                            binding.tvRouteName.isEnabled = false
                            binding.tvRouteName.isFocusable = false
                            binding.tvRouteName.isFocusableInTouchMode = false
                            binding.btnEdit.visibility = View.VISIBLE
                            binding.fabMap.visibility = View.VISIBLE
                            binding.fabEdit.visibility = View.VISIBLE
                            binding.lyPrivate.visibility = View.GONE
                        }
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            remove = false
            binding.btnRemoveOrSave.text = "저장"
            binding.tvRouteName.isEnabled = true
            binding.tvRouteName.isFocusable = true
            binding.tvRouteName.isFocusableInTouchMode = true
            binding.tvRouteName.requestFocus()
            binding.tvRouteName.setSelection(binding.tvRouteName.text.length)
            binding.tvRouteName.hint = binding.tvRouteName.text
            binding.tvRouteName.setText("")
            binding.btnEdit.visibility = View.GONE

            binding.fabEdit.visibility = View.GONE
            binding.fabMap.visibility = View.GONE
            binding.lyPrivate.visibility = View.VISIBLE
        }

        binding.switchPrivate.setOnClickListener {
            gustoViewModel.patchPublish(gustoViewModel.currentRouteId, binding.switchPrivate.isClickable) { result ->
                when(result) {
                    1-> {
                        Toast.makeText(context,"공개/비공개 설정 완료", Toast.LENGTH_SHORT ).show()
                    }
                }
            }
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = gustoViewModel.markerListLiveData.value!!
        val boardAdapter = MapRoutesAdapter(itemList,binding.lyNull,requireActivity(),0)
        boardAdapter.notifyDataSetChanged()

        binding.tvRouteName.setText(gustoViewModel.routeName)
        binding.tvRouteName.setHint(gustoViewModel.routeName)
        binding.rvRoutes.adapter = boardAdapter
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        gustoViewModel.groupFragment = 0
        gustoViewModel.listFragment="route"
        //gustoViewModel.markerListLiveData.value?.clear()
    }


}