package com.example.zenithtasks.ui.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zenithtasks.Navigation.Screen
import com.example.zenithtasks.data.Task
import com.example.zenithtasks.data.TaskStatus
import com.example.zenithtasks.ui.ui.components.DraggableTaskItem
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.delay
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.zenithtasks.viewmodel.TaskViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size

// For TopAppBar customization
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.ui.text.style.TextAlign // NEW IMPORT
import androidx.compose.ui.Alignment // Ensure this is imported for Column content alignment
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskBoardScreen(
    navController: NavController,
    taskViewModel: TaskViewModel
) {
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())

    // --- DRAG & DROP STATE VARIABLES ---
    var draggedTask by remember { mutableStateOf<Task?>(null) }
    var currentDragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDraggingTask by remember { mutableStateOf(false) }
    var draggedItemSize by remember { mutableStateOf(IntSize.Zero) }

    var draggedItemInitialGlobalPosition by remember { mutableStateOf(Offset.Zero) }

    var draggedItemInitialIndex by remember { mutableStateOf<Int?>(null) }
    var draggedItemTargetIndex by remember { mutableStateOf<Int?>(null) }
    var draggedItemOriginalStatus by remember { mutableStateOf<TaskStatus?>(null) }
    var draggedItemTargetStatus by remember { mutableStateOf<TaskStatus?>(null) }

    val reorderedTasksInTargetColumn = remember { mutableStateListOf<Task>() }

    val taskItemCoordinatesMap = remember { mutableStateMapOf<Long, LayoutCoordinates>() }
    val columnCoordinatesMap = remember { mutableStateMapOf<TaskStatus, LayoutCoordinates>() }

    val lazyListStates = remember {
        mutableStateMapOf<TaskStatus, LazyListState>().apply {
            TaskStatus.entries.forEach { status ->
                this[status] = LazyListState()
            }
        }
    }

    val density = LocalDensity.current

    fun resetDragState() {
        draggedTask = null
        isDraggingTask = false
        currentDragOffset = Offset.Zero
        draggedItemInitialGlobalPosition = Offset.Zero
        draggedItemInitialIndex = null
        draggedItemTargetIndex = null
        draggedItemOriginalStatus = null
        draggedItemTargetStatus = null
        reorderedTasksInTargetColumn.clear()
    }

    // --- DRAG CALLBACKS ---
    val onDragStart: (Task, Offset, TaskStatus) -> Unit = { task, initialTouchOffset, originalStatus ->
        draggedTask = task
        isDraggingTask = true
        currentDragOffset = Offset.Zero
        draggedItemOriginalStatus = originalStatus
        draggedItemTargetStatus = originalStatus
        draggedItemInitialIndex = tasks.filter { it.status == originalStatus }.indexOf(task)

        val originalItemCoords = taskItemCoordinatesMap[task.id]
        draggedItemInitialGlobalPosition = if (originalItemCoords?.isAttached == true) {
            originalItemCoords.positionInRoot()
        } else {
            Offset.Zero
        }

        reorderedTasksInTargetColumn.clear()
        reorderedTasksInTargetColumn.addAll(tasks.filter { it.status == originalStatus })

        Log.d("TaskBoardScreen", "Drag Started for Task: ${task.title} from status: $originalStatus at index: $draggedItemInitialIndex. Initial global pos: $draggedItemInitialGlobalPosition")
    }

    val onDrag: (Offset) -> Unit = { dragAmount ->
        currentDragOffset += dragAmount

        draggedTask?.let { task ->
            val draggedTaskCurrentGlobalPos = draggedItemInitialGlobalPosition + currentDragOffset + Offset(draggedItemSize.width / 2f, draggedItemSize.height / 2f)

            var hoveredColumn: TaskStatus? = null
            for ((status, columnCoords) in columnCoordinatesMap) {
                if (columnCoords.isAttached) {
                    val columnRect = androidx.compose.ui.geometry.Rect(
                        columnCoords.positionInRoot(),
                        columnCoords.size.toSize()
                    )
                    if (columnRect.contains(draggedTaskCurrentGlobalPos)) {
                        hoveredColumn = status
                        break
                    }
                }
            }

            if (hoveredColumn != null && draggedItemTargetStatus != hoveredColumn) {
                draggedItemTargetStatus = hoveredColumn
                draggedItemTargetIndex = null

                reorderedTasksInTargetColumn.clear()
                reorderedTasksInTargetColumn.addAll(tasks.filter { it.status == hoveredColumn && it.id != task.id })
                reorderedTasksInTargetColumn.add(task.copy(status = hoveredColumn))

                Log.d("TaskBoardScreen", "Column Switched: ${task.title} to ${hoveredColumn}")
                return@let
            }

            val currentTargetStatus = draggedItemTargetStatus ?: return@let
            val tasksInCurrentTargetColumnVisual = reorderedTasksInTargetColumn

            val baseTasksInTargetColumn = tasks.filter { it.status == currentTargetStatus && it.id != task.id }

            var newCalculatedIndex: Int = tasksInCurrentTargetColumnVisual.size
            var foundTarget = false

            for (i in baseTasksInTargetColumn.indices) {
                val itemInList = baseTasksInTargetColumn[i]
                val itemCoords = taskItemCoordinatesMap[itemInList.id]
                if (itemCoords?.isAttached == true) {
                    val itemRect = itemCoords.positionInRoot().let { pos ->
                        androidx.compose.ui.geometry.Rect(pos, itemCoords.size.toSize())
                    }

                    if (draggedTaskCurrentGlobalPos.y < itemRect.center.y) {
                        newCalculatedIndex = i
                        foundTarget = true
                        break
                    }
                }
            }

            if (!foundTarget && baseTasksInTargetColumn.isEmpty()) {
                newCalculatedIndex = 0
            }

            if (newCalculatedIndex != draggedItemTargetIndex) {
                val sourceIndex = tasksInCurrentTargetColumnVisual.indexOfFirst { it.id == task.id }

                if (sourceIndex != -1) {
                    tasksInCurrentTargetColumnVisual.apply {
                        val movedItem = removeAt(sourceIndex)
                        add(minOf(newCalculatedIndex, size), movedItem)
                    }
                    draggedItemTargetIndex = newCalculatedIndex
                    Log.d("TaskBoardScreen", "Visual Reordering: ${task.title} moved to index $newCalculatedIndex in ${currentTargetStatus}")
                }
            }
        }
    }

    val onDragEnd: () -> Unit = {
        Log.d("TaskBoardScreen", "Drag Ended for Task: ${draggedTask?.title}")

        draggedTask?.let { task ->
            val finalTargetStatus = draggedItemTargetStatus ?: draggedItemOriginalStatus
            val finalTargetIndex = draggedItemTargetIndex

            if (finalTargetStatus != draggedItemOriginalStatus ||
                (finalTargetStatus == draggedItemOriginalStatus && finalTargetIndex != draggedItemInitialIndex)) {

                Log.d("TaskBoardScreen", "Final update for ${task.title}: to status: $finalTargetStatus at index: $finalTargetIndex")

                if (finalTargetIndex != null) {
                    taskViewModel.moveTaskToPosition(task.id, finalTargetStatus!!, finalTargetIndex)
                } else {
                    if (finalTargetStatus != draggedItemOriginalStatus) {
                        taskViewModel.updateTaskStatus(task.id, finalTargetStatus!!)
                    }
                }
            }
        }
        resetDragState()
    }

    val onDragCancel: () -> Unit = {
        Log.d("TaskBoardScreen", "Drag Cancelled for Task: ${draggedTask?.title}")
        resetDragState()
    }

    LaunchedEffect(isDraggingTask, currentDragOffset) {
        if (isDraggingTask && draggedTask != null) {
            val columnStatus = draggedItemTargetStatus ?: return@LaunchedEffect
            val listState = lazyListStates[columnStatus] ?: return@LaunchedEffect
            val listCoords = columnCoordinatesMap[columnStatus] ?: return@LaunchedEffect

            if (listCoords.isAttached == false) return@LaunchedEffect

            val scrollThreshold = with(density) { 50.dp.toPx() }
            val scrollSpeed = 50

            val draggedItemCenterY = (draggedItemInitialGlobalPosition + currentDragOffset).y + (draggedItemSize.height / 2f)

            val listTop = listCoords.positionInRoot().y
            val listBottom = listTop + listCoords.size.height

            if (draggedItemCenterY < listTop + scrollThreshold) {
                val currentFirstVisibleItemIndex = listState.firstVisibleItemIndex
                val currentFirstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset

                if (currentFirstVisibleItemIndex > 0 || currentFirstVisibleItemScrollOffset > 0) {
                    listState.scrollBy(-scrollSpeed.toFloat())
                    delay(16)
                }
            }
            else if (draggedItemCenterY > listBottom - scrollThreshold) {
                listState.scrollBy(scrollSpeed.toFloat())
                delay(16)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("ZenithTasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditTaskScreen.createRoute())
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskStatus.entries) { status ->
                val currentTasksToDisplay = if (isDraggingTask && draggedItemTargetStatus == status) {
                    reorderedTasksInTargetColumn
                } else if (isDraggingTask && draggedItemOriginalStatus == status && draggedItemTargetStatus != status) {
                    tasks.filter { it.status == status && it.id != draggedTask?.id }
                }
                else {
                    tasks.filter { it.status == status }
                }

                val lazyListState = lazyListStates.getValue(status)

                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .wrapContentHeight()
                        .heightIn(min = 150.dp)
                        .onGloballyPositioned { coordinates ->
                            columnCoordinatesMap[status] = coordinates
                        }
                        .background(
                            color = if (isDraggingTask && draggedItemTargetStatus == status) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = MaterialTheme.shapes.medium
                        )
                        .border(
                            width = if (isDraggingTask && draggedItemTargetStatus == status) 2.dp else 0.dp,
                            color = if (isDraggingTask && draggedItemTargetStatus == status) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally // NEW: Center content horizontally for empty state
                    ) {
                        // Column Header
                        Text(
                            text = status.name.replace("_", " "),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Tasks List - This will grow as tasks are added
                        if (currentTasksToDisplay.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(bottom = 8.dp),
                                state = lazyListState,
                                userScrollEnabled = currentTasksToDisplay.size > 6
                            ) {
                                items(currentTasksToDisplay, key = { task -> task.id }) { task ->
                                    val itemModifier = Modifier.onGloballyPositioned { coordinates ->
                                        if (draggedTask?.id == task.id && !coordinates.isAttached) {
                                            taskItemCoordinatesMap.remove(task.id)
                                        } else {
                                            taskItemCoordinatesMap[task.id] = coordinates
                                        }

                                        if (draggedTask?.id == task.id) {
                                            draggedItemSize = coordinates.size
                                        }
                                    }
                                    DraggableTaskItem(
                                        task = task,
                                        onTaskClick = { taskId ->
                                            if (!isDraggingTask) {
                                                navController.navigate(Screen.AddEditTaskScreen.createRoute(taskId))
                                            }
                                        },
                                        onDeleteClick = { taskToDelete ->
                                            if (!isDraggingTask) {
                                                taskViewModel.deleteTask(taskToDelete)
                                            }
                                        },
                                        onDragStart = { offset -> onDragStart(task, offset, status) },
                                        onDrag = onDrag,
                                        onDragEnd = onDragEnd,
                                        onDragCancel = onDragCancel,
                                        currentOffset = Offset.Zero,
                                        isDragging = false,
                                        modifier = itemModifier.animateItem()
                                    )
                                }
                            }
                        } else {
                            // --- EMPTY STATE VISUALS ---
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 80.dp), // Provide a minimum height for the empty state area
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add, // Using Add icon as a subtle hint
                                    contentDescription = "Add Task",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No tasks in ${status.name.replace("_", " ").toLowerCase(Locale.ROOT)} yet.\nDrag a task here or add a new one!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center, // Center the text
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            // --- END EMPTY STATE VISUALS ---
                        }

                        // Placeholder space when dragging to empty column
                        if (currentTasksToDisplay.isEmpty() && isDraggingTask && draggedItemTargetStatus == status) {
                            Spacer(
                                modifier = Modifier
                                    .height(with(density) { draggedItemSize.height.toDp() })
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            )
                        }
                    }
                }
            }
        }

        // --- Render the dragged task as a floating item ---
        draggedTask?.let { task ->
            if (isDraggingTask) {
                val floatingOffset = draggedItemInitialGlobalPosition + currentDragOffset

                DraggableTaskItem(
                    task = task.copy(status = draggedItemTargetStatus ?: task.status),
                    onTaskClick = { /* No click during drag */ },
                    onDeleteClick = { /* No delete during drag */ },
                    onDragStart = { /* Handled by original item */ },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                    currentOffset = Offset.Zero,
                    isDragging = true,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                floatingOffset.x.roundToInt(),
                                floatingOffset.y.roundToInt()
                            )
                        }
                        .width(with(density) { draggedItemSize.width.toDp() })
                        .height(with(density) { draggedItemSize.height.toDp() })
                )
            }
        }
    }
}