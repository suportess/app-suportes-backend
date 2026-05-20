-- =============================================================
-- V16 — Tabela ITVINCULO_PRODUTO
-- Um registro por par (produto cabeça → produto filho) vinculado
-- =============================================================

CREATE TABLE IF NOT EXISTS itvinculo_produto (
    id                   BIGSERIAL     PRIMARY KEY,
    cd_sessao            BIGINT        NOT NULL REFERENCES vinculo_produto(id),
    nr_linha             INTEGER       NOT NULL,

    -- ── Produto cabeça (antigo) ───────────────────────────────────────────
    cd_produto           VARCHAR(50),
    ds_produto           VARCHAR(300),

    -- ── Produto filho (novo) ──────────────────────────────────────────────
    cd_produto_filho     VARCHAR(50),
    ds_produto_filho     VARCHAR(300),

    -- ── Vínculo SUS ───────────────────────────────────────────────────────
    cd_procedimento_sus  VARCHAR(10)
);

CREATE INDEX IF NOT EXISTS idx_itvp_sessao         ON itvinculo_produto (cd_sessao);
CREATE INDEX IF NOT EXISTS idx_itvp_produto        ON itvinculo_produto (cd_produto);
CREATE INDEX IF NOT EXISTS idx_itvp_produto_filho  ON itvinculo_produto (cd_produto_filho);

COMMENT ON TABLE  itvinculo_produto                         IS 'Item de vínculo SUS: par produto cabeça → produto filho';
COMMENT ON COLUMN itvinculo_produto.cd_sessao               IS 'FK para vinculo_produto (sessão/cabeçalho)';
COMMENT ON COLUMN itvinculo_produto.nr_linha                IS 'Número do item nesta sessão (1-based)';
COMMENT ON COLUMN itvinculo_produto.cd_produto              IS 'Código do produto cabeça (antigo, que doou o SUS)';
COMMENT ON COLUMN itvinculo_produto.cd_produto_filho        IS 'Código do produto filho (novo, que recebeu o SUS)';
COMMENT ON COLUMN itvinculo_produto.cd_procedimento_sus     IS 'Código SUS herdado pelo produto filho';
