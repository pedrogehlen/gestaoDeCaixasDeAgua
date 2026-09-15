package sistema

import enumeradores.TipoMovimentacao
import financeiro.Caixa
import financeiro.Movimentacao
import pessoas.Pessoa
import produto.CaixaDaAgua
import repositorio.*
import validacao.*

fun escolherProduto(): CaixaDaAgua {
    val crud = CRUDCaixaDAgua()
    crud.listar()
    return crud.buscarPorId(lerInteiro("ID da caixa-d'água:")) ?: throw IllegalArgumentException("Produto inexistente.")
}

fun registrarVenda() {
    val cliente = escolherCliente()
    val produto = escolherProduto()
    val quantidade = lerInteiro("Quantidade:")
    require(produto.removerEstoque(quantidade)) { "Estoque insuficiente." }
    val total = Caixa().receita(produto.preco.multiply(quantidade.toBigDecimal()))
    val responsavel = escolherFuncionario()
    // A movimentação só é criada após validar as escolhas.
    val movimento = Movimentacao(total, cliente.nome, "Empresa", "Venda do produto ${produto.id}, quantidade $quantidade",
        TipoMovimentacao.VENDA_PRODUTO, responsavel.id)
    CRUDMovimentacao().registrarProduto(movimento, produto.id, quantidade)
    println("Venda registrada: R$ $total")
}

fun registrarCompra() {
    val fornecedor = escolherFornecedor()
    val produto = escolherProduto()
    val quantidade = lerInteiro("Quantidade:")
    produto.adicionarEstoque(quantidade)
    val valor = lerDinheiro("Valor TOTAL da compra:")
    val responsavel = escolherFuncionario()
    val movimento = Movimentacao(Caixa().despesa(valor), "Empresa", fornecedor.razaoSocial,
        "Compra do produto ${produto.id}, quantidade $quantidade", TipoMovimentacao.COMPRA_PRODUTO, responsavel.id)
    CRUDMovimentacao().registrarProduto(movimento, produto.id, quantidade, fornecedor.id)
    println("Compra registrada.")
}

fun pagarSalario() {
    val funcionario = escolherFuncionario("ID do funcionário que receberá:")
    val responsavel = escolherFuncionario()
    // Polimorfismo real: a variável Pessoa executa a versão de Funcionario.
    val pessoa: Pessoa = funcionario
    val valor = pessoa.receberConta(Caixa().receita(funcionario.salario))
    CRUDMovimentacao().salvar(Movimentacao(valor, "Empresa", funcionario.nome, "Pagamento de salário",
        TipoMovimentacao.PAGAMENTO_SALARIO, responsavel.id))
    println("Salário pago.")
}

fun pagarDespesa() {
    val recebedor = lerTexto("Quem receberá?")
    val valor = lerDinheiro("Valor da despesa:")
    val descricao = lerTexto("Descrição:", 200)
    val responsavel = escolherFuncionario()
    CRUDMovimentacao().salvar(Movimentacao(Caixa().despesa(valor), "Empresa", recebedor, descricao,
        TipoMovimentacao.PAGAMENTO_DESPESA, responsavel.id))
    println("Despesa registrada.")
}

fun menuFinanceiro() {
    do {
        println("""
            1 - Registrar venda
            2 - Registrar compra
            3 - Pagar salário
            4 - Pagar despesa
            5 - Listar movimentações
            6 - Mostrar saldo
            0 - Voltar
        """.trimIndent())
        try {
            when (lerInteiro("Opção:", 0, 6)) {
                0 -> return
                1 -> registrarVenda()
                2 -> registrarCompra()
                3 -> pagarSalario()
                4 -> pagarDespesa()
                5 -> CRUDMovimentacao().listar()
                6 -> println("Saldo: R$ ${CRUDMovimentacao().calcularSaldo()}")
            }
        } catch (e: Exception) { mostrarErro(e) }
    } while (true)
}
