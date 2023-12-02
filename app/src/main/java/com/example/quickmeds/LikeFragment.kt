package com.example.quickmeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quickmeds.Extensions.toast
import com.example.quickmeds.databinding.FragmentLikeBinding
import com.example.quickmeds.models.LikeModel
import com.example.quickmeds.rvadapters.LikeAdapter
import com.example.quickmeds.rvadapters.LikedOnClickInterface
import com.example.quickmeds.rvadapters.LikedProductOnClickInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class LikeFragment :  Fragment(R.layout.fragment_like), LikedProductOnClickInterface,
    LikedOnClickInterface {
    private lateinit var binding: FragmentLikeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: LikeAdapter
    private lateinit var likedProductList: ArrayList<LikeModel>

    private var likeDBRef = Firebase.firestore.collection("LikedProducts")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLikeBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        likedProductList = ArrayList()
        adapter = LikeAdapter(requireContext(),likedProductList,this,this)


        binding.likeActualToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }


        val productLayoutManager = GridLayoutManager(context, 2)
        binding.rvLikedProducts.layoutManager = productLayoutManager
        binding.rvLikedProducts.adapter = adapter


        displayLikedProducts()

    }
    private fun displayLikedProducts() {

        likeDBRef
            .whereEqualTo("uid" , auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (item in querySnapshot) {
                    val likedProduct = item.toObject<LikeModel>()
                    likedProductList.add(likedProduct)
                    adapter.notifyDataSetChanged()
                }

            }
            .addOnFailureListener{
                requireActivity().toast(it.localizedMessage!!)
            }
    }
    override fun onClickProduct(item: LikeModel) {

    }


    override fun onClickLike(item: LikeModel) {
        //todo Remove from Liked Items

        likeDBRef
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .whereEqualTo("pid",item.pid)
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (item in querySnapshot){
                    likeDBRef.document(item.id).delete()
                    likedProductList.remove(item.toObject<LikeModel>())
                    adapter.notifyDataSetChanged()
                    requireActivity().toast("Removed From the Liked Items")
                }

            }
            .addOnFailureListener {
                requireActivity().toast("Failed To Remove From Liked Items")
            }

    }
}