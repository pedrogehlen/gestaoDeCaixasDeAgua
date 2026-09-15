package sistema.caixadeagua

import enumeradores.Cor
import enumeradores.Material
import produto.CaixaDaAgua
import repositorio.CRUDCaixaDAgua

fun editarCaixa() {
    val CRUDCaixaDAgua = CRUDCaixaDAgua()
    CRUDCaixaDAgua.listar()
    println("digite uma caixa que deseja alterar: ")
    val id = readln().toInt()

    println("digite o NOVO marca:")
    val marca = readln()
    println("digite o NOVO modelo:")
    val modelo = readln()
    println("digite o NOVO formato:")
    val formato = readln()
    println("digite o NOVO dimensão:")
    println("digite o NOVO largura:")
    val largura = readln().toDouble()
    println("digite o NOVO profundidade:")
    val profundidade = readln().toDouble()
    println("digite o NOVO altura:")
    val altura = readln().toDouble()
    val dimensao = mutableListOf(largura, altura, profundidade)

    println("Escolha a cor: ")
    Cor.entries.forEach { cor ->
        println("${cor.ordinal} - ${cor.name.replace("_", "")}")
    }
    println("Numero da cor")
    val cor = readln().toInt()

    println("Escolha o material: ")
    Material.entries.forEach { material ->
        println("${material.ordinal} - ${material.name.replace("_", "")}")
    }
    println("numero do material")
    val material = readln().toInt()

    println("digite o NOVO preço:")
    val preco = readln().toBigDecimal()

    CRUDCaixaDAgua.editar(
    CaixaDaAgua(
        marca = marca,
        modelo = modelo,
        formato = formato,
        dimensao = dimensao,
        preco = preco,
        cor = Cor.entries[cor],
        material = Material.entries[material]
    ),
    id
    )


}