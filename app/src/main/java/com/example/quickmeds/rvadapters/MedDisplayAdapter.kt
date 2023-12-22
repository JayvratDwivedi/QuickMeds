package com.example.quickmeds.rvadapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickmeds.databinding.MeddisplaymainItemBinding
import com.example.quickmeds.models.MedDisplayModel

class MedDisplayAdapter(
    private val context: Context,
    private val list: List<MedDisplayModel>,
    private val productClickInterface: ProductOnClickInterface,
    private val likeClickInterface: LikeOnClickInterface,

    ) : RecyclerView.Adapter<MedDisplayAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: MeddisplaymainItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MeddisplaymainItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.tvNameMedDisplayItem.text = "${currentItem.name}"
        holder.binding.tvPriceMedDisplayItem.text = "₹${currentItem.price}"


        Glide
            .with(context)
            .load(currentItem.imageUrl)
            .into(holder.binding.ivMedDisplayItem)


        holder.itemView.setOnClickListener {
            productClickInterface.onClickProduct(list[position])
        }

        holder.binding.btnLike.setOnClickListener {
            if(holder.binding.btnLike.isChecked){
                holder.binding.btnLike.backgroundTintList = ColorStateList.valueOf(Color.RED)
                likeClickInterface.onClickLike(currentItem)
            }
            else{
                holder.binding.btnLike.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}

interface ProductOnClickInterface {
    fun onClickProduct(item: MedDisplayModel)
}

interface LikeOnClickInterface{
    fun onClickLike(item :MedDisplayModel)
}