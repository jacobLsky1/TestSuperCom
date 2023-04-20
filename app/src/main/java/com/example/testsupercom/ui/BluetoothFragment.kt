package com.example.testsupercom.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsupercom.databinding.FragmentBluetoothBinding
import com.example.testsupercom.services.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BluetoothFragment(): Fragment(){



    private lateinit var binding: FragmentBluetoothBinding
    lateinit var viewModel: MainViewModel
    lateinit var deviceRV : RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var noMatches: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBluetoothBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deviceRV = binding.recyclerView
        progressBar = binding.progressBar
        noMatches = binding.textView
        deviceRV.layoutManager = LinearLayoutManager(requireContext())
        setUpServices()
        setUpObservers()
    }

    private fun setUpObservers(){
        viewModel.devices.observe(viewLifecycleOwner) {
            if (it != null) {
                deviceRV.isVisible = true
                noMatches.isVisible = false
                deviceRV.adapter = DeviceAdapter(it)
            } else {
                deviceRV.isVisible = false
                noMatches.isVisible = true
            }
        }

        viewModel.isSeraching.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
    }

    private fun setUpServices(){
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding.switch1.setOnCheckedChangeListener { button, b ->
            if(b){
                viewModel.orgList = listOf()
                viewModel.searchForDevices()

            }else{
                viewModel.cancelDiscovery()
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val text = newText!!
                if (TextUtils.isEmpty(text)) {
                    viewModel.setFilterList("")
                } else {
                    viewModel.setFilterList(text)
                }
                return true
            }
        })
    }

    companion object{
        fun newInstance(): BluetoothFragment {
            return BluetoothFragment()
        }
    }
}