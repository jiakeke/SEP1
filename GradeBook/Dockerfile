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
