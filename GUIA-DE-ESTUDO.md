# Guia de estudo e apresentação

## 1. Uma classe e seus atributos

`CaixaDaAgua`, em `produto`, representa um produto. Tem marca, modelo,
três dimensões (largura, altura e profundidade), cor, material, formato,
preço, ID, quantidade em estoque e fornecedor opcional.
O construtor recebe os dados; `init` valida dimensões, preço e estoque.
`val` impede reatribuição; `var` permite alteração conforme a visibilidade.

## 2. Herança

`Pessoa` é a classe-mãe, com nome, CPF e idade.
`Cliente : Pessoa` e `Funcionario : Pessoa` aproveitam esses atributos.
`Instalador : Funcionario` acrescenta um terceiro nível e fixa setor e
habilidade em instalação. `open` permite herdar de uma classe.

## 3. Polimorfismo real

Em `MenuFinanceiro.kt → pagarSalario()`, o funcionário é colocado em
`val pessoa: Pessoa = funcionario`. A chamada
`pessoa.receberConta(...)` executa o `override` de `Funcionario`,
que devolve um valor negativo. Esse valor é usado na movimentação do salário.
A variável tem tipo Pessoa, mas o comportamento vem do objeto concreto.
Instalador herda essa implementação de Funcionario.

## 4. Encapsulamento

`CaixaDaAgua.quantidadeEstoque` tem `private set`: outro arquivo pode ler,
mas não atribuir diretamente. Os métodos `adicionarEstoque` e
`removerEstoque` controlam alterações em memória. A retirada inválida
retorna `false`; uma adição inválida lança uma exceção.
No banco, compras e vendas atualizam o estoque na mesma transação do dinheiro.
Não há saldo mutável exposto: `CRUDMovimentacao.calcularSaldo()` soma os
lançamentos persistidos. `Caixa.receita()` e `despesa()` validam e
preparam o sinal do valor; não gravam dinheiro sozinhas.

## 5. Como adicionar um Estagiário

Essa classe ainda não existe. Uma extensão simples seria declarar
`class Estagiario(...): Funcionario(...)` e encaminhar os dados ao
construtor da classe-mãe, como faz Instalador. Seria necessário acrescentar
a opção no cadastro. Se for importante recuperar a distinção depois de
reiniciar o programa, será preciso persistir um campo que identifique essa
categoria e ajustar a leitura do CRUD. Só criar a subclasse não faz o banco
reconhecê-la automaticamente.

## 6. Enum

`Setor` restringe escolhas a FINANCEIRO, ADMINISTRATIVO, LOGISTICA e
INSTALACAO. O menu mostra `Setor.entries`; o banco recebe `setor.name`.
A leitura usa `Setor.valueOf(...)`. Cor, Material, Turno, Habilidade,
StatusServico e TipoMovimentacao seguem a mesma ideia.

## 7. Packages

- `pessoas`: classes de pessoas e fornecedor.
- `produto`: caixa-d'água e serviço.
- `financeiro`: valores e movimentações.
- `enumeradores`: opções fixas.
- `validacao`: leitura e conferência das entradas.
- `repositorio`: SQL e JDBC.
- `sistema`: menus e sequência das operações.

Os packages agrupam responsabilidades e permitem usar imports claros.

## 8. Conexão PostgreSQL

Os CRUDs herdam `ConexaoPostgres`. A função `conectar()` carrega
`org.postgresql.Driver` com `Class.forName` e abre `c` com
`DriverManager.getConnection(url, user, senha)`. Ela continua sem retorno.
A URL usa o banco local `caixaDaAgua`, usuário postgres e senha masterkey.
Cada operação fecha seus statements e sua conexão manualmente em `finally`.

## 9. O que mudaria para MySQL

Seria necessário instalar o driver JDBC do MySQL e trocar driver e URL.
Também adaptar SQL: SERIAL, FLOAT8[], regex do CHECK, agregação de texto
e INSERT com RETURNING não têm uso idêntico nos dois bancos.
As três dimensões poderiam virar três colunas numéricas.
Não basta trocar a URL. Essa migração não foi implementada nem testada.

## 10. Dependências e interface

Os menus chamam os CRUDs, que utilizam as classes e a conexão.
As classes não chamam os menus nem os CRUDs: não há dependência circular
entre esses packages. A validação não importa classes de pessoas.
`InterfaceJPA<T>` é o contrato já existente com salvar, listar, editar
e excluir. Apesar do nome, ela é uma interface própria: o projeto usa JDBC,
não a biblioteca JPA. Os CRUDs implementam esse contrato.

