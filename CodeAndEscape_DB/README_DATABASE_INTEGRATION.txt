CODE AND ESCAPE - DATABASE INTEGRATION
======================================

WHAT WAS ADDED
---------------
A lightweight embedded local database layer was integrated without changing the
existing game puzzles, UI flow, answers, graphics, sounds, or backend validation.

The database file is created automatically at:
    codeandescape.db

No MySQL, XAMPP, SQLite installation, internet connection, or third-party JDBC
JAR is required.

DATABASE PURPOSES
-----------------
The database keeps a persistent snapshot and session/event information for:

1. Current room
2. Remaining timer
3. Solved/unsolved puzzle status
4. Inventory items
5. Game-over state
6. Game-won state
7. Game sessions
8. Important game events such as puzzle completion and inventory changes
9. Manual save/load through the existing SaveManager API

This makes the database useful for save/resume functionality, player/session
history, debugging, progress tracking, and future score/leaderboard features.

IMPORTANT
---------
The normal game still starts exactly as before. The database is an automatic
persistence layer and does not require a new screen or database server.

RUN ON WINDOWS
--------------
1. Install JDK 17 or newer.
2. Extract this ZIP completely.
3. Double-click:
       RUN_CODE_AND_ESCAPE.bat

OR run from Command Prompt/PowerShell:

    cd CodeAndEscape_FINAL_DB
    RUN_CODE_AND_ESCAPE.bat

MANUAL COMMANDS
---------------
From the project root:

    mkdir out
    dir /s /b src\com\escape\*.java > sources.txt
    javac -encoding UTF-8 -d out @sources.txt
    del sources.txt
    xcopy /E /I /Y src\assets out\assets
    java -cp out com.escape.MainApp

INTELLIJ IDEA
-------------
1. Open the extracted project.
2. Make sure a JDK 17+ is selected.
3. Mark the 'src' folder as Sources Root if IntelliJ does not detect it.
4. Run:
       com.escape.MainApp
5. On the first run, codeandescape.db will appear in the working directory.

SAVE MANAGER
------------
The original SaveManager class is still present with the same public methods:

    saveGame(state)
    loadGame()
    saveExists()
    deleteSave()

These methods now use codeandescape.db instead of savegame.dat.

TESTING
-------
The original IntegrationTest is still present. After compiling, it can be run
with:

    java -cp out com.escape.IntegrationTest

Expected final line:
    ALL 6 FRONTEND -> BACKEND TESTS PASSED

DATABASE FILE
-------------
Do not manually edit codeandescape.db. If it becomes corrupted, simply delete
it and start the application again; a fresh database will be created.
