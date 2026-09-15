package sistema.caixadeagua

import enumeradores.Cor
import enumeradores.Material
import produto.CaixaDaAgua
import repositorio.CRUDCaixaDAgua
import repositorio.CRUDFornecedor
import validacao.*

fun lerCaixa(): CaixaDaAgua {
    val marca = lerTexto("Marca (:cancelar para voltar):", 60)
    val modelo = lerTexto("Modelo:", 60)
    val largura = lerDimensao("Largura:")
    val altura = lerDimensao("Altura:")
    val profundidade = lerDimensao("Profundidade:")
    val dimensao = mutableListOf(largura, altura, profundidade)

    Cor.entries.forEach { println("${it.ordinal} - ${it.name}") }
    val cor = lerInteiro("Número da cor:", 0, Cor.entries.lastIndex)
    Material.entries.forEach { println("${it.ordinal} - ${it.name}") }
    val material = lerInteiro("Número do material:", 0, Material.entries.lastIndex)
    val formato = lerTexto("Formato:", 40)
    val preco = lerDinheiro("Preço de venda:", true)
    val fornecedores = CRUDFornecedor()
    fornecedores.listar()
    val numero = lerInteiro("ID do fornecedor (0 - sem fornecedor):", 0)
    val fornecedorId: Int? = if (numero == 0) null else numero
    if (fornecedorId != null) require(fornecedores.buscarPorId(fornecedorId) != null) { "Fornecedor inexistente." }
    return CaixaDaAgua(marca, modelo, dimensao, Cor.entries[cor], Material.entries[material], formato, preco, fornecedorId = fornecedorId)
}

fun cadastrarNovaCaixa() {
    CRUDCaixaDAgua().salvar(lerCaixa())
    println("Caixa cadastrada com estoque zero. Registre uma compra para abastecer.")
}
