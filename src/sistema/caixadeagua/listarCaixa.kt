package sistema.caixadeagua

import repositorio.CRUDCaixaDAgua

fun listarCaixa(){
    val CRUDCaixaDAgua = CRUDCaixaDAgua()
    CRUDCaixaDAgua.listar()

    //var caixaDaAgua : List<CaixaDaAgua> = listOf()

   // listaDeTeste.forEach { c ->
     //   println("-------------------")
       // println("Modelo: ${c.modelo}")
       // println("Modelo: ${c.marca}")
       // println("Modelo: ${c.dimensao}")
       // println("Modelo: ${c.cor}")
       // println("Modelo: ${c.formato}")
       // println("Modelo: ${c.material}")
       // println("Modelo: ${formatador.format(c.preco)}")

    //}
}