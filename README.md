# ShotSync Scorecard

Created by:
- Dilan Pais
- Gavin Marshall
- Ivory Sarpong
- Matthew Sotelo
- Michael Opoku
- Nathan Ketterlinus

## Installation Guide
1. Download and Install Android Studio for you corresponding device at (https://developer.android.com/studio).

2. Once you finish the installation process for Android Studio, unzip the project folder on your computer.

3. At the top of the window, click the **Open** menu option and select the unzipped project folder.

4. Once you open the project folder using Android Studio, your window should look the same as below. Navigate to the right-hand panel and click on **Device Manager**.  
   <img width="1179" alt="image" src="https://github.com/user-attachments/assets/e36f914a-ba19-468c-965f-9922471d8762" />

5. Press the **+** icon and then select **Create Virtual Device**.  
   <img width="1179" alt="image" src="https://github.com/user-attachments/assets/1f055681-09e4-41d5-a4bc-0c51f7896ad0" />

6. Select a model of phone you would like to emulate. For this example, I choose **Medium Phone**, and then select **Next** and then **Finish** on the next window.  
   <img width="1012" alt="image" src="https://github.com/user-attachments/assets/95f8aa5c-ebc4-43e1-8eba-b5974f0ef4d2" />

7. Back at the main screen, click the **Run** button at the top to emulate our application.  
   <img width="1179" alt="image" src="https://github.com/user-attachments/assets/8000f46b-b5a4-492b-99a0-d98009e59629" />

---  
## Basic Usage

#### Navbar

The navbar across the bottom has 4 buttons that take the user to all major functionality of the app. From left to right:
1. Home
    - Shows games currently in the Database.
    - Click on game to see more details
    - Hold to delete
2. Add New
    1. Scan
        - Import game from physical Scorecard.
    2. Manual
        - Import game by hand.
            - Use "-" in date field
3. Extras
    1. Achievements
        - Coming soon!
    2.  Acknowledgements
        - See what everyone worked on!
4. Settings
    - Toggle Theme

#### Scanning a Game:

The repository contains a demo scorecard, "exampleScorecard.png" in the root folder of the project. You can either put this on your physical device, or store it in the emulated device's memory, as explained below. Once in app, you can select "Pick from Gallery", to import it into your database!

#### Adding "exampleScorecard.png" to emulator:

click Device Explorer on the far left > right click "/storage/emulated/0/DCIM" > Upload > select "exampleScorecard.png" or other desired file from explorer.

If the image is not appearing in app, common troubleshooting tips include:
- try restarting the emulator
- try uploading file to a different location, such as "/storage/emulated/0/Download"