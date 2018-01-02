# scala-minesweeper-wui

This is a web project with play framework. The application is a user interface
for minesweeper game. It connects to a running minesweeper game with actor configuration.

## Running

The configuration for the connection to a running minesweeper core actor system
is located in ```conf/application.conf``` under

```
akka {
  ...
  minesweeper {
    controllerActor = "akka.tcp://minesweeper@127.0.0.1:5555/user/controller"
    publisherActor = "akka.tcp://minesweeper@127.0.0.1:5555/user/publisher"
  }
}
```

Run this using [sbt](http://www.scala-sbt.org/).  If you downloaded this project 
from http://www.playframework.com/download then you'll find a prepackaged version of sbt in the project directory:

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.