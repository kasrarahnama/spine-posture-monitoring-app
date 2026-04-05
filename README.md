# 🧠 SPINE – Real-Time Posture Monitoring System

<p align="center">
  <b>Production-style Android system for continuous posture monitoring using smartphone sensors</b><br>
  <i>Real-time • Sensor-driven • Always-on • Battery-efficient</i>
</p>

---

## 🚀 Live Overview

SPINE is a real-time posture monitoring system built for Android that continuously analyzes user posture using only built-in smartphone sensors.

Unlike wearable-based solutions, SPINE provides a **hardware-free, always-available posture tracking system** that runs in the background and delivers immediate corrective feedback.

---

## 🎯 Key Capabilities

- ⚡ **Real-time posture classification** (GOOD / BAD)
- 📡 **Continuous monitoring via foreground service**
- 📳 **Instant feedback system** (vibration + notification)
- ⚙️ **User-configurable sensitivity** (thresholds + hold time)
- 🧠 **False-positive reduction logic** (movement filtering)
- 🗂️ **Persistent posture history tracking (Room DB)**
- 📈 **Time-based posture behavior insights**
- 🔋 **Optimized for long-running background execution**

---

## 🏗️ System Architecture
[ Sensors Layer ]
Accelerometer + Gyroscope
↓
[ Sensor Fusion & Processing ]
Orientation Estimation (Pitch / Roll)
↓
[ Posture Evaluation Engine ]
Threshold + Temporal Filtering Logic
↓
[ Foreground Monitoring Service ]
Continuous Background Execution
↓
[ Feedback Layer ]
Vibration + Notification System
↓
[ Persistence Layer ]
Room Database (Posture Logs)
↓
[ UI Layer ]
Main Screen • Settings • History
---

## ⚙️ Tech Stack

- **Platform:** Android SDK  
- **Language:** Java / Kotlin  
- **Sensors:** Accelerometer & Gyroscope APIs  
- **Database:** Room Persistence Library  
- **Architecture:** Service-oriented real-time system  

---

## 🧪 Core Logic (Engineering Detail)

SPINE uses a **baseline-relative posture model**:

- User calibrates a neutral posture
- System continuously measures orientation deviation
- A posture is classified as BAD only if:
  - Deviation exceeds threshold  
  - AND persists beyond a configured duration  

👉 This avoids noise from natural micro-movements and reduces false alerts.

---

## ⚡ Performance Characteristics

- Sensor sampling rate: ~30–60 Hz  
- Detection latency: < 200 ms  
- Background stability: Foreground service (non-killable)  
- Battery usage: optimized for long sessions  

---

## 🧠 Engineering Challenges & Solutions

### ❌ Problem: False alerts due to natural movements  
✔️ Solution: Temporal filtering (hold-time threshold)

### ❌ Problem: App killed in background  
✔️ Solution: Android Foreground Service implementation

### ❌ Problem: Sensor noise  
✔️ Solution: Continuous sampling + threshold-based smoothing

### ❌ Problem: User variability  
✔️ Solution: Calibration-based baseline system

---

## 📂 Project Structure
spine/
├── app/
│   ├── src/
│   ├── java/
│   ├── res/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
---

## 📸 Demo & Screenshots

> 🔴 (Highly recommended to add)
> /screenshots/main.png
/screenshots/settings.png
/screenshots/history.png
> 👉 Adding visuals significantly improves project impact.

---

## 🔮 Future Roadmap

- 🤖 ML-based posture classification (beyond threshold logic)
- ☁️ Cloud sync (Firebase / backend integration)
- 📊 Advanced analytics dashboard
- 🧑‍⚕️ Health recommendations system
- ⌚ Wearable device integration

---

## 🧠 Why This Project Matters

Modern device usage leads to chronic posture issues.  
SPINE demonstrates how **real-time systems + mobile sensors** can be leveraged to build practical health-focused applications without additional hardware.

---

## 👤 Author

**Kasra Rahnama Fard**  
📍 Toronto, Canada  
🔗 https://github.com/kasrarahnama  

---

## ⭐ Support

If you found this project valuable, consider giving it a ⭐

---
