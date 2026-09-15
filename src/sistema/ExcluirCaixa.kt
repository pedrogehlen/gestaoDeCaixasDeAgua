package sistema

import repositorio.CRUDCaixaDAgua
import validacao.lerInteiro

fun excluirCaixa() {
    val crud = CRUDCaixaDAgua()
    crud.listar()
    crud.excluir(lerInteiro("ID que deseja excluir:"))
    println("Caixa excluída.")
}
