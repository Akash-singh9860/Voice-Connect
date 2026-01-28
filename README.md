# VoiceConnect: Android VoIP Calling App

**A full-stack VoIP calling application built with Android Jetpack Compose, Twilio Voice SDK, and Firebase Cloud Messaging (FCM), backed by a custom PHP token server for secure authentication and real-time routing.**

![Status](https://img.shields.io/badge/Status-Completed-success)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Backend](https://img.shields.io/badge/Backend-PHP-blue)

## 📱 Project Overview

VoiceConnect demonstrates a robust implementation of Voice-over-IP (VoIP) capabilities on Android. It handles the complexities of real-time audio, background signaling, and app-to-app calling using industry-standard tools.

### Key Features
* **Crystal Clear Voice Calls:** Powered by WebRTC and Twilio's Global Network.
* **App-to-App Calling:** call other users directly by their identity (e.g., "mobile", "web").
* **Background Wake-Up:** Uses High-Priority FCM Push Notifications to wake up the device for incoming calls, even when the app is killed.
* **Full-Screen Incoming UI:** Native-style incoming call overlay using Android's `fullScreenIntent`.
* **Modern Android Tech Stack:** Built entirely with Jetpack Compose, Coroutines, and Hilt.
* **Secure Authentication:** JWT-based access tokens generated via a custom PHP backend.

---

## 🛠 Tech Stack

### Android (Client)
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material3)
* **Dependency Injection:** Dagger Hilt
* **Asynchronous:** Kotlin Coroutines & Flow
* **VoIP SDK:** Twilio Voice Android SDK (`com.twilio:voice-android`)
* **Signaling:** Firebase Cloud Messaging (FCM V1)
* **Persistence:** Jetpack DataStore
* **Networking:** Retrofit / OkHttp

### Backend (Server)
* **Language:** PHP 7.4+
* **SDK:** Twilio SDK for PHP
* **Server:** Built-in PHP Server (for development) or Apache/Nginx
* **Architecture:** REST API for Token Generation & TwiML Call Routing

---

##  🏗 Architecture Flow
<img width="600" height="800" alt="VOIP implementation flow" src="https://github.com/user-attachments/assets/b470a69b-5a55-48a6-9d4b-041a635f6277" />

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

##🤝 Contributing
Contributions, issues, and feature requests are welcome!

Fork the Project

Create your Feature Branch (git checkout -b feature/AmazingFeature)

Commit your Changes (git commit -m 'Add some AmazingFeature')

Push to the Branch (git push origin feature/AmazingFeature)

Open a Pull Request

---

Developed with ❤️ using Jetpack Compose & Twilio
