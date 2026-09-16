package sistema

import sistema.caixadeagua.cadastrarNovaCaixa
import sistema.caixadeagua.editarCaixa
import sistema.caixadeagua.listarCaixa
import validacao.lerInteiro

fun menuProduto() {
    do {
        println("""
            GESTÃO DE CAIXAS-D'ÁGUA
            1 - Cadastrar caixa-d'água
            2 - Editar caixa-d'água
            3 - Listar caixas-d'água
            4 - Excluir caixa-d'água
            0 - Voltar
        """.trimIndent())
        try {
            when (lerInteiro("Opção:", 0, 4)) {
                0 -> return
                1 -> cadastrarNovaCaixa()
                2 -> editarCaixa()
                3 -> listarCaixa()
                4 -> excluirCaixa()
            }
        } catch (e: Exception) { mostrarErro(e) }
    } while (true)
}
