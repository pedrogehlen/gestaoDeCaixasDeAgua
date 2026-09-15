-- Esquema para banco vazio. Não execute sobre tabelas existentes.
BEGIN;
CREATE TABLE fornecedor (
    id SERIAL PRIMARY KEY,
    razao_social VARCHAR(100) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE
        CHECK (cnpj ~ '^[0-9]{14}$')
);

CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE
        CHECK (cpf ~ '^[0-9]{11}$'),
    idade INTEGER NOT NULL
        CHECK (idade >= 18)
);

CREATE TABLE funcionario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE
        CHECK (cpf ~ '^[0-9]{11}$'),
    idade INTEGER NOT NULL
        CHECK (idade >= 16),
    salario NUMERIC(10, 2) NOT NULL
        CHECK (salario >= 0),
    setor VARCHAR(30) NOT NULL
        CHECK (
            setor IN (
                'FINANCEIRO',
                'ADMINISTRATIVO',
                'LOGISTICA',
                'INSTALACAO'
            )
        ),
    turno VARCHAR(20) NOT NULL
        CHECK (
            turno IN (
                'MATUTINO',
                'VESPERTINO',
                'NOTURNO'
            )
        ),
    habilidade VARCHAR(30) NOT NULL
        CHECK (
            habilidade IN (
                'INSTALACAO',
                'FINANCEIRO',
                'ADMINISTRATIVO',
                'LOGISTICA'
            )
        )
);

CREATE TABLE caixa_da_agua (
    id SERIAL PRIMARY KEY,
    marca VARCHAR(60) NOT NULL,
    modelo VARCHAR(60) NOT NULL,
    dimensao FLOAT8[] NOT NULL
        CHECK (array_length(dimensao, 1) = 3),
    cor VARCHAR(30) NOT NULL,
    material VARCHAR(30) NOT NULL,
    formato VARCHAR(40) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL
        CHECK (preco >= 0),
    quantidade_estoque INTEGER NOT NULL DEFAULT 0
        CHECK (quantidade_estoque >= 0),
    fornecedor_id INTEGER,

    CONSTRAINT fk_caixa_fornecedor
        FOREIGN KEY (fornecedor_id)
        REFERENCES fornecedor(id)
        ON DELETE SET NULL
);

CREATE TABLE servico (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    caixa_id INTEGER NOT NULL,
    data_instalacao DATE NOT NULL,
    preco NUMERIC(10, 2) NOT NULL
        CHECK (preco > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO'
        CHECK (
            status IN (
                'AGENDADO',
                'CONCLUIDO',
                'CANCELADO'
            )
        ),

    CONSTRAINT fk_servico_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES cliente(id),

    CONSTRAINT fk_servico_caixa
        FOREIGN KEY (caixa_id)
        REFERENCES caixa_da_agua(id)
);

CREATE TABLE servico_funcionario (
    servico_id INTEGER NOT NULL,
    funcionario_id INTEGER NOT NULL,

    PRIMARY KEY (servico_id, funcionario_id),

    CONSTRAINT fk_sf_servico
        FOREIGN KEY (servico_id)
        REFERENCES servico(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sf_funcionario
        FOREIGN KEY (funcionario_id)
        REFERENCES funcionario(id)
);

CREATE TABLE movimentacao (
    id SERIAL PRIMARY KEY,
    valor NUMERIC(10, 2) NOT NULL
        CHECK (valor <> 0),
    pagador VARCHAR(100) NOT NULL,
    recebedor VARCHAR(100) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descricao VARCHAR(200) NOT NULL,
    tipo VARCHAR(30) NOT NULL
        CHECK (
            tipo IN (
                'VENDA_PRODUTO',
                'VENDA_SERVICO',
                'COMPRA_PRODUTO',
                'PAGAMENTO_SALARIO',
                'PAGAMENTO_DESPESA'
            )
        ),
    responsavel_id INTEGER NOT NULL,

    CONSTRAINT fk_movimentacao_responsavel
        FOREIGN KEY (responsavel_id)
        REFERENCES funcionario(id)
);

CREATE OR REPLACE VIEW saldo_caixa AS
SELECT COALESCE(SUM(valor), 0) AS saldo
FROM movimentacao;
COMMIT;
