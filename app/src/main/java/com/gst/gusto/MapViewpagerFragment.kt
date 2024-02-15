package com.gst.gusto


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast

class MapViewpagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map_viewpager, container, false)

        // ImageButton 찾기
        val listViewBtn = view.findViewById<ImageButton>(R.id.list_view_btn)

        // 클릭 리스너 설정
        listViewBtn.setOnClickListener {
            // 버튼이 클릭되었을 때 실행될 동작
            Toast.makeText(requireContext(), "목록보기 버튼이 클릭되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
