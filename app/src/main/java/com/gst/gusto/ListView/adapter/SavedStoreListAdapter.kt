
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gst.gusto.api.StoreData
import com.gst.gusto.databinding.ItemStoreBinding

class SavedStoreListAdapter : RecyclerView.Adapter<SavedStoreListAdapter.StoreViewHolder>() {

    private val storeList = mutableListOf<StoreData>()
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(dataSet: StoreData)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun submitList(newList: List<StoreData>) {
        storeList.clear()
        storeList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class StoreViewHolder(private val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(storeData: StoreData) {
            binding.tvItemStoreTitle.text = storeData.storeName
            binding.tvItemStoreCategory.text = storeData.category
            binding.tvItemStoreLocation.text = storeData.address

            // 이미지 로딩 (Glide 사용)
            Glide.with(binding.root.context)
                .load(storeData.reviewImg3.getOrNull(0))
                .into(binding.ivItemStoreImg1)

            Glide.with(binding.root.context)
                .load(storeData.reviewImg3.getOrNull(1))
                .into(binding.ivItemStoreImg2)

            Glide.with(binding.root.context)
                .load(storeData.reviewImg3.getOrNull(2))
                .into(binding.ivItemStoreImg3)

            binding.root.setOnClickListener {
                itemClickListener?.onClick(storeData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(storeList[position])
    }

    override fun getItemCount(): Int = storeList.size
}
