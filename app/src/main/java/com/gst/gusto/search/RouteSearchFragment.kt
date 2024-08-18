package com.gst.gusto.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.adapter.CategoryAdapter
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSearch
import com.gst.gusto.api.ResponseStoreListItem
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

        //binding.edtRouteSearchbox.requestFocus()
        //util.openKeyboard(requireActivity())

        /**
         * category server 연결
         */

        binding.edtRouteSearchbox.text.clear()

        gustoViewModel.myAllCategoryList.clear()
        val rvSearchCategory = binding.rvRouteCategory

        val mCategoryAdapter = CategoryAdapter(view, "search", requireFragmentManager())
        mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
        mCategoryAdapter.viewModel = gustoViewModel
        mCategoryAdapter.mContext = context
        rvSearchCategory.adapter = mCategoryAdapter
        rvSearchCategory.layoutManager = LinearLayoutManager(this.requireActivity())

        var hasNext = false

        gustoViewModel.getPPMyCategory(null){
                result, getHasNext ->
            when(result){
                1 -> {
                    //success
                    mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                    hasNext = getHasNext
                    mCategoryAdapter.notifyDataSetChanged()
                }
                else-> {
                    Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }

            }
        }

        rvSearchCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                // 페이징 처리
                if(rvPosition == totalCount && hasNext) {
                    gustoViewModel.getPPMyCategory(gustoViewModel.myAllCategoryList.last().myCategoryId) {result, getHasNext ->
                        hasNext = getHasNext
                        when(result) {
                            1 -> {
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed({
                                    mCategoryAdapter.submitList(gustoViewModel.myAllCategoryList)
                                    mCategoryAdapter.notifyDataSetChanged()
                                }, 1000)

                            }
                            else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })


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

        fun searchRouteKeyword(){
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
                                    gustoViewModel!!.routeStorTmpData = ResponseStoreListItem(dataSet.storeId.toInt(),dataSet.storeName,dataSet.address,0,"")
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
        }
        binding.ivRouteSearchbox.setOnClickListener {
            var handled = false
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtRouteSearchbox.windowToken, 0)
            handled = true
            searchRouteKeyword()

            handled
        }

        binding.edtRouteSearchbox.setOnKeyListener { v, keyCode, event ->
            var handled = false
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // 엔터 눌렀을때 행동
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtRouteSearchbox.windowToken, 0)
                handled = true
                searchRouteKeyword()
            }
            handled
            false
        }

    }

}