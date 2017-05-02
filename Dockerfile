FROM maven:3.5-jdk-8-onbuild

ENTRYPOINT java -jar /usr/src/app/target/timezone-bot-1.0-SNAPSHOT-jar-with-dependencies.jar