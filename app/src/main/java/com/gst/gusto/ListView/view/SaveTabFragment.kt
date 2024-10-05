package com.gst.gusto.ListView.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentSaveTabBinding

class SaveTabFragment : Fragment() {

    private lateinit var binding: FragmentSaveTabBinding
    private val gustoViewModel: GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaveTabBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.tvMapSaveDong.text = gustoViewModel.dong.value!!
        setupTabLayout()

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

        binding.ivMapMapBack.setOnClickListener {
            findNavController().popBackStack()
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
