# Caixas-d'água — Kotlin, JDBC e PostgreSQL

Programa de console para um trabalho acadêmico. Inclui cadastros de produtos,
clientes, fornecedores e funcionários; estoque por compra/venda; instalação
com equipe; salários, despesas, movimentações e saldo.

## Requisitos e execução

No IntelliJ IDEA, abra este projeto e execute a função `main()` de
`src/Main.kt`. Use o JDK 21 e mantenha o driver PostgreSQL nas bibliotecas
do módulo. O projeto foi configurado com a biblioteca `drivePostgres`
apontando para `D:/drivePostgres`. Em outro computador, ajuste essa
biblioteca para a pasta que contém o arquivo do driver JDBC PostgreSQL.

Antes da primeira execução, instale o PostgreSQL e configure o banco conforme
as instruções abaixo. Depois, execute `src/Main.kt`.

Também é possível abrir um PowerShell nesta pasta e executar:

```powershell
.\executar.ps1
```

O script compila os fontes atuais antes de iniciar. Para somente compilar:

```powershell
.\executar.ps1 -SomenteCompilar
```

Se as instalações mudarem de pasta, informe `-JavaHome`, `-KotlinHome`
e `-Driver`. Não há Gradle, frameworks ou bibliotecas novas.
As únicas dependências de execução são Java, Kotlin e o driver JDBC existente.

## Configurar o banco de dados

A pasta `banco` contém somente o arquivo [schema.sql](banco/schema.sql),
que cria as tabelas e a consulta de saldo em um banco vazio.

Para instalar em outro computador pelo pgAdmin:

1. Conecte-se ao PostgreSQL e abra o **Query Tool** no banco `postgres`.
2. Execute o comando abaixo para criar o banco:

```sql
CREATE DATABASE "caixaDaAgua";
```

3. Atualize a lista de bancos e abra o **Query Tool** no banco `caixaDaAgua`.
4. Abra o arquivo `banco/schema.sql` e execute seu conteúdo nesse banco.
5. Confira a configuração em `src/repositorio/ConexaoPostgres.kt`:

- Endereço: `localhost:5432`.
- Banco: `caixaDaAgua`.
- Usuário: `postgres`.
- Senha configurada no projeto: `masterkey`.

Se o PostgreSQL do seu computador usar outra senha, ajuste esse valor no
arquivo de conexão. As aspas no comando de criação preservam as letras
maiúsculas do nome do banco.

O script cria a estrutura sem dados de exemplo. Cadastre as pessoas e os
produtos pelos menus do programa.

**Se o banco já estiver configurado, não execute o `schema.sql` novamente.**
No computador em que o projeto foi desenvolvido, essa configuração já foi feita.

## Menus

### Menu principal

```text
0 - Gestão de caixas-d'água
1 - Gestão de pessoas
2 - Serviços
3 - Financeiro
4 - Sair
```

### Gestão de caixas-d'água

Ao selecionar **0** no menu principal, abre-se o submenu:

```text
1 - Cadastrar caixa-d'água
2 - Editar caixa-d'água
3 - Listar caixas-d'água
4 - Excluir caixa-d'água
0 - Voltar
```

Depois de uma operação, o programa permanece nesse submenu.
Selecione **0 — Voltar** para retornar ao menu principal.
O estoque começa em zero e é movimentado pelas compras e vendas no Financeiro.

### Outras áreas

- **Gestão de pessoas:** cadastrar, listar, editar e excluir clientes,
  fornecedores e funcionários.
- **Serviços:** agendar, listar, concluir e cancelar instalações.
- **Financeiro:** registrar compras e vendas, pagar salários e despesas,
  listar movimentações e consultar o saldo.

## Roteiro para demonstrar

1. Entre em **1 — Gestão de pessoas**.
2. Cadastre um funcionário do financeiro e um do setor de instalação.
3. Cadastre um cliente e um fornecedor.
4. Cadastre um produto em **0 — Gestão de caixas-d'água → 1 — Cadastrar caixa-d'água**; seu estoque começa em zero.
5. Entre em **3 — Financeiro → 2 — Registrar compra** para abastecer.
   Informe o valor **total** da compra.
6. Registre uma venda; o preço unitário vem do cadastro do produto.
7. Em **2 — Serviços**, agende a instalação e escolha um ou mais instaladores.
8. Conclua o serviço para registrar a receita.
9. Demonstre pagamento de salário, despesa, listagem e saldo.
10. Use **0** para voltar dos submenus e **4** para sair do menu inicial.

Digite `:cancelar` em um campo de cadastro/operação para voltar sem salvar.
Valores monetários aceitam vírgula ou ponto decimal, sem separador de milhar.
CPF e CNPJ devem conter somente dígitos.

## Apresentação

Leia [GUIA-DE-ESTUDO.md](GUIA-DE-ESTUDO.md): explica os conceitos usando
o código que realmente está no projeto, além das limitações da solução.
