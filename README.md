# BrightBeat — Personal Wellness Tracker 📱💙

<p align="center">
  <img
    src="docs/images/BrightBeat-cover.png"
    alt="GoviSaviya Agricultural Services Rental Platform"
    width="100%"
  />
</p>

BrightBeat is an Android application built with Kotlin to help users develop healthier daily routines through habit tracking, mood journaling, and hydration monitoring. It brings everyday wellness activities into one place with progress tracking, reminders, and visual summaries.

As a Data Science undergraduate, I see BrightBeat as more than a mobile application: it is also a foundation for exploring how personal wellness data can be collected, organized, visualized, and potentially used to support data-driven self-improvement.

## ✨ Key Features

### ✅ Habit Tracking
- Create and manage personal habits.
- Track daily habit completion.
- Monitor progress toward consistent routines.
- View habit information through the application dashboard.

### 😊 Mood Journal
- Record daily moods using a dedicated mood-tracking interface.
- Maintain mood entries for historical review.
- Explore mood history and weekly mood visualizations.

### 💧 Hydration Monitoring
- Set daily hydration goals.
- Record water intake.
- Configure hydration reminder settings.
- Receive hydration reminders through Android background-processing and notification components.

### 📊 Wellness Dashboard and Visualization
- View wellness information in a centralized dashboard.
- Visualize mood trends over time.
- Monitor habit progress and hydration activity.
- Turn recorded wellness information into easier-to-understand visual summaries.

### 🏆 Achievements and Personalization
- Achievement-related data and supporting utility components.
- Onboarding screens for introducing the application.
- Login and registration interfaces.
- Settings and reminder configuration.

## 🛠️ Technology Stack

| Area | Technologies |
|---|---|
| Language | Kotlin |
| Platform | Android SDK |
| UI | XML layouts, View Binding, Material Components |
| Navigation | Android Navigation Component |
| Lists and interfaces | RecyclerView, custom adapters and dialogs |
| Charts | MPAndroidChart |
| Data organization | Data models and repository classes |
| Local preferences | SharedPreferences-related utilities |
| Background tasks | Android WorkManager |
| Notifications | Notification APIs and Broadcast Receivers |
| Build system | Gradle with Kotlin DSL |

## 🏗️ Project Structure

The project separates data models, repositories, UI screens, background tasks, and utility classes to keep responsibilities organized.

```text
BrightBeat/
├── app/
│   └── src/main/
│       ├── java/com/example/brightbeat/
│       │   ├── auth/
│       │   ├── onboarding/
│       │   ├── data/
│       │   ├── repository/
│       │   ├── ui/
│       │   │   ├── dashboard/
│       │   │   ├── habits/
│       │   │   ├── hydration/
│       │   │   ├── mood/
│       │   │   └── settings/
│       │   ├── receivers/
│       │   ├── work/
│       │   └── utils/
│       ├── res/
│       └── AndroidManifest.xml
├── gradle/
├── app/build.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── settings.gradle.kts
└── README.md
```

### Architecture Overview

- **Data models:** Represent concepts such as habits, mood entries, hydration records, and achievements.
- **Repositories:** Organize data-related operations for the main wellness features.
- **UI components:** Separate dashboard, habit, hydration, mood, and settings functionality.
- **Background components:** Handle hydration-related scheduled work and alarm events.
- **Utilities:** Support shared preferences, date/time operations, notifications, and achievement-related functionality.

## 📈 Data Science and Analytics Perspective

BrightBeat provides a practical foundation for exploring the relationship between software development and personal data analytics.

### 1. Behavioral Data Collection
The application includes data structures for tracking wellness activities, including:

- Habit records and completion activity.
- Mood entries over time.
- Hydration records and intake goals.
- Achievement-related information.

### 2. Trend Analysis and Visualization
Recorded data can help users review their routines and understand changes over time. The application's mood visualization features provide a starting point for exploring time-based wellness patterns.

### 3. Potential Analytical Questions
With suitable historical data, future analysis could investigate questions such as:

- How does habit completion change from week to week?
- Which days show higher or lower recorded mood levels?
- How consistently do users meet their hydration goals?
- Are there observable associations between habit completion and recorded mood?

These are analytical opportunities, not claims of established correlations or health outcomes.

### 4. Future Data Science Enhancements
Possible future extensions include:

- Statistical analysis of wellness trends.
- Correlation analysis between habits, mood, and hydration.
- Personalized recommendations based on historical activity.
- Mood trend forecasting using appropriately validated models.
- Habit consistency prediction.
- Exportable datasets and wellness reports.

Any predictive features would require suitable data, privacy safeguards, and evaluation before being used to provide recommendations.

## 🚀 Getting Started

### Prerequisites

- Android Studio.
- A compatible JDK and Android SDK configured through Android Studio.
- An Android emulator or a physical Android device.
- Git, if cloning the repository through the command line.

### Run the Application

**Option 1 — Android Studio**

1. Clone or download this repository.
2. Open Android Studio.
3. Select **Open** and choose the project root folder.
4. Allow Gradle to sync and resolve the project dependencies.
5. Create or select an Android emulator, or connect a physical Android device with USB debugging enabled.
6. Run the `app` configuration using the **Run** button.

**Option 2 — Windows terminal**

Open a terminal in the project root and run:

```powershell
.\gradlew.bat assembleDebug
```

If the build succeeds, the debug APK is normally generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

If Gradle sync or compilation fails, check that Android Studio has the required SDK components installed and that the configured JDK is compatible with the project's Gradle setup.

### Clone the Repository

Replace `YOUR-USERNAME` with the GitHub account that owns this repository.

```bash
git clone https://github.com/YOUR-USERNAME/BrightBeat.git
```

Then open the cloned `BrightBeat` folder in Android Studio.


## 🎓 Learning Outcomes

This project provided practical experience with:

- Kotlin-based Android development.
- Organizing application code using data models and repository classes.
- Building interactive mobile interfaces.
- Implementing local preferences and data-related components.
- Scheduling background work and handling notifications.
- Displaying recorded information through charts.
- Structuring a multi-feature mobile application.

## 🔮 Future Improvements

- Cloud synchronization and backup.
- Stronger account authentication and account management.
- Exporting wellness data for analysis.
- More detailed statistical dashboards.
- Personalized wellness recommendations.
- Machine learning experiments using suitable, privacy-conscious datasets.
- Automated testing and improved accessibility.

## 👨‍💻 Author

**Kavindu Lakshitha**  
BSc (Hons) in Information Technology — Data Science Undergraduate

Areas of interest: Data Sciece, Machine Learning, Artificial Intelligence, Mobile Application Development, and Data Visualization.


*Built with Kotlin and Android development tools.*
