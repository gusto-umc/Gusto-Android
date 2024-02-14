package com.gst.gusto.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.search.adapter.SearchStoreAdapter
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private val sampleResultArray = arrayListOf<StoreSearch>(
        StoreSearch(1, "구스또 1호점", "양식", R.drawable.sample_store_4_img),
        StoreSearch(2, "구스또 2호점", "양식", R.drawable.sample_store_2_img),
        StoreSearch(3, "구스또 3호점", "양식", R.drawable.sample_store_3_img),
        StoreSearch(4, "구스또 4호점", "양식", R.drawable.sample_store_img),
        StoreSearch(5, "구스또 5호점", "양식", R.drawable.sample_store_4_img)
        )
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
         * map, group 구분 -> category visibility 설정 -> argument 처리
         */


        /**
         * Rv 어댑터 연결, 클릭 리스너 설정, 검색 클릭 리스너
         */
        val mSearshResultAdapter = SearchStoreAdapter()
        binding.ivSearchSearchbox.setOnClickListener {
            //공백 확인
            if (binding.edtSearchSearchbox.text.isNullOrBlank()) {
                binding.rvSearchResult.visibility = View.GONE
                binding.fabSearchMap.visibility = View.GONE
            } else {
                // 서버 연결 후 검샥 결과 response
                //데이터셋 저장 후 연결(공백일 때 동작 확인)
                mSearshResultAdapter.submitList(sampleResultArray)
                mSearshResultAdapter.setItemClickListener(object :
                    SearchStoreAdapter.OnItemClickListener {
                    override fun onClick(v: View, dataSet: StoreSearch) {
                        //fab visibility 설정
                        binding.fabSearchMap.visibility = View.GONE
                        //데이터 넣기
                        //페이지 이동
                        Navigation.findNavController(view)
                            .navigate(R.id.action_searchFragment_to_storeDetailFragment)
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
            binding.edtSearchSearchbox.text.clear()
        }

        /**
         * 지도보기 fab 클릭 리스너
         */
        binding.fabSearchMap.setOnClickListener{
            //데이터 버장
            //페이지 이동
            Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_fragment_map)
        }
    }
}