package com.example.quickmeds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.quickmeds.Extensions.toast
import com.example.quickmeds.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)
        auth = FirebaseAuth.getInstance()



        if (auth.currentUser != null) {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_signInFragmentFragment_to_mainFragment)
        }

        binding.btnSignIn.setOnClickListener {

            if (
                binding.etEmailSignIn.text.isNotEmpty() &&
                binding.etPasswordSignIn.text.isNotEmpty()
            ) {


                signinUser(binding.etEmailSignIn.text.toString(),
                    binding.etPasswordSignIn.text.toString())


            } else {
                requireActivity().toast("Some Fields are Empty")
            }
        }


        binding.tvNavigateToSignUp.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_signInFragmentFragment_to_signUpFragment)
        }


    }

    private fun signinUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    requireActivity().toast("Sign In Successful")

                    Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_signInFragmentFragment_to_mainFragment)
                } else {
                    requireActivity().toast(task.exception!!.localizedMessage!!)

                }


            }

    }
}