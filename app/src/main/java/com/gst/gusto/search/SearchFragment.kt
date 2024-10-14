package com.gst.gusto.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.gst.gusto.search.adapter.SearchStoreAdapter
import com.gst.gusto.R
import com.gst.gusto.util.util
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseSearch
import com.gst.gusto.api.ResponseSearch3
import com.gst.gusto.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private lateinit var mKeepStoreAdpater : SearchStoreAdapter
    private lateinit var mSearchStoreAdapter: SearchStoreAdapter
    private var hasNext = false


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
            util.openKeyboard(requireActivity())
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
            mKeepStoreAdpater.submitList(gustoViewModel.mapKeepArray2)
            mKeepStoreAdpater.setItemClickListener(object :
                SearchStoreAdapter.OnItemClickListener {
                override fun onClick(v: View, dataSet: ResponseSearch3) {
                    //데이터 넣기
                    gustoViewModel.selectStoreId = dataSet.storeId
                    gustoViewModel.storeIdList = gustoViewModel.mapKeepStoreIdArray
                    //페이지 이동
                    view?.let {
                        Navigation.findNavController(it)
                            .navigate(R.id.action_searchFragment_to_fragment_map_viewpager3)
                    }
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
                gustoViewModel.mapSearchArray2.clear()
                gustoViewModel.mapKeepArray2.clear()
                gustoViewModel.mapSearchStoreIdArray.clear()
                gustoViewModel.mapKeepStoreIdArray2.clear()
                gustoViewModel.searchKeepKeyword = binding.edtSearchSearchbox.text.toString()
                gustoViewModel.getPSearchResult(binding.edtSearchSearchbox.text.toString(), null){
                        result, getHasNext->
                    when(result){
                        1 -> {
                            hasNext = getHasNext
                            Log.d("search rv", hasNext.toString())
                            if(gustoViewModel.mapSearchArray2.isNullOrEmpty()){
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
                            mSearshResultAdapter.submitList(gustoViewModel.mapSearchArray2)
                            mSearshResultAdapter.setItemClickListener(object :
                                SearchStoreAdapter.OnItemClickListener {
                                override fun onClick(v: View, dataSet: ResponseSearch3) {
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
                            mSearshResultAdapter.notifyDataSetChanged()

                            //키보드 내리기
                            util.hideKeyboard(this.requireActivity())
                        }
                        else -> {
                            //fail
                            Log.d("search result", "fail")
                        }
                    }
                }


            }
        }

        binding.rvSearchResult

        binding.rvSearchKeep

        binding.edtSearchSearchbox.setOnKeyListener { v, keyCode, event ->
            var handled = false
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                // 엔터 눌렀을때 행동
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtSearchSearchbox.windowToken, 0)
                handled = true
                searchKeyword()
            }
            else if(keyCode === KeyEvent.KEYCODE_BACK){
                Log.d("KEYCODE_BACK", "KEYCODE_BACK")
                //Navigation.findNavController(view).
        }

            true
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

          binding.rvSearchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                          super.onScrolled(recyclerView, dx, dy)

                          val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                          // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                          val totalCount = recyclerView.adapter?.itemCount?.minus(1)
                          Log.d("search scroll", "rv search, rvPosition : ${rvPosition}, totalCount : $totalCount, hasNext : ")
                          // 페이징 처리
                          if(rvPosition == totalCount && hasNext) {
                              gustoViewModel.getPSearchResult(gustoViewModel.searchKeepKeyword, gustoViewModel.searchCursorId) { result, getHasNext ->
                                  hasNext = getHasNext
                                  when(result) {
                                      1 -> {
                                          val handler = Handler(Looper.getMainLooper())
                                          handler.postDelayed({
                                              mSearshResultAdapter?.submitList(gustoViewModel.mapSearchArray2)
                                              mSearshResultAdapter?.notifyDataSetChanged()
                                          }, 3000)

                                      }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정m", Toast.LENGTH_SHORT).show()
                                  }
                              }
                          }
                      }
                  })

                  binding.rvSearchKeep.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                          super.onScrolled(recyclerView, dx, dy)
                          Log.d("search scroll", "rv keep")
                          val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                          // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                          val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                          // 페이징 처리
                          if(rvPosition == totalCount && hasNext) {
                              gustoViewModel.getPSearchResult(gustoViewModel.searchKeepKeyword, gustoViewModel.searchCursorId) { result, getHasNext ->
                                  hasNext = getHasNext
                                  when(result) {
                                      1 -> {
                                          val handler = Handler(Looper.getMainLooper())
                                          handler.postDelayed({
                                              mKeepStoreAdpater?.submitList(gustoViewModel.mapSearchArray2)
                                              mKeepStoreAdpater?.notifyDataSetChanged()
                                          }, 3000)

                                      }
                                      else -> Toast.makeText(requireContext(), "서버와의 연결 불안정m", Toast.LENGTH_SHORT).show()
                                  }
                              }
                          }
                      }
                  })

        MobileAds.initialize(requireContext())
        val adLoader = AdLoader.Builder(requireContext(),resources.getString(R.string.admob_native))
            .forNativeAd { nativeAd ->
                // Handle the native ad loaded callback
                val styles = NativeTemplateStyle.Builder()
                    .build()
                val template = binding.nativeAdTemplate
                template.setStyles(styles)
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    Log.e("AdLoader", "Failed to load ad: ${adError}")
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        if(!gustoViewModel.keepFlag){
            binding.edtSearchSearchbox.text.clear()
        }

    }


}