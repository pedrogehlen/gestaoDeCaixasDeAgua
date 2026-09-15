"""Verificações de console sem frameworks; usa somente o banco separado de testes."""
from pathlib import Path
import os
import subprocess
from datetime import date, timedelta

ROOT = Path(__file__).resolve().parent.parent
JAVA = str(Path.home() / ".jdks/ms-21.0.10/bin/java.exe")
# O usuário do processo de verificação pode ser diferente do dono do projeto.
JAVA = r"C:\Users\Pedro Luiz\.jdks\ms-21.0.10\bin\java.exe"
PSQL = r"C:\Program Files\PostgreSQL\18\bin\psql.exe"
DRIVER = r"D:\drivePostgres\postgresql-42.7.13.jar"
DB = "revisao1_verificacao_20260915_1835"
env = dict(os.environ, PGPASSWORD="masterkey")
log = []

def sql(query, database=DB):
    result = subprocess.run([PSQL, "-X", "-h", "localhost", "-U", "postgres", "-d", database,
                             "-v", "ON_ERROR_STOP=1", "-At", "-c", query],
                            capture_output=True, text=True, encoding="utf-8", env=env, check=True)
    return result.stdout.strip()

def caso(nome, entrada, esperado, principal=False):
    jar = ROOT / (".execucao/aplicacao.jar" if principal else "verificacao/menus.jar")
    result = subprocess.run(
        [JAVA, "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
         "-cp", f"{jar};{DRIVER}", "MainKt"],
        input=entrada, capture_output=True, text=True, encoding="utf-8", timeout=25)
    assert result.returncode == 0, (nome, result.stderr)
    for trecho in esperado:
        assert trecho in result.stdout, (nome, trecho, result.stdout)
    assert "Exception" not in result.stderr, (nome, result.stderr)
    log.append(f"OK: {nome}\n{result.stdout}")

caso("Menu principal inválido e saída", "abc\n4\n", ["Opção inválida", "Adeus"])
caso("Leitura no banco principal preservado", "2\n7\n6\n0\n4\n", ["Prudence", "Saldo: R$ 0"], True)
caso("CPF inválido cancela cadastro", "5\n5\nNome Teste\n123\n0\n4\n", ["CPF inválido", "Cadastro cancelado"])
caso("Salário negativo e cancelamento", "5\n5\nNome Teste\n78901234567\n20\n-10\n:cancelar\n0\n4\n", ["sem sinal negativo", "Operação cancelada"])
caso("CPF cliente inválido", "5\n1\nTeste\n123\n0\n4\n", ["CPF inválido"])
caso("CNPJ inválido", "5\n3\nTeste\n123\n0\n4\n", ["CNPJ inválido"])
caso("Texto vazio e cancelar", "5\n1\n   \n:cancelar\n0\n4\n", ["Preencha com 1", "Operação cancelada"])
caso("Inteiro inválido", "7\nabc\n0\n4\n", ["Informe um inteiro"])
caso("Dimensão e cor inválidas", "0\nTeste\nTeste\n0\nabc\n1\n1\n1\n99\n:cancelar\n4\n", ["dimensão positiva", "Informe um inteiro", "Operação cancelada"])
caso("Cliente inexistente", "7\n1\n999999\n0\n4\n", ["Cliente inexistente"])
caso("Fornecedor inexistente", "7\n2\n999999\n0\n4\n", ["Fornecedor inexistente"])
caso("Produto inexistente", "7\n1\n1\n999999\n0\n4\n", ["Produto inexistente"])
caso("Estoque insuficiente", "7\n1\n1\n1\n999999\n0\n4\n", ["Estoque insuficiente"])
caso("Responsável inexistente", "7\n4\nLoja\n10\nTeste\n999999\n0\n4\n", ["Funcionário inexistente"])
caso("Dinheiro malformado e negativo", "7\n4\nLoja\nabc\n-1\n1.234\n:cancelar\n0\n4\n", ["Formato monetário inválido", "sem sinal negativo", "duas casas decimais"])
caso("Serviço inexistente", "6\n3\n999999\n0\n4\n", ["Serviço inexistente"])
assert sql("SELECT count(*) FROM movimentacao") == "5"
assert sql("SELECT quantidade_estoque FROM caixa_da_agua WHERE id=1") == "8"

