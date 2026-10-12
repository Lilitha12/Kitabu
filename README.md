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

5. Database Schema
   BookEntity (tableName: "books")
   | Field | Type | Description |
   |-------|------|-------------|
   | `bookId` | `Long` (PK, auto-generated) | Primary key |
   | `title` | `String` | Book title |
   | `author` | `String` | Author name |
   | `category` | `String` | Academic category |
   | `isAvailable` | `Boolean` (default: true) | Availability status |
   | `imageRes` | `String` | Drawable resource name for cover image |

   BookingEntity (tableName: "bookings")
   | Field | Type | Description |
   |-------|------|-------------|
   | `bookingId` | `Long` (PK, auto-generated) | Primary key |
   | `bookOwnerId` | `Long` (FK → BookEntity.bookId, CASCADE) | Referenced book |
   | `userName` | `String` | Borrower name (from student profile) |
   | `bookingDate` | `Long` | Reservation timestamp (milliseconds) |
   | `returnDeadline` | `Long` | Return deadline timestamp (milliseconds) |
   | `status` | `BookingStatus` (Enum) | PENDING, ACTIVE, RETURNED |

**Type Converters**
Room does not natively support enums. A Converters class handles:

BookingStatus → String (for storage)
String → BookingStatus (for retrieval)
Pre-Population
The database is pre-populated with 10 academic textbooks on first creation via RoomDatabase.Callback.onCreate().

6. **Student Profile System**
   Profile Data (DataStore Preferences)
   | Field | Key | Validation |
   |-------|-----|------------|
   | Full Name | `student_name` | Letters and spaces only, max 40 chars |
   | Student Number | `student_number` | Alphanumeric only, max 15 chars |
   | Course | `student_course` | Letters and spaces only, max 30 chars |

   **Profile Security**
   Profile is required before any reservation can be made.
   If no profile exists, the reservation bottom sheet displays a warning and a "Go to Profile" button.
   The "Confirm Reservation" button is disabled until a profile is set.
   All text fields are sanitized — special characters are stripped.
   Student name is automatically pulled from the profile when reserving.

7. **CRUD Operations**
   **CREATE** — Reserve a Book
   1.Set up student profile (name, student number, course)
   2.Tap "Reserve" on an available book in the catalog
   3.A Modal Bottom Sheet appears showing:
    Book cover image
    Book title and author
    Borrower name (from profile)
    Rental duration selector (7, 14, 21, or 30 days)
    Calculated return date 
   4.Tap "Confirm Reservation"
   5.A Success Dialog appears with the book cover and confirmation details
   6.The?the book's badge changes from green "Available" to red "Borrowed" reactively

   **READ** — Browse & Search Catalog
   Scroll the book catalog to seeBto see all 10 textbooks with cover images
   Type in the search bar to filter by title or author (SQL LIKE query)
   Use filter chips: All / Available / Borrowed
   Navigate to "My Books" to see active reservations with book details, dates, and days remaining
   
   **UPDATE** — Renew a Booking
   1.Go to "My Books" dashboard
   2.Tap "Ren;Renew" on an active booking
   3.The return deadline extendsCextends by 7 days
   4.The "Days Left" display updates immediately
   
  **UPDATE** — Return a Book
   1.Go to "My Books" dashboard
   2.Tap "Return" on an active booking
   3.The booking status changes to RETURNED
   4.The book becomes "Available" again in the catalog (reactive UI update)
   
**DELETE** — Cancel a Pending Reservation
   1.Go to "My Books" dashboard
   2.Tap "Cancel" on a booking with PENDING status
   3.The booking is removed from the database
   4.The book becomes available again

8. **Reactive UI & State Management**
   | Mechanism | Where Used |
   |-----------|------------|
   | Kotlin `Flow` | Room DAO query returns (e.g., `getAllBooks()`, `getActiveBookings()`) |
   | `StateFlow` | ViewModel exposes state via `.stateIn()` with `WhileSubscribed(5000)` |
   | `collectAsState()` | Compose screens observe ViewModel state |
   | `flatMapLatest` | Combines search query + filter for reactive catalog updates |
   | `combine` | Joins bookings + books for dashboard display |

When a book is reserved or returned, all UI elements update immediately without manual refresh,
the Room Flow emits the new data, the ViewModel StateFlow updates and Compose recomposes the affected composables.

