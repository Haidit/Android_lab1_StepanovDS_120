package com.haidit.intersvyaztestovoe.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.haidit.intersvyaztestovoe.R
import com.haidit.intersvyaztestovoe.databinding.FragmentListBinding
import com.haidit.intersvyaztestovoe.domain.adapters.mainList.ListItemOnClickListener
import com.haidit.intersvyaztestovoe.domain.adapters.mainList.ListMainAdapter
import com.haidit.intersvyaztestovoe.domain.models.ListElement
import com.haidit.intersvyaztestovoe.domain.models.makeLine
import com.haidit.intersvyaztestovoe.utils.getArrayListFromSharedPreferences
import com.haidit.intersvyaztestovoe.utils.saveArrayListToSharedPreferences
import java.io.File

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by activityViewModels()
    private lateinit var adapter: ListMainAdapter
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var menuHost: MenuHost
    private lateinit var menuProvider: MenuProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuHost = requireActivity()
        setMenuProvider()
        menuHost.addMenuProvider(menuProvider)
        setAdapter()
        makeRequest()
        viewModel.ids = getArrayListFromSharedPreferences("ids")
    }

    private fun setMenuProvider() {
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionSearch -> {
                        val searchView: SearchView = menuItem.actionView as SearchView

                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(p0: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(msg: String): Boolean {
                                filter(msg)
                                return false
                            }
                        })
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        menuHost.removeMenuProvider(menuProvider)
    }

    private fun setAdapter() {
        adapter =
            ListMainAdapter(requireContext(), viewModel.list, object : ListItemOnClickListener {
                override fun onClicked(listElement: ListElement) {
                    val action = ListFragmentDirections.actionNavigationListToNavigationDetailed(
                        listElement.name, listElement.description, listElement.picture
                    )
                    findNavController().navigate(action)
                }
            }, object : ListItemOnClickListener {
                override fun onClicked(listElement: ListElement) {
                    favourite(listElement)
                    adapter.notifyDataSetChanged()
                }
            })
        val listRV = binding.itemsList
        listRV.layoutManager = LinearLayoutManager(context)
        listRV.adapter = adapter
    }

    private fun makeRequest() {
        if (viewModel.list.size != 0) {
            return
        }
        val url = "https://65acac44adbd5aa31bdf6d9f.mockapi.io/api/v1/cats"
        val queue = Volley.newRequestQueue(requireContext())

        val request = StringRequest(Request.Method.GET, url, { result ->
            viewModel.parseData(result)
            adapter.notifyDataSetChanged()
        }, { _ ->
            Snackbar.make(binding.root, getString(R.string.api_error), Snackbar.LENGTH_LONG).show()
        })
        queue.add(request)
    }

    private fun favourite(listElement: ListElement) {
        if (listElement.id.toString() in viewModel.ids) {
            return
        }
        viewModel.ids.add(listElement.id.toString())
        saveArrayListToSharedPreferences("ids", viewModel.ids)
        val file = File(requireContext().filesDir, "Favourites.txt")
        file.appendText(makeLine(listElement))
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<ListElement> = ArrayList()

        for (item in viewModel.list) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_data_found), Toast.LENGTH_SHORT)
                .show()
        } else {
            adapter.filterList(filteredList)
        }
    }


}
