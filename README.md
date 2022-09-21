<img src="https://user-images.githubusercontent.com/93240608/191438845-b65f4fd1-4585-4ef9-8548-1d530ba0caa5.jpg" width="100%" height="50%">


# CrewCaller
This Android application helps crew members working in the film industry keep track of their day-to-day lives. Users can schedule upcoming and past workdays with their start and wraps and the application will automatically provide a summary of the total hours worked and earnings for each production. 

You can choose to view:
- Next scheduled work day
- A list of all recorded work days and details
- A list of productions and their summaries 
- A list of saved contacts
- A list of customized pay rates
- A calendar view with days worked highlighted

# Upcoming Work Fragment
This fragment uses the devices current date and time to retrive the next upcoming work day from the database and displays its details on the screen. Using the start time saved for entry a TimerTask is created and a counter displays elapsed time worked and earnings. When the Wrap Time button is clicked the user is presented with a dialoge to select the correct end time for the day. Once confirmed the total hours and earnings are finalized and added to the production summary.
Using the devices location, weather data is retrieved using the OpenWeatherMap API and displayed at the bottom of the screen.

<img src="https://user-images.githubusercontent.com/93240608/191449677-7824a5b0-d5c1-4f4b-aba3-b26a72a0d71e.gif" width="50%" height="50%">


# Scheduled Work Fragment
This fragment displays all past and future work days organized by date, with entries without a wrap time set displayed in white and completed entries greyed out. When an item is clicked the user will be directed to a details screen that displays all saved information for the given entry. 

Scheduled Work List             |  Solarized Ocean
:-------------------------:|:-------------------------:
<img src="https://user-images.githubusercontent.com/93240608/191453177-ce9f9ebd-7e36-4bde-946e-dbcb890050ce.jpg" width="50%" height="50%">
  |  <img src="https://user-images.githubusercontent.com/93240608/191453378-956b0388-e0ad-4cba-bad1-562ef0f7b870.jpg" width="50%" height="50%">


# Bookmarked Fragment
This fragment loads a list of bookmarked articles which is stored within SharedPreferences. Users can remove individual articles by clicking on the trash icon.

<img src="./images/bookmarkscreen.jpg" width="50%" height="50%">


# Preferences Fragment
This fragment allows the user to select location and language settings for the articles populated within the Covid-19 News Fragment and can also choose to limit the data shown in the Covid-19 Cases Fragment to a specific province. From this screen you are also able to restore the default preference settings and clear all the saved articles from the Bookmarked Fragment.

<img src="./images/preferencescreen.jpg" width="50%" height="50%">


# API
- OpenWeatherMap API

# Developed By
Grayson Ruffo

      Copyright 2022 Grayson Ruffo
