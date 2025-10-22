# 🧩 Estrutura da API

## 1. `/users`
Controla as informações dos usuários e suas credenciais.

### Endpoints
| Método | Rota | Descrição |
|--------|------|------------|
| `GET /users` | Lista todos os usuários |
| `GET /users/:id` | Retorna um usuário específico |
| `POST /users` | Cria um novo usuário |
| `PATCH /users/:id` | Atualiza parcialmente os dados de um usuário |
| `PUT /users/:id` | Atualiza completamente os dados de um usuário |
| `DELETE /users/:id` | Remove um usuário (opcional) |

---

## 2. `/applications`
Controla as aplicações montadas pelo usuário (por exemplo, serviços implantados).

### Endpoints
| Método | Rota | Descrição |
|--------|------|------------|
| `GET /applications` | Lista todas as aplicações do usuário |
| `GET /applications/:id` | Retorna detalhes de uma aplicação específica |
| `POST /applications` | Cria uma nova aplicação |
| `PATCH /applications/:id` | Atualiza parcialmente uma aplicação |
| `PUT /applications/:id` | Atualiza totalmente uma aplicação |
| `DELETE /applications/:id` | Remove uma aplicação |

---

## 3. `/services-available`
Controla os **serviços disponíveis** ao usuário (React, Angular, Spring, etc).

### Endpoints
| Método | Rota | Descrição |
|--------|------|------------|
| `GET /services-available` | Lista os serviços disponíveis |
| `GET /services-available/:id` | Retorna um serviço específico |
| `POST /services-available` | Adiciona um novo serviço disponível |
| `PATCH /services-available/:id` | Atualiza parcialmente um serviço |
| `PUT /services-available/:id` | Atualiza totalmente um serviço |
| `DELETE /services-available/:id` | Remove um serviço disponível |

---

## 4. `/databases-available`
Controla os **bancos de dados** disponíveis ao usuário (MySQL, PostgreSQL, MongoDB...).

### Endpoints
| Método | Rota | Descrição |
|--------|------|------------|
| `GET /databases-available` | Lista os bancos de dados disponíveis |
| `GET /databases-available/:id` | Retorna um banco específico |
| `POST /databases-available` | Adiciona um novo tipo de banco |
| `PATCH /databases-available/:id` | Atualiza parcialmente um banco |
| `PUT /databases-available/:id` | Atualiza completamente um banco |
| `DELETE /databases-available/:id` | Remove um banco disponível |

---

## 5. `/logs`
Controla os **logs das ações** do sistema (deploys, erros, execuções, etc).

### Endpoints
| Método | Rota | Descrição |
|--------|------|------------|
| `GET /logs` | Lista os logs |
| `GET /logs/:id` | Retorna detalhes de um log específico |
| `POST /logs` | Cria um novo log (ex: resultado de execução) |
| `PATCH /logs/:id` | Atualiza parcialmente um log (geralmente não usado) |
| `PUT /logs/:id` | Atualiza completamente um log |
| `DELETE /logs/:id` | Remove um log |

---

## 6. `/scripts`
Armazena **scripts e templates** de aplicações do usuário para uso posterior (ex: scripts de deploy, build, infra).

### Endpoints
| Método | Rota | Descrição |
|--------|------|------------|
| `GET /scripts` | Lista todos os scripts do usuário |
| `GET /scripts/:id` | Retorna um script específico |
| `POST /scripts` | Cria ou salva um novo script |
| `PATCH /scripts/:id` | Atualiza parcialmente um script |
| `PUT /scripts/:id` | Atualiza completamente um script |
| `DELETE /scripts/:id` | Remove um script |

---

## 💡 Sugestão de Integração

Essa API pode ser usada junto a:
- **Frontend** em Angular ou React (para interface do usuário);
- **Back-end** em Spring Boot (para lógica e persistência);
- **Banco** PostgreSQL (armazenamento de dados);
- **Serviços de containerização (Docker)** para rodar as aplicações montadas;
- **Proxmox API** para provisionar VMs automaticamente.

---

## 🧱 Exemplo de Arquitetura

