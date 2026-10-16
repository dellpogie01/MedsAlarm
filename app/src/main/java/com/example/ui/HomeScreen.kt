package com.example.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MedicationEntity
import com.example.ui.theme.AlarmRed
import com.example.ui.theme.MedsBlue
import com.example.ui.theme.MedsDoneCardBg
import com.example.ui.theme.MedsDoneText
import com.example.ui.theme.MedsGreenTime
import com.example.ui.theme.MedsPendingButton
import com.example.ui.theme.MedsPendingCardBg
import com.example.viewmodel.MedsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MedsViewModel,
    onNavigateToAddAlarm: () -> Unit
) {
    val activity = LocalContext.current as? Activity
    val medications by viewModel.medications.collectAsState()
    val currentTimeMillis by viewModel.currentTime.collectAsState()

    // Confirmation dialog state for deletion
    var medicationToDelete by remember { mutableStateOf<MedicationEntity?>(null) }

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm:ss a z", Locale.getDefault())
    val currentDate = Date(currentTimeMillis)
    val formattedDate = dateFormat.format(currentDate)
    val formattedTime = timeFormat.format(currentDate)

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 56.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.meds_alarm_illustration),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Fit
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "MedsAlarm",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                                color = MedsBlue,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Today is $formattedDate",
                                color = MedsGreenTime,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                maxLines = 1
                            )
                            Text(
                                text = formattedTime,
                                color = MedsGreenTime,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                maxLines = 1
                            )
                        }
                    }
                    IconButton(
                        onClick = { activity?.finish() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 4.dp, top = 4.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Exit app", tint = MedsBlue)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddAlarm,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.testTag("fab_add_alarm")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (medications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // All Caps: "NO MEDICATION REMINDERS SET"
                        Text(
                            text = "NO MEDICATION REMINDERS SET",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // All Caps: "TAP '+' TO ADD YOUR FIRST REMINDER"
                        Text(
                            text = "TAP '+' TO ADD YOUR FIRST REMINDER",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedsBlue
                        )
                    }
                }
            } else {
                // Compact list allowing many items to show at once
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(medications, key = { it.id }) { med ->
                        CompactMedicationCard(
                            medication = med,
                            currentTimeMillis = currentTimeMillis,
                            onTakeDose = { viewModel.markDoseTaken(med) },
                            onDeleteClick = { medicationToDelete = med }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    medicationToDelete?.let { med ->
        AlertDialog(
            onDismissRequest = { medicationToDelete = null },
            title = {
                Text(
                    text = "Delete Reminder?",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${med.name}?",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMedication(med)
                        medicationToDelete = null
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = AlarmRed)
                ) {
                    Text(
                        "OK",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { medicationToDelete = null },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
fun CompactMedicationCard(
    medication: MedicationEntity,
    currentTimeMillis: Long,
    onTakeDose: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isCompleted = medication.currentDoneCount >= medication.doseCountPerDay
    val cardBackgroundColor = if (isCompleted) {
        MedsDoneCardBg // Light green when complete
    } else {
        MedsPendingCardBg
    }

    val pendingCount = (medication.doseCountPerDay - medication.currentDoneCount).coerceAtLeast(0)
    val statusText = "${medication.currentDoneCount} Done, $pendingCount Pending"
    val frequencyText = "${medication.dosage} ${medication.formType} • ${medication.doseCountPerDay}x a Day"
    val repeatDayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val repeatDaysText = medication.repeatDays
        .split(",")
        .mapNotNull { it.trim().toIntOrNull()?.let(repeatDayNames::getOrNull) }
        .joinToString(", ")
        .ifBlank { "None" }

    // Calculate countdown for next dose
    val nextDoseTimeMillis = getNextDoseMillis(medication, currentTimeMillis)
    val diffMillis = (nextDoseTimeMillis - currentTimeMillis).coerceAtLeast(0L)
    val hours = diffMillis / (1000 * 60 * 60)
    val minutes = (diffMillis / (1000 * 60)) % 60
    val seconds = (diffMillis / 1000) % 60
    val countdownStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // All texts in the list strictly black font color
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Reminder",
                            tint = AlarmRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Frequency in black font color
                Text(
                    text = frequencyText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color.Black
                )

                if (medication.instruction.isNotBlank()) {
                    Text(
                        text = "Instruction: ${medication.instruction}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color.Black,
                        maxLines = 2
                    )
                }
                Text(
                    text = "Days: $repeatDaysText",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color.Black
                )

                // Status in black font color
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.Black
                )

                // Countdown in black font color
                Text(
                    text = if (isCompleted) "All doses taken for today" else "Next in $countdownStr",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (isCompleted) {
                Surface(
                    color = Color(0xFFC8E6C9),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Done",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MedsDoneText
                    )
                }
            } else {
                Button(
                    onClick = onTakeDose,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedsPendingButton,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Take",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

fun getNextDoseMillis(med: MedicationEntity, currentMillis: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = currentMillis
        var h = med.firstHour
        if (med.amPm == "PM" && h < 12) h += 12
        if (med.amPm == "AM" && h == 12) h = 0
        set(Calendar.HOUR_OF_DAY, h)
        set(Calendar.MINUTE, med.firstMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val intervalMillis = (med.intervalHours * 3600 * 1000).toLong()
    var targetTime = cal.timeInMillis

    while (targetTime <= currentMillis && med.doseCountPerDay > 0) {
        targetTime += intervalMillis
    }
    return targetTime
}
