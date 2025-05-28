package com.example.todo
import androidx.compose.foundation.Image
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.data.Category


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(

    viewModel: CategoryViewModel = hiltViewModel(),
    onCategoryClick: (Category) -> Unit,
    themeViewModel: ThemeViewModel
) {
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    )
    {
        Scaffold(
            topBar = {

                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background),
                        ) {
                            Text(
                                "Task Box",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium


                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { themeViewModel.toggleTheme() }) {
                            Text(
                                text = if (themeViewModel.isDarkTheme.value) "☀️" else "🌙",
                                fontSize = 20.sp
                            )
                        }
                        IconButton(onClick = {
                            editingCategory = null
                            dialogText = ""
                            showDialog = true
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Category",modifier = Modifier.size(30.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )

            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Categories", fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        modifier = Modifier.padding(vertical = 8.dp)

                    )
                }

                if (categories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight()
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.cate),
                                    contentDescription = "Empty Category",
                                    modifier = Modifier
                                        .height(200.dp)
                                        .padding(bottom = 16.dp)
                                )
                                Text(
                                    text = "Category is Empty",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                } else {
                    items(categories) { category ->
                        Box {
                            Box(

                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(top = 8.dp)
                                    .background(Color.Transparent)
                                    .shadow(6.dp, RoundedCornerShape(8.dp))

                            )
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clickable { onCategoryClick(category) }
                                    .clip(RoundedCornerShape(16.dp)),
                                elevation = CardDefaults.elevatedCardElevation(6.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row {
                                        IconButton(onClick = {
                                            editingCategory = category
                                            dialogText = category.name
                                            showDialog = true
                                        }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        IconButton(onClick = {
                                            categoryToDelete = category
                                            showDeleteDialog = true

                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = if (editingCategory == null) "New Category" else "Edit Category",color = MaterialTheme.colorScheme.onSurface)

                },
                text = {
                    TextField(
                        value = dialogText,
                        onValueChange = { dialogText = it },
                        singleLine = true,
                        label = { Text("Category Name" , color = MaterialTheme.colorScheme.onSurface) }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (dialogText.isNotBlank()) {
                            if (editingCategory == null) {
                                viewModel.addCategory(dialogText)
                            } else {
                                viewModel.updateCategory(editingCategory!!.copy(name = dialogText))
                            }
                            showDialog = false
                            dialogText = ""
                            editingCategory = null
                        }
                    }) {
                        Text("Save" , color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        editingCategory = null
                        dialogText = ""
                    }) {
                        Text("Cancel" , color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )


        }
        if (showDeleteDialog && categoryToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    categoryToDelete = null
                },
                title = {
                    Text("Delete Category", color = MaterialTheme.colorScheme.onSurface)
                },
                text = {
                    Text(
                        "Are you sure you want to delete the category \"${categoryToDelete?.name}\"?",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteCategory(categoryToDelete!!)
                        showDeleteDialog = false
                        categoryToDelete = null
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        categoryToDelete = null
                    }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
