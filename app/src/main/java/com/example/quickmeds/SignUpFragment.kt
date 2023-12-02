package com.example.quickmeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.quickmeds.Extensions.toast
import com.example.quickmeds.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth : FirebaseAuth



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        auth = FirebaseAuth.getInstance()






        binding.btnSignUp.setOnClickListener {

            if (
                binding.etEmailSignUp.text.isNotEmpty() &&
                binding.etNameSignUp.text.isNotEmpty() &&
                binding.etPasswordSignUp.text.isNotEmpty()
            ) {

                createUser(binding.etEmailSignUp.text.toString(),binding.etNameSignUp.text.toString())


            }
        }
        binding.tvNavigateToSignIn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragmentFragment)
        }


    }

    private fun createUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    requireActivity().toast("New User created")

                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_signUpFragment_to_mainFragment)

                }
                else{
                    requireActivity().toast(task.exception!!.localizedMessage)
                }

            }

    }

}