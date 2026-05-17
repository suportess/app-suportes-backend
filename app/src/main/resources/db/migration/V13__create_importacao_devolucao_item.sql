-- =============================================================
-- V13 — Tabela IMPORTACAO_DEVOLUCAO_ITEM
-- Um registro por linha da planilha processada
-- =============================================================

CREATE TABLE IF NOT EXISTS importacao_devolucao_item (
    id                   BIGSERIAL      PRIMARY KEY,
    cd_sessao            BIGINT         NOT NULL REFERENCES importacao_devolucao(id),
    nr_linha             INTEGER        NOT NULL,

    -- ── Dados visíveis na tela (planilha + lookup MV) ─────────────────────
    cd_produto           VARCHAR(50),
    ds_produto           VARCHAR(300),
    cd_estoque           VARCHAR(50),
    ds_estoque           VARCHAR(200),
    cd_fornecedor        VARCHAR(50),
    nm_fornecedor        VARCHAR(300),
    cd_unidade           VARCHAR(50),
    ds_unidade           VARCHAR(100),
    qt_devolvida         NUMERIC(18,4),
    tp_movimento         VARCHAR(30),
    cd_mot_dev           INTEGER,
    tp_devolucao         VARCHAR(1)     DEFAULT 'C',

    -- ── Saldos MV consultados e exibidos na tela ─────────────────────────
    vl_saldo_estoque     NUMERIC(18,4),
    vl_saldo_ficha       NUMERIC(18,4),
    vl_saldo_consig_forn NUMERIC(18,4),
    vl_saldo_lotes       NUMERIC(18,4),

    -- ── Resultado Oracle (preenchido após execução do bloco) ─────────────
    st_execucao          VARCHAR(10),           -- 'ok' | 'erro' | 'pendente'
    qt_total_devolvida   NUMERIC(18,4),
    qt_nao_atendida      NUMERIC(18,4),
    ds_erro              VARCHAR(2000),
    json_resultado       TEXT,                  -- JSON completo retornado pelo bloco
    dt_execucao          TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_imp_dev_item_sessao  ON importacao_devolucao_item (cd_sessao);
CREATE INDEX IF NOT EXISTS idx_imp_dev_item_produto ON importacao_devolucao_item (cd_produto);

COMMENT ON TABLE  importacao_devolucao_item                    IS 'Linha individual de uma sessão de importação de devolução';
COMMENT ON COLUMN importacao_devolucao_item.cd_sessao          IS 'FK para importacao_devolucao (sessão)';
COMMENT ON COLUMN importacao_devolucao_item.nr_linha           IS 'Número da linha na planilha (1-based)';
COMMENT ON COLUMN importacao_devolucao_item.st_execucao        IS 'ok | erro | pendente';
COMMENT ON COLUMN importacao_devolucao_item.json_resultado     IS 'JSON completo retornado pelo bloco Oracle devolver-saldo-consig';
COMMENT ON COLUMN importacao_devolucao_item.qt_nao_atendida    IS 'Quantidade solicitada que nao foi devolvida (devolucao parcial)';
