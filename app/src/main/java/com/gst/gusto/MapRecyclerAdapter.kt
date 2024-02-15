import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gst.gusto.R
import com.gst.gusto.Util.util.Companion.setImage

class MapRecyclerAdapter(val list: ArrayList<String>) : RecyclerView.Adapter<MapRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recycler_pic: ImageView = itemView.findViewById(R.id.picture)
        //어디에 넣을지!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.map_recycler_view_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setImage(holder.recycler_pic,list[position],holder.itemView.context)
        //holder.recycler_pic.setImageDrawable(list[position].pic)
    }
}

data class Item(val pic: Drawable)
