package sistema.caixadeagua
import enumeradores.Cor
import enumeradores.Material
import produto.CaixaDaAgua
import repositorio.CRUDCaixaDAgua

fun cadastrarNovaCaixa(){
    println("Digite a marca: ")
    val marca = readln()

    println("Digite o modelo: ")
    val modelo = readln()

    println("Digite a largura: ")
    val largura = readln().toDouble()
    println("Digite a altura: ")
    val altura = readln().toDouble()
    println("Digite a profundidade: ")
    val profundidade = readln().toDouble()
    //A dimensao é a combinação das 3 variaveis a cima
    val dimensao = mutableListOf<Double>(largura, altura, profundidade)

    println("Escolha a cor: ")
    Cor.entries.forEach { cor ->
        println("${cor.ordinal} - ${cor.name.replace("_","")}")
    }
    println("Numero da cor")
    val cor = readln().toInt()

    println("Escolha o material: ")
    Material.entries.forEach { material ->
        println("${material.ordinal} - ${material.name.replace("_", "")}")
    }
    println("numero do material")
    val material = readln().toInt()

    println("Escolha o formato: ")
    val formato = readln()

    println("Qual é o preco: ")
    val preco = readln().toBigDecimal()

    println()

    val conexao = CRUDCaixaDAgua()//cria a variavel de conexao com o banoc
    conexao.salvar(
        CaixaDaAgua(
            marca = marca,
            modelo = modelo,
            dimensao = dimensao,
            cor = Cor.entries[cor],
            material = Material.entries[material],
            formato = formato,
            preco = preco
        )
    )
}