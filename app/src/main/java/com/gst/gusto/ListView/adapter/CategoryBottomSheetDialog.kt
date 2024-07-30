package com.gst.gusto.ListView.adapter

import android.media.Image
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gst.gusto.ListView.Model.CategoryDetail
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.RequestAddCategory
import org.w3c.dom.Text

class CategoryBottomSheetDialog(var data : CategoryDetail? = null, val itemClick : (Int) -> Unit) : BottomSheetDialogFragment() {


    var isAdd = false
    var categoryEdiBottomSheetData = data
    var viewModel : GustoViewModel? = null

    var selectedIconInt : Int = 1
    private val sampleIconArray = arrayListOf<Int>(
        R.drawable.ic_chat,
        R.drawable.ic_wine,
        R.drawable.ic_taco,
        R.drawable.ic_shrimp,
        R.drawable.ic_rice_cate,
        R.drawable.ic_reserv,
        R.drawable.ic_noodle,
        R.drawable.ic_music,
        R.drawable.ic_moods,
        R.drawable.ic_money,
        R.drawable.ic_likes,
        R.drawable.ic_friends,
        R.drawable.ic_fresh,
        R.drawable.ic_dish,
        R.drawable.ic_cake,
        R.drawable.ic_bread
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.map_listview_bottomsheetdialog_add, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d("dialog data", categoryEdiBottomSheetData.toString())

        view?.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.setOnClickListener {
            dialog?.dismiss()
        }

        if(isAdd){
            //카테고리 추가 상황 시
            //icon click listener
            view?.findViewById<ImageView>(R.id.iv_category_add_icon)?.setOnClickListener {
                if(view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.isVisible){
                    view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.GONE
                }
                else{
                    view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.VISIBLE
                }
            }
            view?.findViewById<Switch>(R.id.switch_category_public)?.isEnabled = true
            view?.findViewById<TextView>(R.id.tv_category_save)?.setOnClickListener {
                val title = view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)!!.text.toString()
                val desc = view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)!!.text.toString()
                var descData : String = ""

                if(title.isNullOrBlank()){
                    Toast.makeText(context, "카테고리명을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(desc.isNullOrBlank()){
                        descData = ""
                    }
                    else{
                        descData = desc
                    }
                    //2. 서버 연결
                    viewModel!!.addCategory(categoryName = title, desc = descData, categoryIcon = selectedIconInt, public = "PUBLIC"){
                            result ->
                        when(result){
                            0 -> {
                                //연결 성공
                                itemClick(0)
                            }
                            1-> {
                                //연결 실패
                                itemClick(1)
                            }
                        }
                    }
                    dialog?.dismiss()

                }

            }

        }
        else{

            // 수정 상황
            view?.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.tv_category_edit)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.tv_category_bottomsheet_banner)?.text = "카테고리 수정하기"
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)?.setText(categoryEdiBottomSheetData?.categoryName)
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)?.isEnabled = false
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)?.setText(categoryEdiBottomSheetData?.categoryDesc)
            view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)?.isEnabled = false
            // 아이콘 데이터 적용
            view?.findViewById<ImageView>(R.id.iv_category_add_icon)?.setImageResource(viewModel!!.findIconResource(categoryEdiBottomSheetData?.categoryIcon!!))
            selectedIconInt = categoryEdiBottomSheetData!!.categoryIcon!!

            view?.findViewById<TextView>(R.id.tv_category_edit)?.setOnClickListener {
                view?.findViewById<ImageView>(R.id.iv_bottomsheet_category_x)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.tv_category_edit)?.visibility = View.GONE
                view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)?.isEnabled = true
                view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)?.isEnabled = true
                // 아이콘 클릭리스너
                //icon click listener
                view?.findViewById<ImageView>(R.id.iv_category_add_icon)?.setOnClickListener {
                    if(view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.isVisible){
                        view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.GONE
                    }
                    else{
                        view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.VISIBLE
                    }
                }
                view?.findViewById<Switch>(R.id.switch_category_public)?.isEnabled = true
                //스위치 클릭 리스너
            }
            //스위치 데이터 적용
            //스위치 변경 불가
            view?.findViewById<Switch>(R.id.switch_category_public)?.isChecked =
                categoryEdiBottomSheetData!!.isPublic
            view?.findViewById<Switch>(R.id.switch_category_public)?.isEnabled = false

            view?.findViewById<TextView>(R.id.tv_category_save)?.setOnClickListener {
                val title = view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_title)!!.text.toString()
                val desc = view?.findViewById<EditText>(R.id.edt_category_add_bottomsheet_desc)!!.text.toString()
                var descData : String = ""

                if(title.isNullOrBlank()){
                    Toast.makeText(context, "카테고리명을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(desc.isNullOrBlank()){
                        descData = ""
                    }
                    else{
                        descData = desc
                    }
                    //2. 서버 연결
                    viewModel!!.editCategory(categoryName = title, desc = descData, categoryIcon = selectedIconInt, public = "PUBLIC", categoryId = categoryEdiBottomSheetData!!.id.toLong()){
                            result ->
                        when(result){
                            0 -> {
                                //연결 성공
                                itemClick(0)
                            }
                            1-> {
                                //연결 실패
                                itemClick(1)
                            }
                        }
                    }
                    dialog?.dismiss()

                }

            }

        }

        // 아이콘 Rv 연결
        val dCategoryIconAdapter = CategoryIconAdapter(sampleIconArray)
        dCategoryIconAdapter.setItemClickListener(object : CategoryIconAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, data: Int) {
                //선택 아이콘 변경
                selectedIconInt = (position+1)
                //iv src 변경 적용
                view?.findViewById<ImageView>(R.id.iv_category_add_icon)!!.setImageResource(sampleIconArray[position])
                view?.findViewById<ImageView>(R.id.iv_category_add_icon)!!.setImageResource(sampleIconArray[position])
                //rv visibility 변경
                view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)!!.visibility = View.GONE
            }

        })
        view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)?.adapter = dCategoryIconAdapter
        view?.findViewById<RecyclerView>(R.id.rv_category_add_icon)?.layoutManager = GridLayoutManager(this.activity, 8)

    }
}