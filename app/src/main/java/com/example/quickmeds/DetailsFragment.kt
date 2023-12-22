package com.example.quickmeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.quickmeds.Extensions.toast
import com.example.quickmeds.databinding.FragmentDetailsBinding
import com.example.quickmeds.models.MedDisplayModel
import com.example.quickmeds.models.ProductOrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




class DetailsFragment : Fragment(R.layout.fragment_details) {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var productDatabaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val args: DetailsFragmentArgs by navArgs()

    private val orderDatabaseReference = Firebase.firestore.collection("orders")

    private lateinit var currentUID :  String
    private lateinit var orderImageUrl:String
    private lateinit var orderName:String
    private var orderQuantity:Int  = 1
    private lateinit var orderPrice:String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailsBinding.bind(view)

        productDatabaseReference = FirebaseDatabase.getInstance().getReference("products")

        val productId = args.productId
        auth = FirebaseAuth.getInstance()

        currentUID = auth.currentUser!!.uid

        binding.detailActualToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }



        // region implements firebase product display
        val valueEvent = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val products = dataSnapshot.getValue(MedDisplayModel::class.java)

                        if (products!!.id == productId) {
                            Glide
                                .with(requireContext())
                                .load(products.imageUrl)
                                .into(binding.ivDetails)

                            orderImageUrl = products.imageUrl!!
                            orderName = products.name!!
                            orderPrice = products.price!!

                            binding.tvDetailsProductPrice.text = "₹${products.price}"
                            binding.tvDetailsProductName.text = "${products.name}"
                            binding.tvDetailsProductDescription.text = products.description
                        }


                    }


                }

            }

            override fun onCancelled(error: DatabaseError) {
                requireActivity().toast(error.message)
            }

        }


        productDatabaseReference.addValueEventListener(valueEvent)

        // endregion implements firebase product display


        binding.btnDetailsAddToCart.setOnClickListener {

            // TODO: Add Data to FireBase FireStore Database

            val orderedProduct = ProductOrderModel(currentUID, productId, orderImageUrl, orderName, orderQuantity, orderPrice)

            addDataToOrdersDatabase(orderedProduct)

            Navigation.findNavController(view).navigate(R.id.action_detailsFragment_to_cartFragment)
        }


    }

    private fun addDataToOrdersDatabase(orderedProduct: ProductOrderModel) {

        orderDatabaseReference.add(orderedProduct).addOnCompleteListener{task ->
            if(task.isSuccessful){
                requireActivity().toast("Order Successfully Delivered")
            }else{
                requireActivity().toast(task.exception!!.localizedMessage!!)
            }
        }

    }
}