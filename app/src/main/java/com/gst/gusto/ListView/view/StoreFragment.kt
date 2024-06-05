package com.gst.gusto.ListView.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.adapter.CategoryAdapter
import com.gst.gusto.ListView.adapter.ListViewStoreAdapter
import com.gst.gusto.ListView.adapter.StoreAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.PResponseStoreListItem
import com.gst.gusto.api.ResponseStoreListItem
import com.gst.gusto.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {

    private lateinit var binding : FragmentStoreBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private lateinit var mStoreAdapter : StoreAdapter
    private var hasNext = false
    private var signal = "map"

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

        val rvStore = binding.rvStore
        gustoViewModel.myAllStoreList.clear()
        var sign = arguments?.getString("sign")
        signal = sign!!


        if(sign != "search"){
            mStoreAdapter = StoreAdapter(view, "map")
        }
        else if(sign == "search"){
            mStoreAdapter = StoreAdapter(view, "search")
        }

        mStoreAdapter.setItemClickListener(object : StoreAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: PResponseStoreListItem, sign: String) {
                if(sign == "map"){
                    gustoViewModel!!.selectedDetailStoreId = dataSet.storeId!!.toInt()
                    Navigation.findNavController(view).navigate(R.id.action_storeFragment_to_storeDetailFragment)
                }
                else if(sign == "search"){
                    gustoViewModel!!.routeStorTmpData = ResponseStoreListItem(dataSet.storeId.toInt(),dataSet.storeName,dataSet.address,0,"")
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
            }

        })

        /**
         * 0. args 확인
         */

        if(sign == "map"){
            binding.tvStoreEdit.visibility = View.VISIBLE
            binding.tvSavedStore.visibility = View.VISIBLE
        }
        else{
            binding.tvStoreEdit.visibility = View.INVISIBLE
            binding.tvSavedStore.visibility = View.INVISIBLE
        }

        /**
         * 1. 데이터 연결
         * viewmodel의 selectedCategoryInfo 변수에서 가져오기
         */
        binding.ivStoreCategory.setImageResource(gustoViewModel.findIconResource(gustoViewModel.selectedCategoryInfo!!.categoryIcon))
        binding.tvStoreCategoryName.text = gustoViewModel.selectedCategoryInfo!!.categoryName
        binding.tvStoreCount.text = "${gustoViewModel.selectedCategoryInfo!!.pinCnt}개"



        /**
         * 2. , rv onclick 처리, 페이징
         */
        if(sign != "feed"){
            //서버 연결
            mStoreAdapter?.mContext = context
            mStoreAdapter?.submitList(gustoViewModel.myAllStoreList)
            mStoreAdapter?.gustoViewModel = gustoViewModel
            binding.rvStore.adapter = mStoreAdapter
            binding.rvStore.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


            rvStore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                                        mStoreAdapter?.submitList(gustoViewModel.myAllStoreList)
                                        mStoreAdapter?.notifyDataSetChanged()
                                    }, 1000)

                                }
                                else -> Toast.makeText(requireContext(), "서버와의 연결 불안정m", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })
        }
        else{
            //서버 연결
            mStoreAdapter?.mContext = context
            mStoreAdapter?.submitList(gustoViewModel.myAllStoreList)
            mStoreAdapter?.gustoViewModel = gustoViewModel
            binding.rvStore.adapter = mStoreAdapter
            binding.rvStore.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


            rvStore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val rvPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                    val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                    // 페이징 처리
                    if(rvPosition == totalCount && hasNext) {
                        gustoViewModel.getPPOtherStore(gustoViewModel.selectedCategoryInfo!!.myCategoryId, gustoViewModel.currentFeedNickname, gustoViewModel.myAllStoreList.last().pinId) { result, getHasNext ->
                            hasNext = getHasNext
                            when(result) {
                                1 -> {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed({
                                        mStoreAdapter?.submitList(gustoViewModel.myAllStoreList)
                                        mStoreAdapter?.notifyDataSetChanged()
                                    }, 1000)

                                }
                                else -> Toast.makeText(requireContext(), "서버와의 연결 불안정f", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })
        }



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

    override fun onResume() {
        super.onResume()

        if(signal == "map" || signal == "my" || signal == "search"){
            gustoViewModel.myAllStoreList.clear()
            gustoViewModel.getPPMyStore(gustoViewModel. selectedCategoryInfo!!.myCategoryId, null){
                    result, getHasNext ->
                when(result){
                    1 -> {
                        //success
                        mStoreAdapter?.submitList(gustoViewModel.myAllStoreList)
                        hasNext = getHasNext
                        mStoreAdapter?.notifyDataSetChanged()
                        binding.tvStoreCount.text = "${gustoViewModel.myAllStoreList.size}개"
                    }
                    else-> {
                        Toast.makeText(requireContext(), "서버와의 연결 불안정r", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        else if (signal == "feed"){
            gustoViewModel.myAllStoreList.clear()
            gustoViewModel.getPPOtherStore(gustoViewModel. selectedCategoryInfo!!.myCategoryId, gustoViewModel.currentFeedNickname, null){
                    result, getHasNext ->
                when(result){
                    1 -> {
                        //success
                        mStoreAdapter?.submitList(gustoViewModel.myAllStoreList)
                        hasNext = getHasNext
                        mStoreAdapter?.notifyDataSetChanged()
                        binding.tvStoreCount.text = "${gustoViewModel.myAllStoreList.size}개"
                    }
                    else-> {
                        Toast.makeText(requireContext(), "서버와의 연결 불안정r", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        else{

        }



    }

}