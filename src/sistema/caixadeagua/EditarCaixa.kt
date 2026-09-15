package sistema.caixadeagua

import repositorio.CRUDCaixaDAgua
import validacao.lerInteiro

fun editarCaixa() {
    val crud = CRUDCaixaDAgua()
    crud.listar()
    val id = lerInteiro("ID da caixa que deseja alterar:")
    require(crud.buscarPorId(id) != null) { "Produto inexistente." }
    crud.editar(lerCaixa(), id)
    println("Caixa atualizada.")
}
