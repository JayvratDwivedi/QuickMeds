package com.example.quickmeds

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmeds.Extensions.toast
import com.example.quickmeds.databinding.FragmentMainBinding
import com.example.quickmeds.models.MedDisplayModel
import com.example.quickmeds.rvadapters.CategoryOnClickInterface
import com.example.quickmeds.rvadapters.LikeOnClickInterface
import com.example.quickmeds.rvadapters.MainCategoryAdapter
import com.example.quickmeds.rvadapters.MedDisplayAdapter
import com.example.quickmeds.rvadapters.ProductOnClickInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.quickmeds.models.LikeModel



class MainFragment : Fragment(R.layout.fragment_main),
    CategoryOnClickInterface,
    ProductOnClickInterface, LikeOnClickInterface {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var productList: ArrayList<MedDisplayModel>
    private lateinit var categoryList: ArrayList<String>
    private lateinit var productsAdapter: MedDisplayAdapter
    private lateinit var categoryAdapter: MainCategoryAdapter
    private lateinit var auth: FirebaseAuth
    private var likeDBRef = Firebase.firestore.collection("LikedProducts")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(view)
        categoryList = ArrayList()
        productList = ArrayList()
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        auth = FirebaseAuth.getInstance()



        // region implements products Recycler view

        val productLayoutManager = GridLayoutManager(context, 2)
        productsAdapter = MedDisplayAdapter(requireContext(), productList, this,this)
        binding.rvMainProductsList.layoutManager = productLayoutManager
        binding.rvMainProductsList.adapter = productsAdapter
        setProductsData()
        // endregion implements products Recycler view


        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainFragment -> {
                    Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_mainFragment_self)
                    true
                }
                R.id.likeFragment -> {
//                    requireActivity().toast("Like Page coming Soon")
                    Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                        .navigate(R.id.action_mainFragment_to_likeFragment2)
                    true
                }
                R.id.cartFragment -> {

                    Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                        .navigate(R.id.action_mainFragment_to_cartFragment)

                    true
                }
                R.id.profileFragment -> {

                    auth.signOut()
                    Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_mainFragment_to_signInFragmentFragment)
                    true
                }
                else -> false

            }

        }


    }
    private fun setCategoryList() {
        Log.d("MainFragment", "setCategoryList: Start")
        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                categoryList.clear()
                categoryList.add("Trending")

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(MedDisplayModel::class.java)

                        categoryList.add(products!!.brand!!)

                    }

                    categoryAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

        }


        databaseReference.addValueEventListener(valueEvent)




    }


    private fun setProductsData() {

        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(MedDisplayModel::class.java)
                        productList.add(products!!)
                    }

                    productsAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

        }

        databaseReference.addValueEventListener(valueEvent)

    }

    override fun onClickCategory(button: Button) {
        binding.tvMainCategories.text = button.text

        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(MedDisplayModel::class.java)

                        if (products!!.brand == button.text) {
                            productList.add(products)
                        }

                        if (button.text == "Trending") {
                            productList.add(products)
                        }

                    }

                    productsAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

        }

        databaseReference.addValueEventListener(valueEvent)


    }


    override fun onClickProduct(item: MedDisplayModel) {

        val direction = MainFragmentDirections
            .actionMainFragmentToDetailsFragment(
                item.id!!
            )

        Navigation.findNavController(requireView())
            .navigate(direction)


    }

    override fun onClickLike(item: MedDisplayModel) {

        likeDBRef
            .add(LikeModel(item.id , auth.currentUser!!.uid , item.brand , item.description , item.imageUrl , item.name ,item.price))
            .addOnSuccessListener {
                requireActivity().toast("Added to Liked Items")
            }
            .addOnFailureListener {
                requireActivity().toast("Failed to Add to Liked")
            }

    }
}