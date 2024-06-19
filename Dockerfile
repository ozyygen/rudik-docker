# Use the base image
FROM ubuntu:latest

# Clone the repository and set the working directory
#RUN git clone https://github.com/stefano-ortona/rudik.git
#WORKDIR /rudik

# Update packages and install wget, Java, and JDK
RUN apt update && \
    apt install -y wget openjdk-8-jdk python3-pip

# Download and install Maven
RUN wget https://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz &&\
tar -zxf apache-maven-3.2.5-bin.tar.gz &&\
cp -R apache-maven-3.2.5 /usr/local &&\
ln -s /usr/local/apache-maven-3.2.5/bin/mvn /usr/bin/mvn



# Build the project
#RUN mvn -U clean install -DskipTests
