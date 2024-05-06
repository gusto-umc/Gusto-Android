package com.gst.gusto.ListView.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentStoreEditBinding

class StoreEditFragment : Fragment() {

    private lateinit var binding : FragmentStoreEditBinding
    private val gustoViewModel : GustoViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 1. 데이터 적용 -> 서버 연결 필요
         */


        /**
         * 2. rv 연결
         */

        /**
         * 3. onclick event
         */
        binding.ivStoreEditBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabStoreEditDelete.setOnClickListener{
            //서버 연결
        }

        /**
         * 4. 전체 선택
         */
    }

}