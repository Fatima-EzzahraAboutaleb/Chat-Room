# Chat Room

A desktop chat application built with Java Swing and WebSockets. Users can create an account, sign in, chat in real time, see who is online, and load recent chat history. MySQL stores accounts and messages.

## Features

- Login and account registration
- Real-time public chat over WebSockets
- Online user list and join/leave notifications
- Recent message history
- Swing desktop interface
- MySQL persistence

## Requirements

- JDK 8 or later (including `java` and `javac` on your `PATH`)
- MySQL Server
- The dependency JARs included in `lib/`

## Database setup

1. Start MySQL and create the application database:

   ```sql
   CREATE DATABASE chat_room_db;
   ```

2. Select `chat_room_db` in MySQL Workbench (or your preferred MySQL client) and run [`sql/create_tables_v2.sql`](sql/create_tables_v2.sql).

   The script creates the account, chat, message, and membership tables, and seeds the default chat used by the application.

3. The current database connection settings are in `src/server/DatabaseManager.java`:

   ```text
   URL:      jdbc:mysql://localhost:3306/chat_room_db
   User:     root
   Password: (empty)
   ```

   Update these values in `DatabaseManager` if your local MySQL credentials differ. The database verifier in `src/util/DatabaseVerifier.java` uses the same connection defaults.

## Build and run (Windows PowerShell)

Run these commands from the repository root.

Compile the Java sources:

```powershell
New-Item -ItemType Directory -Force .\bin | Out-Null
$javaFiles = Get-ChildItem .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp "lib/*" -d bin $javaFiles
```

Start the WebSocket server in one terminal:

```powershell
java -cp "bin;lib/*" server.ChatServer
```

Start the desktop client in another terminal:

```powershell
java -cp "bin;lib/*" client.Main
```

The server listens on port `9001`; clients connect to `ws://localhost:9001`. Start the server before opening the client. To use multiple clients on the same machine, launch `client.Main` more than once.

## Project layout

```text
src/
  client/   Swing interface and WebSocket client
  server/   WebSocket server and MySQL access
  shared/   Messages exchanged between client and server
  util/     Database and password utilities
sql/        MySQL schema
lib/        Java dependencies
```

## Dependencies

The required JARs are included in `lib/`:

- Java-WebSocket 1.5.4
- Gson 2.8.8
- MySQL Connector/J 8.0.33
- SLF4J 2.0.9

## Notes

- The application currently connects to a single local WebSocket server and stores messages in the default chat.
- The `package.json` in the repository is not used to build or run this Java application.
- This project is intended for local development and learning, not production deployment.
