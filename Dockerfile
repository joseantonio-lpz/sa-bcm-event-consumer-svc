# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Compilar el proyecto sin correr tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final
FROM eclipse-temurin:21-jre
WORKDIR /app

# (Opcional pero recomendable) Establecer zona horaria del sistema
RUN ln -sf /usr/share/zoneinfo/America/Mexico_City /etc/localtime && echo "America/Mexico_City" > /etc/timezone


# Copiar el JAR compilado
COPY --from=build /app/target/bcm-event-consumer.jar app.jar

# Puerto de la app
EXPOSE 8080

# Ejecutar la aplicación con zona horaria correcta en JVM
ENTRYPOINT ["java", "-Duser.timezone=America/Mexico_City", "-jar", "app.jar"]


#docker build -t bcm-event-consumer
#docker run -d -p 8088:8080 --name bcm-event-consumer bcm-event-consumer