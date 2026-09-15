package sistema

import enumeradores.*
import pessoas.*
import repositorio.*
import validacao.*

fun lerCliente(): Cliente {
    val nome = lerTexto("Nome (:cancelar para voltar):")
    val cpf = lerTexto("CPF (11 dígitos):")
    require(validarCpf(cpf)) { "CPF inválido. Cadastro cancelado." }
    val idade = lerInteiro("Idade (mínimo 18):", 18, 130)
    return Cliente(nome, cpf, idade)
}

fun lerFornecedor(): Fornecedor {
    val nome = lerTexto("Razão social (:cancelar para voltar):")
    val cnpj = lerTexto("CNPJ (14 dígitos):")
    require(validarCnpj(cnpj)) { "CNPJ inválido. Cadastro cancelado." }
    return Fornecedor(nome, cnpj)
}

fun lerFuncionario(): Funcionario {
    val nome = lerTexto("Nome (:cancelar para voltar):")
    val cpf = lerTexto("CPF (11 dígitos):")
    require(validarCpf(cpf)) { "CPF inválido. Cadastro cancelado." }
    val idade = lerInteiro("Idade (mínimo 16):", 16, 130)
    val salario = lerDinheiro("Salário:", true)
    Turno.entries.forEach { println("${it.ordinal} - ${it.name}") }
    val turno = Turno.entries[lerInteiro("Turno:", 0, Turno.entries.lastIndex)]
    Setor.entries.forEach { println("${it.ordinal} - ${it.name}") }
    val setor = Setor.entries[lerInteiro("Setor:", 0, Setor.entries.lastIndex)]
    if (setor == Setor.INSTALACAO) return Instalador(nome, cpf, idade, salario, turno)
    Habilidade.entries.forEach { println("${it.ordinal} - ${it.name}") }
    val habilidade = Habilidade.entries[lerInteiro("Habilidade:", 0, Habilidade.entries.lastIndex)]
    return Funcionario(nome, cpf, idade, salario, turno, habilidade, setor)
}

fun menuPessoas() {
    val clientes = CRUDCliente()
    val fornecedores = CRUDFornecedor()
    val funcionarios = CRUDFuncionario()
    do {
        println("""
            1 - Cadastrar cliente
            2 - Listar clientes
            3 - Cadastrar fornecedor
            4 - Listar fornecedores
            5 - Cadastrar funcionário
            6 - Listar funcionários
            7 - Editar cliente
            8 - Excluir cliente
            9 - Editar fornecedor
            10 - Excluir fornecedor
            11 - Editar funcionário
            12 - Excluir funcionário
            0 - Voltar
        """.trimIndent())
        try {
            when (lerInteiro("Opção:", 0, 12)) {
                0 -> return
                1 -> { clientes.salvar(lerCliente()); println("Cliente cadastrado.") }
                2 -> clientes.listar()
                3 -> { fornecedores.salvar(lerFornecedor()); println("Fornecedor cadastrado.") }
                4 -> fornecedores.listar()
                5 -> { funcionarios.salvar(lerFuncionario()); println("Funcionário cadastrado.") }
                6 -> funcionarios.listar()
                7 -> {
                    clientes.listar()
                    val id = lerInteiro("ID:")
                    require(clientes.buscarPorId(id) != null) { "Cliente inexistente." }
                    clientes.editar(lerCliente(), id)
                    println("Cliente atualizado.")
                }
                8 -> { clientes.listar(); clientes.excluir(lerInteiro("ID para excluir:")); println("Cliente excluído.") }
                9 -> {
                    fornecedores.listar()
                    val id = lerInteiro("ID:")
                    require(fornecedores.buscarPorId(id) != null) { "Fornecedor inexistente." }
                    fornecedores.editar(lerFornecedor(), id)
                    println("Fornecedor atualizado.")
                }
                10 -> { fornecedores.listar(); fornecedores.excluir(lerInteiro("ID para excluir:")); println("Fornecedor excluído.") }
                11 -> {
                    funcionarios.listar()
                    val id = lerInteiro("ID:")
                    require(funcionarios.buscarPorId(id) != null) { "Funcionário inexistente." }
                    funcionarios.editar(lerFuncionario(), id)
                    println("Funcionário atualizado.")
                }
                12 -> { funcionarios.listar(); funcionarios.excluir(lerInteiro("ID para excluir:")); println("Funcionário excluído.") }
            }
        } catch (e: Exception) { mostrarErro(e) }
    } while (true)
}

fun escolherCliente(): Cliente {
    val crud = CRUDCliente()
    crud.listar()
    return crud.buscarPorId(lerInteiro("ID do cliente:")) ?: throw IllegalArgumentException("Cliente inexistente.")
}

fun escolherFornecedor(): Fornecedor {
    val crud = CRUDFornecedor()
    crud.listar()
    return crud.buscarPorId(lerInteiro("ID do fornecedor:")) ?: throw IllegalArgumentException("Fornecedor inexistente.")
}

fun escolherFuncionario(mensagem: String = "ID do funcionário responsável:"): Funcionario {
    val crud = CRUDFuncionario()
    crud.listar()
    return crud.buscarPorId(lerInteiro(mensagem)) ?: throw IllegalArgumentException("Funcionário inexistente.")
}
