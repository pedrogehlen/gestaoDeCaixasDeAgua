# Caixas-d'água — Kotlin, JDBC e PostgreSQL

Programa de console para um trabalho acadêmico. Inclui cadastros de produtos,
clientes, fornecedores e funcionários; estoque por compra/venda; instalação
com equipe; salários, despesas, movimentações e saldo.

## Executar neste computador

No IntelliJ IDEA, abra este projeto e execute a função `main()` de
`src/Main.kt`. Use o JDK 21 e mantenha o driver PostgreSQL nas bibliotecas
do módulo. A biblioteca `drivePostgres` já aponta para `D:/drivePostgres`.

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

## Banco local

A conexão original foi preservada:

- Banco: `caixaDaAgua`, em `localhost:5432`.
- Usuário: `postgres`.
- Senha local do trabalho: `masterkey`.

O banco deste computador foi atualizado em 15/09/2026. **Não execute novamente
o script de atualização.** Os dois produtos antigos foram preservados com
estoque inicial zero. Não foram criadas pessoas ou movimentações fictícias
no banco principal.

Arquivos disponíveis:

- `banco/schema.sql`: criação completa para um banco vazio.
- `verificacao/atualizar-banco-existente.sql`: registro da atualização já aplicada à estrutura antiga,
  usada neste computador. Se houver movimentações antigas, interrompe para
  não inventar pagador, recebedor ou responsável.
- `verificacao/backup-antes-da-atualizacao.sql`: backup local anterior à mudança.

Em outro computador, crie o banco com o nome exato:

```sql
CREATE DATABASE "caixaDaAgua";
```

Depois, conectado a esse banco, execute `banco/schema.sql`.
As aspas na criação preservam as letras maiúsculas do nome.

## Roteiro para demonstrar

1. Entre em **5 — Gestão de pessoas**.
2. Cadastre um funcionário do financeiro e um do setor de instalação.
3. Cadastre um cliente e um fornecedor.
4. Cadastre um produto pela opção **0**; seu estoque começa em zero.
5. Entre em **7 — Financeiro → 2 — Registrar compra** para abastecer.
   Informe o valor **total** da compra.
6. Registre uma venda; o preço unitário vem do cadastro do produto.
7. Em **6 — Serviços**, agende a instalação e escolha um ou mais instaladores.
8. Conclua o serviço para registrar a receita.
9. Demonstre pagamento de salário, despesa, listagem e saldo.
10. Use **0** para voltar dos submenus e **4** para sair do menu inicial.

Digite `:cancelar` em um campo de cadastro/operação para voltar sem salvar.
Valores monetários aceitam vírgula ou ponto decimal, sem separador de milhar.
CPF e CNPJ devem conter somente dígitos.

## Verificações

Foram executados testes de compilação, regras e persistência em PostgreSQL.
Os testes usam o banco separado `revisao1_verificacao_20260915_1835`.
A cópia da conexão para testes altera somente o nome desse banco; o arquivo
de conexão do projeto continua apontando para `caixaDaAgua`.

Consulte `verificacao/RELATORIO.md` para resultados e limites.
Os arquivos em `verificacao` não são parte dos fontes da aplicação.
Não execute `Verificacao.kt` contra o banco principal.

## Apresentação

Leia [GUIA-DE-ESTUDO.md](GUIA-DE-ESTUDO.md): explica os conceitos usando
o código que realmente está no projeto, além das limitações da solução.
