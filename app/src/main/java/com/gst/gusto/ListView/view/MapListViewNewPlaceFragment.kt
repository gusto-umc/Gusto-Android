package com.gst.gusto.ListView.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.ListView.adapter.DummyStoreAdapter
import com.gst.gusto.ListView.adapter.StoreData
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentMapListViewNewPlaceBinding

class MapListViewNewPlaceFragment : Fragment() {

    private lateinit var binding: FragmentMapListViewNewPlaceBinding
    private lateinit var adapter: DummyStoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩 설정
        binding = FragmentMapListViewNewPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더미 데이터 설정
        val dummyData = listOf(
            StoreData(
                title = "새로운 핫플레이스 1",
                category = "카페",
                location = "서울특별시 강남구 역삼로 10",
                imageRes1 = R.drawable.gst_dummypic,
                imageRes2 = R.drawable.gst_dummypic,
                imageRes3 = R.drawable.gst_dummypic
            ),
            StoreData(
                title = "새로운 핫플레이스 2",
                category = "음식점",
                location = "서울특별시 서초구 서초동 20",
                imageRes1 = R.drawable.gst_dummypic,
                imageRes2 = R.drawable.gst_dummypic,
                imageRes3 = R.drawable.gst_dummypic
            ),
            StoreData(
                title = "새로운 핫플레이스 3",
                category = "디저트",
                location = "서울특별시 마포구 연남동 15",
                imageRes1 = R.drawable.gst_dummypic,
                imageRes2 = R.drawable.gst_dummypic,
                imageRes3 = R.drawable.gst_dummypic
            )
        )

        // 어댑터 초기화 및 RecyclerView 연결
        adapter = DummyStoreAdapter(dummyData)
        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMapSaveUnvisited.adapter = adapter
    }
}
