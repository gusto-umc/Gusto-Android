package com.gst.gusto.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSearch
import com.gst.gusto.databinding.FragmentRouteSearchBinding
import com.gst.gusto.search.adapter.SearchStoreAdapter

class RouteSearchFragment : Fragment() {

    private lateinit var binding : FragmentRouteSearchBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_route_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * category server 연결
         */
        val mCategoryAdapter = ListViewCategoryAdapter("route", requireFragmentManager(), view)

        gustoViewModel.getAllUserCategory {
            result ->
            when(result){
                0 -> {
                    mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                    mCategoryAdapter.viewModel = gustoViewModel
                    binding.rvRouteCategory.adapter = mCategoryAdapter
                    binding.rvRouteCategory.layoutManager = LinearLayoutManager(this.requireActivity())
                    binding.rvRouteCategory.visibility = View.VISIBLE
                }
                1 -> {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * 1. category Rv 연결, store Rv 연결
         */


        /**
         * 2. edt 빈칸 일 때 event 처리
         */
        binding.edtRouteSearchbox.doAfterTextChanged {
            binding.tvRouteNoResult.visibility = View.GONE
            if(binding.edtRouteSearchbox.text.isNullOrBlank()){
                binding.rvRouteSearchResult.visibility = View.GONE
                binding.rvRouteCategory.visibility = View.VISIBLE
            }
            else{
                binding.rvRouteCategory.visibility = View.GONE
            }
        }

        /**
         * 3. 검색 iv clickListener
         */
        val mRouteResultAdapter = SearchStoreAdapter()
        binding.ivRouteSearchbox.setOnClickListener {
            binding.rvRouteCategory.visibility = View.GONE
            binding.rvRouteSearchResult.visibility = View.VISIBLE
            //서버 연결 -> rv에 데이터 연결
            //공백 확인
            if (binding.edtRouteSearchbox.text.isNullOrBlank()) {
                binding.rvRouteSearchResult.visibility = View.GONE
            } else {
                // 서버 연결 후 검샥 결과 response
                util.hideKeyboard(this.requireActivity())
                gustoViewModel.getSearchResult(binding.edtRouteSearchbox.text.toString()){
                        result ->
                    when(result){
                        0 -> {
                            //success
                            //데이터셋 저장 후 연결(공백일 때 동작 확인)
                            mRouteResultAdapter.submitList(gustoViewModel.mapSearchArray)
                            mRouteResultAdapter.mContext = context
                            mRouteResultAdapter.setItemClickListener(object :
                                SearchStoreAdapter.OnItemClickListener {
                                override fun onClick(v: View, dataSet: ResponseSearch) {
                                    //페이지 이동 -> 루트 추가, 수정 화면으로 이동
                                    findNavController().popBackStack()
                                }

                            })
                            //visibility 설정, 어댑터 연결
                            binding.rvRouteSearchResult.visibility = View.VISIBLE
                            binding.rvRouteSearchResult.adapter = mRouteResultAdapter
                            binding.rvRouteSearchResult.layoutManager = LinearLayoutManager(this.requireActivity())
                            //키보드 내리기
                            if(gustoViewModel.mapSearchArray.isNullOrEmpty()){
                                binding.rvRouteCategory.visibility = View.GONE
                                binding.rvRouteSearchResult.visibility = View.GONE
                                binding.tvRouteNoResult.visibility = View.VISIBLE
                            }
                            else{
                                binding.rvRouteCategory.visibility = View.GONE
                                binding.rvRouteSearchResult.visibility = View.VISIBLE
                                binding.tvRouteNoResult.visibility = View.GONE
                            }
                        }
                        1 -> {
                            //fail
                            Log.d("search result", "fail")
                        }
                    }
                }


            }
            binding.edtRouteSearchbox.text.clear()
        }



    }
}