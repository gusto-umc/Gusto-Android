package com.gst.gusto.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.search.adapter.SearchStoreAdapter
import com.gst.gusto.R
import com.gst.gusto.Util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSearch
import com.gst.gusto.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Rv 어댑터 연결, 클릭 리스너 설정, 검색 클릭 리스너
         */

        binding.edtSearchSearchbox.setOnClickListener {
            binding.tvNoResult.visibility = View.GONE
        }

        val mSearshResultAdapter = SearchStoreAdapter()
        mSearshResultAdapter.mContext = context
        binding.ivSearchSearchbox.setOnClickListener {
            //공백 확인
            if (binding.edtSearchSearchbox.text.isNullOrBlank()) {
                binding.rvSearchResult.visibility = View.GONE
                binding.fabSearchMap.visibility = View.GONE
            } else {
                // 서버 연결 후 검샥 결과 response
                util.hideKeyboard(this.requireActivity())
                gustoViewModel.getSearchResult(binding.edtSearchSearchbox.text.toString()){
                    result ->
                    when(result){
                        0 -> {
                            if(gustoViewModel.mapSearchArray.isNullOrEmpty()){
                                binding.rvSearchResult.visibility = View.GONE
                                binding.tvNoResult.visibility = View.VISIBLE
                            }
                            else{
                                binding.rvSearchResult.visibility = View.VISIBLE
                                binding.tvNoResult.visibility = View.GONE
                            }
                            //success
                            //데이터셋 저장 후 연결(공백일 때 동작 확인)
                            mSearshResultAdapter.submitList(gustoViewModel.mapSearchArray)
                            mSearshResultAdapter.setItemClickListener(object :
                                SearchStoreAdapter.OnItemClickListener {
                                override fun onClick(v: View, dataSet: ResponseSearch) {
                                    //fab visibility 설정
                                    binding.fabSearchMap.visibility = View.GONE
                                    //데이터 넣기
                                    gustoViewModel.selectStoreId = dataSet.storeId
                                    gustoViewModel.storeIdList = gustoViewModel.mapSearchStoreIdArray
                                    //페이지 이동
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_searchFragment_to_fragment_map_viewpager3)
                                }

                            })
                            //visibility 설정, 어댑터 연결
                            binding.rvSearchResult.visibility = View.VISIBLE
                            binding.rvSearchResult.adapter = mSearshResultAdapter
                            binding.rvSearchResult.layoutManager = LinearLayoutManager(this.requireActivity())
                            //키보드 내리기
                            // fab visibility 설정
                            binding.fabSearchMap.visibility = View.VISIBLE
                        }
                        1 -> {
                            //fail
                            Log.d("search result", "fail")
                        }
                    }
                }


            }
            binding.edtSearchSearchbox.text.clear()
        }

        /**
         * 지도보기 fab 클릭 리스너
         */
        binding.fabSearchMap.setOnClickListener{
            //데이터 저장
            gustoViewModel.selectStoreId = gustoViewModel.mapSearchStoreIdArray[0]
            gustoViewModel.storeIdList = gustoViewModel.mapSearchStoreIdArray
            //페이지 이동
           Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_fragment_map_viewpager3)
        }
    }
}