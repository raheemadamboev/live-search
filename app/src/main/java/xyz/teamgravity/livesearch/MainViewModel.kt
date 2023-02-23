package xyz.teamgravity.livesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    companion object {
        private const val DELAY_SEARCH = 1_000L
        private const val SHARING_TIMEOUT = 5_000L
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searching = MutableStateFlow(false)
    val searching: StateFlow<Boolean> = _searching.asStateFlow()

    private val _people = MutableStateFlow(PersonDatabase.value)
    val people: StateFlow<List<PersonModel>> = _query
        .debounce(DELAY_SEARCH)
        .combine(_people) { query, people ->
            _searching.emit(true)

            delay(1_500L) // artificial delay to simulate network call

            if (query.isBlank()) {
                _searching.emit(false)
                return@combine people
            }

            val filteredPeople = people.filter { it.matches(query) }
            _searching.emit(false)
            return@combine filteredPeople
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT),
            initialValue = _people.value
        )

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun onQueryChange(value: String) {
        viewModelScope.launch { _query.emit(value) }
    }
}