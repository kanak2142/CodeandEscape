-- Code and Escape - JDBC/MySQL demonstration queries
USE codeandescape;

-- 1. Show all players and their unique IDs
SELECT player_id, player_name, discord_id, created_at, last_login
FROM players;

-- 2. Show sessions linked to players
SELECT gs.session_id, p.player_name, p.discord_id, p.player_id,
       gs.started_at, gs.ended_at, gs.status
FROM game_sessions gs
LEFT JOIN players p ON p.player_id = gs.player_id
ORDER BY gs.started_at DESC;

-- 3. Show current game state
SELECT * FROM game_state;

-- 4. Show solved/unsolved puzzles
SELECT * FROM puzzle_progress;

-- 5. Show collected inventory
SELECT * FROM inventory;

-- 6. Show latest gameplay events
SELECT * FROM game_events
ORDER BY event_id DESC
LIMIT 20;

-- 7. Best combined demonstration: player + session + state
SELECT p.player_id, p.player_name, p.discord_id,
       gs.session_id, gs.status, st.current_room,
       st.time_remaining, st.game_won, st.game_over
FROM players p
JOIN game_sessions gs ON gs.player_id = p.player_id
LEFT JOIN game_state st ON st.session_id = gs.session_id
ORDER BY gs.started_at DESC;
