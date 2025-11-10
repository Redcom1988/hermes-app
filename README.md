📱 HERMES APP - Sistem Manajemen HR & Proyek Terpadu

Hermes App adalah aplikasi mobile Android yang menggabungkan Human
Resources Management System (HRMS) dengan Project Management System
(PMS) dalam satu platform terintegrasi. Dibangun sepenuhnya menggunakan
Kotlin dengan arsitektur modern. 🎯 FITUR-FITUR LENGKAP 1. 📍 Manajemen
Kehadiran (Attendance Management)

Fitur Check-In/Check-Out:

    Sistem absensi digital dengan timestamp otomatis
    Check-in dengan pemilihan tugas yang akan dikerjakan
    Check-out dengan konfirmasi tugas yang telah diselesaikan
    Pelacakan lokasi kerja (Office/WFH)
    Validasi waktu kerja minimum sebelum check-out

Monitoring Jam Kerja:

    Tracking jam kerja harian real-time
    Perhitungan total jam kerja vs jam kerja yang diperlukan
    Visualisasi kalender kehadiran bulanan
    Statistik kehadiran per periode
    Riwayat sesi kehadiran lengkap

Detail Attendance:

    Informasi waktu mulai dan selesai
    Durasi kerja dalam format jam:menit
    Lokasi check-in/check-out
    Daftar tugas yang dikerjakan selama sesi
    Status sesi (aktif/selesai)
    Indicator visual untuk sesi aktif

2.  ✅ Manajemen Tugas (Task Management)

Pembuatan & Assignment:

    Buat tugas baru dengan deskripsi lengkap
    Assign tugas ke developer tertentu
    Set prioritas dan deadline
    Kategori tugas berdasarkan proyek
    Status tracking (Pending, In Progress, Completed)

Tampilan Role-Based:

    Developer View: Daftar tugas yang di-assign ke mereka
    Project Manager View: Semua tugas tim + kemampuan create & assign
    Admin View: Overview semua tugas di perusahaan

Task Details:

    Informasi lengkap tugas (judul, deskripsi, deadline)
    Daftar developer yang ditugaskan
    History progress tugas
    Komentar dan catatan
    Attachment dan dokumentasi

Task Integration:

    Integrasi dengan sistem attendance (tugas dikerjakan saat check-in)
    Link tugas dengan work plan
    Reporting tugas per karyawan
    Analytics produktivitas

3.  👥 Manajemen Karyawan (Employee Management)

Profil Karyawan:

    Data pribadi lengkap (nama, foto, tanggal lahir, gender)
    Informasi kontak (nomor telepon, alamat)
    Status aktif/non-aktif

Informasi Organisasi:

    Divisi/departemen karyawan
    Role dalam tim (PM, Developer, dll)
    User account terkait
    Tanggal bergabung

Employee Analytics:

    Total jam kerja
    Jumlah tugas selesai
    Tingkat kehadiran

4.  🤝 Manajemen Rapat (Meeting Management)

Penjadwalan Meeting:

    Buat entry record rapat baru
    Set judul, deskripsi, dan agenda
    Tentukan tanggal dan waktu
    Pilih lokasi (fisik/online)

Manajemen Peserta:

    Input user internal (karyawan)
    Input klien eksternal

Detail Meeting:

    Agenda dan topik pembahasan
    Catatan meeting (notes)
    Attachment dokumen

Meeting History:

    Riwayat semua meeting
    Filter berdasarkan tanggal
    Search meeting berdasarkan judul

5.  🏢 Manajemen Klien (Client Management)

Database Klien:

    Data lengkap klien (nama lengkap, perusahaan)
    Informasi kontak (email, telepon, alamat)
    Kategori klien
    Status aktif/non-aktif

Client Data Management:

    Penyimpanan data akun klien (credentials)
    Multiple account per klien
    Tipe akun (email, social media, hosting, dll)
    Password management (encrypted)

Client Services:

    Daftar layanan yang digunakan klien
    History layanan yang pernah diberikan
    Service details dengan custom fields
    Contract dan agreement

Client Detail View:

    Overview informasi klien
    Tab untuk data akun
    Tab untuk services
    Tab untuk meeting history
    Quick actions (edit, add data, add service)

6.  🛠️ Manajemen Layanan (Service Management)

Services Logs:

    Daftar semua layanan yang ditawarkan
    Tipe layanan (Website Development, Mobile App, SEO, dll)
    Deskripsi layanan lengkap
    Pricing info

Service Types:

    Kategori layanan yang dapat dikustomisasi
    Custom fields untuk setiap tipe
    Flexible data structure
    Template untuk layanan umum

