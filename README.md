# Finmarka – Money Tracker App

Offline-first personal finance app for Android. Track income & expenses, set budgets, view reports. No login, no cloud.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM
- **Database:** Room (SQLite)
- **DI:** Hilt
- **Navigation:** Navigation Compose
- **Charts:** Vico (Compose)
- **Min SDK:** 24

## Features

- **Transactions:** Add / edit / delete income or expense (amount, type, category, date, note, payment mode).
- **Categories:** Default categories (Food, Transport, Shopping, Bills, Entertainment, Health, Salary, Business, Others) + add / edit / delete custom.
- **Dashboard:** Total balance, monthly income/expense, recent transactions.
- **Budgets:** Monthly budget per category, used/remaining, over-budget warning.
- **Reports:** Daily / monthly / yearly; category-wise expense breakdown.
- **Search & filter:** By date range, category, type.
- **App lock:** PIN (4–6 digits) and biometric (fingerprint).
- **Settings:** Categories, backup/restore, app lock, currency, theme (light/dark/system), font scale.
- **Backup:** Export to JSON/CSV; import from JSON (placeholders in Settings).

## Project Structure

```
app/src/main/java/com/appstudio/finmarka/
├── data/
│   ├── local/           # Room entities, DAOs, DB, prefs
│   ├── model/           # TransactionType, PaymentMode
│   └── repository/      # Transaction, Category, Budget, Backup
├── di/                  # Hilt modules (DB, Gson)
├── domain/model/        # Transaction, Category, Budget
├── navigation/          # Screen routes
├── ui/
│   ├── screens/         # Splash, Lock, Dashboard, Transactions, Add/Edit, Budget, Reports, Settings
│   ├── theme/           # Colors, Theme, Typography
│   ├── util/            # FormatUtils (currency, date)
│   └── viewmodel/       # ViewModels for each screen
├── FinmarkaApplication.kt
└── MainActivity.kt
```

## Running

1. Open in Android Studio.
2. Sync Gradle (JDK 11+).
3. Run on device/emulator (min SDK 24).

## Database (Room)

- **transactions:** id, amount, type, categoryId, date, note, paymentMode, createdTimestamp
- **categories:** id, name, type (INCOME/EXPENSE)
- **budgets:** id, categoryId, limitAmount, month, year

Default categories are inserted on first run via `CategoryRepository.ensureDefaultCategories()`.

## Privacy

- All data is stored locally; no internet required.
- No ads SDK; no backend; no login.
