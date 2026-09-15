import enumeradores.*
import financeiro.*
import pessoas.*
import produto.*
import repositorio.*
import validacao.*
import java.math.BigDecimal
import java.time.LocalDate
import java.sql.DriverManager

private var verificacoes = 0
private fun verificar(condicao: Boolean, descricao: String) {
    check(condicao) { descricao }
    verificacoes++
    println("OK: $descricao")
}
private fun falha(descricao: String, acao: () -> Unit) {
    var falhou = false
    try { acao() } catch (e: Exception) { falhou = true }
    verificar(falhou, descricao)
}
private fun numero(sql: String): Int {
    val c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/revisao1_verificacao_20260915_1835", "postgres", "masterkey")
    try {
        val stmt = c.createStatement()
        try {
            val rs = stmt.executeQuery(sql)
            try { rs.next(); return rs.getInt(1) } finally { rs.close() }
        } finally { stmt.close() }
    } finally { c.close() }
}

fun main() {
    Class.forName("org.postgresql.Driver")
    check(numero("SELECT count(*) FROM cliente") == 0) { "Execute uma vez em banco de teste vazio." }
    verificar(validarCpf("12345678901") && !validarCpf("123") && !validarCpf("abcdefghijk"), "Regex CPF")
    verificar(validarCnpj("12345678901234") && !validarCnpj("123") && !validarCnpj("1234567890123A"), "Regex CNPJ")
    falha("Funcionário com salário negativo") { Funcionario("Teste", "12345678901", 20, "-1".toBigDecimal(), Turno.MATUTINO, Habilidade.FINANCEIRO, Setor.FINANCEIRO) }
    falha("Funcionário com CPF inválido") { Instalador("Teste", "123", 20, "10".toBigDecimal(), Turno.MATUTINO) }
    falha("Cliente menor de idade") { Cliente("Teste", "12345678901", 17) }
    falha("Fornecedor com CNPJ inválido") { Fornecedor("Teste", "123") }
    falha("Texto obrigatório vazio") { Fornecedor("", "12345678901234") }
    falha("Receita zero") { Caixa().receita(BigDecimal.ZERO) }
    falha("Despesa negativa") { Caixa().despesa("-1".toBigDecimal()) }

    val clientes = CRUDCliente()
    val fornecedores = CRUDFornecedor()
    val funcionarios = CRUDFuncionario()
    val produtos = CRUDCaixaDAgua()
    val servicos = CRUDServico()
    val financeiro = CRUDMovimentacao()
    verificar(financeiro.calcularSaldo().compareTo(BigDecimal.ZERO) == 0, "Saldo inicial vazio")
    clientes.salvar(Cliente("Cliente Teste", "12345678901", 25))
    fornecedores.salvar(Fornecedor("Fornecedor Teste", "12345678901234"))
    funcionarios.salvar(Funcionario("Financeiro Teste", "23456789012", 30, "100".toBigDecimal(), Turno.MATUTINO, Habilidade.FINANCEIRO, Setor.FINANCEIRO))
    funcionarios.salvar(Instalador("Instalador Teste", "34567890123", 25, "100".toBigDecimal(), Turno.VESPERTINO))
    funcionarios.salvar(Instalador("Segundo Instalador", "45678901234", 26, "100".toBigDecimal(), Turno.MATUTINO))
    val cliente = clientes.listarTodos().first()
    val fornecedor = fornecedores.listarTodos().first()
    val responsavel = funcionarios.listarTodos().first()
    val instaladores = funcionarios.listarTodos().filter { it is Instalador }
    verificar(instaladores.size == 2, "Herança reconstruída do banco e dois setores")
    val pessoa: Pessoa = instaladores.first()
    verificar(pessoa.receberConta("100".toBigDecimal()) == "-100".toBigDecimal(), "Polimorfismo Pessoa/Funcionario")
    falha("CPF duplicado recusado") { clientes.salvar(Cliente("Duplicado", cliente.cpf, 30)) }
    falha("CNPJ duplicado recusado") { fornecedores.salvar(Fornecedor("Duplicado", fornecedor.cnpj)) }
    verificar(clientes.buscarPorId(999999) == null && fornecedores.buscarPorId(999999) == null && funcionarios.buscarPorId(999999) == null, "IDs de pessoas inexistentes")
    clientes.editar(Cliente("Cliente Editado", cliente.cpf, 26), cliente.id)
    fornecedores.editar(Fornecedor("Fornecedor Editado", fornecedor.cnpj), fornecedor.id)
    funcionarios.editar(Funcionario("Financeiro Editado", responsavel.cpf, 31, "100".toBigDecimal(), Turno.NOTURNO, Habilidade.ADMINISTRATIVO, Setor.ADMINISTRATIVO), responsavel.id)
    verificar(clientes.buscarPorId(cliente.id)?.nome == "Cliente Editado", "Editar cliente")
    verificar(fornecedores.buscarPorId(fornecedor.id)?.razaoSocial == "Fornecedor Editado", "Editar fornecedor")
    verificar(funcionarios.buscarPorId(responsavel.id)?.setor == Setor.ADMINISTRATIVO, "Mudança de setor")
    val produto = CaixaDaAgua("Marca", "Modelo", mutableListOf(1.0, 2.0, 3.0), Cor.AZUL_FORTE, Material.INOX, "Redondo", "80".toBigDecimal(), fornecedorId = fornecedor.id)
    verificar(!produto.removerEstoque(1) && !produto.removerEstoque(0) && !produto.removerEstoque(-1), "Estoque insuficiente/zero/negativo")
    falha("Adicionar estoque zero") { produto.adicionarEstoque(0) }
    falha("Adicionar estoque negativo") { produto.adicionarEstoque(-1) }
    produto.adicionarEstoque(3)
    verificar(produto.removerEstoque(3) && produto.quantidadeEstoque == 0, "Métodos de estoque")
    produtos.salvar(produto)
    val idProduto = produtos.listarTodos().first().id
    produtos.editar(CaixaDaAgua("Marca Editada", "Modelo Editado", mutableListOf(3.0, 4.0, 5.0), Cor.CINZA, Material.POLIETILENO, "Quadrado", "80".toBigDecimal(), fornecedorId = fornecedor.id), idProduto)
    val editado = produtos.buscarPorId(idProduto)!!
    verificar(editado.dimensao == mutableListOf(3.0, 4.0, 5.0) && editado.cor == Cor.CINZA && editado.material == Material.POLIETILENO && editado.formato == "Quadrado", "Editar todas as características do produto")
    verificar(produtos.buscarPorId(999999) == null && servicos.buscarPorId(999999) == null, "Produto/serviço inexistente")

    fun movimento(valor: String, tipo: TipoMovimentacao, id: Int = responsavel.id) =
        Movimentacao(valor.toBigDecimal(), "Pagador Teste", "Recebedor Teste", "Verificação", tipo, id)
    falha("Movimentação zero") { movimento("0", TipoMovimentacao.VENDA_PRODUTO) }
    falha("Sinal incompatível") { movimento("10", TipoMovimentacao.COMPRA_PRODUTO) }
    financeiro.registrarProduto(movimento("-500", TipoMovimentacao.COMPRA_PRODUTO), idProduto, 10, fornecedor.id)
    verificar(produtos.buscarPorId(idProduto)!!.quantidadeEstoque == 10, "Compra abastece estoque")
    financeiro.registrarProduto(movimento("160", TipoMovimentacao.VENDA_PRODUTO), idProduto, 2)
    verificar(produtos.buscarPorId(idProduto)!!.quantidadeEstoque == 8, "Venda reduz estoque")
    falha("Venda acima do estoque") { financeiro.registrarProduto(movimento("999", TipoMovimentacao.VENDA_PRODUTO), idProduto, 9) }
    falha("Venda quantidade negativa") { financeiro.registrarProduto(movimento("80", TipoMovimentacao.VENDA_PRODUTO), idProduto, -1) }
    falha("Produto inexistente na compra") { financeiro.registrarProduto(movimento("-80", TipoMovimentacao.COMPRA_PRODUTO), 999999, 1, fornecedor.id) }
    falha("Fornecedor inexistente na compra") { financeiro.registrarProduto(movimento("-80", TipoMovimentacao.COMPRA_PRODUTO), idProduto, 1, 999999) }
    falha("Responsável inexistente reverte estoque") { financeiro.registrarProduto(movimento("80", TipoMovimentacao.VENDA_PRODUTO, 999999), idProduto, 1) }
    verificar(produtos.buscarPorId(idProduto)!!.quantidadeEstoque == 8 && numero("SELECT count(*) FROM movimentacao") == 2, "Falhas não alteram estoque nem lançamentos")
    financeiro.salvar(movimento("-100", TipoMovimentacao.PAGAMENTO_SALARIO))
    financeiro.salvar(movimento("-25", TipoMovimentacao.PAGAMENTO_DESPESA))
    falha("Despesa com responsável inexistente") { financeiro.salvar(movimento("-25", TipoMovimentacao.PAGAMENTO_DESPESA, 999999)) }

    val equipe = instaladores.map { it.id }.toMutableList()
    servicos.salvar(Servico(cliente.id, idProduto, LocalDate.now().plusDays(1), "200".toBigDecimal(), funcionarios = equipe))
    val idServico = numero("SELECT max(id) FROM servico")
    verificar(numero("SELECT count(*) FROM servico_funcionario WHERE servico_id = $idServico") == 2, "N:N com dois instaladores")
    falha("Agendamento sem equipe") { servicos.salvar(Servico(cliente.id, idProduto, LocalDate.now(), "20".toBigDecimal())) }
    falha("Equipe sem habilidade de instalação") { servicos.salvar(Servico(cliente.id, idProduto, LocalDate.now(), "20".toBigDecimal(), funcionarios = mutableListOf(responsavel.id))) }
    falha("Cliente inexistente no serviço") { servicos.salvar(Servico(999999, idProduto, LocalDate.now(), "20".toBigDecimal(), funcionarios = equipe)) }
    verificar(numero("SELECT count(*) FROM servico") == 1, "Agendamento inválido não deixa serviço incompleto")
    falha("Conclusão com responsável inválido reverte status") { financeiro.concluirServico(idServico, movimento("200", TipoMovimentacao.VENDA_SERVICO, 999999)) }
    verificar(servicos.buscarPorId(idServico)!!.status == StatusServico.AGENDADO, "Status revertido após falha financeira")
    financeiro.concluirServico(idServico, movimento("200", TipoMovimentacao.VENDA_SERVICO))
    verificar(servicos.buscarPorId(idServico)!!.status == StatusServico.CONCLUIDO, "Conclusão gera receita")
    falha("Não concluir serviço duas vezes") { financeiro.concluirServico(idServico, movimento("200", TipoMovimentacao.VENDA_SERVICO)) }
    falha("Não cancelar concluído") { servicos.cancelar(idServico) }
    servicos.salvar(Servico(cliente.id, idProduto, LocalDate.now(), "20".toBigDecimal(), funcionarios = equipe))
    val cancelado = numero("SELECT max(id) FROM servico")
    servicos.cancelar(cancelado)
    verificar(servicos.buscarPorId(cancelado)!!.status == StatusServico.CANCELADO, "Cancelar serviço")
    falha("Não concluir cancelado") { financeiro.concluirServico(cancelado, movimento("20", TipoMovimentacao.VENDA_SERVICO)) }
    verificar(financeiro.calcularSaldo().compareTo("-265".toBigDecimal()) == 0, "Saldo: -500 +160 -100 -25 +200 = -265")
    verificar(numero("SELECT count(*) FROM movimentacao WHERE data_hora IS NOT NULL AND responsavel_id IS NOT NULL") == 5, "Cinco movimentos completos")
    falha("Não excluir funcionário vinculado") { funcionarios.excluir(responsavel.id) }
    falha("Não excluir cliente vinculado") { clientes.excluir(cliente.id) }
    falha("Não excluir produto vinculado") { produtos.excluir(idProduto) }
    falha("Histórico financeiro não editável") { financeiro.editar(movimento("-1", TipoMovimentacao.PAGAMENTO_DESPESA), 1) }
    falha("Histórico financeiro não excluível") { financeiro.excluir(1) }

    clientes.salvar(Cliente("Excluir Teste", "56789012345", 30))
    val clienteExcluir = clientes.listarTodos().last().id
    clientes.excluir(clienteExcluir)
    verificar(clientes.buscarPorId(clienteExcluir) == null, "Excluir cliente sem vínculos")
    funcionarios.salvar(Instalador("Excluir Teste", "67890123456", 30, "1".toBigDecimal(), Turno.MATUTINO))
    val funcionarioExcluir = funcionarios.listarTodos().last().id
    funcionarios.excluir(funcionarioExcluir)
    verificar(funcionarios.buscarPorId(funcionarioExcluir) == null, "Excluir funcionário sem vínculos")
    fornecedores.salvar(Fornecedor("Excluir Teste", "23456789012345"))
    val fornecedorExcluir = fornecedores.listarTodos().last().id
    produtos.salvar(CaixaDaAgua("Excluir", "Excluir", mutableListOf(1.0, 1.0, 1.0), Cor.BRANCO, Material.INOX, "Redondo", "1".toBigDecimal(), fornecedorId = fornecedorExcluir))
    val produtoExcluir = produtos.listarTodos().last().id
    fornecedores.excluir(fornecedorExcluir)
    verificar(produtos.buscarPorId(produtoExcluir)!!.fornecedorId == null, "Excluir fornecedor mantém produto com FK nula")
    produtos.excluir(produtoExcluir)
    verificar(produtos.buscarPorId(produtoExcluir) == null, "Excluir produto sem vínculos")
    clientes.listar()
    fornecedores.listar()
    funcionarios.listar()
    produtos.listar()
    servicos.listar()
    financeiro.listar()
    println("TOTAL: $verificacoes verificações aprovadas.")
}
