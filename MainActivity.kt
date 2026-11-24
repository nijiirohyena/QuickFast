package com.example.still

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

private val Cream = Color(0xFFF7F7F2)
private val Card = Color(0xFFFFFFFF)
private val Ink = Color(0xFF252A26)
private val Muted = Color(0xFF858B84)
private val Sage = Color(0xFF718675)
private val PaleSage = Color(0xFFE7EDE6)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("still", MODE_PRIVATE)
        setContent { StillApp(prefs) }
    }
}

data class Schedule(val label: String, val fastHours: Int, val eatHours: Int) {
    override fun toString() = "$label · ${fastHours}h fast"
}

@Composable
fun StillApp(prefs: android.content.SharedPreferences) {
    val schedules = listOf(
        Schedule("12:12", 12, 12), Schedule("14:10", 14, 10),
        Schedule("16:8", 16, 8), Schedule("18:6", 18, 6), Schedule("20:4", 20, 4)
    )
    var scheduleIndex by remember { mutableIntStateOf(prefs.getInt("schedule", 2).coerceIn(0, 4)) }
    var startedAt by remember { mutableLongStateOf(prefs.getLong("started", 0L)) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var tab by remember { mutableIntStateOf(0) }
    var history by remember { mutableStateOf(prefs.getString("history", "")!!.split("|").filter { it.isNotBlank() }) }
    var customHours by remember { mutableIntStateOf(prefs.getInt("custom", 16)) }

    LaunchedEffect(Unit) { while (true) { now = System.currentTimeMillis(); delay(1000) } }
    val active = startedAt > 0L
    val elapsed = if (active) max(0L, (now - startedAt) / 1000L) else 0L
    val targetSeconds = schedules[scheduleIndex].fastHours * 3600L
    val progress by animateFloatAsState(if (active) (elapsed.toFloat() / targetSeconds).coerceIn(0f, 1f) else 0f, label = "progress")

    MaterialTheme(colorScheme = lightColorScheme(primary = Sage, background = Cream, surface = Card, onBackground = Ink, onSurface = Ink)) {
        Scaffold(containerColor = Cream, bottomBar = {
            NavigationBar(containerColor = Card, tonalElevation = 0.dp) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Today") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.CalendarMonth, null) }, label = { Text("History") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
            }
        }) { padding ->
            when (tab) {
                0 -> HomeScreen(Modifier.padding(padding), active, elapsed, progress, schedules[scheduleIndex], startedAt, now,
                    onStart = { startedAt = System.currentTimeMillis(); prefs.edit().putLong("started", startedAt).apply() },
                    onEnd = {
                        val ended = System.currentTimeMillis()
                        val item = "${startedAt},$ended"
                        history = listOf(item) + history.take(29)
                        prefs.edit().putLong("started", 0L).putString("history", history.joinToString("|")).apply()
                        startedAt = 0L
                    })
                1 -> HistoryScreen(Modifier.padding(padding), history)
                else -> SettingsScreen(Modifier.padding(padding), schedules, scheduleIndex, { scheduleIndex = it; prefs.edit().putInt("schedule", it).apply() })
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier, active: Boolean, elapsed: Long, progress: Float, schedule: Schedule, startedAt: Long, now: Long, onStart: () -> Unit, onEnd: () -> Unit) {
    Column(modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Still", fontSize = 27.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                Text(SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(now)), color = Muted, fontSize = 13.sp)
            }
            Box(Modifier.background(PaleSage, CircleShape).padding(horizontal = 13.dp, vertical = 8.dp)) {
                Text(if (active) "FASTING" else "READY", color = Sage, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
        Spacer(Modifier.height(26.dp))
        Text(if (active) "Your fast is in progress" else "A little time for yourself", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(18.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(252.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 13.dp.toPx()
                drawArc(PaleSage, -90f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round), topLeft = Offset(stroke, stroke), size = Size(size.width - stroke * 2, size.height - stroke * 2))
                drawArc(Sage, -90f, 360f * progress, false, style = Stroke(stroke, cap = StrokeCap.Round), topLeft = Offset(stroke, stroke), size = Size(size.width - stroke * 2, size.height - stroke * 2))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (active) "TIME FASTED" else "YOUR PLAN", color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(8.dp))
                Text(if (active) formatDuration(elapsed) else schedule.label, color = Ink, fontSize = 34.sp, fontWeight = FontWeight.Medium)
                Text(if (active) "of ${schedule.fastHours} hours" else "${schedule.fastHours} hours fasting", color = Muted, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(20.dp))
        if (active) {
            val remaining = max(0L, schedule.fastHours * 3600L - elapsed)
            InfoCard("ESTIMATED FAST END", formatClock(startedAt + schedule.fastHours * 3_600_000L), "${formatDuration(remaining)} remaining")
        } else {
            InfoCard("CURRENT SCHEDULE", schedule.label, "${schedule.fastHours}h fasting · ${schedule.eatHours}h eating")
        }
        Spacer(Modifier.height(18.dp))
        Button(onClick = if (active) onEnd else onStart, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Sage)) {
            Text(if (active) "End fast" else "Start fast", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(14.dp))
        Text(if (active) "Your timer continues if you close the app." else "Choose a schedule in Settings whenever you like.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))
        Text("Still is a tracking tool, not medical advice. Fasting isn't suitable for everyone.", color = Muted, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun InfoCard(title: String, value: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().background(Card, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(title, color = Muted, fontSize = 10.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(7.dp))
        Text(value, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}

@Composable
fun HistoryScreen(modifier: Modifier, history: List<String>) {
    Column(modifier.fillMaxSize().padding(22.dp)) {
        Text("Your history", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.SemiBold)
        Text("Completed fasting sessions", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(22.dp))
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No sessions yet", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text("Your completed fasts will appear here.", color = Muted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history) { entry ->
                    val parts = entry.split(",")
                    val start = parts.getOrNull(0)?.toLongOrNull() ?: 0L
                    val end = parts.getOrNull(1)?.toLongOrNull() ?: start
                    val duration = max(0L, (end - start) / 1000L)
                    Row(Modifier.fillMaxWidth().background(Card, RoundedCornerShape(16.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(start)), color = Ink, fontWeight = FontWeight.Medium)
                            Text("${formatClock(start)} – ${formatClock(end)}", color = Muted, fontSize = 12.sp)
                        }
                        Text(formatDuration(duration), color = Sage, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier, schedules: List<Schedule>, selected: Int, onSelect: (Int) -> Unit) {
    Column(modifier.fillMaxSize().padding(22.dp)) {
        Text("Settings", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.SemiBold)
        Text("Choose your fasting schedule", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(22.dp))
        schedules.forEachIndexed { index, schedule ->
            Row(Modifier.fillMaxWidth().padding(bottom = 9.dp).background(Card, RoundedCornerShape(15.dp)).padding(horizontal = 15.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(schedule.label, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text("${schedule.fastHours} hours fasting · ${schedule.eatHours} hours eating", color = Muted, fontSize = 12.sp)
                }
                RadioButton(selected = selected == index, onClick = { onSelect(index) }, colors = RadioButtonDefaults.colors(selectedColor = Sage))
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("About Still", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(6.dp))
        Text("Version 0.1.0 · Your data stays on this device. Still is a simple tracker and does not provide medical guidance.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
}

fun formatClock(millis: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
