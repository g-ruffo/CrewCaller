# CrewCaller
Built with Kotlin, this Android application allows users to track their work schedule and earnings while providing local weather data using a RESTful API and the device’s location. Users can schedule upcoming and past workdays with their start and wrap times and the application will automatically provide a summary of the total hours worked and earnings for each production. 

You can choose to view:
- Next scheduled work day
- A list of all recorded work days and details
- A list of productions and their summaries 
- A list of saved contacts
- A list of customized pay rates
- A calendar view with days worked highlighted

# Screen Shots
<img src="https://user-images.githubusercontent.com/93240608/193431965-f71ca4ea-0d75-40ae-88e2-37ab8933c8fb.png" width="100%">

<img src="https://user-images.githubusercontent.com/93240608/191449677-7824a5b0-d5c1-4f4b-aba3-b26a72a0d71e.gif" width="40%">


# Description 
-	Integrates Firebase Authentication and Cloud Firestore to allow users to login and access their data from any device.
-	Uses Room database to store saved entries locally on the device.
-	Using implicit intents, users can quickly and easily email, call and navigate to saved locations using the application of their choice.
-	Network calls are run on a background using Kotlin Coroutines to avoid stalling the UI.
-	Built using the MVVM design pattern.
-	Employed JUnit and Espresso framework to perform both unit and UI tests.

## Frameworks
- Retrofit
- JUnit
- Espresso
- Koin
- Firebase


# Permissions
- Access Fine Location
- Access Course Location
- Access Background Location
- Access Network State
- Internet
- Wake Lock
- Call Phone

# API
- OpenWeatherMap API

# Required OS
Android 8.0 and up.

# Developed By
Grayson Ruffo

      Copyright 2022 Grayson Ruffo
