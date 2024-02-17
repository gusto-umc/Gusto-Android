package com.gst.gusto.review.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseCalReviews
import com.gst.gusto.databinding.FragmentCalendarReviewBinding
import com.gst.gusto.review.adapter.CalendarReviewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import kotlin.coroutines.resume


class CalendarReviewFragment : Fragment() {

    lateinit var binding: FragmentCalendarReviewBinding
    lateinit var adapter: CalendarReviewAdapter

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarReviewBinding.inflate(inflater, container, false)

        initView()
        getData()

        return binding.root
    }

    fun initView(){
        adapter = CalendarReviewAdapter(ArrayList(), context,
                itemClickListener = { reviewId ->
                    val bundle = Bundle()
                    bundle.putLong("reviewId", reviewId)     // 리뷰 아이디 넘겨 주면 됨
                    bundle.putString("page","review")
                    findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail, bundle)
                })

        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 7)

            monthTextView.text = "${LocalDate.now().monthValue}월"
        }
    }

    fun getData(){

        val daysInMonth = YearMonth.now().lengthOfMonth()

        val calList = List<ResponseCalReviews?>(daysInMonth){null}.toMutableList()

        for(day in 1..daysInMonth){
            val dates = LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue, 1)

            gustoViewModel.getTokens(requireActivity() as MainActivity)
            gustoViewModel.calView(null, 100, dates) { result, response ->
                if (result == 1) {
                    response?.reviews?.let{
                        it.forEach { item ->
                            val date = LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue, day)
                            if(item.visitedDate == date.toString()){
                                calList[(day - 1)] = ResponseCalReviews(item.reviewId, item.visitedDate, item.images)
                            }
                        }
                    }

                }
                Log.d("listResponse", response.toString())
                adapter.calendarList = calList
                adapter.notifyDataSetChanged()
                Log.d("listCal", calList.toString())
            }
        }


    }

}