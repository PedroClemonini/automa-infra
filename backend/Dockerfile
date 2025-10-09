# Estágio 1: Build (Compilação e Empacotamento)
FROM openjdk:21-slim AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos de build primeiro para otimizar o cache
# Maven: pom.xml e .mvn (se usar wrapper)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copia o código fonte
COPY src ./src

# Se usar Maven Wrapper, execute o build para gerar o JAR
# O -DskipTests pula os testes.
RUN ./mvnw package -DskipTests

# Estágio 2: Distribuição (Imagem Final de Execução)
# Usamos a imagem JRE 21 leve para a execução
FROM eclipse-temurin:21

# Define a porta que o Spring Boot usa por padrão
EXPOSE 8080

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR do estágio de build para o estágio de distribuição
# Ajuste o nome do arquivo JAR conforme a saída do seu build (target/*.jar)
COPY --from=build /app/target/*.jar app.jar

# Ponto de entrada para rodar a aplicação Spring Boot
# O comando 'java -jar' é o padrão
ENTRYPOINT ["java", "-jar", "app.jar"]
