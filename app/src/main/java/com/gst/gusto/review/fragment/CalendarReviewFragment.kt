package com.gst.gusto.review.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.api.ResponseCalReview
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
import java.util.Calendar
import kotlin.coroutines.resume


class CalendarReviewFragment : Fragment() {

    lateinit var binding: FragmentCalendarReviewBinding
    lateinit var adapter: CalendarReviewAdapter

    val calendar = Calendar.getInstance()
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH) +  1

    private val gustoViewModel : GustoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
    }

    fun initView(){
        adapter = CalendarReviewAdapter(ArrayList(), context,
            itemClickListener = { reviewId ->
                if(reviewId != 0L){
                    val bundle = Bundle()
                    bundle.putLong("reviewId", reviewId)
                    bundle.putString("page","review")
                    findNavController().navigate(R.id.action_reviewFragment_to_reviewDetail, bundle)
                }
            })

        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 7)

            monthTextView.text = "${LocalDate.now().monthValue}월"

            calBtnLeft.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                updateCalendar()
                observeData()
            }

            calBtnRight.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                updateCalendar()
                observeData()
            }

            calReviewFab.setOnClickListener {
                gustoViewModel.reviewReturnPos = 1
                view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_reviewCalendarFragment_to_reviewAddSearch) }
            }

        }
    }

    fun updateCalendar() {
        binding.monthTextView.text = "${calendar.get(Calendar.MONTH) + 1}월" // +1을 해주어야 올바른 월을 표시합니다.
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1 // +1을 해주어야 합니다.
    }


    fun getData(year: Int, month: Int): LiveData<ResponseCalReview?> {
        val liveData = MutableLiveData<ResponseCalReview?>()
        val dates = LocalDate.of(year, month, 1)

        gustoViewModel.calView(null, 100, dates) { result, response ->
            if (result == 1) {
                response?.let {
                    liveData.postValue(it)
                }
            }
            Log.d("calResponse", liveData.value.toString())
        }

        return liveData
    }

    fun observeData() {
        getData(year, month).observe(viewLifecycleOwner, Observer { response ->
            setData(response, year, month)
        })
    }

    fun setData(response: ResponseCalReview?, year:Int, month: Int) {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val calList = List<ResponseCalReviews?>(daysInMonth){null}.toMutableList()

        for(day in 1..daysInMonth){
            response?.reviews?.forEach { item ->
                val visitedDate = LocalDate.parse(item.visitedDate)
                if (visitedDate.monthValue == month && visitedDate.year == year) {
                    calList[visitedDate.dayOfMonth - 1] = ResponseCalReviews(item.reviewId, item.visitedDate, item.images)
                }
            }
        }

        Log.d("calList", calList.toString())

        adapter.calendarList = calList
        adapter.notifyDataSetChanged()
    }

}