package pessoas

class Cliente(nomeCliente: String, cpfCliente: String, idadeCliente: Int, val id: Int = 0)
    : Pessoa(nomeCliente, cpfCliente, idadeCliente) {
    init {
        require(validacao.validarCpf(cpf)) { "CPF deve conter 11 dígitos." }
        require(nome.isNotBlank() && nome.length <= 100 && idade >= 18) { "Cliente precisa de nome e idade mínima de 18 anos." }
    }
}
