📚 Kitabu — Library Book Rental & Reservation System
Student Name: Lilitha Maseti
Student Number: 24301414
Module: MADB372 — Mobile Application Development B
Assignment: SF1
Date: 2026

1. **Project Overview**
   Kitabu (Swahili for "book") is a native Android application that modernizes library book checkouts
   and tracks workflows for academic institutions. The app enables students to set up a personal profile,
   browse an interactive catalog of academic textbooks, reserve available titles, view return deadlines
   and manage booking lifecycles. This all happens in real time with a responsive, offline-first design.

Problem Statement
Academic institutions rely on traditional library operations that suffer from:

**Lack of real-time inventory visibility** — students travel to the library only to find out books are not available.
**Inefficient rental tracking** — no automated mobile tracking leads to overdue fines.
**Friction in reservation lifecycles** — manual renewals, returns and cancellations cause bottlenecks.
**No offline availability** — web-based catalogs fail on poor mobile networks.

Kitabu solves all of these with a local Room Database, reactive Kotlin Flow streams and a modern Jetpack Compose UI.

2. **Tech Stack** 
**Technology**          **Version**                      **Purpose**
   Kotlin                  2.0.21                          Application logic 
   Jetpack Compose	       BOM 2024.12.01	               Declarative UI (Material 3)
   Room Database	       2.6.1                           Local SQLite persistence
   Kotlin Coroutines	     -                             Asynchronous execution
   Kotlin Flow	             -	                           Reactive state management
   Navigation Compose	   2.8.5	                       Single-activity navigation
   DataStore Preferences   1.1.4	                       Student profile persistence
   KSP	                   2.0.21-1.0.28	               Room annotation processing
   MVVM Architecture	     -	                           Separation of concerns

3. Architecture
   ┌──────────────────────────────────────────────┐
   │ UI Layer │
   │ (Jetpack Compose Screens) │
   │ BookCatalogScreen · ReservationsDashboard │
   │ ProfileScreen │
   ├──────────────────────────────────────────────┤
   │ ViewModel Layer │
   │ (StateFlow + viewModelScope) │
   │ BookCatalogViewModel · ReservationsViewModel│
   ├──────────────────────────────────────────────┤
   │ Repository Layer │
   │ (Business Logic Abstraction) │
   │ LibraryRepository │
   ├──────────────────────────────────────────────┤
   │ Data Layer │
   │ Room DAOs · Entities · DataStore │
   │ BookDao · BookingDao · ProfileManager │
   └──────────────────────────────────────────────┘

### Package Structure

com.stadiolinks.kitabu/
├── data/
│ ├── entity/ → BookEntity, BookingEntity
│ ├── dao/ → BookDao, BookingDao
│ ├── database/ → AppDatabase, Converters
│ ├── repository/ → LibraryRepository
│ └── profile/ → ProfileManager
├── domain/
│ └── model/ → BookingStatus (PENDING, ACTIVE, RETURNED)
├── ui/
│ ├── theme/ → Color, Theme, Typography
│ ├── screens/ → BookCatalogScreen
│ │ → ReservationsDashboardScreen
│ │ → ProfileScreen
│ └── navigation/ → AppNavigation (3 routes)
├── viewmodel/ → BookCatalogViewModel
│ → ReservationsViewModel
└── MainActivity.kt → Single activity entry point

---

## 4. Setup Instructions

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android device or emulator running API 26+

### Installation Steps

1. Clone the repository:
   ```bash
   git clone [YOUR-GITHUB-REPO-URL]