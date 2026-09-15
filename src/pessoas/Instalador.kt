package pessoas

import enumeradores.*
import java.math.BigDecimal

class Instalador(nome: String, cpf: String, idade: Int, salario: BigDecimal, turno: Turno, id: Int = 0)
    : Funcionario(nome, cpf, idade, salario, turno, Habilidade.INSTALACAO, Setor.INSTALACAO, id)
