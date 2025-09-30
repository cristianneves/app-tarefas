# API de Gerenciamento de Tarefas Colaborativa
## API RESTful de alta performance para gerenciamento de tarefas, construída com Java e Spring Boot. Focada em colaboração, organização e escalabilidade.

### Funcionalidades

Autenticação JWT: Sistema seguro de registro e login via Bearer Token.

Gestão de Tarefas & Subtarefas: CRUD completo com suporte a tarefas aninhadas.

Colaboração: Sistema de convites com permissões (VISUALIZAR, EDITAR).

Organização: Suporte a Categorias, Prioridades e Datas de Vencimento.

Anexos: Upload e download de arquivos vinculados às tarefas.

Dashboard Analítico: Métricas de produtividade do usuário.

### Stack Tecnológica

Backend: Java 17, Spring Boot 3, Spring Security, Spring Data JPA

Banco de Dados: PostgreSQL (Produção), H2 (Desenvolvimento)

Autenticação: JSON Web Tokens (JWT)

Build: Apache Maven

Utilitários: Lombok, MapStruct (sugerido)

### Como Executar

Pré-requisitos:
  
- JDK 17+
- Apache Maven 3.8+
- Docker & Docker Compose (Recomendado)

#### 1. Clonar o Repositório
 
```bash
git clone https://seu-repositorio/api-tarefas.git
```
cd api-tarefas

#### 2. Configurar Variáveis de Ambiente

Crie um arquivo .env na raiz do projeto a partir do exemplo:

```bash
cp .env.example .env
```

Agora, edite o arquivo .env com suas configurações locais:

#### Configuração do Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/seu_banco
DB_USER=seu_user
DB_PASS=sua_senha

#### Segredo para a assinatura do JWT (use um gerador de string aleatória forte)
JWT_SECRET=sua-chave-secreta-de-minimo-32-caracteres-aqui

#### Diretório para upload de arquivos
UPLOAD_DIR=./uploads
O arquivo application.properties já está configurado para ler estas variáveis.

#### 3. Executar a Aplicação

Compile o projeto e execute:

```bash
mvn spring-boot:run
```
A API estará disponível em http://localhost:8080.

### 🐳 Docker
Para garantir um ambiente consistente e facilitar o deploy, utilize Docker.

Construir a imagem Docker:

```bash
docker build -t sua-imagem/api-tarefas .
```

Executar com Docker Compose:
O docker-compose.yml orquestra a API e o banco de dados juntos. Certifique-se que seu arquivo .env está preenchido.

```bash
docker-compose up -d
```
A API estará disponível em http://localhost:8080.

### Endpoints:

Autenticação       /auth/{login,registrar}	  Gerencia o registro e login de usuários.
Tarefas	           /tarefas              	CRUD de tarefas, subtarefas e gestão de status.
Categorias	       /categoria	            CRUD de categorias para organização de tarefas.
Convites	         /convites	            Gestão de convites de colaboração em tarefas.
Anexos	           /anexos	              Download de arquivos anexados.
