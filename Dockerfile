FROM gradle:8.5 AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test


FROM amazoncorretto:17
WORKDIR /usr/app/
COPY --from=BUILD /usr/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c","java ${JAVA_OPTS} -jar /usr/app/app.jar"]