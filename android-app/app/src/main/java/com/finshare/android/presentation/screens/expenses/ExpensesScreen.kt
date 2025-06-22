package com.finshare.android.presentation.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.finshare.android.domain.model.Expense
import com.finshare.android.domain.model.ExpensePayer
import com.finshare.android.domain.model.ExpenseSplit
import com.finshare.android.domain.model.SplitMethod
import com.finshare.android.presentation.components.BottomNavigationBar
import java.math.BigDecimal
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(navController: NavController) {
    // Mock data for demonstration
    val expenses = remember {
        listOf(
            Expense(
                id = "1",
                groupId = "1",
                description = "Groceries",
                amount = BigDecimal("45.67"),
                category = "Food & Dining",
                createdBy = "user1",
                createdAt = LocalDateTime.now().minusDays(1),
                splitMethod = SplitMethod.EQUAL,
                payers = listOf(ExpensePayer("user1", BigDecimal("45.67"))),
                splits = listOf(
                    ExpenseSplit("user1", BigDecimal("15.22")),
                    ExpenseSplit("user2", BigDecimal("15.22")),
                    ExpenseSplit("user3", BigDecimal("15.23"))
                )
            ),
            Expense(
                id = "2",
                groupId = "2",
                description = "Hotel Booking",
                amount = BigDecimal("280.00"),
                category = "Travel",
                createdBy = "user4",
                createdAt = LocalDateTime.now().minusDays(3),
                splitMethod = SplitMethod.EQUAL,
                payers = listOf(ExpensePayer("user4", BigDecimal("280.00"))),
                splits = listOf(
                    ExpenseSplit("user1", BigDecimal("70.00")),
                    ExpenseSplit("user4", BigDecimal("70.00")),
                    ExpenseSplit("user5", BigDecimal("70.00")),
                    ExpenseSplit("user6", BigDecimal("70.00"))
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Expenses") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to add expense */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "All Expenses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No expenses yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Add your first expense to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseCard(expense = expense)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseCard(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${expense.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${expense.splits.size} people",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}