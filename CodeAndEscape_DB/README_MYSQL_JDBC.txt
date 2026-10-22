CODE AND ESCAPE - MYSQL + JDBC + PLAYER LOGIN
================================================

Java: JDK 26.0.2.1
Database: MySQL 8.0+
Connectivity: MySQL Connector/J 9.7.0 (JDBC)
Game timer: 10 minutes

1. INSTALL MYSQL
-----------------
Install MySQL Server and remember the root password. MySQL Workbench is optional but recommended for the practical demonstration.

2. SET THE MYSQL PASSWORD
--------------------------
Open db.properties and set:
    db.user=root
    db.password=YOUR_MYSQL_PASSWORD

Do not share your password.

3. SETUP
---------
Run SETUP_MYSQL.bat once.
It downloads the MySQL Connector/J driver if needed and compiles the project.

4. RUN
-------
Run RUN_CODE_AND_ESCAPE.bat.
The first screen is now a Detective Login screen.
Enter:
- Name
- Discord ID

The application generates a UUID-based unique Player ID and stores the player in the MySQL players table.
If the same Discord ID is entered later, the existing Player ID is reused and last_login is updated.

5. DATABASE STRUCTURE
----------------------
players           -> person identity: unique Player ID, name, Discord ID, timestamps
game_sessions     -> each play session, linked to player_id
game_state        -> current room, remaining timer, win/loss state
puzzle_progress   -> solved/unsolved puzzles for the session
inventory         -> items collected during the session
game_events       -> gameplay event history

Relationship:
players
   |
   +--> game_sessions
             |
             +--> game_state
             +--> puzzle_progress
             +--> inventory
             +--> game_events

6. WHAT THE LOGIN FEATURE DOES
------------------------------
The login screen does not replace the game logic. It runs before the existing game begins.
The player enters their name and Discord ID.
A unique Player ID is generated using UUID.randomUUID().toString().
The Player ID is stored in MySQL and becomes the identity key for future game sessions.
The session table stores player_id as a foreign key.

7. BEST TEACHER DEMONSTRATION
-----------------------------
A. Start MySQL Server.
B. Run the game.
C. Enter your Name and Discord ID.
D. Show the generated Unique Player ID.
E. Click START CASE.
F. Open MySQL Workbench.
G. Run:
      USE codeandescape;
      SHOW TABLES;
H. Run:
      SELECT player_id, player_name, discord_id FROM players;
I. Run:
      SELECT * FROM game_sessions;
J. Solve one puzzle in the game.
K. Run:
      SELECT * FROM puzzle_progress;
L. Run:
      SELECT * FROM game_state;
M. Run:
      SELECT * FROM game_events ORDER BY event_id DESC LIMIT 20;
N. If an item is collected:
      SELECT * FROM inventory;

8. BEST ONE-SHOT JOIN QUERY
---------------------------
SELECT p.player_id, p.player_name, p.discord_id,
       gs.session_id, gs.status, st.current_room,
       st.time_remaining, st.game_won, st.game_over
FROM players p
JOIN game_sessions gs ON gs.player_id = p.player_id
LEFT JOIN game_state st ON st.session_id = gs.session_id
ORDER BY gs.started_at DESC;

This proves that the person who logged in is linked to the game session and its current game state.

9. JDBC COMPONENTS TO EXPLAIN
-----------------------------
Driver              -> MySQL JDBC driver
DriverManager       -> establishes the connection
Connection          -> active database connection
PreparedStatement   -> parameterized INSERT/SELECT/UPDATE/DELETE
ResultSet            -> reads SELECT results

10. IMPORTANT
-------------
The database stores persistent gameplay/player data, not the Java source code, images or sounds.
The existing puzzles and Swing game remain the same. The new login screen only identifies the player before the game session starts.
