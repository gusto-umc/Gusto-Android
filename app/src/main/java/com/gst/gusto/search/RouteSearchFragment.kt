package com.gst.gusto.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.StoreSearch
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentRouteSearchBinding
import com.gst.gusto.search.adapter.SearchStoreAdapter

class RouteSearchFragment : Fragment() {

    private lateinit var binding : FragmentRouteSearchBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    private var sampleRouteCategoryData = arrayListOf<CategorySimple>(
        CategorySimple(0, "카페", 0, 2),
        CategorySimple(1, "한식", 0, 0),
        CategorySimple(2, "일식", 0, 2),
        CategorySimple(3, "양식", 0, 2)

    )

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_route_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * category server 연결
         */

        /**
         * 1. category Rv 연결, store Rv 연결
         */
        val mCategoryAdapter = ListViewCategoryAdapter("route", requireFragmentManager(), view)
        mCategoryAdapter.submitList(gustoViewModel.myMapCategoryList)
        mCategoryAdapter.viewModel = gustoViewModel
        binding.rvRouteCategory.adapter = mCategoryAdapter
        binding.rvRouteCategory.layoutManager = LinearLayoutManager(this.requireActivity())

        /**
         * 2. edt 빈칸 일 때 event 처리
         */
        binding.edtSearchSearchbox.doAfterTextChanged {
            if(binding.edtSearchSearchbox.text.isNullOrBlank()){
                binding.rvRouteSearchResult.visibility = View.GONE
                binding.rvRouteCategory.visibility = View.VISIBLE
            }
        }

        /**
         * 3. 검색 iv clickListener
         */
        binding.ivSearchSearchbox.setOnClickListener {
            binding.rvRouteCategory.visibility = View.GONE
            binding.rvRouteSearchResult.visibility = View.VISIBLE
            //서버 연결 -> rv에 데이터 연결
        }

        /**
         * 검색 결과 store Rv 연결, clickListener
         */
        val mSerachStoreAdapter = SearchStoreAdapter()
        mSerachStoreAdapter.submitList(sampleResultArray)
        mSerachStoreAdapter.setItemClickListener(object :SearchStoreAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: StoreSearch) {
            //데이터 넘기기
            //루t트 페이지로 넘어가기
                Toast.makeText(context, dataSet.storeName, Toast.LENGTH_SHORT).show()
            }

        })
        binding.rvRouteSearchResult.adapter = mSerachStoreAdapter
        binding.rvRouteSearchResult.layoutManager = LinearLayoutManager(this.requireActivity())

    }
}