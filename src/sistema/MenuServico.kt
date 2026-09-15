package sistema

import enumeradores.*
import financeiro.*
import produto.Servico
import repositorio.*
import validacao.*

fun agendarServico() {
    val cliente = escolherCliente()
    val produto = escolherProduto()
    val data = lerData("Data da instalação (AAAA-MM-DD):")
    val preco = lerDinheiro("Preço do serviço:")
    val equipe = mutableListOf<Int>()
    do {
        val funcionario = escolherFuncionario("ID do instalador:")
        require(funcionario.setor == Setor.INSTALACAO && funcionario.habilidade == Habilidade.INSTALACAO) { "Escolha um funcionário de instalação." }
        if (funcionario.id !in equipe) equipe.add(funcionario.id)
    } while (lerInteiro("Adicionar outro instalador? 1 - Sim / 0 - Não", 0, 1) == 1)
    CRUDServico().salvar(Servico(cliente.id, produto.id, data, preco, funcionarios = equipe))
    println("Serviço agendado.")
}

fun concluirServico() {
    val crud = CRUDServico()
    crud.listar()
    val servico = crud.buscarPorId(lerInteiro("ID do serviço:")) ?: throw IllegalArgumentException("Serviço inexistente.")
    require(servico.status == StatusServico.AGENDADO) { "Serviço já finalizado." }
    val cliente = CRUDCliente().buscarPorId(servico.clienteId) ?: throw IllegalArgumentException("Cliente inexistente.")
    val responsavel = escolherFuncionario()
    val movimento = Movimentacao(Caixa().receita(servico.preco), cliente.nome, "Empresa",
        "Conclusão do serviço ${servico.id}", TipoMovimentacao.VENDA_SERVICO, responsavel.id)
    CRUDMovimentacao().concluirServico(servico.id, movimento)
    println("Serviço concluído e receita registrada.")
}

fun menuServico() {
    do {
        println("""
            1 - Agendar serviço
            2 - Listar serviços
            3 - Concluir serviço
            4 - Cancelar serviço
            0 - Voltar
        """.trimIndent())
        try {
            when (lerInteiro("Opção:", 0, 4)) {
                0 -> return
                1 -> agendarServico()
                2 -> CRUDServico().listar()
                3 -> concluirServico()
                4 -> {
                    val crud = CRUDServico()
                    crud.listar()
                    crud.cancelar(lerInteiro("ID do serviço:"))
                    println("Serviço cancelado.")
                }
            }
        } catch (e: Exception) { mostrarErro(e) }
    } while (true)
}
