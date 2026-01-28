# Vesto - Personal Finance Tracker

A modern Android app built with Kotlin and Jetpack Compose to help students track expenses, manage budgets, and gain financial insights.

## ✨ Features

- 💰 **Monthly Budget Tracking** - Set and monitor your monthly budget with visual progress indicators
- 📊 **Expense Categories** - Track expenses across 8 predefined categories or create custom ones
- 📈 **Visual Insights** - Weekly and monthly pie charts for spending analysis
- 💱 **Multi-Currency Support** - Support for PKR, USD, EUR, GBP, INR, AED, and SAR
- 🌙 **Dark Mode** - Beautiful pastel color scheme with dark mode support
- 🔔 **Budget Alerts** - Notifications when approaching budget limits
- 📱 **Offline First** - All data stored locally for privacy and offline access
- ♿ **Accessibility** - Screen reader support and large text compatibility

## 🏗️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with StateFlow
- **Database**: Room for local persistence
- **Navigation**: Compose Navigation
- **Charts**: Vico library
- **Async**: Kotlin Coroutines & Flow

## 📁 Project Structure

```
app/src/main/java/com/vesto/
├── data/              # Data layer
│   ├── local/         # Room database (entities, DAOs)
│   └── repository/    # Repository pattern
├── ui/                # UI layer (Composables)
│   ├── onboarding/
│   ├── dashboard/
│   ├── expense/
│   ├── insights/
│   ├── settings/
│   └── theme/
├── viewmodel/         # ViewModels
├── navigation/        # Navigation setup
└── MainActivity.kt
```

## 🚀 Build & Run

1. Clone the repository
2. Open in Android Studio (Hedgehog or newer)
3. Sync Gradle dependencies
4. Run on emulator or device (min SDK 26)

## 📱 Screenshots

_Coming soon!_

## 🤝 Contributing

This is a student project for learning modern Android development.

## 📄 License

MIT License