Service Assignment:

    Link layanan dengan klien
    Assign tim untuk layanan
    Track status pengerjaan
    Documentation dan deliverables

Custom Fields:

    Field dinamis per tipe layanan
    Input validation
    Data types (text, number, date, dll)
    Required/optional fields

7.  🏛️ Manajemen Divisi (Division Management)

Struktur Organisasi:

    Daftar semua divisi perusahaan
    Tipe divisi:
        PM (Project Manager): Divisi manajemen proyek
        DEV (Developer): Divisi development
        Custom divisions lainnya

Division Details:

    Nama divisi
    Deskripsi dan responsibilities
    Jumlah karyawan per divisi
    Division head/leader

Division Analytics:

    Produktivitas per divisi
    Task completion rate
    Attendance statistics
    Resource allocation

8.  📅 Rencana Kerja (Work Plan/Schedule)

Work Hour Planning:

    Jadwal jam kerja harian karyawan
    Set waktu mulai dan selesai yang direncanakan
    Pilih lokasi kerja (Office/WFH/Anywhere)
    Recurring schedules (harian, mingguan)

Schedule Management:

    Kalender view untuk work plans
    Filter berdasarkan:
        Tanggal/periode
        Karyawan tertentu
        Lokasi kerja

Work Plan Features:

    Edit dan update schedule
    Delete work plans
    Conflict detection (overlapping schedules)

Reporting:

    Planned vs actual hours
    Attendance compliance
    Location tracking (office vs remote)
    Team availability overview

9.  👤 Manajemen Pengguna (User Management)

User Accounts:

    Login credentials (email/username, password)
    User roles:
        Admin: Full access ke semua fitur
        User: Limited access based on role

User Details:

    Email dan profile information
    Created date
    Last login tracking
    Account status (active/suspended)

User-Employee Link:

    Setiap user dapat memiliki data employee
    Tidak semua user adalah employee (bisa admin saja)
    Link untuk akses HR features

Access Control:

    Role-based permissions
    Feature access management
    Data visibility rules
    Action restrictions per role

10. 🏠 Dashboard Home

Personalized Welcome:

    Greeting dengan nama user
    Role badge (Admin/PM/Developer/Employee)
    Profile picture

Attendance Quick Action (untuk Employee):

    Current status card:
        Status: Checked In/Out
        Waktu check-in/out hari ini
        Total jam kerja hari ini
        Jam kerja yang diperlukan
        Progress bar jam kerja
    Quick button untuk Check In/Out
    Task selection untuk check-in

Employee Dashboard:

    Employee details card:
        Divisi
        Phone number
        Address
        Birth date
        Gender

Admin Statistics Card:

    Total karyawan
    Total divisi
    Total user accounts
    System overview metrics
    Quick links ke management screens

Task Overview (PM & Developer):

    Total tugas assigned
    Jumlah tugas belum selesai
    Upcoming deadlines
    Recent task activities
    Quick access ke task details

11. ⚙️ Pengaturan (Settings)

App Preferences:

    Theme settings (Light/Dark mode)

Data Management:

    Cache management

🔐 SISTEM ROLE & PERMISSIONS Admin

    ✅ Full access ke semua fitur
    ✅ User management
    ✅ Division management
    ✅ System configuration
    ✅ View semua data (global)
    ✅ Create, edit, delete semua entities

Project Manager (PM)

    ✅ Task management (create, assign, track)
    ✅ View team attendance
    ✅ Meeting management
    ✅ Client management
    ✅ Service assignment
    ✅ Work plan creation
    ✅ Team analytics
    ❌ System settings
    ❌ User role management

Developer (DEV)

    ✅ View assigned tasks
    ✅ Update task status
    ✅ Personal attendance (check-in/out)
    ✅ View own work plan
    ✅ Participate in meetings
    ❌ Create/assign tasks
    ❌ Access other developers' data
    ❌ Client management

💻 TEKNOLOGI & ARSITEKTUR Platform & Language:

    Android Native
    Kotlin 100% (modern, null-safe)
    Minimum SDK: Android 7.0+ (API 24)

UI Framework:

    Jetpack Compose: Modern declarative UI
    Material Design 3: Latest design system
    Custom components untuk reusability
    Responsive layouts untuk berbagai ukuran layar

Architecture:

    MVVM Pattern (Model-View-ViewModel)
    Screen Model dari Voyager untuk state management
    Repository Pattern untuk data layer
    Clean Architecture dengan separation of concerns

