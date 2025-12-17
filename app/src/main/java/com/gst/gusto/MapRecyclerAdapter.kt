import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.ResponseSavedStoreData
import com.gst.gusto.util.util.Companion.setImage

class MapRecyclerAdapter(val list: List<ResponseSavedStoreData>,val activity: MainActivity) : RecyclerView.Adapter<MapRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val storeDistance: TextView = itemView.findViewById(R.id.storeDistance)
        val storeLocation: TextView = itemView.findViewById(R.id.storeLocation)
        val btnMore: TextView = itemView.findViewById(R.id.btnMore)
        val picture: ImageView = itemView.findViewById(R.id.picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.map_recycler_view_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // 텍스트 설정
        holder.storeName.text = item.storeName
        holder.storeDistance.text = "100m 임시"
        holder.storeLocation.text = item.address

        // 이미지 설정 (setImage 유틸 사용)
        setImage(holder.picture, item.reviewImg, holder.itemView.context)

        // 버튼 클릭 리스너
        holder.btnMore.setOnClickListener {
            // 여기서 클릭 이벤트 처리
            activity.gustoViewModel.selectedDetailStoreId = item.storeId.toInt()
            activity.getCon().navigate(R.id.action_fragment_map_to_storeDetailFragment)
            // 예: Toast.makeText(holder.itemView.context, "${item.storeName} 더보기 클릭", Toast.LENGTH_SHORT).show()
        }
    }
}
