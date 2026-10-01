# 📦 BorrowBox Android Application (Java & XML)

**BorrowBox** is a modern student peer-to-peer item borrowing and sharing platform built natively for Android using **Java** and **XML layouts**, following official Android Studio standards and Material 3 design guidelines.

---

## 📱 Features & Screens

| # | Screen | Description |
|---|---|---|
| 1 | **Splash Screen** | Branded splash header with glowing logo, tagline, and animated transition |
| 2 | **Onboarding Screen** | 3-page interactive carousel with custom illustrations and dot indicators |
| 3 | **Login Screen** | Campus email/password login, show/hide password toggle, and Google Auth option |
| 4 | **Signup Screen** | Full registration form with live password strength indicator and college fields |
| 5 | **Home Screen** | Hero gradient header, unread notifications bell, search trigger, horizontal categories, campus stats banner, and 2-column recommended items grid |
| 6 | **Explore Screen** | Instant search, filter trigger, sort chips (*Nearest, Recent, Popular, Available*), and live item grid |
| 7 | **Search Screen** | Auto-focus search input, recent searches with individual removal and "Clear all", plus popular category chips |
| 8 | **Item Details Screen** | 280dp hero banner image with bookmark/share overlays, status badge, About section, Owner Profile card with rating and Message button, and sticky bottom borrow button |
| 9 | **Borrow Request Screen** | Item summary, duration chips (*1 day to 7 days*), automatic return date calculator, request reason input, and Request Sent celebration view |
| 10 | **Activity Screen** | 3 segmented tabs (*Borrowed, Lent, Requests*), status badges (*Approved, Borrowed, Pending, Overdue, Returned*), QR code trigger, and incoming request Accept/Reject actions |
| 11 | **QR Verification Screen** | In-person handover verification QR display, transaction ID, instructions, and "Mark as Returned" flow |
| 12 | **Add Item Wizard** | 4-step listing wizard with top progress indicators: Step 1 Photo Upload, Step 2 Details (Name, Category, Description), Step 3 Preferences (Duration, Handover location), Step 4 Live Preview Card, and Item Listed success state |
| 13 | **Rating & Review Screen** | Interactive 5-star rating selector with dynamic mood feedback (*Terrible -> Excellent!*), review comments, quick tag chips, and Thank You screen |
| 14 | **Borrowing History Screen** | Completed transactions filter (*All, Borrowed, Lent*) with borrow/return dates and 5-star ratings |
| 15 | **My Items Screen** | Listed item management with segmented tabs (*Available, Borrowed, Inactive*), borrower names, and Edit/Deactivate action buttons |
| 16 | **Notifications Screen** | Unread badges, "Mark all read" button, and color-coded notification cards for Requests, Approvals, Due dates, and Reminders |
| 17 | **Profile Screen** | User profile avatar with verified badge, student details, star rating, stats counters (*12 Lent, 8 Borrowed, 19 Returns*), and account menu |

---

## 🏗️ Architecture & Project Structure

```text
BorrowBox/
├── build.gradle                        # Top-level build configuration
├── settings.gradle                     # Project modules configuration
├── gradle.properties                   # JVM & AndroidX settings
├── app/
│   ├── build.gradle                    # App module dependencies (Material, Glide, ViewPager2, CardView)
│   └── src/main/
│       ├── AndroidManifest.xml         # App manifest registering all 13 Activities & permissions
│       ├── java/com/borrowbox/app/
│       │   ├── models/
│       │   │   ├── Item.java           # Item data model
│       │   │   ├── Transaction.java    # Transaction & borrowing model
│       │   │   ├── NotificationItem.java # Notifications model
│       │   │   ├── Category.java       # Category model
│       │   │   └── OnboardingSlide.java # Onboarding slide model
│       │   ├── data/
│       │   │   └── DataManager.java    # Central data manager singleton with sample data & helpers
│       │   ├── adapters/
│       │   │   ├── CategoryAdapter.java # Horizontal category chips
│       │   │   ├── ItemGridAdapter.java # 2-column card grid with bookmarking
│       │   │   ├── SearchResultAdapter.java # Search results adapter
│       │   │   ├── TransactionAdapter.java  # Borrowed/Lent/Requests adapter
│       │   │   ├── NotificationAdapter.java # Notification items adapter
│       │   │   ├── MyItemAdapter.java       # My items adapter
│       │   │   ├── HistoryAdapter.java      # Borrowing history adapter
│       │   │   └── OnboardingPagerAdapter.java # ViewPager2 slide adapter
│       │   └── ui/
│       │       ├── activities/
│       │       │   ├── SplashActivity.java
│       │       │   ├── OnboardingActivity.java
│       │       │   ├── LoginActivity.java
│       │       │   ├── SignupActivity.java
│       │       │   ├── MainActivity.java
│       │       │   ├── SearchActivity.java
│       │       │   ├── ItemDetailsActivity.java
│       │       │   ├── BorrowRequestActivity.java
│       │       │   ├── QRVerificationActivity.java
│       │       │   ├── RatingActivity.java
│       │       │   ├── HistoryActivity.java
│       │       │   ├── MyItemsActivity.java
│       │       │   └── NotificationsActivity.java
│       │       └── fragments/
│       │           ├── HomeFragment.java
│       │           ├── ExploreFragment.java
│       │           ├── AddItemFragment.java
│       │           ├── ActivityFragment.java
│       │           └── ProfileFragment.java
│       └── res/
│           ├── drawable/               # 30+ vector icons, custom gradients, shapes & badges
│           ├── layout/                 # 27 pixel-perfect XML layout files
│           ├── menu/                   # bottom_nav_menu.xml
│           ├── values/                 # colors.xml, strings.xml, themes.xml
│           └── xml/                    # backup_rules.xml, data_extraction_rules.xml
```

---

## 🚀 How to Open in Android Studio

1. Open **Android Studio**.
2. Click **File** -> **Open...**
3. Select this folder (`c:\Users\nitya\OneDrive\Desktop\MAD Mini_Project`).
4. Android Studio will automatically detect the Gradle configuration and sync project dependencies.
5. Connect an Android phone via USB debugging or start an Android Emulator.
6. Click **Run 'app'** (Green Play button).
