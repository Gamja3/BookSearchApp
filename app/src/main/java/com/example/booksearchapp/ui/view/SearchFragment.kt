package com.example.booksearchapp.ui.view

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksearchapp.R
import com.example.booksearchapp.databinding.FragmentFavoriteBinding
import com.example.booksearchapp.databinding.FragmentSearchBinding
import com.example.booksearchapp.ui.adapter.BookSearchAdapter
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.example.booksearchapp.util.Constants.SEARCH_BOOKS_TIME_DELAY


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var bookSearchAdapter: BookSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookSearchViewModel = (activity as MainActivity).bookSearchViewModel
        setupRecyclerView()
        searchBooks()

        bookSearchViewModel.searchResult.observe(viewLifecycleOwner){response ->
            val books = response.documents
            bookSearchAdapter.submitList(books)
        }
    }

    private fun setupRecyclerView(){
        bookSearchAdapter = BookSearchAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = bookSearchAdapter
        }

        bookSearchAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionFragmentSearchToFragmentBook(it)
            findNavController().navigate(action)
        }
    }

    private fun searchBooks(){
        var startTime = System.currentTimeMillis()
        var endTime: Long

        binding.etSearch.text =
            Editable.Factory.getInstance().newEditable(bookSearchViewModel.query)

        binding.etSearch.addTextChangedListener { text: Editable? ->
            endTime = System.currentTimeMillis()
            if (endTime- startTime >= SEARCH_BOOKS_TIME_DELAY){
                text?.let {
                    val query = it.toString().trim()
                    if (query.isNotEmpty()){
                        bookSearchViewModel.searchBooks(query)
                    }
                }
            }
            startTime = endTime
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}