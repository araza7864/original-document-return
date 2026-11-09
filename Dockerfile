FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY origdocreturn-domain/ ./
RUN mvn -B clean package

FROM tomcat:11-jre21-temurin
COPY --from=build /build/target/OrigDocReturn.war /usr/local/tomcat/webapps/OrigDocReturn.war
EXPOSE 8080
