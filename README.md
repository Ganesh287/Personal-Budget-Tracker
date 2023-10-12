# Personal Budget Tracker Application


## Description:

The Personal Budget Tracker is a finance management application that allows users to keep track of their daily income and expenses. It offers features such as setting budget limits, receiving notifications for low balances, and managing transactions. Users can also update their profiles, change passwords, and delete their accounts.

## Folder Structure:

_res_: Contains XML files for the frontend.

_java_: Contains Java files for the backend.

## Connecting to Firebase:

-Create a Firebase project.
-Obtain the necessary credentials (API key, database URL, etc.).
-Update the Firebase configuration in the code to connect to your project.

## Additional Setup:
Ensure that you have a working IDE for Java development.

## Usage Instructions:

### Backend Logic:
Review and modify the Java files in the java directory to suit your specific needs.

### Frontend Design:
Explore and customize the XML layout files in the res/layout directory to match your UI preferences.

## Dependencies
### Firebase Database

`implementation 'com.google.firebase:firebase-database:20.2.2'`

The Firebase Database dependency allows for real-time data synchronization and storage in the Firebase cloud. It is used for storing and managing data in the application.

### Lottie Animation Library

` implementation 'com.airbnb.android:lottie:6.0.1' `

Lottie is an animation library by Airbnb that renders After Effects animations in real-time, allowing for smooth animations in the application.

### AndroidX AppCompat

` implementation 'androidx.appcompat:appcompat:1.6.1' `

AndroidX AppCompat provides backward-compatible versions of many modern UI components, ensuring consistent behavior across different Android versions.

### Material Design Components

` implementation 'com.google.android.material:material:1.9.0' `

Material Design Components provides UI components following the Material Design guidelines, giving the app a modern and consistent look and feel.

### ConstraintLayout

''' implementation 'androidx.constraintlayout:constraintlayout:2.1.4' '''
ConstraintLayout is a flexible layout manager for Android that allows you to create complex layouts with a flat view hierarchy.

### Firebase Authentication

` implementation 'com.google.firebase:firebase-auth:22.0.0' `

Firebase Authentication provides a simple way to authenticate users in your app, allowing for secure login and user management.

### Firebase Firestore

```bash implementation 'com.google.firebase:firebase-firestore:24.6.1' bash```

Firebase Firestore is a NoSQL cloud database that allows for efficient storage and synchronization of app data. It is used for managing user data and transactions.

