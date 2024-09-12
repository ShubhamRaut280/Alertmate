# Alertmate

## Overview
The **Alertmate** is designed to assist users in critical situations by connecting them with family members in real-time. The app provides features like live location sharing, crash detection, and emergency notifications via SMS. It allows users to instantly notify their family in case of emergencies, either automatically or with a simple action like pressing the power button 4 or more number of times.

## Features
- **Add Family Members**: Add family members to your emergency contact list.
- **Live Location Tracking**: View real-time locations of all family members.
- **Crash Detection**: Automatically detect vehicle crashes using accelerometer data and notify family members with the user’s location.
- **Emergency Alerts**: Notify family members via SMS with the user’s location by:
  - Crash detection
  - Pressing the power button four or more times in case of emergencies.
- **SMS Notifications**: Send emergency SMS notifications automatically with or without user interaction.

## Technologies Used
- **Kotlin**: Primary language for Android development.
- **Java**: Additional functionality in the Android environment.
- **Retrofit**: Network operations for making API calls.
- **Firebase Realtime Database**: Real-time location tracking of family members.
- **Firebase Firestore**: Managing family member data and user details.

## Getting Started
To run this project, follow the steps below:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/emergency-help-app.git
   cd emergency-help-app
2.  **Configure Firebase**

 - **Create a Firebase project** and add the Android app.
 -  **Download** `google-services.json` from Firebase and place it in the `app/` directory.
 -  **Set up** Firebase Realtime Database and Firestore.

2.  **Build and Run**

  - Open the project in **Android Studio**.
  - Sync the project with **Gradle**.
  - Build and run the app on your device.

## Permissions
This app requires the following permissions:
- **Location**: To share the user's live location with family members.
- **SMS**: To send emergency SMS notifications.
- **Accelerometer Access**: To detect vehicle crashes.


## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any bug fixes or new features.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
   
   
