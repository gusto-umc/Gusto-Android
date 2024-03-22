package com.gst.gusto.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.search.adapter.SearchStoreAdapter
import com.gst.gusto.R
import com.gst.gusto.util.util
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

        //변수
        val mKeepStoreAdpater = SearchStoreAdapter()
        val mSearshResultAdapter = SearchStoreAdapter()

        mSearshResultAdapter.mContext = context
        mKeepStoreAdpater.mContext = context


        if(!gustoViewModel.keepFlag){
            // 첫 진임 시
            //binding.edtSearchSearchbox.requestFocus()
            //util.openKeyboard(requireActivity())
            //저장 rv visibility 설정
            binding.rvSearchKeep.visibility = View.GONE
            binding.tvNoResult.visibility = View.GONE
            binding.fabSearchMap.visibility = View.GONE
            binding.edtSearchSearchbox.text.clear()

        }
        else{
            //목록보기에서 이동 시
            util.hideKeyboard(this.requireActivity())
            gustoViewModel.mapSearchArray = gustoViewModel.mapKeepArray
            gustoViewModel.mapSearchStoreIdArray = gustoViewModel.mapKeepStoreIdArray


            //edt에 검색 결과 넣기
            binding.edtSearchSearchbox.setText(gustoViewModel.searchKeepKeyword)

            //visibility 처리
            binding.rvSearchKeep.visibility = View.VISIBLE
            binding.rvSearchResult.visibility = View.GONE
            binding.tvNoResult.visibility = View.GONE
            binding.fabSearchMap.visibility = View.VISIBLE

            //저장 rv 연결
            mKeepStoreAdpater.submitList(gustoViewModel.mapKeepArray)
            mKeepStoreAdpater.setItemClickListener(object :
                SearchStoreAdapter.OnItemClickListener {
                override fun onClick(v: View, dataSet: ResponseSearch) {
                    //데이터 넣기
                    gustoViewModel.selectStoreId = dataSet.storeId
                    gustoViewModel.storeIdList = gustoViewModel.mapKeepStoreIdArray
                    //페이지 이동
                    Navigation.findNavController(view)
                        .navigate(R.id.action_searchFragment_to_fragment_map_viewpager3)
                }

            })
            //visibility 설정, 어댑터 연결
            binding.rvSearchKeep.visibility = View.VISIBLE
            binding.rvSearchKeep.adapter = mKeepStoreAdpater
            binding.rvSearchKeep.layoutManager = LinearLayoutManager(this.requireActivity())

            //keep flag 바꾸기
            gustoViewModel.keepFlag = false

        }



        /**
         * Rv 어댑터 연결, 클릭 리스너 설정, 검색 클릭 리스너
         */
        binding.edtSearchSearchbox.setOnClickListener {
            binding.edtSearchSearchbox.text.clear()
            binding.tvNoResult.visibility = View.GONE
            binding.rvSearchKeep.visibility = View.GONE
            binding.rvSearchResult.visibility = View.GONE
            binding.fabSearchMap.visibility = View.GONE
        }

        /**
         * 검색 함수
         */

        fun searchKeyword(){
            //공백 확인
            if (binding.edtSearchSearchbox.text.isNullOrBlank()) {
                binding.rvSearchResult.visibility = View.GONE
                binding.rvSearchKeep.visibility = View.GONE
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
                                binding.rvSearchKeep.visibility = View.GONE
                                binding.tvNoResult.visibility = View.GONE
                                binding.fabSearchMap.visibility = View.VISIBLE
                            }
                            //success
                            //데이터셋 저장 후 연결(공백일 때 동작 확인)
                            mSearshResultAdapter.submitList(gustoViewModel.mapSearchArray)
                            mSearshResultAdapter.setItemClickListener(object :
                                SearchStoreAdapter.OnItemClickListener {
                                override fun onClick(v: View, dataSet: ResponseSearch) {
                                    //데이터 넣기
                                    gustoViewModel.selectStoreId = dataSet.storeId
                                    gustoViewModel.storeIdList = gustoViewModel.mapKeepStoreIdArray
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
                            util.hideKeyboard(this.requireActivity())
                        }
                        1 -> {
                            //fail
                            Log.d("search result", "fail")
                        }
                    }
                }


            }
        }

        binding.edtSearchSearchbox.setOnKeyListener { v, keyCode, event ->
            var handled = false
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                // 엔터 눌렀을때 행동
                val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtSearchSearchbox.windowToken, 0)
                handled = true
                searchKeyword()
            }
            handled
            false
        }


        binding.layoutSearchSearchbox.setOnClickListener {
            var handled = false
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtSearchSearchbox.windowToken, 0)
            handled = true
            searchKeyword()

            handled
        }

        /**
         * 지도보기 fab 클릭 리스너
         */
        binding.fabSearchMap.setOnClickListener{
            //데이터 저장
            gustoViewModel.selectStoreId = gustoViewModel.mapKeepStoreIdArray[0]
            gustoViewModel.storeIdList = gustoViewModel.mapKeepStoreIdArray

            //페이지 이동
           Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_fragment_map_viewpager3)
        }
    }

    override fun onResume() {
        super.onResume()
        if(!gustoViewModel.keepFlag){
            binding.edtSearchSearchbox.text.clear()
        }
    }
}