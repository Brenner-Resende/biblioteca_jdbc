# Biblioteca - Sistema de Gerenciamento de Empréstimos

Este projeto é um sistema de gerenciamento de empréstimos de livros desenvolvido em **Java**, com conexão ao banco de dados **MySQL** utilizando **JDBC** e o padrão de projeto **DAO (Data Access Object)**. O sistema permite realizar operações de CRUD para todas as entidades envolvidas, bem como gerenciar a disponibilidade de livros e consultar históricos de empréstimos.

---

## Funcionalidades Implementadas

### 1. CRUD Completo
- **Usuários:**
  - Cadastro de novos usuários.
  - Atualização de dados de usuários existentes.
  - Exclusão de usuários.
  - Consulta de usuários por ID.

- **Livros:**
  - Cadastro de novos livros.
  - Atualização de informações de livros.
  - Exclusão de livros.
  - Consulta de livros por ID.

- **Empréstimos:**
  - Registro de novos empréstimos.
  - Atualização de empréstimos existentes.
  - Devolução de livros com atualização automática de estoque.
  - Consulta de empréstimos por ID.

---

### 2. Controle de Disponibilidade de Livros
- Listagem de livros com exemplares disponíveis.
- Validação de disponibilidade antes de registrar um empréstimo.
- Atualização automática do estoque ao registrar ou concluir um empréstimo.

---

### 3. Recursos Adicionais
- **Histórico de Empréstimos:** Consulta de todos os empréstimos realizados de um livro específico, exibindo dados do usuário, data de empréstimo e devolução.
- **Notificações:**
  - Sistema de alerta para livros com baixo estoque (a ser implementado).
  - Lista de livros indisponíveis (a ser implementado).

---

## Tecnologias Utilizadas
- **Linguagem de Programação:** Java
- **Banco de Dados:** MySQL
- **JDBC:** Para conexão e manipulação do banco de dados.
- **Padrão DAO:** Para organização do código e separação das responsabilidades.

---

## Estrutura do Banco de Dados

### Tabelas
1. **usuario**
   - `id_usuario` (PK)
   - `nome`
   - `email`
   - `telefone`

2. **livro**
   - `id_livro` (PK)
   - `titulo`
   - `autor`
   - `num_exemplares`

3. **emprestimo**
   - `id_emprestimo` (PK)
   - `id_usuario` (FK)
   - `id_livro` (FK)
   - `data_emprestimo`
   - `data_devolucao`

---

## Como Executar o Projeto

### 1. Configurar o Banco de Dados
- Crie um banco de dados no MySQL utilizando as instruções SQL fornecidas na pasta `database`.
- Atualize o arquivo de configuração `db.properties` com suas credenciais do banco de dados:
  ```properties
  db.url=jdbc:mysql://localhost:3306/sua_base_de_dados
  db.user=seu_usuario
  db.password=sua_senha
  ```

### 2. Executar o Projeto
- Importe o projeto em sua IDE (ex.: IntelliJ, Eclipse).
- Certifique-se de que as dependências JDBC estão configuradas no classpath.
- Execute a aplicação pela classe principal.

---

## Melhorias Futuras
- Implementar sistema de notificações para livros com baixo estoque e indisponíveis.
- Adicionar interface gráfica (GUI) para facilitar o uso do sistema.
- Implementar autenticação e controle de acesso para operações administrativas.

---

## Autor
Este projeto foi desenvolvido por Brenner Resende Borges.

---

## Licença
Este projeto está licenciado sob a [MIT License](LICENSE).
