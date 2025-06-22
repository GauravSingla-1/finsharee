package com.finshare.android.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finshare.android.domain.model.*
import com.finshare.android.presentation.viewmodel.MainViewModel
import com.finshare.android.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Groups", "Expenses", "AI", "Analytics", "Settlements")

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("FinShare") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 -> GroupsScreen(viewModel)
            1 -> ExpensesScreen(viewModel)
            2 -> AIScreen(viewModel)
            3 -> AnalyticsScreen(viewModel)
            4 -> SettlementsScreen(viewModel)
        }
    }
}

@Composable
fun GroupsScreen(viewModel: MainViewModel) {
    val groupsState by viewModel.groups.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Groups",
                style = MaterialTheme.typography.headlineSmall
            )
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Group")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (groupsState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error loading groups",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = groupsState.message ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadGroups() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is Resource.Success -> {
                val groups = groupsState.data ?: emptyList()
                if (groups.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text("No groups found")
                            Text("Create your first group to get started")
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(groups) { group ->
                            GroupItem(group = group)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { groupName ->
                viewModel.createGroup(groupName)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun GroupItem(group: Group) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.groupName,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(Icons.Default.Group, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Created by: ${group.createdBy}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Members: ${group.members.size}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ExpensesScreen(viewModel: MainViewModel) {
    val expensesState by viewModel.expenses.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.headlineSmall
            )
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (expensesState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error loading expenses",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = expensesState.message ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = { viewModel.loadExpenses() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is Resource.Success -> {
                val expenses = expensesState.data ?: emptyList()
                if (expenses.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text("No expenses found")
                            Text("Add your first expense to get started")
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(expenses) { expense ->
                            ExpenseItem(expense = expense)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateExpenseDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { description, amount, category ->
                viewModel.createExpense(
                    description = description,
                    amount = amount,
                    category = category,
                    groupId = "default-group-id" // In real app, user would select group
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${expense.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            expense.category?.let {
                Text(
                    text = "Category: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Split Method: ${expense.splitMethod}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AIScreen(viewModel: MainViewModel) {
    var selectedAITab by remember { mutableIntStateOf(0) }
    val aiTabs = listOf("Categorize", "Trip Budget", "Chat")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "AI Assistant",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedAITab) {
            aiTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedAITab == index,
                    onClick = { selectedAITab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedAITab) {
            0 -> CategorizeScreen(viewModel)
            1 -> TripBudgetScreen(viewModel)
            2 -> ChatScreen(viewModel)
        }
    }
}

@Composable
fun CategorizeScreen(viewModel: MainViewModel) {
    val categorizeResult by viewModel.categorizeResult.collectAsStateWithLifecycle()
    var merchantText by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = merchantText,
            onValueChange = { merchantText = it },
            label = { Text("Merchant Name") },
            placeholder = { Text("e.g., Starbucks") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (optional)") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (merchantText.isNotBlank()) {
                    val amountValue = amount.toDoubleOrNull()
                    viewModel.categorizeExpense(merchantText, "DEBIT", amountValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = merchantText.isNotBlank()
        ) {
            Text("Categorize Expense")
        }

        Spacer(modifier = Modifier.height(16.dp))

        categorizeResult?.let { result ->
            when (result) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Error: ${result.message}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is Resource.Success -> {
                    result.data?.let { data ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Predicted Category: ${data.predicted_category}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Confidence: ${(data.confidence_score * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                data.alternative_categories?.let { alternatives ->
                                    if (alternatives.isNotEmpty()) {
                                        Text(
                                            text = "Alternatives: ${alternatives.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall
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
}

@Composable
fun TripBudgetScreen(viewModel: MainViewModel) {
    val tripBudgetResult by viewModel.tripBudgetResult.collectAsStateWithLifecycle()
    var description by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Trip Description") },
            placeholder = { Text("e.g., 5-day vacation to Paris for 2 people") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration in days (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (description.isNotBlank()) {
                    val durationValue = duration.toIntOrNull()
                    viewModel.generateTripBudget(
                        description = description,
                        destination = destination.takeIf { it.isNotBlank() },
                        duration = durationValue
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = description.isNotBlank()
        ) {
            Text("Generate Trip Budget")
        }

        Spacer(modifier = Modifier.height(16.dp))

        tripBudgetResult?.let { result ->
            when (result) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Error: ${result.message}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is Resource.Success -> {
                    result.data?.let { data ->
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Trip Budget Plan",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                data.budget_items.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.category,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            Text(
                                                text = item.description,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Text(
                                            text = "$${item.estimated_cost}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total Estimated Cost:",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "$${data.total_estimated_cost} ${data.currency}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
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

@Composable
fun ChatScreen(viewModel: MainViewModel) {
    val chatResult by viewModel.chatResult.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf("") }
    var conversationHistory by remember { mutableStateOf(listOf<ChatMessage>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Chat with FinShare Co-Pilot",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chat messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversationHistory) { chatMessage ->
                ChatMessageItem(chatMessage)
            }
        }

        // Message input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Ask about expenses, budgeting, etc.") },
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        val userMessage = ChatMessage("user", message)
                        conversationHistory = conversationHistory + userMessage
                        viewModel.chatWithCopilot(message, conversationHistory)
                        message = ""
                    }
                },
                enabled = message.isNotBlank()
            ) {
                Text("Send")
            }
        }

        // Handle chat result
        chatResult?.let { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        val assistantMessage = ChatMessage("model", data.reply)
                        conversationHistory = conversationHistory + assistantMessage
                        viewModel.clearChatResult()
                    }
                }
                is Resource.Error -> {
                    val errorMessage = ChatMessage("model", "Sorry, I encountered an error. Please try again.")
                    conversationHistory = conversationHistory + errorMessage
                    viewModel.clearChatResult()
                }
                is Resource.Loading -> {
                    // Show loading indicator
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val budgetsState by viewModel.budgets.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Analytics & Budgets",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (budgetsState) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Error -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Error loading budgets")
                        Text(budgetsState.message ?: "Unknown error")
                        Button(onClick = { viewModel.loadBudgets() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is Resource.Success -> {
                val budgets = budgetsState.data ?: emptyList()
                if (budgets.isEmpty()) {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("No budgets found")
                            Text("Create budgets to track spending")
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(budgets) { budget ->
                            BudgetItem(budget)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItem(budget: Budget) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = budget.category,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${budget.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Period: ${budget.period}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SettlementsScreen(viewModel: MainViewModel) {
    val settlementsState by viewModel.settlements.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Settlements",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (settlementsState) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Error -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Error loading settlements")
                        Text(settlementsState.message ?: "Unknown error")
                        Button(onClick = { viewModel.loadSettlements() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is Resource.Success -> {
                val settlements = settlementsState.data ?: emptyList()
                if (settlements.isEmpty()) {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("No settlements found")
                            Text("Settlements will appear when expenses are split")
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(settlements) { settlement ->
                            SettlementItem(settlement)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettlementItem(settlement: Settlement) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "From: ${settlement.fromUserId}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${settlement.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "To: ${settlement.toUserId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${if (settlement.isSettled) "Settled" else "Pending"}",
                style = MaterialTheme.typography.bodySmall,
                color = if (settlement.isSettled) 
                    MaterialTheme.colorScheme.tertiary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group") },
        text = {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(groupName) },
                enabled = groupName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String?) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (description.isNotBlank() && amountValue != null) {
                        onConfirm(description, amountValue, category.takeIf { it.isNotBlank() })
                    }
                },
                enabled = description.isNotBlank() && amount.toDoubleOrNull() != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}