package com.mizzugi.kensiro.app.panri.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mizzugi.kensiro.app.panri.fragment.adapter.ShowListDiseaseRA
import com.mizzugi.kensiro.app.panri.plugin.ItemClickSupport
import com.mizzugi.kensiro.app.panri.plugin.callbacks.OnRequestDialog
import com.mizzugi.kensiro.app.panri.viewmodel.ShowListDiseaseViewModel

class ShowListDiseaseFragment : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var showListDiseaseViewModel: ShowListDiseaseViewModel
    private var onItemClickListener: ItemClickSupport.OnItemClickListener? = null

    private var onRequestDialog: OnRequestDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        RecyclerView(inflater.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            layoutManager = LinearLayoutManager(context)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view as RecyclerView
        mRecyclerView.tag = RECYCLER_TAG
        ItemClickSupport.addTo(mRecyclerView)?.onItemClickListener = onItemClickListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showListDiseaseViewModel =
            ViewModelProviders.of(this).get(ShowListDiseaseViewModel::class.java)
        showListDiseaseViewModel.apply {
            finishedLoading.observe(this@ShowListDiseaseFragment, Observer {
                onRequestDialog?.requestDialog(!it)
            })

            listDiseaseViewModel.observe(this@ShowListDiseaseFragment, Observer {
                mRecyclerView.apply {
                    adapter = ShowListDiseaseRA(it)
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            load()
        }
    }

    override fun onResume() {
        super.onResume()
        ItemClickSupport.addTo(mRecyclerView)?.onItemClickListener = onItemClickListener
    }

    override fun onPause() {
        super.onPause()
        ItemClickSupport.removeFrom(mRecyclerView)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickSupport.OnItemClickListener) {
            onItemClickListener = context
        }

        if (context is OnRequestDialog) {
            onRequestDialog = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onItemClickListener = null
    }

    companion object {
        const val RECYCLER_TAG = "ShowListDiseaseFragment"
    }
}