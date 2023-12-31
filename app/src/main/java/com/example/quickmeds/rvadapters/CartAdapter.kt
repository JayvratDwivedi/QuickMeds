package com.example.quickmeds.rvadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmeds.SwipeToDelete
import com.example.quickmeds.databinding.CartproductItemBinding
import com.example.quickmeds.models.CartModel
import com.bumptech.glide.Glide

class CartAdapter(
    private val context : Context,
    private val list:ArrayList<CartModel>,
    private val onLongClickRemove: OnLongClickRemove
    ): RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CartproductItemBinding):RecyclerView.ViewHolder(binding.root){

        private val onSwipeDelete = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                list.removeAt(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CartproductItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = list[position]

        Glide
            .with(context)
            .load(currentItem.imageUrl)
            .into(holder.binding.ivCartProduct)


        holder.binding.tvCartProductName.text = currentItem.name
        holder.binding.tvCartProductPrice.text = "₹${currentItem.price}"
        holder.binding.tvCartItemCount.text = currentItem.quantity.toString()

        var count = holder.binding.tvCartItemCount.text.toString().toInt()

        holder.binding.btnCartItemAdd.setOnClickListener {
//            count++
            // TODO: Update Quantity in Database also
//            holder.binding.tvCartItemCount.text = count.toString()

        }

        holder.binding.btnCartItemMinus.setOnClickListener {
//            count--
            // TODO: Update Quantity in Database also
//            holder.binding.tvCartItemCount.text = count.toString()
        }

        holder.itemView.setOnLongClickListener {
            onLongClickRemove.onLongRemove(currentItem , position)
            true
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    interface OnLongClickRemove{
        fun onLongRemove(item:CartModel , position: Int)
    }
}