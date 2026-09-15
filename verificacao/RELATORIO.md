# Relatório de verificação

Data: 15/09/2026.

## Resultado

- Compilação final dos fontes: **aprovada, sem erros ou avisos**.
- Regras e persistência JDBC: **58 verificações aprovadas**.
- Uso dos menus por entradas de console: **40 cenários aprovados**.
- Conexão indisponível e driver ausente: **2 verificações aprovadas**.
- Banco principal: os **dois produtos originais** permanecem cadastrados;
  nenhuma movimentação de teste foi gravada nele.

## Ambiente

JDK Microsoft 21.0.10, compilador Kotlin instalado com IntelliJ IDEA 2026.1.1,
driver PostgreSQL 42.7.13 e servidor PostgreSQL 18 local.
O programa foi recompilado a partir de src; não utilizou classes antigas em out.

Os testes de escrita usaram exclusivamente o banco separado
`revisao1_verificacao_20260915_1835`. O banco ficou disponível com seus dados
de verificação, separado de `caixaDaAgua`.

## O que foi coberto

Cadastros, consultas, edição e exclusão de clientes, fornecedores, funcionários
e produtos; enum de setor e reconstrução de Instalador; herança e polimorfismo;
estoque zero, negativo e insuficiente; dimensões inválidas; valor monetário
malformado, negativo, zero e com casas decimais excessivas; CPF/CNPJ inválidos
ou duplicados; texto obrigatório vazio; cancelamento de cadastro; IDs inexistentes.

Compra e venda atualizam estoque e dinheiro. Salário e despesa geram saídas.
Serviços foram associados a dois instaladores, listados, concluídos e cancelados.
Foram recusados instaladores não habilitados, conclusão repetida, conclusão
de cancelado e cancelamento de concluído.

Falhas por responsável inexistente confirmaram rollback do estoque e do status.
Faltas de cliente/fornecedor e exclusões com vínculos confirmaram as FKs.
A ausência de driver foi exercitada no menu real; a indisponibilidade do servidor
foi simulada com uma conexão a uma porta sem PostgreSQL, sem parar o banco local.

## Conferência de valores

A primeira sequência produziu:

`-500 + 160 - 100 - 25 + 200 = -265`.

A sequência adicional pelos menus produziu:

`-265 - 100 + 160 - 100 - 25 + 300 = -30`.

O banco de testes terminou com saldo **-30,00**, dez movimentações e oito
unidades do produto usado nas vendas. Cancelar serviço não alterou o saldo.

## Banco existente

Foi realizada leitura da estrutura e dos registros antes da alteração.
O banco tinha duas caixas e nenhuma movimentação.
Foi salvo `backup-antes-da-atualizacao.sql`, e a atualização foi primeiro
executada com rollback. Depois foi aplicada ao banco principal.
O preço dos produtos foi convertido de texto para NUMERIC sem alterar seus
valores. O estoque novo iniciou em zero. A coluna antiga de data foi mantida
como opcional; a aplicação usa data_hora.

O arquivo `schema.sql` foi usado para criar as sete tabelas e a view no banco
separado de testes. O script de atualização é específico para a estrutura
original e não deve ser reaplicado.

## Evidências locais

- `compilacao-final.txt`: resultado da compilação entregue.
- `resultado-testes.txt`: execução das 58 verificações.
- `resultado-menus.txt`: entradas exercitadas e saídas dos 40 cenários.
- `resultado-conexao.txt` e `resultado-sem-driver.txt`: falhas controladas.
- `Verificacao.kt`, `testar-menus.py` e `VerificarConexao.kt`: verificações
  sem frameworks, fora dos fontes da aplicação.

Os testes foram executados em sequência sobre um banco de teste inicialmente
vazio. Não devem ser repetidos cegamente sobre os dados já produzidos.
Para repetir em outro banco vazio, ajuste o nome somente nos arquivos de
verificação e na cópia de ConexaoPostgres usada para compilar os testes.
O projeto principal não precisa dessa cópia nem de Python para funcionar.

## Limites

Não houve teste de carga, múltiplos usuários ou falha de rede exatamente
durante commit. Não há parcelas, autenticação ou estorno.
Esses pontos e as decisões de simplicidade estão no GUIA-DE-ESTUDO.md.