## 11. Quando a movimentação nasce

O objeto `Movimentacao` é criado no menu depois de validar os dados e escolher
o responsável. O objeto ainda está somente na memória.
A gravação acontece no INSERT de `CRUDMovimentacao`.
Compras/vendas e conclusão de serviço só se confirmam após `commit()`.
Se houver falha, `rollback()` desfaz as alterações daquela transação.

## 12. Caminho de uma venda

`menuFinanceiro → registrarVenda → escolherCliente/escolherProduto`.
O menu lê a quantidade, verifica estoque e multiplica preço pela quantidade.
Escolhe o responsável, cria a movimentação positiva e chama
`registrarProduto`. O CRUD reduz o estoque e insere o lançamento.
Se o INSERT falhar, o estoque volta ao valor anterior no banco.
A alteração feita no objeto do menu antes da gravação é apenas em memória.

## 13. Uso simultâneo

O projeto é pensado para uma pessoa usando o console localmente.
Há transações e uma condição SQL que evita vender além do estoque,
mas não há controle completo de múltiplos usuários.
Por exemplo, preço ou nome podem mudar entre a escolha no menu e a gravação.
Não foram implementados autenticação, sessões ou bloqueios empresariais.

## 14. Relação N:N

Um serviço pode ter vários funcionários e o mesmo funcionário pode
participar de vários serviços. `servico_funcionario` guarda
`servico_id` e `funcionario_id`, juntos formando a chave primária.
O cadastro permite adicionar mais de um instalador; a listagem usa JOIN
para mostrar seus nomes e IDs. A equipe é salva junto com o serviço.
A função `buscarPorId` de serviço carrega seus dados principais; a equipe
é consultada pela listagem, não preenchida nesse retorno.

## 15. Responsável pela transação

O menu lista os funcionários e pede um ID.
`escolherFuncionario()` busca o registro e interrompe se ele não existe.
A movimentação guarda `responsavel_id`, uma chave estrangeira.
Não é login: a pessoa é escolhida em cada operação.
Qualquer funcionário cadastrado pode ser o responsável financeiro.

## 16. Script e chaves estrangeiras

`banco/schema.sql` cria sete tabelas e a view `saldo_caixa`.
As FKs ligam produto a fornecedor, serviço a cliente/produto, equipe a
serviço/funcionário e movimentação ao responsável.
Elas impedem referências inexistentes e várias exclusões com vínculos.
Excluir fornecedor deixa o fornecedor do produto nulo.
O banco local foi atualizado com o script separado
`verificacao/atualizar-banco-existente.sql`, preservando os produtos.
A coluna antiga opcional `data_movimentacao` foi mantida no banco migrado;
o programa usa `data_hora`.

## 17. Caminho completo de uma consulta

Exemplo: escolher um cliente. O menu chama `CRUDCliente.listar()`.
Esse método chama `listarTodos()`, que conecta, prepara
`SELECT * FROM cliente ORDER BY id`, executa e percorre o ResultSet.
Cada linha vira um Cliente. A listagem imprime esses objetos.
Após escolher o ID, `buscarPorId` retorna o correspondente ou nulo.
Para manter simplicidade, esse método procura na lista inteira; não há paginação.
Nas escritas, PreparedStatement separa o SQL dos valores: cada ? recebe
um parâmetro por posição, com métodos como setString e setInt.

## 18. Se o banco cair

`conectar()` captura SQLException, informa o problema e repassa a exceção.
O menu captura o erro e não informa sucesso. O `finally` tenta fechar os
recursos. Nas operações com mais de uma escrita, há tentativa de rollback.
Não existe funcionamento offline ou reconexão automática.
Se a conexão cair exatamente na confirmação do commit, consulte o extrato
e o estoque antes de repetir: não há mecanismo de confirmação remota ou
proteção contra repetição nesse caso.

## 19. Funcionário e setor

Todo Funcionario tem um Setor. Para instalação, o menu cria Instalador,
fixando setor e habilidade em INSTALACAO.
Outros setores usam Funcionario e permitem selecionar a habilidade.
Ao ler do banco, o CRUD recria Instalador quando os dois campos indicam
instalação. Há quatro setores disponíveis; nenhum cadastro fictício foi
adicionado automaticamente ao banco principal.

## 20. Mudança de setor

Use Gestão de pessoas → Editar funcionário. O menu solicita os dados e
`CRUDFuncionario.editar` atualiza o mesmo ID.
Os campos são `val`; por isso o menu cria outro objeto com os novos valores.
O histórico continua referenciando esse ID.
A mudança não reavalia equipes já agendadas: essa é uma limitação conhecida.

