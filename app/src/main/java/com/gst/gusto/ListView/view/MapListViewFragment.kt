package com.gst.gusto.ListView.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩 초기화
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview, container, false)
        Log.d("launching","binding")
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 요소 초기화
        val categoryRvShow = binding.rvMapListviewCategoryShow
        val categoryRvEdit = binding.rvMapListviewCategoryEdit

        var cateShowAdapter : ListViewCategoryAdapter? = null
        var cateEditAdapter : ListViewEditCategoryAdapter? = null

        gustoViewModel.categorySlist.clear()
        Log.d("launching","clear")

        fun getMapCategories() {
            gustoViewModel.getMapCategory(gustoViewModel.dong.value!!) { result ->
                when (result) {
                    0 -> {
                        Log.d("launching","동 값 찾기 성공")
                        Log.d("dong", gustoViewModel.dong.value!!.toString())
                        binding.tvTestAll.text = "${gustoViewModel.dong.value}의 저장 맛집"

                        cateShowAdapter = ListViewCategoryAdapter("show", requireFragmentManager(), view)
                        cateShowAdapter!!.submitList(gustoViewModel.myMapCategoryList)
                        cateShowAdapter!!.viewModel = gustoViewModel
                        cateShowAdapter!!.mContext = context
                        categoryRvShow.adapter = cateShowAdapter
                        Log.d("launching","어댑터")
                        categoryRvShow.layoutManager = LinearLayoutManager(this.requireActivity())
                        Log.d("launching","카테고리")

                        cateEditAdapter = ListViewEditCategoryAdapter()
                        cateEditAdapter!!.submitList(gustoViewModel.myMapCategoryList)
                        Log.d("launching","카테괴2")
                        cateEditAdapter!!.viewModel = gustoViewModel
                        categoryRvEdit.adapter = cateEditAdapter
                        categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                        Log.d("launching","맛집찾기 성공")
                    }
                    1 -> {
                        Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        getMapCategories()
        Log.d("launching","CATE 성공!")

        binding.ivMapListviewBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        binding.ivMapListviewEditBack.setOnClickListener {
            binding.cbMapListviewAll.isChecked = false
            gustoViewModel.changeCategoryList(false, null)
            if (cateEditAdapter != null) {
                cateEditAdapter!!.selectedAllCategoryFlag = false
                categoryRvEdit.adapter = cateEditAdapter
                categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
            }
            goShow()
        }

        binding.tvMapListviewEdit.setOnClickListener {
            goEdit()
        }

        binding.layoutListviewOrder.visibility = View.GONE
        binding.layoutListviewOrder.setOnClickListener {
            changeOrderFlag()
        }

        binding.layoutListviewSelect.setOnClickListener {
            if (binding.cbMapListviewAll.isChecked) {
                binding.cbMapListviewAll.isChecked = false
                gustoViewModel.changeCategoryList(false, null)
                gustoViewModel.clearItem()
                if (cateEditAdapter != null) {
                    cateEditAdapter!!.selectedAllCategoryFlag = false
                    categoryRvEdit.adapter = cateEditAdapter
                    categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                }
            } else {
                gustoViewModel.clearItem()
                binding.cbMapListviewAll.isChecked = true
                if (cateEditAdapter != null) {
                    cateEditAdapter!!.selectedAllCategoryFlag = true
                    categoryRvEdit.adapter = cateEditAdapter
                    categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                }
            }
        }

        binding.cbMapListviewAll.setOnClickListener {
            if (binding.cbMapListviewAll.isChecked) {
                binding.cbMapListviewAll.isChecked = false
                gustoViewModel.changeCategoryList(false, null)
                gustoViewModel.clearItem()
                if (cateEditAdapter != null) {
                    cateEditAdapter!!.selectedAllCategoryFlag = false
                    categoryRvEdit.adapter = cateEditAdapter
                    categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                }
            } else {
                gustoViewModel.clearItem()
                binding.cbMapListviewAll.isChecked = true
                if (cateEditAdapter != null) {
                    cateEditAdapter!!.selectedAllCategoryFlag = true
                    categoryRvEdit.adapter = cateEditAdapter
                    categoryRvEdit.layoutManager = LinearLayoutManager(this.requireActivity())
                }
            }
        }

        gustoViewModel.selectedCategoryList.observe(viewLifecycleOwner, Observer {
            binding.cbMapListviewAll.isChecked = it.size == gustoViewModel.myMapCategoryList!!.size
        })

        binding.fabMapListviewAdd.setOnClickListener {
            val categoryAddBottomSheetDialog = CategoryBottomSheetDialog() {
                when (it) {
                    0 -> getMapCategories()
                    1 -> Toast.makeText(context, "추가 fail", Toast.LENGTH_SHORT).show()
                }
            }
            categoryAddBottomSheetDialog.isAdd = true
            categoryAddBottomSheetDialog.categoryEdiBottomSheetData = null
            categoryAddBottomSheetDialog.viewModel = gustoViewModel
            categoryAddBottomSheetDialog.show(requireFragmentManager(), categoryAddBottomSheetDialog.tag)
        }

        binding.fabMapListviewDelete.setOnClickListener {
            if (!gustoViewModel.selectedCategoryList.value.isNullOrEmpty()) {
                gustoViewModel.deleteCateogories(gustoViewModel.selectedCategoryList.value!!) { result ->
                    when (result) {
                        0 -> getMapCategories()
                        1 -> Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            gustoViewModel.cateRemoveFlag = false
            gustoViewModel.changeCategoryFlag(false)
            goShow()
        }

        gustoViewModel.cateEditFlag.observe(viewLifecycleOwner, Observer {
            getMapCategories()
        })
    }

    fun goShow() {
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

    fun goEdit() {
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

    fun changeOrderFlag() {
        when (orderFlag) {
            0 -> {
                binding.tvListviewOrder.text = "오래된순"
                orderFlag = 1
            }
            1 -> {
                binding.tvListviewOrder.text = "ㄱ 부터"
                orderFlag = 2
            }
            2 -> {
                binding.tvListviewOrder.text = "ㅎ 부터"
                orderFlag = 3
            }
            3 -> {
                binding.tvListviewOrder.text = "방문횟수 ↑"
                orderFlag = 4
            }
            4 -> {
                binding.tvListviewOrder.text = "방문횟수 ↓"
                orderFlag = 5
            }
            else -> {
                binding.tvListviewOrder.text = "최신순"
                orderFlag = 0
            }
        }
    }
}
