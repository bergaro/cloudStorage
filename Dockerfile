FROM adoptopenjdk/openjdk11
EXPOSE 24001
ADD build/libs/cloudStorage.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]