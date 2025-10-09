# ---------------------------------------------
# Estágio 1: Build (Cria os arquivos estáticos)
# ---------------------------------------------
# Usa a imagem Node.js 20 para instalar dependências e rodar o build
FROM node:20-alpine AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos de configuração para otimizar o cache
COPY package.json ./

# Instala as dependências
RUN npm install 
# Copia o código fonte da aplicação
COPY . .

# Roda o comando de build (ajuste se a pasta de saída for diferente de 'dist')
RUN npm run build 

# ---------------------------------------------
# Estágio 2: Distribuição (Servidor web leve)
# ---------------------------------------------
# Usamos a imagem mais leve do Nginx para servir os arquivos estáticos
FROM nginx:alpine

# Copia a configuração personalizada do Nginx (para Proxy Reverso)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia o resultado do build do estágio 'build' para o diretório de serviço do Nginx
# ATENÇÃO: Verifique se a sua pasta de build é 'dist' ou 'build' ou 'out'
COPY --from=build /app/dist /usr/share/nginx/html

# A porta 80 é a porta padrão do Nginx
EXPOSE 80

# Comando para manter o Nginx rodando em primeiro plano
CMD ["nginx", "-g", "daemon off;"]
