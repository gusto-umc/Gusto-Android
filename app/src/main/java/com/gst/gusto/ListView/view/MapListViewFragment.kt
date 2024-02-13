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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.ListView.Model.CategorySimple
import com.gst.gusto.ListView.Model.Store
import com.gst.gusto.ListView.adapter.CategoryBottomSheetDialog
import com.gst.gusto.ListView.adapter.ListViewCategoryAdapter
import com.gst.gusto.ListView.adapter.ListViewEditCategoryAdapter
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapListviewBinding

class MapListViewFragment : Fragment() {

    private lateinit var binding : FragmentMapListviewBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var orderFlag = 0
    // 0 : 최신순, 1 : 오래된 순, 2 : ㄱ 부터, 3: ㅎ부터, 4 : 방문횟수 높은 순, 5 : 방문회수 낮은 순
    private var sampleCategoryData = arrayListOf<CategorySimple>(
        CategorySimple(0, "카페", 0, 2),
        CategorySimple(1, "한식", 0, 0),
        CategorySimple(2, "일식", 0, 2),
                CategorySimple(3, "양식", 0, 2)

    )

    private var sampleStoreDataSave = arrayListOf<Store>(
        Store(id = 0, storeName = "구스토 레스토랑", location = "메롱시 메로나동 바밤바 24-6 1층", visitCount = null, storePhoto = 1, serverCategory ="양식", isSaved = false),
        Store(id = 1, storeName = "Gusto Restaurant", location = "메롱시 메로나동 바밤바 24-6 1층", visitCount = null, storePhoto = 1, serverCategory = "양식", isSaved = true)
    )


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

        /**
         * 카테고리Show 연결
         * 아이템 클릭 리스너
         */
        val cateShowAdapter = ListViewCategoryAdapter("show", requireFragmentManager(), view)
        cateShowAdapter.submitList(sampleCategoryData)
        categoryRvShow.adapter = cateShowAdapter
        categoryRvShow.layoutManager = LinearLayoutManager(this.requireActivity())


        /**
         * 카테고리Edit 연결
         * 체크박스 리스너 처리
         */
        val cateEditAdapter = ListViewEditCategoryAdapter("edit", view)
        cateEditAdapter.submitList(sampleCategoryData)
        categoryRvEdit.adapter = cateEditAdapter
        categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())

        /**
         * 뒤로가기 버튼, 시스템 뒤로가기 클릭리스너
         */
        binding.ivMapListviewBack.setOnClickListener {
            //Toast.makeText(this.requireContext(), "뒤로가기 클릭", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(view).navigate(R.id.action_mapListViewFragment_to_fragment_map)
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
            Toast.makeText(this.requireContext(), "편집 클릭", Toast.LENGTH_SHORT).show()
            goEdit()
        }

        /**
         * 정렬 순서 클릭 리스너
         */
        binding.layoutListviewOrder.setOnClickListener {
            // 0 : 최신순, 1 : 오래된 순, 2 : ㄱ 부터, 3: ㅎ 부터, 4 : 방문횟수 높은 순, 5 : 방문회수 낮은 순
            changeOrderFlag()
        }

        /**
         * 전체선택 클릭 리스너
         */
        binding.cbMapListviewAll.setOnCheckedChangeListener { buttonView, isChecked ->

        }

        /**
         * 추가fab 클릭 리스너
         */
        binding.fabMapListviewAdd.setOnClickListener {
            val categoryAddBottomSheetDialog = CategoryBottomSheetDialog(){
                when(it){
                    0 -> {
                        //추가 성공
                        Toast.makeText(context, "추가 성공", Toast.LENGTH_SHORT).show()
                        //카테고리 새로 받아와서 연결시키기
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
            Toast.makeText(this.requireContext(), "선택 삭제 클릭", Toast.LENGTH_SHORT).show()

            //삭제 동작
            //어댑터에 알리기
            cateShowAdapter.notifyDataSetChanged()
            categoryRvShow.adapter = cateShowAdapter
            cateEditAdapter.notifyDataSetChanged()
            categoryRvEdit.adapter = cateEditAdapter
            goShow()
        }



    }
    fun goShow(){
        binding.layoutListviewOrder.visibility = View.VISIBLE
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