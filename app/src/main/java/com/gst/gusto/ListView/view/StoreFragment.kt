package com.gst.gusto.ListView.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.CategoryAdapter
import com.gst.gusto.ListView.adapter.ListViewStoreAdapter
import com.gst.gusto.ListView.adapter.StoreAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {

    private lateinit var binding : FragmentStoreBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 1. 데이터 연결
         * viewmodel의 selectedCategoryInfo 변수에서 가져오기
         */
        //binding.ivStoreBack.setImageResource(gustoViewModel.findIconResource(gustoViewModel.selectedCategoryInfo!!.categoryIcon))
        //binding.tvStoreCategoryName.text = gustoViewModel.selectedCategoryInfo!!.categoryName
        //저장 개수



        /**
         * 2. , rv onclick 처리, 페이징
         */
        val mStoreAdapter = StoreAdapter(view)
        //서버 연결
        mStoreAdapter.mContext = context
        mStoreAdapter.submitList(gustoViewModel.testStoreData!!)
        mStoreAdapter.gustoViewModel = gustoViewModel
        binding.rvStore.adapter = mStoreAdapter
        binding.rvStore.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        /**
         * 3. onclick 처리
         */
        binding.ivStoreBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvStoreEdit.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_storeFragment_to_storeEditFragment)
        }


        /**
         * 4. 정렬 처리 -> 서버
         */
        binding.layoutStoreOrder.setOnClickListener {
            var order = "ㄱ 부터"
            when(binding.tvListviewOrder.text){
                "ㄱ 부터" -> {
                    binding.tvListviewOrder.text = "ㅎ 부터"
                    order = "ㄱ 부터"
                }
                "ㅎ 부터" -> {
                    binding.tvListviewOrder.text = "방문횟수 ↑"
                    order = "ㅎ 부터"
                }
                "방문횟수 ↑" -> {
                    binding.tvListviewOrder.text = "방문횟수 ↓"
                    order = "방문횟수 ↑"
                }
                "방문횟수 ↓" -> {
                    binding.tvListviewOrder.text = "최신순"
                    order = "방문횟수 ↓"
                }
                "최신순" -> {
                    binding.tvListviewOrder.text = "오래된순"
                    order = "최신순"
                }
                else -> {
                    binding.tvListviewOrder.text = "ㄱ 부터"
                    order = "오래된순"
                }
            }
            //서버 연결 -> rv 연결
            //order변수 화룡해서 서버에 정렬 순서 정보 보내기
        }

    }

}