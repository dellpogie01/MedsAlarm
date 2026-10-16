# MedsAlarm Android App Implementation Plan

An Android application built with Kotlin and Jetpack Compose that provides reliable medication reminders, status bar notifications with sound/vibration using native AlarmManager, countdown timers, and daily tracking.

## User Review & Critical Decisions

> [!IMPORTANT]
> The app will feature a Material 3 UI with a 3-second splash screen, a fixed top app bar with live date/time and compact layout, a bottom-right FAB, a compact scrollable alarm list with dynamic "Take" / "Done" states and countdown timers, and an Add Alarm form with automatic time spacing for multi-dose schedules.

- **Confirmed Architecture**: Single-activity architecture with Navigation Compose or clean state switching, Room database for local persistence of medication alarms and daily take status, and AlarmManager (`setExactAndAllowWhileIdle`) + BroadcastReceiver for reliable notifications even when the app is closed.
- **Notifications & Permissions**: Implements runtime notification permission (`POST_NOTIFICATIONS`) and exact alarm permissions (`SCHEDULE_EXACT_ALARM`) with NotificationChannel for Android 8.0+.

---

## 1. Overview & Core Concept

- **What It Does**: MedsAlarm helps users never miss a dose. It provides automated background notifications, exact time alarms, daily dose tracking (e.g., "2 Done, 1 Pending"), countdown timers for the next dose, and quick one-tap marking of doses as taken.
- **Target Audience**: Patients, elderly individuals, or anyone managing daily medications/supplements.
- **Key Value**: Offline-first reliability using native AlarmManager and robust local storage.

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Splash Screen**: Launches with app logo, title "MedsAlarm", and subtitle "Medication Reminders Made Simple" for ~3 seconds, then transitions smoothly to Home.
  2. **Home Screen**:
     - **Top App Bar**: Pinned M3 bar with app icon, title/subtitle, and current date/time (`Today is <Day>, <MMMM dd, yyyy> | hh:mm:ss AM/PM`) compressed cleanly in one line.
     - **Alarm List**: Scrollable list of active medications for the current day. Each card shows Medicine Name, Frequency (e.g. "1 Tab 3x a Day"), Status ("2 Done, 1 Pending"), Countdown Timer ("Next in 04:15:30"), and a "Take" button (or light green background with a dark green "Done" label when all daily doses are completed).
     - **Floating Action Button**: Prominent `+` FAB anchored at bottom-right.
  3. **Add Alarm Screen (`+`)**: Form with text fields (Medicine Name, Dosage, Instruction), medication form icon selector (tablet, capsule, injection), time picker (AM/PM), repeat days toggles (S M T W T F S), dose count per day (auto-calculates interval like 2x -> +12h, 3x -> +8h), notification options checkboxes (Status Bar Notification, Sound Alarm, Vibrate Alarm), and a "Set" button.
- **Visual Identity & Theme**:
  - Material 3 dynamic color scheme (`MaterialTheme.colorScheme`) with primary container tones, soft surface cards, and success light green accents for completed doses.

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Native AlarmManager vs WorkManager**
  - *Chosen Approach*: `AlarmManager.setExactAndAllowWhileIdle()` paired with `BroadcastReceiver`.
  - *Why*: Ensures exact alarms fire precisely at scheduled medication times even when the device is in Doze mode or the app is closed.
- **Decision 2: Room Database for Local Persistence**
  - *Chosen Approach*: Room database storing alarms, frequencies, and daily completion logs.
  - *Why*: Robust persistence that survives app restarts and device reboots (with boot receiver).

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                        UI Layer                        │
│ (SplashScreen, HomeScreen, AddAlarmScreen, AlarmCard)  │
└──────────────────────────┬─────────────────────────────┘
                           │ State & Events
┌──────────────────────────▼─────────────────────────────┐
│                    MedsViewModel                       │
│     (Manages alarms, countdowns, take actions)         │
└──────────────────────────┬─────────────────────────────┘
                           │ Room DB & AlarmManager
┌──────────────────────────▼─────────────────────────────┘
│              Data & System Services Layer              │
│       (Room Database, AlarmManager, Notification)      │
└────────────────────────────────────────────────────────┘
```

- **Data Models**:
  - `MedicationEntity`: id, name, dosage, instruction, formType (tablet/capsule/injection), firstHour, firstMinute, amPm, repeatDays (String/List), doseCountPerDay, intervalHours, notifyStatusbar, notifySound, notifyVibrate, currentDoneCount, lastResetDate.
- **Interactive Component Mapping**:
  - **Take Button**: Updates `currentDoneCount` in Room and ViewModel, re-schedules next alarm if applicable, and updates card background to light green when `currentDoneCount == doseCountPerDay`.
  - **Add Alarm**: Validates inputs, saves to Room, calculates successive alarm timestamps, and schedules AlarmManager pending intents.
