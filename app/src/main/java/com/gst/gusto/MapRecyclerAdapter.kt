
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.util.util.Companion.setImage

class MapRecyclerAdapter(val list: ArrayList<String>) : RecyclerView.Adapter<MapRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //어댑터 클래서 외부 혹은 내부에 RecyclerView.ViewHolder클래스를 상속
        val recyclerPic: ImageView = itemView.findViewById(R.id.picture) //map_recycler_view_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //뷰가 만들어질때(create) 호출되는 메소드
        Log.d("log_test","onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_recycler_view_list, parent, false)
        Log.d("log_test","onCreateViewHolder2")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        //리스트 수를 return
        Log.d("log_test", "${list.take(10).count()}")
        return minOf(10, list.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //뷰가 바인드(Bind)될때 호출되는 메소드
        //onBindViewHolder는 뷰에 내용이 씌워질때
        Log.d("log_test","onBindViewHolder")
        setImage(holder.recyclerPic, list[position], holder.itemView.context)
        Log.d("log_test","onBindViewHolder2")
    }
    /*
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setImage(holder.recycler_pic,list[position],holder.itemView.context)
        //holder.recycler_pic.setImageDrawable(list[position].pic)
    }
     */
}
