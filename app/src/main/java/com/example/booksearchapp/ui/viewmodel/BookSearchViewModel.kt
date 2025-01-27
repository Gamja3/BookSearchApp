package com.example.booksearchapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.data.repository.BookSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookSearchViewModel (
    private val bookSearchRepository: BookSearchRepository,
    private val savedStateHandle: SavedStateHandle
    ): ViewModel(){

    //Api
    private val _searchResult = MutableLiveData<SearchResponse>()
    val searchResult: LiveData<SearchResponse> get() = _searchResult

    fun searchBooks(query: String) = viewModelScope.launch(Dispatchers.IO) {
        val response = bookSearchRepository.searchBooks(query, "accuracy",1, 30)
        if(response.isSuccessful){
            response.body()?.let { body ->
                _searchResult.postValue(body)
            }
        }
    }

    //Room
    fun saveBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.insetBooks(book)
    }

    fun deleteBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.deleteBooks(book)
    }

//    val favoriteBooks: Flow<List<Book>> = bookSearchRepository.getFavoriteBooks()
      val favoriteBooks: StateFlow<List<Book>> = bookSearchRepository.getFavoriteBooks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    //savedState
    var query = String()
        set(value) {
            field = value
            savedStateHandle.set(SAVE_STATE_KEY, value)
        }
    init {
        query = savedStateHandle.get<String>(SAVE_STATE_KEY) ?: ""
    }

    companion object{

    private const val SAVE_STATE_KEY = "query"
    }
}