package com.gst.gusto.ListView.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.NewPlaceAdapter
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapListViewNewPlaceBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapListViewNewPlaceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapListViewNewPlaceFragment : Fragment() {
    private lateinit var binding : FragmentMapListViewNewPlaceBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapListViewNewPlaceBinding.inflate(inflater, container, false)
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_listview_save, container, false) as FragmentMapListviewSaveBinding

        val view = binding.root


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //처음도전하는 연결_저장 0, 방문 X


        val newPlaceAdapter =  NewPlaceAdapter{ storeItem ->
            // 아이템 클릭 시 처리
        }

        binding.rvMapSaveUnvisited.apply {
            adapter = newPlaceAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        // 어댑터에 데이터 설정
        //newPlaceAdapter.submitList(storeList)

        /*
        val mSaveXAdapter = SavedStoreListAdapter("save", view)
        mSaveXAdapter.mContext = context
        mSaveXAdapter.submitList(gustoViewModel.mapUnvisitedList)
        mSaveXAdapter.setItemClickListener(object : SavedStoreListAdapter.OnItemClickListener{
            override fun onClick(v: View, dataSet: ResponseSavedStoreData) {
                gustoViewModel.selectStoreId = dataSet.storeId.toLong()
                gustoViewModel.storeIdList.clear()
                gustoViewModel.storeIdList = gustoViewModel.unsavedStoreIdList
                Navigation.findNavController(view).navigate(R.id.action_mapListViewSaveFragment_to_fragment_map_viewpager)
            }
        })
        binding.rvMapSaveUnvisited.adapter = mSaveXAdapter
        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(this.requireActivity())
         */


        //여기 어때요? _ 처음 가 보는


        /*
        //뒤로가기 클리 리스너
        binding.ivMapMapBack.setOnClickListener{
            findNavController().popBackStack()
        }

        //현재 위치(동) 받아오기
        binding.tvMapSaveDong.text = gustoViewModel.dong.value


         */
    }


}