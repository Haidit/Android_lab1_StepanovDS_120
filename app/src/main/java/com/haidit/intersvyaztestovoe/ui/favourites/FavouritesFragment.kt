package com.haidit.intersvyaztestovoe.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.haidit.intersvyaztestovoe.R
import com.haidit.intersvyaztestovoe.databinding.EditItemDialogBinding
import com.haidit.intersvyaztestovoe.databinding.FragmentFavouritesBinding
import com.haidit.intersvyaztestovoe.domain.adapters.mainList.ListItemOnClickListener
import com.haidit.intersvyaztestovoe.domain.adapters.mainList.ListMainAdapter
import com.haidit.intersvyaztestovoe.domain.models.ListElement
import com.haidit.intersvyaztestovoe.domain.models.makeLine
import com.haidit.intersvyaztestovoe.utils.getArrayListFromSharedPreferences
import com.haidit.intersvyaztestovoe.utils.saveArrayListToSharedPreferences
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val viewModel: FavouritesViewModel by activityViewModels()
    private lateinit var adapter: ListMainAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        readFavourites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.list.clear()
    }

    private fun setAdapter() {
        adapter =
            ListMainAdapter(requireContext(), viewModel.list, object : ListItemOnClickListener {
                override fun onClicked(listElement: ListElement) {
                    showDialog(listElement)
                }
            }, object : ListItemOnClickListener {
                override fun onClicked(listElement: ListElement) {

                }
            })
        val listRV = binding.itemsList
        listRV.layoutManager = LinearLayoutManager(context)
        listRV.adapter = adapter
    }

    private fun readFavourites() {
        val file = File(requireContext().filesDir, "Favourites.txt")
        try {
            BufferedReader(FileReader(file)).use { br ->
                br.lines().forEach {
                    val elementParts = it.split(";")
                    viewModel.list.add(
                        ListElement(
                            elementParts[1],
                            elementParts[2],
                            elementParts[3],
                            elementParts[0].toInt()
                        )
                    )
                    adapter.notifyItemInserted(-1)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showDialog(listElement: ListElement) {
        val dialogBinding = EditItemDialogBinding.inflate(layoutInflater)

        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        val positiveButton = dialogBinding.saveButton
        val negativeButton = dialogBinding.deleteButton
        dialogBinding.nameET.setText(listElement.name)
        dialogBinding.descET.setText(listElement.description)

        dialog.show()
        positiveButton.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext()).apply {
                setTitle(resources.getString(R.string.save_alert))
                setMessage(resources.getString(R.string.alert_check))

                setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    val oldLine = makeLine(listElement)
                    listElement.name = dialogBinding.nameET.text.toString()
                    listElement.description = dialogBinding.descET.text.toString()
                    val newLine = makeLine(listElement)
                    adapter.notifyDataSetChanged()
                    change(oldLine, newLine)
                }

                setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                setCancelable(true)
            }.create().show()
            dialog.dismiss()
        }
        negativeButton.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext()).apply {
                setTitle(resources.getString(R.string.delete_alert))
                setMessage(resources.getString(R.string.alert_check))

                setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    change(makeLine(listElement), "")
                    viewModel.ids = getArrayListFromSharedPreferences("ids")
                    viewModel.ids.remove(listElement.id.toString())
                    saveArrayListToSharedPreferences("ids", viewModel.ids)
                    viewModel.list.remove(listElement)
                    adapter.notifyDataSetChanged()
                }

                setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                setCancelable(true)
            }.create().show()
            dialog.dismiss()
        }
    }

    private fun change(oldLine: String, newLine: String) {
        val file = File(requireContext().filesDir, "Favourites.txt")
        val content = file.readText()
        val updatedContent = content.replace(oldLine, newLine)
        file.writeText(updatedContent)
    }

    private fun function1(text: String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()

        adapter.notifyDataSetChanged()
    }
}