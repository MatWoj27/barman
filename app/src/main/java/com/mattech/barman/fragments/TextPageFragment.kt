package com.mattech.barman.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mattech.barman.databinding.FragmentTextPageBinding

interface DescriptionActionListener {
    fun addDescription()
}

class TextPageFragment : Fragment() {
    private lateinit var binding: FragmentTextPageBinding
    private lateinit var listener: DescriptionActionListener

    companion object {
        const val TEXT_ARG_KEY = "text"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as DescriptionActionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTextPageBinding.inflate(inflater, container, false)
        binding.textFiled.movementMethod = ScrollingMovementMethod()
        binding.addDescription.setOnClickListener { listener.addDescription() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(TEXT_ARG_KEY) }?.let {
            binding.text = it.getString(TEXT_ARG_KEY)
            binding.executePendingBindings()
        }
    }
}