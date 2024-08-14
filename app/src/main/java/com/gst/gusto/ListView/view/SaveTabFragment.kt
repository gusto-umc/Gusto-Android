package com.gst.gusto.ListView.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentSaveTabBinding

class SaveTabFragment : Fragment() {

    private lateinit var binding: FragmentSaveTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveTabBinding.inflate(inflater, container, false)
        val view = binding.root

        setupTabLayout()

        return view
    }

    private fun setupTabLayout() {
        val tabKnownPlaces = binding.tabKnownPlaces
        val tabNewPlace = binding.tabNewPlace

        tabKnownPlaces.setOnClickListener {
            selectTab(0)
        }

        tabNewPlace.setOnClickListener {
            selectTab(1)
        }

        // 초기 선택
        selectTab(0)
    }

    private fun selectTab(position: Int) {
        val tabKnownPlaces = binding.tabKnownPlaces
        val tabNewPlace = binding.tabNewPlace

        if (position == 0) { //아는 가게
            tabKnownPlaces.setBackgroundResource(R.drawable.tab_selected_left)
            tabKnownPlaces.setTextColor(resources.getColor(R.color.main_C))
            tabNewPlace.setBackgroundResource(R.drawable.tab_unselected_right)
            tabNewPlace.setTextColor(resources.getColor(R.color.gray_3))

            childFragmentManager.beginTransaction()
                .replace(R.id.tab_content, MapListViewSaveFragment())
                .commit()
        } else { //new place
            tabKnownPlaces.setBackgroundResource(R.drawable.tab_unselected_left)
            tabKnownPlaces.setTextColor(resources.getColor(R.color.gray_3))
            tabNewPlace.setBackgroundResource(R.drawable.tab_selected_right)
            tabNewPlace.setTextColor(resources.getColor(R.color.main_C))

            childFragmentManager.beginTransaction()
                .replace(R.id.tab_content, MapListViewNewPlaceFragment())
                .commit()
        }
    }
}
