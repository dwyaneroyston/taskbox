package com.example.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.CategoryRepository
import com.example.todo.data.Priority
import com.example.todo.data.Todo
import com.example.todo.data.TodoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoryId = MutableStateFlow<Int?>(null)
    private val _categoryName = MutableStateFlow<String?>(null)
    val categoryName: StateFlow<String?> = _categoryName

    val todos: StateFlow<List<Todo>> = _categoryId
        .filterNotNull()
        .flatMapLatest { id -> todoDao.getTodosForCategory(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setCategoryId(id: Int) {
        _categoryId.value = id
        viewModelScope.launch {
            categoryRepository.getCategoryById(id).collect { category ->
                _categoryName.value = category?.name
            }
        }
    }

    fun addTodo(title: String,priority: Priority) {
        val id = _categoryId.value ?: return
        val priority = priority

        viewModelScope.launch {
            todoDao.insertTodo(Todo(title = title, categoryId = id , priority = priority))
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoDao.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }
}
