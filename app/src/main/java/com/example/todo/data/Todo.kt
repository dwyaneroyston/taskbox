package com.example.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    High, Medium, Low
}

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val categoryId: Int,
    val priority: Priority = Priority.Medium,
    val isDone: Boolean = false


)