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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
//import kotlin.math.minOf
import androidx.compose.ui.graphics.Color

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

    // FIX: Declare draggedItemInitialGlobalPosition here
    var draggedItemInitialGlobalPosition by remember { mutableStateOf(Offset.Zero) }

    var draggedItemInitialIndex by remember { mutableStateOf<Int?>(null) } // Original index in its column
    var draggedItemTargetIndex by remember { mutableStateOf<Int?>(null) } // Potential new index in its column
    var draggedItemOriginalStatus by remember { mutableStateOf<TaskStatus?>(null) } // Original status
    var draggedItemTargetStatus by remember { mutableStateOf<TaskStatus?>(null) } // Current hovered target status

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
        draggedItemInitialGlobalPosition = Offset.Zero // Reset this too
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
        currentDragOffset = Offset.Zero // Reset offset for new drag
        draggedItemOriginalStatus = originalStatus
        draggedItemTargetStatus = originalStatus // Initially, target status is the same as original
        draggedItemInitialIndex = tasks.filter { it.status == originalStatus }.indexOf(task)

        // Capture the global position of the dragged item HERE, when it's attached.
        // This coordinates object is from the original item, so we check if it's attached before getting position.
        val originalItemCoords = taskItemCoordinatesMap[task.id]
        draggedItemInitialGlobalPosition = if (originalItemCoords?.isAttached == true) {
            originalItemCoords.positionInRoot()
        } else {
            // Fallback: If for some reason not attached (unlikely onDragStart),
            // maybe use a default or log an error.
            Offset.Zero
        }


        // Initialize reorderedTasksInTargetColumn with tasks from the original column
        reorderedTasksInTargetColumn.clear()
        reorderedTasksInTargetColumn.addAll(tasks.filter { it.status == originalStatus })

        Log.d("TaskBoardScreen", "Drag Started for Task: ${task.title} from status: $originalStatus at index: $draggedItemInitialIndex. Initial global pos: $draggedItemInitialGlobalPosition")
    }

    val onDrag: (Offset) -> Unit = { dragAmount ->
        currentDragOffset += dragAmount

        draggedTask?.let { task ->
            // Calculate draggedTaskCurrentGlobalPos using initial global position + current drag offset
            // We do NOT query taskItemCoordinatesMap[task.id]?.positionInRoot() here.
            val draggedTaskCurrentGlobalPos = draggedItemInitialGlobalPosition + currentDragOffset + Offset(draggedItemSize.width / 2f, draggedItemSize.height / 2f)


            // 1. Detect which column the dragged item is currently over
            var hoveredColumn: TaskStatus? = null
            for ((status, columnCoords) in columnCoordinatesMap) {
                // Check columnCoords.isAttached too for safety
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

            // 2. Handle Column Switching (Visual update)
            if (hoveredColumn != null && draggedItemTargetStatus != hoveredColumn) {
                draggedItemTargetStatus = hoveredColumn
                draggedItemTargetIndex = null // Reset target index when switching columns

                // Re-initialize reorderedTasksInTargetColumn with tasks from the NEW hovered column
                reorderedTasksInTargetColumn.clear()
                reorderedTasksInTargetColumn.addAll(tasks.filter { it.status == hoveredColumn && it.id != task.id })
                reorderedTasksInTargetColumn.add(task.copy(status = hoveredColumn)) // Add to end for now

                Log.d("TaskBoardScreen", "Column Switched: ${task.title} to ${hoveredColumn}")
                return@let // Exit early, let the next onDrag update handle precise positioning in new column
            }

            // 3. Handle Reordering within the current target column (after a potential switch)
            val currentTargetStatus = draggedItemTargetStatus ?: return@let // Should be non-null after init/switch
            val tasksInCurrentTargetColumnVisual = reorderedTasksInTargetColumn // Use the visual list directly

            val baseTasksInTargetColumn = tasks.filter { it.status == currentTargetStatus && it.id != task.id }

            var newCalculatedIndex: Int = tasksInCurrentTargetColumnVisual.size // Default to end if no specific target found
            var foundTarget = false

            // Iterate over the *base tasks* in the target column to find the precise drop zone
            for (i in baseTasksInTargetColumn.indices) {
                val itemInList = baseTasksInTargetColumn[i]
                val itemCoords = taskItemCoordinatesMap[itemInList.id]
                // Only use coordinates if they are attached
                if (itemCoords?.isAttached == true) {
                    val itemRect = itemCoords.positionInRoot().let { pos ->
                        androidx.compose.ui.geometry.Rect(pos, itemCoords.size.toSize())
                    }

                    // Check if the dragged item's center is above the center of this item
                    if (draggedTaskCurrentGlobalPos.y < itemRect.center.y) {
                        newCalculatedIndex = i
                        foundTarget = true
                        break
                    }
                }
            }

            // If no specific target found, or list is empty, newCalculatedIndex defaults to size (end)
            if (!foundTarget && baseTasksInTargetColumn.isEmpty()) {
                newCalculatedIndex = 0
            }


            // Apply visual reordering if the calculated index has changed
            if (newCalculatedIndex != draggedItemTargetIndex) {
                val sourceIndex = tasksInCurrentTargetColumnVisual.indexOfFirst { it.id == task.id }

                if (sourceIndex != -1) { // Ensure the dragged task is in the list
                    tasksInCurrentTargetColumnVisual.apply {
                        val movedItem = removeAt(sourceIndex)
                        add(minOf(newCalculatedIndex, size), movedItem) // Ensure index is not out of bounds
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

    // --- NEW: LaunchedEffect for Auto-Scrolling during Drag ---
    LaunchedEffect(isDraggingTask, currentDragOffset) {
        if (isDraggingTask && draggedTask != null) {
            val columnStatus = draggedItemTargetStatus ?: return@LaunchedEffect
            val listState = lazyListStates[columnStatus] ?: return@LaunchedEffect
            val listCoords = columnCoordinatesMap[columnStatus] ?: return@LaunchedEffect

            // Check if listCoords is attached before using positionInRoot()
            if (listCoords.isAttached == false) return@LaunchedEffect


            val scrollThreshold = with(density) { 50.dp.toPx() }
            val scrollSpeed = 50

            // Calculate draggedItemCenterY based on its absolute floating position from stored initial + current offset
            val draggedItemCenterY = (draggedItemInitialGlobalPosition + currentDragOffset).y + (draggedItemSize.height / 2f)

            val listTop = listCoords.positionInRoot().y
            val listBottom = listTop + listCoords.size.height

            // Scroll up
            if (draggedItemCenterY < listTop + scrollThreshold) {
                val currentFirstVisibleItemIndex = listState.firstVisibleItemIndex
                val currentFirstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset

                if (currentFirstVisibleItemIndex > 0 || currentFirstVisibleItemScrollOffset > 0) {
                    listState.scrollBy(-scrollSpeed.toFloat())
                    delay(16)
                }
            }
            // Scroll down
            else if (draggedItemCenterY > listBottom - scrollThreshold) {
                listState.scrollBy(scrollSpeed.toFloat())
                delay(16)
            }
        }
    }
    // --- END NEW LaunchedEffect ---

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Task Board") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditTaskScreen.createRoute())
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
                        .fillMaxHeight()
                        .width(280.dp)
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
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = status.name.replace("_", " "),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 8.dp),
                            state = lazyListState
                        ) {
                            items(currentTasksToDisplay, key = { task -> task.id }) { task ->
                                // Capture coordinates only if the item is NOT the dragged task's original (to avoid nulling out coords)
                                // OR if it's attached. If it's the dragged item's original and now detached, remove its coords from map.
                                val itemModifier = Modifier.onGloballyPositioned { coordinates ->
                                    if (draggedTask?.id == task.id && !coordinates.isAttached) {
                                        taskItemCoordinatesMap.remove(task.id)
                                    } else {
                                        taskItemCoordinatesMap[task.id] = coordinates
                                    }

                                    if (draggedTask?.id == task.id) { // Still update size if it's the actual dragged task
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
                                    onDragCancel = onDragCancel, // This will still get called if the system cancels
                                    currentOffset = Offset.Zero,
                                    isDragging = false,
                                    modifier = itemModifier.animateItem()
                                )
                            }
                            item {
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
            }
        }

        // --- Render the dragged task as a floating item ---
        draggedTask?.let { task ->
            if (isDraggingTask) {
                // Floating item's position is always based on stored initial position + current drag offset
                val floatingOffset = draggedItemInitialGlobalPosition + currentDragOffset

                DraggableTaskItem(
                    task = task.copy(status = draggedItemTargetStatus ?: task.status),
                    onTaskClick = { /* No click during drag */ },
                    onDeleteClick = { /* No delete during drag */ },
                    onDragStart = { /* Handled by original item */ },
                    onDrag = { /* Handled by original item */ },
                    onDragEnd = { /* Handled by original item */ },
                    onDragCancel = { /* Handled by original item */ },
                    currentOffset = Offset.Zero, // This item's actual offset is applied by Modifier.offset
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