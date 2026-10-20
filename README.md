# VolumeFind 📐

**VolumeFind** is a modern, intuitive, and feature-rich Android application designed for calculating volumes of various 3D geometric shapes with instant unit conversions, calculation history, and detailed step-by-step formula breakdowns.

Built with **Kotlin**, **Jetpack Compose**, and **Material 3**, VolumeFind provides a clean, responsive user experience tailored for students, engineers, architects, and anyone who needs quick and precise 3D volume calculations.

---

## 🌟 Features

- 🔷 **Supported 3D Shapes**:
  - **Cube** ($V = s^3$)
  - **Rectangular Prism** ($V = l \times w \times h$)
  - **Cylinder** ($V = \pi r^2 h$)
  - **Sphere** ($V = \frac{4}{3}\pi r^3$)
  - **Cone** ($V = \frac{1}{3}\pi r^2 h$)
  - **Square Pyramid** ($V = \frac{1}{3}s^2 h$)
  - **Triangular Prism** ($V = \frac{1}{2}b \cdot h \cdot L$)
  - **Hemisphere** ($V = \frac{2}{3}\pi r^3$)

- 🔄 **Comprehensive Unit Conversions**:
  - **Input Length Units**: Meters ($m$), Centimeters ($cm$), Millimeters ($mm$), Inches ($in$), Feet ($ft$), Yards ($yd$).
  - **Output Volume Units**: Cubic Meters ($m^3$), Cubic Centimeters ($cm^3$), Cubic Millimeters ($mm^3$), Cubic Inches ($in^3$), Cubic Feet ($ft^3$), Cubic Yards ($yd^3$), Liters ($L$), Milliliters ($mL$), Gallons ($gal$), Fluid Ounces ($fl\ oz$).

- 📊 **Step-by-Step Calculation Breakdown**:
  - View explicit formulas, intermediate variable substitutions, and unit conversion steps.
  - Dynamic shape diagrams providing visual context for dimensions.

- 📜 **History & Favorites**:
  - Auto-save calculation history with timestamp and shape metadata.
  - Search, filter, favorite, or reload previous calculations directly into the input fields.

- ⚙️ **Settings & Customization**:
  - Adjustable decimal precision (0 to 6 decimal places).
  - Custom default units and preset values.
  - App theme support: Light Mode, Dark Mode, or System Default.
  - Interactive haptic feedback toggle.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Domain Architecture
- **State Management**: `StateFlow` & `State`
- **Navigation**: Navigation Compose
- **Asynchronous Execution**: Kotlin Coroutines & Flow
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 37 (Android 15+)

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio)
- JDK 11 or higher
- Android SDK 37

### Building & Running

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/VolumeFind.git
   cd VolumeFind
   ```

2. **Open in Android Studio**:
   Open Android Studio and select **Open**, then choose the `VolumeFind` project directory.

3. **Build the Project**:
   Run via Gradle wrapper:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 📂 Project Structure

```
app/src/main/java/com/example/volumefind/
├── data/              # Repositories & Data Persistence (Settings & History)
├── domain/            # Domain Logic (Calculators, Converters, Formatters, Validators)
│   ├── calculator/    # Volume and Unit Calculation Engines
│   ├── model/         # Core Data Models (VolumeShape, Units, Calculations)
│   └── validator/     # Input Validation & Parsing
├── ui/                # UI Layer (Jetpack Compose Screens & Components)
│   ├── about/         # About Screen
│   ├── components/    # Reusable Compose UI Components & Shape Diagrams
│   ├── history/       # History Screen
│   ├── home/          # Home Screen & VolumeFindViewModel
│   ├── navigation/    # Navigation Host & Routes
│   ├── settings/      # Settings Screen
│   └── theme/         # Material 3 Color Schemes & Typography
└── MainActivity.kt    # Entry Point
```

---

## 📄 License

```
Copyright 2025 VolumeFind
```
