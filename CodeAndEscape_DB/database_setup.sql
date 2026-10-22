-- Code and Escape - MySQL + JDBC database setup
CREATE DATABASE IF NOT EXISTS codeandescape;
USE codeandescape;

CREATE TABLE IF NOT EXISTS players (
  player_id VARCHAR(36) PRIMARY KEY,
  player_name VARCHAR(100) NOT NULL,
  discord_id VARCHAR(100) NOT NULL UNIQUE,
  created_at DATETIME NOT NULL,
  last_login DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS game_sessions (
  session_id VARCHAR(36) PRIMARY KEY,
  player_id VARCHAR(36) NULL,
  started_at DATETIME NOT NULL,
  ended_at DATETIME NULL,
  status VARCHAR(20) NOT NULL,
  FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS game_state (
  session_id VARCHAR(36) PRIMARY KEY,
  saved_at DATETIME NOT NULL,
  save_reason VARCHAR(100),
  current_room VARCHAR(50) NOT NULL,
  time_remaining INT NOT NULL,
  game_over BOOLEAN NOT NULL,
  game_won BOOLEAN NOT NULL,
  FOREIGN KEY (session_id) REFERENCES game_sessions(session_id)
);

CREATE TABLE IF NOT EXISTS puzzle_progress (
  session_id VARCHAR(36) NOT NULL,
  puzzle_id VARCHAR(50) NOT NULL,
  solved BOOLEAN NOT NULL,
  PRIMARY KEY (session_id,puzzle_id),
  FOREIGN KEY (session_id) REFERENCES game_sessions(session_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS inventory (
  session_id VARCHAR(36) NOT NULL,
  item_id VARCHAR(100) NOT NULL,
  item_name VARCHAR(200),
  description TEXT,
  icon_path VARCHAR(500),
  detailed_clue TEXT,
  PRIMARY KEY (session_id,item_id),
  FOREIGN KEY (session_id) REFERENCES game_sessions(session_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS game_events (
  event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(36),
  event_time DATETIME NOT NULL,
  event_type VARCHAR(100) NOT NULL,
  room VARCHAR(50),
  time_remaining INT,
  solved_puzzles INT,
  FOREIGN KEY (session_id) REFERENCES game_sessions(session_id) ON DELETE SET NULL
);
