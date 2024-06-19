# rudik/Dockerfile
FROM maven:latest

RUN apt update && apt install -y wget python3-pip git build-essential cmake

WORKDIR /app

RUN git clone https://github.com/ppapotti/Rudik.git

# Copy the local pom.xml file to replace the existing one in the cloned repository

WORKDIR /app/Rudik

COPY pom.xml /app/Rudik/pom.xml

COPY Configuration.xml /app/Rudik/src/main/config/Configuration.xml

COPY App.java /app/Rudik/src/main/java/asu/edu/rule_miner/rudik/App.java


RUN mvn -U clean install -DskipTests

# rudik/Dockerfile
CMD ["java", "-cp", "target/rule_miner-0.0.1-SNAPSHOT.jar", "asu.edu.rule_miner.rudik.App"]
