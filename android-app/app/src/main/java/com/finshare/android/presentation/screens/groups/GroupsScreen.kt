package com.finshare.android.presentation.screens.groups

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
import com.finshare.android.domain.model.Group
import com.finshare.android.presentation.components.GroupCard
import com.finshare.android.presentation.components.BottomNavigationBar
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(navController: NavController) {
    // Mock data for demonstration
    val groups = remember {
        listOf(
            Group(
                id = "1",
                name = "Roommates",
                imageUrl = null,
                createdBy = "user1",
                createdAt = LocalDateTime.now(),
                members = listOf("user1", "user2", "user3")
            ),
            Group(
                id = "2",
                name = "Trip to Hawaii",
                imageUrl = null,
                createdBy = "user1",
                createdAt = LocalDateTime.now(),
                members = listOf("user1", "user4", "user5", "user6")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Groups") },
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
                onClick = { /* Navigate to create group */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
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
                text = "Active Groups",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (groups.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No groups yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Create your first group to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groups) { group ->
                        GroupCard(
                            group = group,
                            onClick = { /* Navigate to group details */ }
                        )
                    }
                }
            }
        }
    }
}