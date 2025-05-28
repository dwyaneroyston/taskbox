package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow



@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Int): Flow<Category?>


    @Query("SELECT * FROM Category")
    fun getAllCategories(): Flow<List<Category>>

    @Insert
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}
@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo WHERE categoryId = :categoryId")
    fun getTodosForCategory(categoryId: Int): Flow<List<Todo>>

    @Insert
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)
}
