package com.gst.gusto.list.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentGroupBinding
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.ListGroupAdapter
import com.gst.gusto.list.adapter.RestItem

class GroupFragment : Fragment() {

    lateinit var binding: FragmentGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_listFragment)
        }

        val rv_board = binding.rvGroup

        val itemList = ArrayList<RestItem>()

        itemList.add(RestItem("구스또 레스토랑","메롱시 메로나동 바밤바 24-6 1층",R.drawable.ic_launcher_background,R.drawable.ic_launcher_background))
        itemList.add(RestItem("구스또 레스토랑","메롱시 메로나동 바밤바 24-6 1층",R.drawable.ic_launcher_background,R.drawable.ic_launcher_background))
        itemList.add(RestItem("구스또 레스토랑","메롱시 메로나동 바밤바 24-6 1층",R.drawable.ic_launcher_background,R.drawable.ic_launcher_background))
        itemList.add(RestItem("구스또 레스토랑","메롱시 메로나동 바밤바 24-6 1층",R.drawable.ic_launcher_background,R.drawable.ic_launcher_background))


        val boardAdapter = GroupAdapter(itemList)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        return binding.root

    }

}