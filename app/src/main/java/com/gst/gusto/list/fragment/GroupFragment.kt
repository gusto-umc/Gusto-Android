package com.gst.gusto.list.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.util.DiaLogFragment
import com.gst.gusto.util.util.Companion.setImage
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentListGroupMBinding
import com.gst.gusto.list.adapter.GroupViewpagerAdapter
import java.lang.Math.abs

class GroupFragment : Fragment() {

    lateinit var binding: FragmentListGroupMBinding
    lateinit var mPager : ViewPager2
    private val gustoViewModel : GustoViewModel by activityViewModels()
    private var otherGroupMemberId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListGroupMBinding.inflate(inflater, container, false)


        binding.ivBack.setOnClickListener {
            val adapter = mPager.adapter as GroupViewpagerAdapter

            gustoViewModel.itemList.clear()
            gustoViewModel.tmpName =""

            binding.btnSave.visibility =View.GONE
            val frag = adapter.getCurrentFragment()
            Log.d("frag",frag.toString())
            if(frag is GroupRoutesFragment) {
                val tmp = frag.getCon()
                if(tmp!=null) {
                    if(tmp.currentDestination !=null && (tmp.currentDestination!!.id == R.id.fragment_group_m_route_stores
                                || tmp.currentDestination!!.id == R.id.fragment_group_m_route_create)) {
                        tmp.navigate(R.id.fragment_group_m_route_routes)
                    } else findNavController().popBackStack()
                } else findNavController().popBackStack()

            } else findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            val adapter = mPager.adapter as GroupViewpagerAdapter
            val frag = adapter.getCurrentFragment() as GroupRoutesFragment

            (frag.getNavHost().childFragmentManager.primaryNavigationFragment as GroupRouteCreateFragment).getRequestRoutesData()
            gustoViewModel.createRoute {result ->
                when(result) {
                    1 -> {
                        gustoViewModel.requestRoutesData = null
                        val tmp = frag.getCon()

                        if(frag is GroupRoutesFragment) {
                            if(tmp!=null) {
                                if(tmp.currentDestination !=null && (tmp.currentDestination!!.id == R.id.fragment_group_m_route_stores)) {
                                    tmp.navigate(R.id.fragment_group_m_route_routes)
                                } else findNavController().popBackStack()
                            } else findNavController().popBackStack()
                        } else findNavController().popBackStack()
                        binding.btnSave.visibility =View.GONE
                    }
                }
            }

            gustoViewModel.itemList.clear()
            gustoViewModel.tmpName =""
        }
        binding.lyPeople.setOnClickListener {
            gustoViewModel.getGroupMembers {result ->
                when(result) {
                    1 -> {

                        gustoViewModel.followListTitleName= "그룹 리스트&루트"
                        findNavController().navigate(R.id.action_groupFragment_to_followListFragment)
                    }
                    else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.tvNotice.setOnClickListener {
            val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_notice, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .create()

            mBuilder?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mBuilder?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            mBuilder.show()

            //팝업 사이즈 조절
            DisplayMetrics()
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            val display = (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            display.getSize(size)
            val screenWidth = size.x
            val popupWidth = (screenWidth * 0.8).toInt()
            mBuilder?.window?.setLayout(popupWidth, WindowManager.LayoutParams.WRAP_CONTENT)

            //팝업 타이틀 설정, 버튼 작용 시스템
            mDialogView.findViewById<TextView>(R.id.btn_dialog_one).setOnClickListener( {
                gustoViewModel.editGroupOption(null,mDialogView.findViewById<EditText>(R.id.tv_dialog_one_desc).text.toString()) {result, ->
                    when(result) {
                        1 -> {
                            binding.tvNotice.text = mDialogView.findViewById<EditText>(R.id.tv_dialog_one_desc).text.toString()
                            mBuilder.dismiss()
                        }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
        binding.btnRemoveInviteExit.setOnClickListener {
            if(gustoViewModel.groupOner) {
                if(binding.lyOnerYes.visibility == View.VISIBLE) binding.lyOnerYes.visibility = View.GONE
                else binding.lyOnerYes.visibility = View.VISIBLE
            } else {
                if(binding.lyOnerNot.visibility == View.VISIBLE) binding.lyOnerNot.visibility = View.GONE
                else binding.lyOnerNot.visibility = View.VISIBLE
            }
        }
        binding.btnOnerInvite.setOnClickListener {
            binding.lyOnerYes.visibility = View.GONE
            val dialogFragment = DiaLogFragment({ selectedItem ->
                // 아이템 클릭 이벤트를 처리하는 코드를 작성합니다.
                when (selectedItem) {
                    1 -> {

                    }
                }
            }, R.layout.bottomsheetdialog_invite, gustoViewModel,requireActivity() as MainActivity)
            dialogFragment.show(parentFragmentManager, dialogFragment.tag)
        }
        binding.btnOnerExit.setOnClickListener {
            if(otherGroupMemberId!=-1) {
                gustoViewModel.transferOwnership(otherGroupMemberId) {result ->
                    when(result) {
                        1 -> {
                            gustoViewModel.leaveGroup {result ->
                                when(result) {
                                    1 -> {
                                        findNavController().popBackStack()
                                    }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                gustoViewModel.deleteGroup {result ->
                    when(result) {
                        1 -> {
                            findNavController().popBackStack()
                        }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        binding.btnOnerRemove.setOnClickListener {
            gustoViewModel.deleteGroup {result ->
                when(result) {
                    1 -> {
                        findNavController().popBackStack()
                    }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnExit.setOnClickListener {
            gustoViewModel.leaveGroup {result ->
                when(result) {
                    1 -> {
                        findNavController().popBackStack()
                    }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnInvite.setOnClickListener {
            binding.lyOnerYes.visibility = View.GONE
            val dialogFragment = DiaLogFragment({ selectedItem ->
                // 아이템 클릭 이벤트를 처리하는 코드를 작성합니다.
                when (selectedItem) {
                    1 -> {

                    }
                }
            }, R.layout.bottomsheetdialog_invite, gustoViewModel,requireActivity() as MainActivity)
            dialogFragment.show(parentFragmentManager, dialogFragment.tag)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = binding.vpGroup
        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if(position==0) {
                    var alpha = 1 - abs(positionOffset*1.3)
                    if(alpha>1) alpha = 1.0
                    else if (alpha<0) alpha = 0.0
                    val mainColor = ContextCompat.getColor(requireContext(), R.color.sub_m)

                    val backgroundColor = Color.argb(
                        ((alpha) * 255).toInt(),
                        Color.red(mainColor),
                        Color.green(mainColor),
                        Color.blue(mainColor)
                    )
                    binding.lyGroup.setBackgroundColor(backgroundColor)
                }
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
        gustoViewModel.getGroup {result, data ->
            when(result) {
                1 -> {
                    if(data!=null) {
                        binding.tvName.text = data.groupName
                        gustoViewModel.currentGroupName = data.groupName
                        binding.tvComment.text = data.groupScript
                        binding.tvNotice.text = data.notice
                        binding.tvPeople.text = "${data.groupMembers.get(0).nickname} 님 외 ${data.groupMembers.size-1}명"
                        otherGroupMemberId = data.groupMembers.get(0).groupMemberId
                        if(otherGroupMemberId == data.owner&&data.groupMembers.size>1) otherGroupMemberId = data.groupMembers.get(1).groupMemberId
                        else otherGroupMemberId = -1
                        if(data.groupMembers.size==1) {
                            setImage(binding.ivProfileImage1,data.groupMembers.get(0).profileImg,requireContext())
                            binding.cdProfileImage2.visibility = View.INVISIBLE
                            binding.cdProfileImage3.visibility = View.INVISIBLE
                        } else if(data.groupMembers.size==2) {
                            setImage(binding.ivProfileImage1,data.groupMembers.get(0).profileImg,requireContext())
                            setImage(binding.ivProfileImage2,data.groupMembers.get(1).profileImg,requireContext())
                            binding.cdProfileImage3.visibility = View.INVISIBLE
                        } else {
                            setImage(binding.ivProfileImage1,data.groupMembers.get(0).profileImg,requireContext())
                            setImage(binding.ivProfileImage2,data.groupMembers.get(1).profileImg,requireContext())
                            setImage(binding.ivProfileImage3,data.groupMembers.get(2).profileImg,requireContext())
                        }
                    }
                }else -> Toast.makeText(requireContext(), "서버와의 연결 불안정", Toast.LENGTH_SHORT).show()
            }
        }
        mPager.adapter = GroupViewpagerAdapter(requireActivity(),GroupRoutesFragment(gustoViewModel.groupFragment),mPager,2)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("viewmodel","destroy gorup fragment ")
        gustoViewModel.groupFragment = 0
        gustoViewModel.itemList.clear()
        gustoViewModel.tmpName =""
    }

    override fun onPause() {
        super.onPause()
        //mPager.adapter = null
    }
    override fun onResume() {
        super.onResume()
        if(gustoViewModel.groupFragment > 0)  {
            mPager.adapter = GroupViewpagerAdapter(requireActivity(),GroupRoutesFragment(gustoViewModel.groupFragment),mPager,2)
            mPager.setCurrentItem(1,false)
            binding.lyGroup.setBackgroundColor(Color.TRANSPARENT)
        }
    }

}