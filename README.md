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
![crewcaller_app_render copy](https://user-images.githubusercontent.com/93240608/193431965-f71ca4ea-0d75-40ae-88e2-37ab8933c8fb.png)


<img src="https://user-images.githubusercontent.com/93240608/193431965-f71ca4ea-0d75-40ae-88e2-37ab8933c8fb.png" width="40%">

<img src="https://user-images.githubusercontent.com/93240608/191449677-7824a5b0-d5c1-4f4b-aba3-b26a72a0d71e.gif" width="40%">


# Scheduled Work Fragment
This fragment displays all past and future work days organized by date, with entries without a wrap time set displayed in white and completed entries greyed out. When an item is clicked, the user will be directed to a details screen that displays all saved information for the given entry. 

<table width="100%">
  <tr>
  <td width="40%"><img src="https://user-images.githubusercontent.com/93240608/191453794-ff1f57b4-a5b2-435b-8e42-07b44ea62d07.jpg">
</td>
  <td width="40%"><img src="https://user-images.githubusercontent.com/93240608/191455757-c0dd2e66-e028-481c-a6ae-afe2c81c9d75.jpg">
</td>
  </tr>
</table>


# Productions Fragment

Here you can view all saved productions with their corresponding phone numbers. When an item is clicked, the user is navigated to the detail fragment where a summary of the total number of days worked is displayed along with the total combined earnings for the given production. 

<table width="100%">
  <tr>
  <td width="40%"><img src="https://user-images.githubusercontent.com/93240608/191459760-09d86f4a-39e7-45a0-95ac-65432770e007.jpg">
</td>
  <td width="40%"><img src="https://user-images.githubusercontent.com/93240608/191459752-d30f99cb-d9b3-473d-b1c7-1ec081e7889a.jpg">
</td>
  </tr>
</table>


# Pay Rate Fragment

This fragment allows users to add or edit different pay rates depending on the production they are working for.

<table width="100%">
  <tr>
  <td width="40%"><img src="https://user-images.githubusercontent.com/93240608/191464212-5532599d-dc99-4e45-bdba-102892f35171.jpg">
</td>
  <td width="40%"><img src="https://user-images.githubusercontent.com/93240608/191464268-4ba86d31-09f3-4498-afc0-6e52b3e596ee.jpg">
</td>
  </tr>
</table>

# Contacts Fragment

The contacts fragment allows for quick access to personal information which can be sorted by production. 

<table width="100%">
  <tr>
  <td width="33.33%"><img src="https://user-images.githubusercontent.com/93240608/191667315-54d3346e-19c1-43db-ad83-66222354f3a7.jpg">
</td>
  <td width="33.33%"><img src="https://user-images.githubusercontent.com/93240608/191667425-73842dc8-8460-4f07-90bc-5a69f83698e9.jpg">
</td>
      <td width="33.33%"><img src="https://user-images.githubusercontent.com/93240608/191667481-41167887-b2c2-4d73-b9d1-483dbc54c0b4.jpg">
</td>
  </tr>
</table>

# Calendar Fragment
The calendar fragment provides a quick overview of all day's work which is displayed by the month, with the days worked highlighted in green.

<img src="https://user-images.githubusercontent.com/93240608/191667469-78bc6c25-fa18-4867-ba4c-9940cf2b943f.jpg" width="40%">


# Preferences Fragment
Utilizing Firebase Authentication and Cloud Firestore, users have the ability to access their saved data on multiple devices by using either an email and password or their Google account.

<img src="https://user-images.githubusercontent.com/93240608/191668376-f2b57fd0-6b62-48db-a80e-473f7626e228.jpg" width="40%">

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

# Developed By
Grayson Ruffo

      Copyright 2022 Grayson Ruffo
