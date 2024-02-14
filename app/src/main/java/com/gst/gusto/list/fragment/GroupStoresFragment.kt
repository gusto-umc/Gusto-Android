package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentListGroupMStoresBinding
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.list.adapter.RestItem

class GroupStoresFragment : Fragment() {

    lateinit var binding: FragmentListGroupMStoresBinding
    private val tmpUri = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimages.velog.io%2Fimages%2Fmumuni%2Fpost%2F21c77b70-773d-46f7-b39c-f9d5cfe27431%2Fimage.png&imgrefurl=https%3A%2F%2Fvelog.io%2F%40mumuni%2FURI-%25EC%2599%2580-URL%25EC%259D%2580-%25EB%25AC%25B4%25EC%2597%2587%25EC%259D%25B8%25EA%25B0%2580&docid=cZmHSev7hFeYfM&tbnid=oVm5pUo9N-5kLM&vet=12ahUKEwifwKivhqWEAxWHs1YBHa5dDdQQM3oECDcQAA..i&w=1304&h=1204&ved=2ahUKEwifwKivhqWEAxWHs1YBHa5dDdQQM3oECDcQAA"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMStoresBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = binding.rv

        val itemList = ArrayList<RestItem>()

        itemList.add(RestItem(
            "구스또 레스토랑",
            "메롱시 메로나동 바밤바 24-6 1층",
            tmpUri,
            tmpUri,
            0,
            0
        ))
        itemList.add(RestItem(
            "구스또 레스토랑",
            "메롱시 메로나동 바밤바 24-6 1층",
            tmpUri,
            tmpUri,
            0,
            0
        ))
        itemList.add(RestItem(
            "구스또 레스토랑",
            "메롱시 메로나동 바밤바 24-6 1층",
            tmpUri,
            tmpUri,
            0,
            0
        ))

        val boardAdapter = GroupAdapter(itemList)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

}