9. Asynchronous Execution
   All database operations run off the main thread:
   | Operation | Threading Mechanism |
   |-----------|-------------------|
   | Insert booking | `viewModelScope.launch { repository.reserveBook(...) }` |
   | Update booking | `viewModelScope.launch { repository.renewBooking(...) }` |
   | Return book | `viewModelScope.launch { repository.returnBook(...) }` |
   | Cancel booking | `viewModelScope.launch { repository.cancelBooking(...) }` |
   | Read queries | Room `Flow` queries are automatically asynchronous |
   | Profile save | `scope.launch { profileManager.saveProfile(...) }` |

10. The 10 Pre-Populated Textbooks
    | # | Title | Author | Category |
    |---|-------|--------|----------|
    | 1 | Introduction to Psychology | James W. Kalat | Psychology |
    | 2 | Principles of Economics | N. Gregory Mankiw | Economics |
    | 3 | Campbell Biology | Lisa A. Urry | Biology |
    | 4 | Chemistry: The Central Science | Theodore E. Brown | Chemistry |
    | 5 | Physics for Scientists and Engineers | Raymond A. Serway | Physics |
    | 6 | Calculus: Early Transcendentals | James Stewart | Mathematics |
    | 7 | Introduction to Algorithms | Thomas H. Cormen | Computer Science |
    | 8 | World History: Patterns of Interaction | McDougal Littell | History |
    | 9 | Business Law: Text and Cases | Kenneth W. Clarkson | Business |
    | 10 | The Elements of Style | William Strunk Jr. | English |

13. Project Structure
    Kitabu_MADB372_SF1/
    ├── app/
    │   └── src/
    │       └── main/
    │           ├── java/com/stadiolinks/kitabu/
    │           │   ├── data/
    │           │   │   ├── dao/
    │           │   │   │   ├── BookDao.kt
    │           │   │   │   └── BookingDao.kt
    │           │   │   ├── database/
    │           │   │   │   ├── AppDatabase.kt
    │           │   │   │   └── Converters.kt
    │           │   │   ├── entity/
    │           │   │   │   ├── BookEntity.kt
    │           │   │   │   └── BookingEntity.kt
    │           │   │   ├── profile/
    │           │   │   │   └── ProfileManager.kt
    │           │   │   └── repository/
    │           │   │       └── LibraryRepository.kt
    │           │   ├── domain/model/
    │           │   │   └── BookingStatus.kt
    │           │   ├── ui/
    │           │   │   ├── navigation/
    │           │   │   │   └── AppNavigation.kt
    │           │   │   ├── screens/
    │           │   │   │   ├── BookCatalogScreen.kt
    │           │   │   │   ├── ProfileScreen.kt
    │           │   │   │   └── ReservationsDashboardScreen.kt
    │           │   │   └── theme/
    │           │   │       ├── Color.kt
    │           │   │       ├── Theme.kt
    │           │   │       └── Type.kt
    │           │   ├── viewmodel/
    │           │   │   ├── BookCatalogViewModel.kt
    │           │   │   └── ReservationsViewModel.kt
    │           │   └── MainActivity.kt
    │           └── res/
    │               └── drawable/
    │                   ├── book_psychology.png
    │                   ├── book_economics.png
    │                   ├── book_biology.png
    │                   ├── book_chemistry.png
    │                   ├── book_physics.png
    │                   ├── book_calculus.png
    │                   ├── book_algorithms.png
    │                   ├── book_history.png
    │                   ├── book_business_law.png
    │                   └── book_elements_style.png
    ├── build.gradle.kts (Project)
    ├── build.gradle.kts (Module :app)
    ├── settings.gradle.kts
    ├── libs.versions.toml
    └── README.md
### Package Structure

15. **References**
        Kotlin Official Documentation: https://kotlinlang.org/docs/
        Jetpack Compose: https://developer.android.com/develop/ui/compose
        Material Design 3: https://m3.material.io/
        Room Database: https://developer.android.com/training/data-storage/room
        Navigation Compose: https://developer.android.com/develop/ui/compose/navigation
        DataStore Preferences: https://developer.android.com/topic/libraries/datastore
        Kotlin Coroutines: https://kotlinlang.org/docs/coroutines-overview.html
        Kotlin Flow: https://kotlinlang.org/docs/flow.html
        KSP: https://developer.android.com/build/migrating-ksp
        Android Studio: https://developer.android.com/studio
        GitHub: https://docs.github.com
        Open Library Covers API: https://openlibrary.org/dev/docs/api/covers










