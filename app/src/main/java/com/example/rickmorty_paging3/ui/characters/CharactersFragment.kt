package com.example.rickmorty_paging3.ui.characters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pagingselectionlib.PagingDataSelectionManager
import com.example.rickmorty_paging3.R
import com.example.rickmorty_paging3.adapter.CharacterAdapter
import com.example.rickmorty_paging3.databinding.FragmentCharactersBinding
import com.example.rickmorty_paging3.model.RickMorty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharactersFragment : Fragment(), CharacterAdapter.CharacterItemListener, ActionMode.Callback {

    private lateinit var binding: FragmentCharactersBinding
    private lateinit var characterAdapter: CharacterAdapter
    private val viewModel: CharacterViewModel by viewModels()

    private var actionMode: ActionMode? = null
    private val selectionManager: PagingDataSelectionManager<RickMorty> by lazy {
        PagingDataSelectionManager(characterAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter(this)
        characterAdapter.tracker = selectionManager

        binding.recyclerView.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.listData.collectLatest {
                characterAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            selectionManager.selection.collectLatest {
                viewModel.setSelection(it)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedCountToAllCount.collectLatest {
                if(actionMode!=null){
                    actionMode?.title = "${it?.first ?: 0} to ${it?.second ?: 0}"
                }
            }
        }
    }

    override fun onCLickCharacter(character: RickMorty) {
        if (actionMode != null) {
            selectionManager.updateSelectionItem(character)
        } else {
            findNavController().navigate(
                R.id.action_charactersFragment_to_characterDetail,
                bundleOf("id" to character.id)
            )
        }
    }

    override fun onLongClickListener(character: RickMorty) {
        if(actionMode == null){
            initActionSelectionMode()
            selectionManager.updateSelectionItem(character)
        }else{
            finishActionSelectionMode()
            selectionManager.deselectAll()
        }
    }


    private fun initActionSelectionMode() {
        val activity = requireActivity() as AppCompatActivity

        actionMode = activity.startSupportActionMode(this)
        actionMode?.title = activity.supportActionBar?.title
    }

    private fun finishActionSelectionMode() {
        actionMode?.finish()
        actionMode = null
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if(item!=null){
            when(item.itemId){
                R.id.menu_close -> {
                    selectionManager.deselectAll()
                    return true
                }
                R.id.menu_select_all -> {
                    selectionManager.selectAll()
                    return true
                }
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
    }
}