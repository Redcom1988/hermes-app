# 📱 HERMES - Integrated HR & Project Management System

[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://www.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Material%20Design-3-757575?logo=material-design&logoColor=white)](https://m3.material.io/)

**Hermes** is a production-ready Android application that seamlessly integrates **Human Resources Management System (HRMS)** with **Project Management System (PMS)** in one unified platform. Built entirely in Kotlin with modern Android development practices, it provides a complete solution for managing employees, tasks, clients, meetings, and services.

**Developed during PKL (Internship) at PT. Asanka**

---

## 📋 Table of Contents

- [Features](#-features)
- [Screenshots](#-screenshots)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Modules](#-modules)
- [Database Schema](#-database-schema)
- [Permissions](#-permissions)
- [Installation](#-installation)
- [API Integration](#-api-integration)
- [User Roles](#-user-roles--permissions)
- [Use Cases](#-use-cases)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### 🏠 **Home Dashboard**
- **Personalized Welcome Card**: User name, role badge (Admin/PM/Developer), and profile picture
- **Employee Quick Status**: Current attendance status (checked in/out) with real-time work hours
- **Admin Statistics**: Total employees, divisions, user accounts, and system metrics
- **Task Overview**: Assigned tasks, pending count, and upcoming deadlines
- **Work Hour Progress**: Visual progress bar showing today's hours vs required hours

### 📍 **Attendance Management**
- **Smart Check-In/Check-Out System**:
  - Timestamp-based check-in with automatic location tracking (GPS coordinates)
  - Task selection during check-in for productivity tracking
  - Work location selection: Office or Anywhere (WFH)
  - Optional photo capture for verification
  - Minimum work hour validation before check-out
  
- **Real-Time Tracking**:
  - Live work duration monitoring with foreground service
  - Persistent notification showing elapsed time (updates every minute)
  - Quick check-out action directly from notification
  - Automatic service restart after device reboot
  
- **Attendance History**:
  - Monthly calendar view with attendance indicators
  - Detailed session list with duration in HH:MM format
  - Work location and GPS coordinates per session
  - Linked tasks per attendance session
  - Date range filtering

### ✅ **Task Management**
- **Task Creation & Editing**:
  - Create tasks with name, description, deadline, and status
  - Support for parent-child task relationships (subtasks)
  - Rich notes field for additional context
  - Date/time picker for deadline setting
  
- **Task Assignment**:
  - Multi-employee assignment with division-based filtering
  - Visual employee selection cards with division info
  - Employee search functionality
  - Cross-reference tracking via `EmployeeTaskCrossRef`
  
- **Task Status Tracking**:
  - Status types: PENDING, IN_PROGRESS, COMPLETED, ISSUE, CANCELLED
  - Color-coded status badges
  - Real-time status updates
  
- **Role-Based Task Views**:
  - **Developer**: See only tasks assigned to them
  - **Project Manager**: View all team tasks + create/assign capabilities
  - **Admin**: Global overview of all tasks
  
- **Task Integration**:
  - Link tasks to attendance sessions via check-in
  - Task confirmation on check-out
  - Analytics and reporting per employee

### 👥 **Employee Management**
- **Complete Employee Database**:
  - Full name, profile image, phone number
  - Gender, birth date, address
  - Division assignment
  - Employment status (active/inactive)
  - User account association
  
- **Employee Analytics**:
  - Total work hours (daily/monthly)
  - Tasks completed count
  - Attendance rate calculation
  - Division-based grouping

### 🏛️ **Division Management**
- **Organizational Structure**:
  - List all company divisions with member count
  - Division types: PM (Project Manager), DEV (Developer), and custom types
  - Required work hours per division
  - Division-specific access control
  
- **Division Analytics**:
  - Productivity metrics per division
  - Task completion rates
  - Attendance statistics
  - Resource allocation insights

### 🤝 **Meeting Management**
- **Meeting Scheduling**:
  - Create meetings with title, notes/agenda
  - Set start and end date/time
  - Location specification
  
- **Participant Management**:
  - Add internal participants (employees) via `MeetingUserCrossRef`
  - Add external clients via `MeetingClientCrossRef`
  - Multi-participant selection with search
  
- **Meeting History**:
  - Chronological meeting list
  - Date-based filtering
  - Search by title
  - Participant list display

### 🏢 **Client Management**
- **Client Database**:
  - Client name, phone number, email, address
  - Active/inactive status tracking
  - Creation/update timestamps
  
- **Client Data Management** (Account Credentials):
  - Store multiple accounts per client (email, hosting, social media, etc.)
  - Account type categorization
  - Encrypted password storage
  - Username/credential management
  - Soft delete for data entries
  
- **Client Detail View**:
  - Tabbed interface for:
    - Client information overview
    - Client data/accounts list
    - Services provided to client
    - Meeting history with client
  - Quick action buttons (Edit, Add Data, Add Service)

### 🛠️ **Service Management**
- **Service Catalog**:
  - Service types with descriptions (Web Development, Mobile App, SEO, etc.)
  - Service status tracking (ongoing, completed, pending)
  - Pricing information
  - Start and expiry dates
  - Client association
  
- **Custom Fields System**:
  - Dynamic fields per service type via `ServiceTypeField`
  - Flexible data structure with `ServiceTypeDataCrossRef`
  - Field customization and validation
  - Template system for common services

### 📅 **Work Hour Planning (Workplan)**
- **Schedule Management**:
  - Daily work hour planning for employees
  - Set planned start and end times
  - Work location selection (Office/Anywhere)
  - Employee assignment
  
- **Calendar View**:
  - Visual monthly calendar with month navigation
  - Date-based filtering
  - Employee-specific schedules
  - Conflict detection for overlapping schedules
  
- **Reporting**:
  - Planned vs actual hours comparison
  - Attendance compliance tracking
  - Location analytics (office vs remote work)
  - Team availability overview

### ⚙️ **Settings & Configuration**
- **App Preferences**:
  - Theme settings (Light/Dark mode support)
  - User preferences management
  
- **Account Management**:
  - Logout functionality
  - Account information display
  
- **Data Management**:
  - **Force Wipe & Repopulate**: Clear local database and re-sync from server
  - Manual sync trigger
  - Cache clearing

### 🔄 **Data Synchronization**
- **Full Sync Architecture**:
  - Push local changes (with temp IDs) to server
  - Clear local database
  - Pull all data from server with real IDs
  - Upsert into local database
  
- **Background Sync**:
  - WorkManager-based periodic sync
  - Manual sync via pull-to-refresh
  - Force sync option in settings
  - Network-based synchronization
  
- **Offline-First Approach**:
  - Local Room database caching
  - Sync status tracking per entity (CREATED, UPDATED, DELETED)
  - Conflict resolution (server wins)
  - Auto-sync on app launch

---

## 📸 Screenshots

> *Coming soon - Add screenshots of main screens*

---

## 🏗️ Architecture

### **MVVM + Clean Architecture**

```
┌─────────────────────────────────────────────────────┐
│                  UI Layer (Compose)                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │   Screen    │  │   Screen    │  │   Screen    │ │
│  │   Model     │  │   Model     │  │   Model     │ │
│  └─────────────┘  └─────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│              Domain Layer (Business Logic)           │
│  ┌─────────────────────────────────────────────┐   │
│  │  Repositories (Interfaces)                   │   │
│  │  - AttendanceRepository                      │   │
│  │  - TaskRepository                            │   │
│  │  - ClientRepository, etc.                    │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │  Domain Models                               │   │
│  │  - Attendance, Task, Client, Meeting, etc.   │   │
│  └─────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                  Data Layer                          │
│  ┌──────────────┐              ┌──────────────┐    │
│  │   Local DB   │              │   Remote API │    │
│  │   (Room)     │              │   (OkHttp)   │    │
│  │              │              │              │    │
│  │ - 19 Entities│              │ - REST APIs  │    │
│  │ - 14 DAOs    │              │ - Auth       │    │
│  │ - Converters │              │ - Sync       │    │
│  └──────────────┘              └──────────────┘    │
│                                                      │
│  ┌─────────────────────────────────────────────┐   │
│  │  Repository Implementations                  │   │
│  │  - Coordinate local + remote data            │   │
│  │  - Handle sync logic                         │   │
│  └─────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### **Key Architectural Decisions**

- **Single Source of Truth**: Room database as the primary data source
- **Offline-First**: App works fully offline with background sync
- **Repository Pattern**: Abstraction layer between data sources and business logic
- **Screen Models**: State management using Voyager's ScreenModel
- **Dependency Injection**: Koin for clean DI
- **Reactive Streams**: Kotlin Flow for reactive data propagation

---

## 💻 Tech Stack

### **Core Technologies**
- **Language**: Kotlin 100%
- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14+ (API 36)

### **UI Framework**
- **Jetpack Compose**: Modern declarative UI
- **Material Design 3**: Latest design system
- **Material Motion Compose**: Smooth animations and transitions
- **Coil**: Image loading and caching

### **Architecture Components**
- **Room**: SQLite database with compile-time SQL verification
- **ViewModel**: Lifecycle-aware state holders
- **Flow & Coroutines**: Asynchronous programming
- **WorkManager**: Background task scheduling
- **Navigation**: Voyager type-safe navigation

### **Dependency Injection**
- **Koin**: Lightweight DI framework

### **Networking**
- **OkHttp**: HTTP client
- **Kotlinx Serialization**: JSON parsing
- **Brotli Compression**: Network optimization
- **DNS over HTTPS**: Secure DNS resolution

### **Storage & Files**
- **SharedPreferences**: User preferences (via PreferenceKTX)
- **Room Database**: Structured data storage (SQLite)

### **Location Services**
- **Google Play Services Location**: GPS coordinate tracking

### **Special Libraries**
- **Voyager**: Navigation and screen management
- **Material Motion Compose**: Advanced animations
- **PreferenceKTX**: Type-safe SharedPreferences wrapper
- **Splash Screen API**: Modern splash screen implementation

---

## 📦 Modules

### **Package Structure** (`dev.redcom1988.hermes`)

```
hermes-app/
├── app/src/main/java/dev/redcom1988/hermes/
│   ├── core/                       # Core utilities
│   │   ├── di/                     # Dependency injection modules
│   │   ├── network/                # Networking utilities
│   │   ├── preference/             # Preferences management
│   │   └── util/                   # Extension functions & helpers
│   │
│   ├── data/                       # Data layer
│   │   ├── local/                  # Local data sources
│   │   │   ├── account_data/       # User & employee data
│   │   │   ├── attendance/         # Attendance data
│   │   │   ├── auth/               # Authentication data
│   │   │   ├── client/             # Client data
│   │   │   ├── meeting/            # Meeting data
│   │   │   ├── service/            # Service data
│   │   │   ├── task/               # Task data
│   │   │   └── workhour_plan/      # Work plan data
│   │   │
│   │   ├── remote/                 # Remote data sources
│   │   │   ├── api/                # API interfaces & implementations
│   │   │   └── model/              # DTOs (Data Transfer Objects)
│   │   │
│   │   └── sync/                   # Sync logic
│   │       ├── SyncDataJob.kt      # Background sync worker
│   │       └── SyncRepositoryImpl.kt
│   │
│   ├── domain/                     # Domain layer (business logic)
│   │   ├── account_data/           # User, employee, division models
│   │   ├── attendance/             # Attendance domain models
│   │   ├── auth/                   # Auth domain models
│   │   ├── client/                 # Client domain models
│   │   ├── meeting/                # Meeting domain models
│   │   ├── service/                # Service domain models
│   │   ├── task/                   # Task domain models
│   │   └── workhour_plan/          # Work plan domain models
│   │
│   ├── ui/                         # UI layer
│   │   ├── component/              # Reusable UI components
│   │   │   ├── AppBar.kt
│   │   │   ├── CurrentStatusCard.kt
│   │   │   ├── TaskCard.kt
│   │   │   ├── StatusChip.kt
│   │   │   └── ... (19+ components)
│   │   │
│   │   ├── main/                   # Main layout & navigation
│   │   │   ├── MainLayout.kt
│   │   │   ├── Sidebar.kt
│   │   │   └── BottomBar.kt
│   │   │
│   │   ├── screen/                 # Feature screens
│   │   │   ├── attendance/         # Attendance screens
│   │   │   ├── client/             # Client screens
│   │   │   ├── division/           # Division screens
│   │   │   ├── employee/           # Employee screens
│   │   │   ├── home/               # Home dashboard
│   │   │   ├── login/              # Login screen
│   │   │   ├── meeting/            # Meeting screens
│   │   │   ├── service/            # Service screens
│   │   │   ├── settings/           # Settings screen
│   │   │   ├── task/               # Task screens
│   │   │   └── workplan/           # Work plan screens
│   │   │
│   │   └── theme/                  # Material 3 theming
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   │
│   ├── service/                    # Android Services
│   │   ├── AttendanceNotificationService.kt
│   │   └── AttendanceNotificationReceiver.kt
│   │
│   └── App.kt                      # Application class
│
└── build.gradle.kts                # Build configuration
```

**Total Statistics:**
- **323 Kotlin files**
- **13,382+ lines of screen code**
- **19 database entities**
- **14 DAOs (Data Access Objects)**
- **10+ feature modules**
- **19+ reusable UI components**

---

## 🗄️ Database Schema

### **Room Database** (`HermesDatabase`)

**Version**: 8 (with destructive migration enabled)

#### **19 Entity Tables:**

| Table Name | Description | Key Fields |
|------------|-------------|------------|
| `UserEntity` | User accounts & credentials | id, email, role |
| `EmployeeEntity` | Employee personal data | id, user_id, division_id, full_name |
| `DivisionEntity` | Company divisions | id, division_name, required_workhours |
| `AttendanceEntity` | Attendance sessions | id, employee_id, start_time, end_time |
| `TaskEntity` | Project tasks | id, task_name, deadline, status |
| `MeetingEntity` | Meeting records | id, title, start_time, end_time |
| `ClientEntity` | Client information | id, name, email, phone_number |
| `ClientDataEntity` | Client account credentials | id, client_id, account_type, username, password |
| `ServiceEntity` | Services provided | id, client_id, service_type_id, price |
| `ServiceTypeEntity` | Service categories | id, name, description |
| `ServiceTypeFieldEntity` | Custom field definitions | id, service_type_id, field_name |
| `WorkhourPlanEntity` | Work schedules | id, employee_id, plan_date, location |
| `AccessEntity` | Permission definitions | id, access_name, description |
| `AttendanceTaskCrossRefEntity` | Attendance ↔ Task | attendance_id, task_id |
| `EmployeeTaskCrossRefEntity` | Employee ↔ Task | employee_id, task_id |
| `MeetingUserCrossRefEntity` | Meeting ↔ User | meeting_id, user_id |
| `MeetingClientCrossRefEntity` | Meeting ↔ Client | meeting_id, client_id |
| `ServiceTypeDataCrossRefEntity` | Service ↔ Custom Fields | field_id, service_id, value |
| `DivisionAccessCrossRefEntity` | Division ↔ Access | division_id, access_id |

#### **Relationships:**

```
User ──1:1── Employee ──M:1── Division
                │
                ├──1:M── Attendance ──M:M── Task
                │
                └──M:M── Task

Client ──1:M── ClientData
       └──1:M── Service ──M:1── ServiceType
                                      └──1:M── ServiceTypeField

Meeting ──M:M── User
         └──M:M── Client

Employee ──1:M── WorkhourPlan

Division ──M:M── Access
```

---

## 🔐 Permissions

### **Required Permissions:**

| Permission | Usage |
|------------|-------|
| `INTERNET` | API communication with backend |
| `POST_NOTIFICATIONS` | Display attendance notification |
| `WAKE_LOCK` | Keep service alive during attendance |
| `ACCESS_FINE_LOCATION` | Precise GPS coordinates for check-in/out |
| `ACCESS_COARSE_LOCATION` | Approximate location tracking |
| `FOREGROUND_SERVICE` | Run attendance tracking service |
| `FOREGROUND_SERVICE_LOCATION` | Location-based foreground service |

---

## 📥 Installation

### **Prerequisites:**
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or newer
- Android SDK 26+
- Kotlin 1.9.0+

### **Setup Steps:**

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/hermes-app.git
   cd hermes-app
   ```

2. **Configure API endpoint:**
   
   Edit `local.properties` (or create if not exists):
   ```properties
   API_BASE_URL=http://your-backend-url:8000/api
   ```

3. **Sync Gradle:**
   ```bash
   ./gradlew sync
   ```

4. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run on device/emulator:**
   ```bash
   ./gradlew installDebug
   ```

### **Backend Setup:**

The app requires a Laravel backend (HRMS project). Ensure:
- Laravel server is running on accessible network
- Database is seeded with test data
- API endpoints are configured correctly
- CORS is enabled for mobile access

Start Laravel server:
```bash
cd path/to/HRMS
php artisan serve --host=0.0.0.0 --port=8000
```

---

## 🌐 API Integration

### **Base URL Configuration:**
Configure in `NetworkModule.kt` or `local.properties`

### **Authentication:**
- **Token-based**: Laravel Sanctum tokens
- **Interceptor**: Automatic token injection via `AuthInterceptor`
- **Token Refresh**: Auto-refresh on 401 response

### **API Endpoints:**

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/mobile/login` | POST | User authentication |
| `/mobile/logout` | POST | User logout |
| `/sync/pull` | GET | Fetch all data (with optional `since` timestamp) |
| `/sync/push` | POST | Push local changes to server |

### **Data Transfer:**
- **Format**: JSON
- **Serialization**: Kotlinx Serialization
- **Compression**: Brotli + Gzip
- **Encoding**: UTF-8

### **Sync Flow:**

```
Mobile App                          Backend Server
    │                                     │
    ├──1. Push local changes─────────────>│
    │   (tasks with ID=-1, new records)   │
    │                                     │
    │<─────2. Server creates records──────┤
    │        (assigns real IDs)           │
    │                                     │
    ├──3. Clear local database────────────│
    │                                     │
    ├──4. Pull all data───────────────────>│
    │   (GET /sync/pull?since=)           │
    │                                     │
    │<─────5. Return all data─────────────┤
    │        (including new records       │
    │         with real IDs)              │
    │                                     │
    ├──6. Insert all into local DB────────│
    │   (temp IDs replaced with real)     │
    │                                     │
    └──7. Save lastSyncTime───────────────┘
```

---

## 👥 User Roles & Permissions

### **1. Admin**
✅ **Full System Access**
- User management (create, edit, delete)
- Division management
- System configuration
- View all data globally
- Create, edit, delete all entities
- Access control management

❌ **No Restrictions**

---

### **2. Project Manager (PM)**
✅ **Allowed:**
- Task management (create, assign, track)
- View team attendance
- Meeting management (schedule, edit)
- Client management (create, edit)
- Service assignment to clients
- Work plan creation for team
- Team analytics and reports

❌ **Restricted:**
- System settings modification
- User role management
- Access to other divisions (if implemented)

---

### **3. Developer (DEV)**
✅ **Allowed:**
- View tasks assigned to them
- Update task status (pending → in progress → completed)
- Personal attendance (check-in/out)
- View own work plan/schedule
- Participate in assigned meetings
- View client details (if assigned to project)

❌ **Restricted:**
- Create or assign tasks
- Access other developers' attendance data
- Client management (create, edit, delete)
- Division management
- User management
- System settings

---

### **4. User (Generic Role)**
✅ **Allowed:**
- Basic authentication
- View own profile
- Update own profile data

❌ **Restricted:**
- Most features (unless also an Employee)

---

## 🎯 Use Cases

### **For Software Houses:**
- Track developer productivity via attendance + task linking
- Manage multiple client projects simultaneously
- Monitor project deadlines and task completion
- Allocate resources across projects
- Generate billing reports based on work hours

### **For Digital Agencies:**
- Client relationship management with credentials storage
- Service catalog with custom pricing
- Meeting coordination with clients
- Team collaboration via task assignments
- Portfolio tracking (services provided)

### **For IT Consulting Firms:**
- Employee time tracking for client billing
- Project-based task management
- Client meeting history and notes
- Resource planning via work hour schedules
- Multi-division organizational structure

### **For Startups & Tech Companies:**
- Simple onboarding with employee management
- Flexible task tracking for agile teams
- Remote work support (WFH attendance tracking)
- Real-time team productivity insights
- Lightweight HR management without complex systems

### **For Remote/Hybrid Teams:**
- Location-based attendance (office vs anywhere)
- GPS verification for check-in/out
- Flexible work schedules via work plans
- Async task collaboration
- Centralized communication via meetings

---

## 📂 Project Structure

### **Layers:**

1. **UI Layer** (`ui/`)
   - Jetpack Compose screens
   - Screen models for state management
   - Reusable UI components
   - Material 3 theming

2. **Domain Layer** (`domain/`)
   - Business entities (models)
   - Repository interfaces
   - Business logic encapsulation
   - Use case implementations

3. **Data Layer** (`data/`)
   - Repository implementations
   - Local data sources (Room)
   - Remote data sources (API)
   - DTOs and mappers
   - Sync logic

4. **Core Layer** (`core/`)
   - Dependency injection
   - Network configuration
   - Utilities and extensions
   - Shared preferences

### **Key Files:**

- **App.kt**: Application entry point, DI initialization
- **MainLayout.kt**: Main navigation container
- **HermesDatabase.kt**: Room database configuration
- **NetworkModule.kt**: OkHttp and API setup
- **SyncDataJob.kt**: Background sync worker
- **AttendanceNotificationService.kt**: Foreground service for tracking

---

## 🤝 Contributing

### **Guidelines:**

1. **Code Style**:
   - Follow Kotlin coding conventions
   - Use ktlint for formatting
   - Write meaningful commit messages

2. **Branching**:
   - `main`: Production-ready code
   - `develop`: Development branch
   - Feature branches: `feature/feature-name`
   - Bugfix branches: `bugfix/bug-description`

3. **Pull Requests**:
   - Provide clear description
   - Reference related issues
   - Ensure all tests pass
   - Update documentation if needed

4. **Testing**:
   - Write unit tests for business logic
   - Test UI with Compose testing library
   - Ensure no regressions

---

## 📄 License

This project was developed during an internship (PKL) at **PT. Asanka**.

All rights reserved to PT. Asanka unless otherwise specified.

---

## 🙏 Acknowledgments

- **PT. Asanka** for the internship opportunity
- **Jetpack Compose** team for the modern UI toolkit
- **Voyager** for type-safe navigation
- **Material Design** for design guidelines
- Open source community for libraries and tools

---

## 📞 Contact

For questions, feedback, or collaboration:

- **Developer**: [Your Name]
- **Email**: [your.email@example.com]
- **GitHub**: [yourusername]
- **Company**: PT. Asanka

---

**Built with ❤️ using Kotlin & Jetpack Compose**