## 21. Cliente versus fornecedor

Cliente é pessoa física e herda nome, CPF e idade de Pessoa.
Fornecedor é empresa, com razão social e CNPJ, portanto fica separado de
Pessoa. Assim não é necessário inventar CPF ou idade para uma empresa.
Parcelas e dívidas não foram implementadas: as operações são à vista.

## 22. Regex símbolo por símbolo

`Regex("^\\d{11}$")`, usada no CPF:

- `^`: início do texto.
- `\\`: escape necessário para representar uma barra invertida na string Kotlin.
- `\d` na regex: dígito.
- `{11}`: exatamente onze repetições.
- `$`: fim do texto.

No CNPJ são quatorze dígitos: `{14}`.
O formato aceito não contém pontos, barras ou hífen.

## 23. Entrada inválida

CPF `123` não corresponde à regex e cancela o cadastro.
Uma quantidade `abc` vira nulo com `toIntOrNull()` e é solicitada novamente.
Estoque insuficiente interrompe a venda sem gravar.
Preço negativo, dimensão zero e índice de enum fora do intervalo são recusados.

## 24. Por que usar regex

É uma forma curta de verificar o formato inteiro de CPF e CNPJ.
A regex do trabalho verifica somente tamanho e dígitos.
Ela não confere dígitos verificadores nem se o documento existe.
Essa limitação é deliberada para manter o exercício simples.

## 25. Nullable

`fornecedorId: Int?` permite produto sem fornecedor.
`buscarPorId(...): Cliente?` permite indicar ID inexistente.
`c: Connection?` começa nula antes de conectar.
O ponto de interrogação faz parte do tipo e permite o valor null.

## 26. Risco de !!

`c!!` afirma que a conexão não é nula.
Se for nula, ocorre uma exceção. O padrão foi preservado.
A conexão agora repassa a falha para o menu, evitando continuar como se
tivesse conectado. O uso de !! continua exigindo cuidado.

## 27. ?:, ?. e !!

- `?:`: usa a alternativa se o valor for nulo. A linha original do menu
  mantém `readln().toIntOrNull()?: 10`.
- `?.`: só acessa o membro se o objeto existir, como `c?.close()`.
- `!!`: afirma que existe e lança exceção se essa afirmação estiver errada.

Em `escolherCliente`, Elvis com `throw` interrompe um ID inexistente.

## 28. Campo obrigatório vazio

`lerTexto` remove espaços nas pontas com trim e exige conteúdo.
Se estiver vazio ou acima do tamanho permitido, pergunta novamente.
`:cancelar` interrompe a operação. O CRUD só é chamado depois de coletar
e validar os dados do cadastro.

## 29. Limitações conhecidas

Não há parcelas, login, API, interface gráfica, estorno ou relatórios avançados.
O saldo pode ser negativo; representa entradas menos saídas.
Não há bloqueio de pagamento de salário repetido por mês.
Preços de produto e salários podem ser cadastrados com zero, mas operações
financeiras de valor zero são recusadas. Serviços exigem preço positivo.
O agendamento não exige uma venda anterior nem reserva estoque.
A compra registra custo total, sem calcular custo médio nem mudar o preço de venda.
Movimentações guardam nomes de pagador/recebedor e a descrição, sem tabelas
separadas de itens da venda. O responsável tem FK.
Edição/exclusão de movimentações e exclusão de serviços são recusadas
explicitamente: o menu oferece cancelar serviços, preservando o histórico.
A opção editar serviço do CRUD altera somente data e preço de um agendado;
não há essa opção no menu. Equipes são definidas ao agendar.
O banco aplica FKs e CHECKs, mas nem toda regra dos menus é reproduzida no SQL.
Os menus pressupõem entrada interativa: encerre pela opção 4 do menu inicial.

## 30. Salário negativo, CPF inválido e cancelamento

No cadastro, salário negativo faz `lerDinheiro` perguntar novamente.
O construtor de Funcionario também recusa salário negativo com require.
CPF inválido cancela o cadastro antes do INSERT.
Se o usuário escrever `:cancelar` durante a leitura dos campos, o menu
informa o cancelamento e nenhum registro é salvo.
Um salário zero é permitido no cadastro, mas seu pagamento é recusado,
pois não haveria movimentação financeira válida.

## Validação da entrega

Os testes reais e seus resultados estão em `verificacao/RELATORIO.md`.
O código foi compilado dos fontes; os arquivos antigos em out não foram usados.
