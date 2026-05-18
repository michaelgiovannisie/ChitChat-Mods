# ChitChat

ChitChat is a simple group chat application built with Java socket programming and Swing GUI.

## Features

- Multiple clients can connect to one server
- Messages are broadcast to all connected users
- Server logs connections and messages to `chatlog.txt`
- Users can type `/list` to see currently connected users

## How to Compile

```bash
javac SocketServer.java SocketClient.java