Navigation:

    Voyager Navigator: Type-safe navigation
    Screen transitions dengan animasi smooth
    Deep linking support
    Bottom navigation untuk main screens
    Drawer navigation untuk menu tambahan

Data Layer:

    Local Database: SQLite dengan Room (kemungkinan)
    Repository Pattern untuk abstraksi data
    Offline-first approach
    Data synchronization dengan backend
    Caching strategy

Key Libraries:

    Kotlin Coroutines: Asynchronous programming
    Flow: Reactive data streams
    Koin/Hilt: Dependency injection
    Material Motion: Smooth animations
    Kotlinx Serialization: JSON parsing

Features Implementation:

    State Management: Compose State & Flow
    Loading States: Progress indicators
    Error Handling: User-friendly error messages
    Form Validation: Input checks
    Date/Time Pickers: Material Design pickers
    Search & Filter: Real-time filtering
    Dialogs & Modals: Compose dialogs

✨ KEUNGGULAN APLIKASI User Experience:

    🎨 Modern UI: Clean, intuitive, Material Design 3
    🚀 Fast Performance: Smooth animations dan transitions
    📱 Responsive: Adaptive untuk berbagai ukuran layar
    🌙 Dark Mode Ready: Support tema gelap (jika diimplementasi)

Functionality:

    🔄 Real-time Updates: Live data synchronization
    📊 Rich Analytics: Comprehensive reports dan statistics
    🔍 Advanced Search: Powerful filtering options
    📅 Calendar Integration: Visual schedule management

Reliability:

    💾 Offline Support: Bekerja tanpa internet
    🔒 Data Security: Encrypted sensitive data
    📦 Auto Backup: Automatic data backup
    ⚡ Error Recovery: Graceful error handling

Productivity:

    ⏱️ Time Tracking: Accurate hour logging
    📋 Task Organization: Clear task hierarchy
    🎯 Goal Tracking: Monitor progress
    📈 Performance Metrics: Productivity insights

Integration:

    🔗 Unified System: HR + Project Management dalam 1 app
    🤝 Cross-module Links: Attendance, tasks, meetings terintegrasi
    📱 Centralized Data: Single source of truth
    🔄 Seamless Flow: Smooth workflow antar fitur

🎯 USE CASES Untuk Perusahaan:

    Monitoring produktivitas karyawan
    Project tracking dan deadline management
    Client relationship management
    Resource allocation dan planning
    Attendance dan payroll integration

Untuk Project Manager:

    Task assignment dan tracking
    Team performance monitoring
    Meeting coordination
    Client communication management
    Project timeline oversight

Untuk Developer:

    Clear task visibility
    Time tracking untuk billing
    Work schedule clarity
    Progress reporting
    Collaboration dengan tim

Untuk HR/Admin:

    Employee database management
    Attendance monitoring
    Division organization
    User access control
    System administration

📊 DATA MODELS (Domain Structure)

Core Entities:

    User: Login credentials dan roles
    Employee: Data karyawan lengkap
    Division: Struktur organisasi
    Attendance: Record kehadiran
    Task: Tugas project
    Meeting: Jadwal meeting
    Client: Data klien
    Service: Layanan perusahaan
    WorkhourPlan: Jadwal kerja

Cross-Reference Tables:

    EmployeeTaskCrossRef: Many-to-many employee-task
    AttendanceTaskCrossRef: Tugas yang dikerjakan per sesi
    ServiceTypeDataCrossRef: Custom field data
    MeetingClientCrossRef: Klien di meeting
    MeetingUserCrossRef: User di meeting

Enums:

    UserRole: ADMIN, USER
    DivisionType: PM, DEV
    WorkLocation: OFFICE, ANYWHERE
    TaskStatus: (kemungkinan Pending, InProgress, Completed)

🚀 KESIMPULAN

Hermes App adalah solusi all-in-one yang powerful untuk perusahaan yang
membutuhkan sistem terintegrasi antara HR management dan project
management. Dengan fitur-fitur lengkap dari attendance tracking, task
management, client management, hingga meeting scheduling, aplikasi ini
dirancang untuk meningkatkan produktivitas dan efisiensi operasional
perusahaan, khususnya untuk tim software development atau project-based
organizations.

Dibangun dengan teknologi modern (Kotlin + Jetpack Compose + Material
Design 3), aplikasi ini menawarkan user experience yang smooth dan
interface yang intuitif, sambil tetap menjaga performa dan reliability.

🎯 Cocok untuk:

    Software houses
    Digital agencies
    IT consulting firms
    Project-based companies
    Startup tech companies
    Remote/hybrid teams

This app was made at PKL at a company called PT. Asanka.
