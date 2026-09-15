package pessoas

class Fornecedor(val razaoSocial: String, val cnpj: String, val id: Int = 0) {
    init {
        require(razaoSocial.isNotBlank() && razaoSocial.length <= 100) { "Informe a razão social (até 100 caracteres)." }
        require(validacao.validarCnpj(cnpj)) { "CNPJ deve conter 14 dígitos." }
    }
}
