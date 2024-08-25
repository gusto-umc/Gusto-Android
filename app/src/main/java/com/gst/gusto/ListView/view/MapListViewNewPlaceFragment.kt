
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.api.GustoViewModel
import com.gst.gusto.databinding.FragmentMapListViewNewPlaceBinding

class MapListViewNewPlaceFragment : Fragment() {

    private var _binding: FragmentMapListViewNewPlaceBinding? = null
    private val binding get() = _binding!!

    private val newPlaceViewModel: GustoViewModel by activityViewModels()

    private lateinit var newPlaceAdapter: NewPlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapListViewNewPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        // 필터 설정 후 데이터 로드
        newPlaceViewModel.setUnsaveFilters(categoryId = 1, townName = "성수동")
    }

    private fun setupRecyclerView() {
        newPlaceAdapter = NewPlaceAdapter()
        binding.rvMapSaveUnvisited.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMapSaveUnvisited.adapter = newPlaceAdapter

        binding.rvMapSaveUnvisited.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val layoutManager = binding.rvMapSaveUnvisited.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                        newPlaceViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
                            if (hasNext) {
                                newPlaceViewModel.tapUnsavedStores()
                            }
                        })
                    }
                }
            }
        })
    }

    private fun setupObservers() {
        newPlaceViewModel.stores.observe(viewLifecycleOwner, Observer { stores ->
            newPlaceAdapter.submitList(stores)
        })

        newPlaceViewModel.hasNext.observe(viewLifecycleOwner, Observer { hasNext ->
            // 페이지네이션 로직은 이미 onScrolled()에서 처리
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
