# 🚀 Guia de Instalação e Execução com Docker Compose

Este projeto utiliza **Docker Compose** para orquestrar três serviços principais:

- **Frontend** — Aplicação web (Vite/Nginx)
- **Backend** — API desenvolvida em Spring Boot
- **Banco de Dados** — PostgreSQL com persistência em volume

---

## 📦 Estrutura do Projeto

A estrutura esperada dos diretórios é a seguinte:

```
.
├── docker-compose.yml
├── frontend/
│   ├── Dockerfile
│   └── ...
└── backend/
    ├── Dockerfile
    └── ...
```

Certifique-se de que o arquivo `docker-compose.yml` está na raiz e que os diretórios `frontend/` e `backend/` contêm seus respectivos `Dockerfile`.

---

## 🛠️ Pré-requisitos

Antes de começar, verifique se você possui instalado em sua máquina:

- [Docker](https://docs.docker.com/get-docker/) — versão 20.10 ou superior  
- [Docker Compose](https://docs.docker.com/compose/) — versão 1.29 ou superior  

Você pode verificar executando:

```bash
docker --version
docker compose version
```

---

## 💻 Instalação no macOS e Linux

### 🐧 Linux (Ubuntu/Debian)

1. Atualize seus pacotes:
   ```bash
   sudo apt update
   ```

2. Instale o Docker:
   ```bash
   sudo apt install docker.io -y
   ```

3. Habilite e inicie o serviço do Docker:
   ```bash
   sudo systemctl enable docker
   sudo systemctl start docker
   ```

4. Adicione seu usuário ao grupo `docker` (para evitar usar `sudo`):
   ```bash
   sudo usermod -aG docker $USER
   ```
   > ⚠️ Saia e entre novamente na sessão para que as permissões tenham efeito.

5. Instale o Docker Compose (caso não venha junto com o Docker):
   ```bash
   sudo apt install docker-compose-plugin -y
   ```

6. Verifique se tudo está instalado corretamente:
   ```bash
   docker --version
   docker compose version
   ```

---

### 🍎 macOS

1. Baixe e instale o **Docker Desktop for Mac**:  
   👉 [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)

2. Após a instalação, abra o aplicativo **Docker Desktop**.  
   Certifique-se de que o Docker está **em execução** (ícone da baleia deve aparecer na barra superior).

3. Verifique a instalação via terminal:
   ```bash
   docker --version
   docker compose version
   ```

> 💡 Dica: o Docker Desktop já inclui o `docker compose` nativo, não é necessário instalar separadamente.

---

## ⚙️ Configuração

O `docker-compose.yml` define três serviços:

### 🧱 Banco de Dados (PostgreSQL)

- Imagem: `postgres:16-alpine`
- Banco: `automa-infra`
- Usuário: `devuser`
- Senha: `devuser`
- Volume persistente: `postgres_data`

### 🧩 Backend (Spring Boot)

- Constrói a imagem a partir de `./backend/Dockerfile`
- Conecta-se ao PostgreSQL via `jdbc:postgresql://db:5432/mydatabase`
- Variáveis principais:
  ```env
  SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydatabase
  SPRING_DATASOURCE_USERNAME=myuser
  SPRING_DATASOURCE_PASSWORD=mypassword
  SPRING_PROFILES_ACTIVE=dev
  ```

### 🌐 Frontend (Vite/Nginx)

- Constrói a imagem a partir de `./frontend/Dockerfile`
- Expõe a aplicação na porta `80`
- Define a variável de ambiente `VITE_API_URL` apontando para a API (`http://api:8080`)

---

## 🚀 Subindo o Ambiente

Para **construir** e **iniciar** todos os containers, execute:

```bash
docker compose up -d --build
```

📋 **Explicação:**
- `-d` executa em modo “detached” (em segundo plano)
- `--build` força a reconstrução das imagens do frontend e backend

---

## 🔍 Verificando os Containers

Para verificar se tudo está rodando corretamente:

```bash
docker ps
```

Você deve ver algo como:

```
CONTAINER ID   IMAGE                STATUS          PORTS
abc123def456   project-frontend     Up 2 minutes    0.0.0.0:80->80/tcp
def456ghi789   project-backend      Up 2 minutes
ghi789jkl012   postgres:16-alpine   Up 2 minutes    5432/tcp
```

---

## 🌍 Acessando a Aplicação

- **Frontend (Web):** [http://localhost](http://localhost)
- **Backend (API):** disponível internamente para o frontend via `http://api:8080`

---

## 🧹 Parando e Limpando Containers

Para **parar** os containers:

```bash
docker compose down
```

Para **remover volumes e dados persistentes** (⚠️ cuidado, isso apaga o banco):

```bash
docker compose down -v
```

---

## 🧰 Comandos Úteis

| Ação | Comando |
|------|----------|
| Ver logs de todos os serviços | `docker compose logs -f` |
| Ver logs de um serviço específico | `docker compose logs -f app` |
| Reconstruir apenas o backend | `docker compose build app` |
| Reiniciar containers | `docker compose restart` |

---

## 🧱 Persistência de Dados

O volume `postgres_data` garante que os dados do banco **não sejam perdidos** quando os containers forem parados.  
Para verificar os volumes criados:

```bash
docker volume ls
```

---

## 🧾 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

> 💡 **Dica:** Sempre que modificar o código do backend ou frontend, reconstrua as imagens usando:
>
> ```bash
> docker compose up -d --build
> ```

