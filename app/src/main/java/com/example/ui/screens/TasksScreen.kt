package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.TaskEntity
import com.example.ui.components.PriorityBadge
import com.example.ui.viewmodel.ReformasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: ReformasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas y Plazos de Obra", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tareas pendientes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        val project = projects.find { it.id == task.projectId }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.status == "Completada",
                                    onCheckedChange = { checked ->
                                        val newSt = if (checked) "Completada" else "Pendiente"
                                        viewModel.updateTask(task.copy(status = newSt))
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = if (task.status == "Completada") Color.Gray else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (project != null) {
                                        Text("Obra: ${project.name}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    if (task.assigneeName.isNotEmpty()) {
                                        Text("Responsable: ${task.assigneeName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                PriorityBadge(priority = task.priority)
                                IconButton(onClick = { viewModel.deleteTask(task) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
            }
        }
    }

    if (showAddTaskDialog) {
        var title by remember { mutableStateOf("") }
        var assignee by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("Media") }
        var selectedProjectId by remember { mutableLongStateOf(projects.firstOrNull()?.id ?: 0L) }
        var projectDropdownExpanded by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showAddTaskDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Nueva Tarea de Obra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Descripción de la tarea *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (projects.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = projectDropdownExpanded,
                            onExpandedChange = { projectDropdownExpanded = !projectDropdownExpanded }
                        ) {
                            val curProj = projects.find { it.id == selectedProjectId }
                            OutlinedTextField(
                                value = curProj?.name ?: "Sin obra asignada",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Obra / Proyecto") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectDropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = projectDropdownExpanded,
                                onDismissRequest = { projectDropdownExpanded = false }
                            ) {
                                projects.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.name) },
                                        onClick = {
                                            selectedProjectId = p.id
                                            projectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = assignee,
                        onValueChange = { assignee = it },
                        label = { Text("Responsable") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Baja", "Media", "Alta", "Urgente").forEach { pr ->
                            FilterChip(
                                selected = priority == pr,
                                onClick = { priority = pr },
                                label = { Text(pr, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddTaskDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val task = TaskEntity(
                                        projectId = if (selectedProjectId > 0L) selectedProjectId else null,
                                        title = title,
                                        assigneeName = assignee,
                                        priority = priority,
                                        status = "Pendiente"
                                    )
                                    viewModel.addTask(task)
                                    showAddTaskDialog = false
                                    Toast.makeText(context, "Tarea añadida", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}
