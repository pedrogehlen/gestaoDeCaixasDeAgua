-- Atualização única do esquema original, inspecionado em 15/09/2026.
-- Preserva os produtos. Interrompe se houver movimentos antigos sem os dados exigidos.
BEGIN;
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM movimentacao) THEN
        RAISE EXCEPTION 'Há movimentos antigos. Preencha os dados reais de pagador, recebedor, tipo e responsável antes de migrar.';
    END IF;
END $$;
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

ALTER TABLE caixa_da_agua
    ALTER COLUMN preco TYPE NUMERIC(10,2) USING preco::NUMERIC(10,2),
    ALTER COLUMN preco SET NOT NULL,
    ALTER COLUMN marca SET NOT NULL,
    ALTER COLUMN modelo SET NOT NULL,
    ALTER COLUMN dimensao SET NOT NULL,
    ALTER COLUMN cor SET NOT NULL,
    ALTER COLUMN material SET NOT NULL,
    ALTER COLUMN formato SET NOT NULL,
    ADD COLUMN quantidade_estoque INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
    ADD COLUMN fornecedor_id INTEGER REFERENCES fornecedor(id) ON DELETE SET NULL,
    ADD CONSTRAINT caixa_preco_valido CHECK (preco >= 0),
    ADD CONSTRAINT caixa_dimensao_valida CHECK (array_length(dimensao, 1) = 3);

ALTER TABLE movimentacao
    ALTER COLUMN valor TYPE NUMERIC(10,2) USING valor::NUMERIC(10,2),
    ALTER COLUMN valor SET NOT NULL,
    ALTER COLUMN descricao SET NOT NULL,
    ADD COLUMN pagador VARCHAR(100) NOT NULL,
    ADD COLUMN recebedor VARCHAR(100) NOT NULL,
    ADD COLUMN data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('VENDA_PRODUTO', 'VENDA_SERVICO', 'COMPRA_PRODUTO', 'PAGAMENTO_SALARIO', 'PAGAMENTO_DESPESA')),
    ADD COLUMN responsavel_id INTEGER NOT NULL REFERENCES funcionario(id),
    ADD CONSTRAINT movimentacao_valor_valido CHECK (valor <> 0);
-- data_movimentacao é mantida como coluna antiga opcional, sem uso pelo programa.
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

CREATE OR REPLACE VIEW saldo_caixa AS
SELECT COALESCE(SUM(valor), 0) AS saldo FROM movimentacao;
COMMIT;