caso("Compra pelo menu", "7\n2\n1\n1\n2\n100,00\n1\n0\n4\n", ["Compra registrada"])
caso("Venda pelo menu", "7\n1\n1\n1\n2\n1\n0\n4\n", ["Venda registrada: R$ 160.00"])
caso("Salário pelo menu", "7\n3\n2\n1\n0\n4\n", ["Salário pago"])
caso("Despesa pelo menu", "7\n4\nLoja Teste\n25\nConta Teste\n1\n0\n4\n", ["Despesa registrada"])
data = (date.today() + timedelta(days=1)).isoformat()
caso("Agendamento com N:N pelo menu", f"6\n1\n1\n1\n{data}\n300\n2\n1\n3\n0\n0\n4\n", ["Serviço agendado"])
servico = sql("SELECT max(id) FROM servico")
caso("Conclusão pelo menu", f"6\n3\n{servico}\n1\n0\n4\n", ["Serviço concluído e receita registrada"])
caso("Conclusão repetida pelo menu", f"6\n3\n{servico}\n0\n4\n", ["Serviço já finalizado"])
caso("Listagens e saldo pelo menu", "5\n2\n4\n6\n0\n6\n2\n0\n7\n5\n6\n0\n4\n",
     ["Cliente Editado", "Fornecedor Editado", "Instalador Teste", "CONCLUIDO", "Saldo: R$ -30.00"])
assert sql("SELECT saldo FROM saldo_caixa") == "-30.00"
assert sql("SELECT quantidade_estoque FROM caixa_da_agua WHERE id=1") == "8"
assert sql("SELECT count(*) FROM movimentacao") == "10"
caso("Data inválida e cancelamento", "6\n1\n1\n1\n2000-01-01\ntexto\n:cancelar\n0\n4\n", ["a partir de hoje", "Formato inválido"])
caso("Pessoa sem habilidade para instalar", f"6\n1\n1\n1\n{data}\n50\n1\n0\n4\n", ["funcionário de instalação"])

caso("Cadastrar cliente pelo menu", "5\n1\nCliente Console\n78901234567\n22\n0\n4\n", ["Cliente cadastrado"])
cliente = sql("SELECT id FROM cliente WHERE cpf='78901234567'")
caso("Editar cliente pelo menu", f"5\n7\n{cliente}\nCliente Console Editado\n78901234567\n23\n0\n4\n", ["Cliente atualizado"])
caso("Excluir cliente pelo menu", f"5\n8\n{cliente}\n0\n4\n", ["Cliente excluído"])
caso("Cadastrar fornecedor pelo menu", "5\n3\nFornecedor Console\n34567890123456\n0\n4\n", ["Fornecedor cadastrado"])
fornecedor = sql("SELECT id FROM fornecedor WHERE cnpj='34567890123456'")
caso("Editar fornecedor pelo menu", f"5\n9\n{fornecedor}\nFornecedor Console Editado\n34567890123456\n0\n4\n", ["Fornecedor atualizado"])
caso("Excluir fornecedor pelo menu", f"5\n10\n{fornecedor}\n0\n4\n", ["Fornecedor excluído"])
caso("Cadastrar funcionário pelo menu", "5\n5\nFuncionario Console\n89012345678\n20\n50\n0\n2\n3\n0\n4\n", ["Funcionário cadastrado"])
funcionario = sql("SELECT id FROM funcionario WHERE cpf='89012345678'")
caso("Editar funcionário para instalador", f"5\n11\n{funcionario}\nInstalador Console\n89012345678\n21\n60\n1\n3\n0\n4\n", ["Funcionário atualizado"])
assert sql(f"SELECT setor FROM funcionario WHERE id={funcionario}") == "INSTALACAO"
caso("Excluir funcionário pelo menu", f"5\n12\n{funcionario}\n0\n4\n", ["Funcionário excluído"])
caso("Cadastrar produto pelo menu", "0\nMarca Console\nModelo Console\n1\n2\n3\n0\n0\nRedondo\n10\n0\n4\n", ["Caixa cadastrada"])
produto = sql("SELECT max(id) FROM caixa_da_agua")
caso("Editar produto pelo menu", f"1\n{produto}\nMarca Nova\nModelo Novo\n3\n2\n1\n1\n1\nQuadrado\n20\n0\n4\n", ["Caixa atualizada"])
caso("Excluir produto pelo menu", f"3\n{produto}\n4\n", ["Caixa excluída"])
caso("Cancelar serviço pelo menu", f"6\n1\n1\n1\n{data}\n50\n2\n0\n0\n4\n", ["Serviço agendado"])
servico = sql("SELECT max(id) FROM servico")
caso("Cancelar agendamento", f"6\n4\n{servico}\n0\n4\n", ["Serviço cancelado"])
assert sql("SELECT saldo FROM saldo_caixa") == "-30.00"
assert sql("SELECT count(*) FROM caixa_da_agua", "caixaDaAgua") == "2"
assert sql("SELECT count(*) FROM movimentacao", "caixaDaAgua") == "0"
resultado = f"TOTAL: {len(log)} cenários de console aprovados."
(ROOT / "verificacao/resultado-menus.txt").write_text("\n\n".join(log) + "\n" + resultado, encoding="utf-8")
print(resultado)
