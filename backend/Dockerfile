# ============================
# 🏗️ Estágio 1: Build
# ============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro (melhor cache)
COPY pom.xml .

# Faz download antecipado das dependências
RUN mvn dependency:go-offline

# Copia o restante do código
COPY src ./src

# Compila e empacota (sem rodar testes)
RUN mvn clean package -DskipTests

# ============================
# 🚀 Estágio 2: Execução
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app
EXPOSE 8080

# Copia o jar do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Inicia o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
