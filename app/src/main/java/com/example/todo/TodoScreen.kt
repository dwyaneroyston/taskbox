package com.example.todo

import androidx.compose.foundation.Image
import com.example.todo.data.Todo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.data.Priority
import androidx.compose.ui.text.style.TextDecoration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    categoryId: Int,
    onBackClick: () -> Unit,
    viewModel: TodoViewModel = hiltViewModel()
) {
    LaunchedEffect(categoryId) {
        viewModel.setCategoryId(categoryId)
    }

    val originalTodos by viewModel.todos.collectAsState()
    var sortByPriority by remember { mutableStateOf(false) }
    val todos = if (sortByPriority) {
        originalTodos.sortedBy { it.priority }
    } else {
        originalTodos
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newTodoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.Medium) }

    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    var editedTodoText by remember { mutableStateOf("") }

    val doneTodos = todos.count { it.isDone }
    val totalTodos = todos.size
    var showDeleteDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }



    Scaffold(
        topBar = {
            val categoryName by viewModel.categoryName.collectAsState()


            TopAppBar(

                title = {
                    Column {
                        Text(
                            text = categoryName ?: "To-dos",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "$totalTodos to-dos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } ,
            )
        },
        floatingActionButton = {
            FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary,onClick = { showAddDialog = true }) {

                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (todos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Empty todo",
                            modifier = Modifier
                                .height(200.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text("No todos yet", style = MaterialTheme.typography.bodyMedium)

                    }
                }
            } else {
                val (pending, done) = todos.partition { !it.isDone }

                LazyColumn {
                    items(pending) { todo ->
                        TodoItem(todo = todo, viewModel = viewModel, onEdit = {
                            editingTodo = it
                            editedTodoText = it.title
                            selectedPriority = it.priority
                        }, onDeleteRequest = {
                            todoToDelete = it
                            showDeleteDialog = true
                        })

                    }

                    if (done.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Done ($doneTodos)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(done) { todo ->
                            TodoItem(
                                todo = todo,
                                viewModel = viewModel,
                                onEdit = {
                                    editingTodo = it
                                    editedTodoText = it.title
                                    selectedPriority = it.priority
                                },
                                onDeleteRequest = {
                                    todoToDelete = it
                                    showDeleteDialog = true
                                },
                                doneOpacity = 0.4f
                            )
                        }
                    }
                }
            }
        }
    }


    // Add Todo Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Todo") },
            text = {
                Column {
                    TextField(
                        value = newTodoText,
                        onValueChange = { newTodoText = it },
                        label = { Text("Todo title") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Priority:")
                    Row {
                        Priority.values().forEach { priority ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (selectedPriority == priority),
                                    onClick = { selectedPriority = priority }
                                )
                                Text(priority.name)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newTodoText.isNotBlank()) {
                        viewModel.addTodo(newTodoText, selectedPriority)
                        newTodoText = ""
                        selectedPriority = Priority.Medium
                        showAddDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Todo Dialog
    if (editingTodo != null) {
        AlertDialog(
            onDismissRequest = { editingTodo = null },
            title = { Text("Edit Todo") },
            text = {
                Column {
                    TextField(
                        value = editedTodoText,
                        onValueChange = { editedTodoText = it },
                        label = { Text("Todo title") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select Priority:")
                    Row {
                        Priority.values().forEach { priority ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (selectedPriority == priority),
                                    onClick = { selectedPriority = priority }
                                )
                                Text(priority.name)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editingTodo?.let { todo ->
                        if (editedTodoText.isNotBlank()) {
                            viewModel.updateTodo(
                                todo.copy(
                                    title = editedTodoText,
                                    priority = selectedPriority
                                )
                            )
                        }
                        editingTodo = null
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTodo = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showDeleteDialog && todoToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                todoToDelete = null
            },
            title = { Text("Delete Todo") },
            text = {
                Text("Are you sure you want to delete the todo \"${todoToDelete?.title}\"?")
            },
            confirmButton = {
                TextButton(onClick = {
                    todoToDelete?.let { viewModel.deleteTodo(it) }
                    showDeleteDialog = false
                    todoToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    todoToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable

fun TodoItem(
    todo: Todo,
    viewModel: TodoViewModel,
    onEdit: (Todo) -> Unit,
    onDeleteRequest: (Todo) -> Unit,
    doneOpacity: Float = 0.4f
)
 {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .alpha(if (todo.isDone) doneOpacity else 1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = { isChecked ->
                    viewModel.updateTodo(todo.copy(isDone = isChecked))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = todo.title,
                    style = TextStyle(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = todo.priority.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (todo.priority) {
                        Priority.High -> MaterialTheme.colorScheme.error
                        Priority.Medium -> MaterialTheme.colorScheme.onSurface
                        Priority.Low -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }
        }
        Row {
            IconButton(onClick = { onEdit(todo) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = {
                onDeleteRequest(todo)
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }


        }
    }
}
