package com.gst.gusto.ListView.view

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.adapter.CategoryBottomSheetDialog
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
import com.gst.gusto.ListView.adapter.ListViewEditCategoryAdapter
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapListviewBinding

class MapListViewFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var orderFlag = 0
    // 0 : 최신순, 1 : 오래된 순, 2 : ㄱ 부터, 3: ㅎ부터, 4 : 방문횟수 높은 순, 5 : 방문회수 낮은 순

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryRvShow = binding.rvMapListviewCategoryShow
        val categoryRvEdit = binding.rvMapListviewCategoryEdit

        var cateShowAdapter : ListViewCategoryAdapter? = null
        var cateEditAdapter : ListViewEditCategoryAdapter? = null


        fun getMapCategories(){
            gustoViewModel.getMapCategory(gustoViewModel.dong.value!!){
                    result ->
                when(result){
                    0 -> {
                        binding.tvTestAll.text = "${gustoViewModel.dong.value}의 저장 맛집"
                        /**
                         * 카테고리Show 연결
                         * 아이템 클릭 리스너
                         */
                        cateShowAdapter = ListViewCategoryAdapter("show", requireFragmentManager(), view)
                        cateShowAdapter!!.submitList(gustoViewModel.myMapCategoryList)
                        cateShowAdapter!!.viewModel = gustoViewModel
                        categoryRvShow.adapter = cateShowAdapter
                        categoryRvShow.layoutManager = LinearLayoutManager(this.requireActivity())

                        /**
                         * 카테고리Edit 연결
                         * 체크박스 리스너 처리
                         */
                        //cateEditAdapter = ListViewEditCategoryAdapter("edit", view, binding.cbMapListviewAll)
                        //데모데이용
                        cateEditAdapter = ListViewEditCategoryAdapter("show", view, binding.cbMapListviewAll)
                        cateEditAdapter!!.submitList(gustoViewModel.myMapCategoryList)
                        cateEditAdapter!!.viewModel = gustoViewModel
                        categoryRvEdit.adapter = cateEditAdapter
                        categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                    }
                    1 -> {
                        Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        // 서버 연결
        getMapCategories()


        /**
         * 뒤로가기 버튼, 시스템 뒤로가기 클릭리스너
         */
        binding.ivMapListviewBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        /**
         * 편집 뒤로가기 클릭 리스
         */
        binding.ivMapListviewEditBack.setOnClickListener {
            goShow()
        }

        /**
         * 편집 버튼 클릭리스너
         */
        binding.tvMapListviewEdit.setOnClickListener {
            goEdit()
        }

        /**
         * 정렬 순서 클릭 리스너
         */
        binding.layoutListviewOrder.visibility = View.GONE
        binding.layoutListviewOrder.setOnClickListener {
            // 0 : 최신순, 1 : 오래된 순, 2 : ㄱ 부터, 3: ㅎ 부터, 4 : 방문횟수 높은 순, 5 : 방문회수 낮은 순
            changeOrderFlag()
        }

        /**
         * 전체선택 클릭 리스너 - 카테고리
         */
        binding.layoutListviewSelect.setOnClickListener {
            binding.cbMapListviewAll.isChecked = true
        }

        binding.cbMapListviewAll.setOnCheckedChangeListener { buttonView, isChecked ->

            if(!isChecked){
                if(!gustoViewModel.cateRemoveFlag){
                    gustoViewModel.selectedCategory.clear()
                }
                else{
                    gustoViewModel.cateRemoveFlag = false
                }
            }
            gustoViewModel.changeCategoryFlag(isChecked)
        }
        gustoViewModel.categoryAllFlag.observe(viewLifecycleOwner, Observer{
            if(it == true){
                for(i in gustoViewModel.myMapCategoryList!!){
                    gustoViewModel.selectedCategory.add(i.myCategoryId)
                }
                binding.cbMapListviewAll.isChecked = true
                cateEditAdapter!!.selectedAllCategoryFlag = true
                categoryRvEdit.adapter = cateEditAdapter
                categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
            }
            else if(it == false){
                if(gustoViewModel.selectedCategory.isNotEmpty()){
                    binding.cbMapListviewAll.isChecked = false
                }
                else{
                    //어댑터 체크 처리
                    if(cateEditAdapter != null){
                        cateEditAdapter!!.selectedAllCategoryFlag = false
                        categoryRvEdit.adapter = cateEditAdapter
                        categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                    }
                    binding.cbMapListviewAll.isChecked = false
                }
            }
        })


        /**
         * 추가fab 클릭 리스너
         */
        binding.fabMapListviewAdd.setOnClickListener {
            val categoryAddBottomSheetDialog = CategoryBottomSheetDialog(){
                when(it){
                    0 -> {
                        //추가 성공
                        //카테고리 새로 받아와서 연결시키기
                        getMapCategories()
                    }
                    1 -> {
                        //추가 실페
                        Toast.makeText(context, "추가 fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            categoryAddBottomSheetDialog.isAdd = true
            categoryAddBottomSheetDialog.categoryEdiBottomSheetData = null
            categoryAddBottomSheetDialog.viewModel = gustoViewModel
            categoryAddBottomSheetDialog.show(requireFragmentManager(), categoryAddBottomSheetDialog.tag)
        }

        /**
         * 삭제fab 클릭 리스너
         */
        binding.fabMapListviewDelete.setOnClickListener {
            Log.d("selected", gustoViewModel.selectedCategory.toString())
            if(!gustoViewModel.selectedCategory.isNullOrEmpty()){
                //카테고리 삭제 진행
//                gustoViewModel.deleteCategory(gustoViewModel.selectedCategory[0]){
//                    result ->
//                    when(result){
//                        0 -> {
//                            //success
//                            getMapCategories()
//                        }
//                        1 -> {
//                            //fail
//                            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
                gustoViewModel.deleteCateogories(gustoViewModel.selectedCategory){
                        result ->
                    when(result){
                        0 -> {
                            //success
                            getMapCategories()
                        }
                        1 -> {
                            //fail
                            Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
            }
            gustoViewModel.cateRemoveFlag = false
            gustoViewModel.changeCategoryFlag(false)
            goShow()
        }


        /**
         * 편집 시 리스트 새로 불러오기
         */
        gustoViewModel.cateEditFlag.observe(viewLifecycleOwner, Observer {
            getMapCategories()
        })

    }
    fun goShow(){
        //binding.layoutListviewOrder.visibility = View.VISIBLE
        binding.layoutListviewSelect.visibility = View.GONE
        binding.cbMapListviewAll.isChecked = false
        binding.rvMapListviewCategoryShow.visibility = View.VISIBLE
        binding.rvMapListviewCategoryEdit.visibility = View.GONE
        binding.layoutMapListviewDelete.visibility = View.GONE
        binding.tvMapListviewEdit.visibility = View.VISIBLE
        binding.fabMapListviewAdd.visibility = View.VISIBLE
        binding.ivMapListviewEditBack.visibility = View.GONE
        binding.ivMapListviewBack.visibility = View.VISIBLE
        binding.layoutMapListview.setBackgroundResource(R.drawable.background_review_add)
    }
    fun goEdit(){
        binding.layoutListviewOrder.visibility = View.GONE
        binding.layoutListviewSelect.visibility = View.VISIBLE
        binding.rvMapListviewCategoryShow.visibility = View.GONE
        binding.rvMapListviewCategoryEdit.visibility = View.VISIBLE
        binding.layoutMapListviewDelete.visibility = View.VISIBLE
        binding.tvMapListviewEdit.visibility = View.GONE
        binding.fabMapListviewAdd.visibility = View.GONE
        binding.layoutMapListview.setBackgroundResource(R.color.white)
        binding.ivMapListviewBack.visibility = View.GONE
        binding.ivMapListviewEditBack.visibility = View.VISIBLE
    }
    fun changeOrderFlag(){
        when(orderFlag){
            0 -> {
                //리스트 정렬 변경(viewmodel)
                binding.tvListviewOrder.text = "오래된순"
                orderFlag = 1
            }
            1 -> {
                //리스트 정렬 변경(viewmodel)
                binding.tvListviewOrder.text = "ㄱ 부터"
                orderFlag = 2
            }
            2 -> {
                //리스트 정렬 변경(viewmodel)
                binding.tvListviewOrder.text = "ㅎ 부터"
                orderFlag = 3
            }
            3 -> {
                //리스트 정렬 변경(viewmodel)
                binding.tvListviewOrder.text = "방문횟수 ↑"
                orderFlag = 4
            }
            4 -> {
                //리스트 정렬 변경(viewmodel)
                binding.tvListviewOrder.text = "방문횟수 ↓"
                orderFlag = 5
            }
            else -> {
                //리스트 정렬 변경(viewmodel)
                binding.tvListviewOrder.text = "최신순"
                orderFlag = 0
            }
        }
    }

}