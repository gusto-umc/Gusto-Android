package com.gst.gusto.ListView.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.adapter.StoreEditAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentStoreEditBinding
import kotlin.concurrent.thread

class StoreEditFragment : Fragment() {

    private lateinit var binding : FragmentStoreEditBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 1. 데이터 적용 -> 서버 연결 필요
         */
        gustoViewModel.selectedStoreIdList.clear()
        gustoViewModel.myAllStoreList.clear()
        binding.tvStoreEditCategory.text = gustoViewModel.selectedCategoryInfo!!.categoryName




        /**
         * 2. rv 연결
         */
        val mStoreEditAdapter = StoreEditAdapter()
        mStoreEditAdapter.mContext = context
        mStoreEditAdapter.gustoViewModel = gustoViewModel
        mStoreEditAdapter.submitList(gustoViewModel.myAllStoreList)
        binding.rvStoreEdit.adapter = mStoreEditAdapter
        binding.rvStoreEdit.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        var hasNext = false

        gustoViewModel.getPPMyStore(gustoViewModel. selectedCategoryInfo!!.myCategoryId, null){
                result, getHasNext ->
            when(result){
                1 -> {
                    //success
                    mStoreEditAdapter.submitList(gustoViewModel.myAllStoreList)
                    hasNext = getHasNext
                    mStoreEditAdapter.notifyDataSetChanged()
                }
                else-> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }

            }
        }

        binding.rvStoreEdit.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                // 페이징 처리
                if(rvPosition == totalCount && hasNext) {
                    gustoViewModel.getPPMyStore(gustoViewModel.selectedCategoryInfo!!.myCategoryId, gustoViewModel.myAllStoreList.last().pinId) { result, getHasNext ->
                        hasNext = getHasNext
                        when(result) {
                            1 -> {
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed({
                                    mStoreEditAdapter.submitList(gustoViewModel.myAllStoreList)
                                    mStoreEditAdapter.notifyDataSetChanged()
                                }, 1000)

                            }
                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })


        /**
         * 3. onclick event
         */
        binding.ivStoreEditBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabStoreEditDelete.setOnClickListener{
            gustoViewModel.deleteStores(gustoViewModel.selectedStoreIdList){
                result ->
                when(result){
                    0 -> {
                        //success
                    }
                    1 -> {
                        //fail
                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()

                    }
                }
                findNavController().popBackStack()
            }
        }

        /**
         * 4. 전체 선택
         */

        binding.cbStoreEditAll.setOnClickListener {
            binding.cbStoreEditAll.isChecked
            if(binding.cbStoreEditAll.isChecked){
                gustoViewModel.updateSelectFlag("all")
            }
            else{
                gustoViewModel!!.updateSelectFlag("nothing")
            }
        }
        val handler = Handler(Looper.getMainLooper())
        gustoViewModel.allFlag.observe(viewLifecycleOwner, Observer {
            if(gustoViewModel.allFlag.value == "all"){
                Log.d("select", "1. all")

                thread(start = true){
                    gustoViewModel.selectedStoreIdList.clear()
                    for(i in gustoViewModel.myAllStoreList){
                        gustoViewModel.selectedStoreIdList.add(i.pinId)
                    }
                    handler.post{
                        Log.d("adapter select list", gustoViewModel!!.selectedStoreIdList.toString())
                        mStoreEditAdapter.notifyDataSetChanged()
                    }
                }
            }
            else if(gustoViewModel.allFlag.value == "nothing"){
                //select all off click
                Log.d("select", "2. nothing")
                thread(start = true){
                    gustoViewModel.selectedStoreIdList.clear()
                    Log.d("adapter select list", gustoViewModel!!.selectedStoreIdList.toString())
                    handler.post{
                        mStoreEditAdapter.notifyDataSetChanged()
                    }
                }
            }
            else if(gustoViewModel.allFlag.value == "changeOn"){
                //each cb on
                Log.d("select", "3. changeOn")
                binding.cbStoreEditAll.isChecked = true
            }
            else if(gustoViewModel.allFlag.value == "changeOff"){
                //each cb off
                Log.d("select", "4. changeOff")
                binding.cbStoreEditAll.isChecked = false
            }
            else{

            }
//            if(gustoViewModel.allFlag.value == "all"){
//                //전체선택
//                binding.cbStoreEditAll.isChecked = true
//                val handler = Handler(Looper.getMainLooper())
//                handler.postDelayed({
//                    gustoViewModel.selectedStoreIdList.clear()
//                    for(i in gustoViewModel.myAllStoreList){
//                        gustoViewModel.selectedStoreIdList.add(i.pinId)
//                    }
//                    mStoreEditAdapter.notifyDataSetChanged()
//                }, 10)
//
//
//
//            }
//            else if(gustoViewModel.allFlag.value == "false"){
//                binding.cbStoreEditAll.isChecked = false
//            }
//            else{
//                //전체 해제
//                binding.cbStoreEditAll.isChecked = false
//                val handler = Handler(Looper.getMainLooper())
//                handler.postDelayed({
//                    gustoViewModel.selectedStoreIdList.clear()
//                    mStoreEditAdapter.notifyDataSetChanged()
//                }, 10)
//            }
        })

    }

}