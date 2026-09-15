package sistema

import repositorio.CRUDCaixaDAgua

fun excluirCaixa(){
    val CRUDCaixaDAgua = CRUDCaixaDAgua()
    CRUDCaixaDAgua.listar()

    println("Digite o ID que deseja excluir:")
    val id = readln().toInt()

    CRUDCaixaDAgua.excluir(id)
}