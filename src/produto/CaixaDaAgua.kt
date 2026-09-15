package produto

import enumeradores.Cor
import enumeradores.Habilidade
import enumeradores.Material
import enumeradores.Turno
import pessoas.Instalador
import java.math.BigDecimal

class CaixaDaAgua (
    /**
     * Marca, Modelo, Dimensão(altura, largura, profundidade), enumeradores.Cor, enumeradores.Material, Formato, pessoas.Instalador, Preço, Fornecedor
     * */
    val marca : String,
    val modelo : String,
    val dimensao : MutableList<Double>,
    val cor : Cor,
    val material : Material,
    val formato : String,
    var preco : BigDecimal,
)