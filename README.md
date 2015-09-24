# meow

## Build the application

You must have openjdk8, git, and sbt installed.

```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-get update
sudo apt-get install openjdk-8-jdk git sbt
```

Then download the code and compile

```
git clone https://github.com/pnmougel/meow.git
cd meow
sbt run
```

## Features

* Easily create folders for the gnome menu

* Create new desktop entries by dragging an application or an url to the application
