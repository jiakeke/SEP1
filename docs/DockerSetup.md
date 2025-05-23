# Docker Setup

## Use XQuartz on Mac to run JavaFX GUI in docker

1. Install XQuartz via brew $ brew install --cask xquartz 
2. Logout and login of your Mac to activate XQuartz as default X11 server 
3. Start XQuartz $ open -a XQuartz 
4. Go to Security Settings and ensure that "Allow connections from network clients" is on
5. Restart your Mac and start XQuartz again
    ```bash
	$ open -a XQuartz 
    ```
6. Check if XQuartz is setup and running correctly
	$ ps aux | grep Xquartz 
7. Ensure that XQuartz is running similar to this: /opt/X11/bin/Xquartz :0 -listen tcp
	:0 means the display is running on display port 0. Important is that its not saying –nolisten tcp which would block any X11 	forwarding to the X11 display. 
8. Allow X11 forwarding via xhost
    ```bash
	$ xhost +
    ```
	This allows any client to connect. If you have security concerns you can append an IP address for a whitelist mechanism.
	Alternatively, if you want to limit X11 forwarding to local containers, you can limit clients to localhost only via
    ```bash
	$ xhost +localhost
    ```
	Be ware: You will always have to run xhost + after a restart of X11 as this is not a persistent setting.

## Dockerfile(For English version)
```Dockerfile
FROM arm64v8/ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    libgtk-3-0 \
    libwebkit2gtk-4.0-37 \
    libgl1-mesa-glx \
    libgl1-mesa-dri \
    libx11-dev \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libx11-6 \
    mesa-utils \
    maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . /app

COPY javafx-sdk-23.0.2 /opt/javafx/javafx-sdk-23.0.2

RUN mvn clean package -DskipTests

CMD ["java", "--module-path", "/opt/javafx/javafx-sdk-23.0.2/lib", "--add-modules", "javafx.controls,javafx.fxml", "-Djava.library.path=/opt/javafx/javafx-sdk-23.0.2/lib", "-Dprism.order=sw", "-jar", "target/gradebook.jar"]
```

## Dockerfile(For CJK version)
```Dockerfile
FROM arm64v8/ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    libgtk-3-0 \
    libwebkit2gtk-4.0-37 \
    libgl1-mesa-glx \
    libgl1-mesa-dri \
    libx11-dev \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libx11-6 \
    mesa-utils \
    locales \
    fonts-noto-cjk \
    fonts-noto \
    fonts-ipafont-mincho fonts-ipafont-gothic \
    maven \
    && rm -rf /var/lib/apt/lists/*

RUN locale-gen zh_CN.UTF-8 ja_JP.UTF-8 && \
    update-locale LANG=zh_CN.UTF-8 LC_ALL=zh_CN.UTF-8
ENV LANG=zh_CN.UTF-8
ENV LC_ALL=zh_CN.UTF-8

WORKDIR /app
COPY . /app
COPY javafx-sdk-23.0.2 /opt/javafx/javafx-sdk-23.0.2

RUN mvn clean package -DskipTests

CMD ["java", "--module-path", "/opt/javafx/javafx-sdk-23.0.2/lib", "--add-modules", "javafx.controls,javafx.fxml", "-Djava.library.path=/opt/javafx/javafx-sdk-23.0.2/lib", "-Dprism.order=sw", "-jar", "target/gradebook.jar"]
```

## Build Docker Image
```bash
docker build -t gradebook .
```

## Run Docker Container
```bash
docker run -e DISPLAY=host.docker.internal:0 -v /tmp/.X11-unix:/tmp/.X11-unix gradebook
```

## Database setup
* Use host.docker.internal instead of localhost in your database connection string
```java
db_link = "jdbc:postgresql://host.docker.internal:5432/mydatabase"; // For PostgreSQL
OR
db_link = "jdbc:mysql://host.docker.internal:3306/mydatabase"; // For MySQL
```
