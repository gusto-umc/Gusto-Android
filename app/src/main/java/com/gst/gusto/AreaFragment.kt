import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.databinding.FragmentAreaBinding

class AreaFragment : Fragment() {
    lateinit var binding: FragmentAreaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_area, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_no_visited_rest)
        val recyclerView2: RecyclerView = view.findViewById(R.id.recyclerView_visited_rest)
        val recyclerView3: RecyclerView = view.findViewById(R.id.recyclerView_age_no_visited_rest)

        val imageResource = R.drawable.visit // 이미지 리소스 ID 가져오기

        // 아이템 담기
        val itemList = ArrayList<String>()

        val adapter = MapRecyclerAdapter(itemList)
        val adapter2 = MapRecyclerAdapter(itemList)
        val adapter3 = MapRecyclerAdapter(itemList)

        recyclerView.adapter = adapter
        recyclerView2.adapter = adapter2
        recyclerView3.adapter = adapter3

        // 그 외의 초기화 작업 수행

        return view

    }
}
