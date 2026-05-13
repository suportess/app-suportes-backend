-- =============================================================
-- V10 — Tabela IMPORTACAO_PRODUTO
--
-- Registra o "de-para" de cada produto importado via planilha:
-- qual linha da planilha originou qual CD_PRODUTO no MV.
-- =============================================================

CREATE TABLE IF NOT EXISTS importacao_produto (
    id              BIGSERIAL    PRIMARY KEY,
    auth0_sub       VARCHAR(255) NOT NULL,
    cd_produto_mv   BIGINT       NOT NULL,
    ds_produto      VARCHAR(255) NOT NULL,
    ds_comercial    VARCHAR(255),
    cd_especie      INTEGER,
    cd_classe       INTEGER,
    cd_sub_cla      INTEGER,
    ds_sub_cla      VARCHAR(255),
    cd_unidade      VARCHAR(20),
    sn_lote         CHAR(1)      NOT NULL DEFAULT 'N',
    sn_validade     CHAR(1)      NOT NULL DEFAULT 'N',
    dt_importacao   TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_imp_sn_lote     CHECK (sn_lote    IN ('S', 'N')),
    CONSTRAINT ck_imp_sn_validade CHECK (sn_validade IN ('S', 'N'))
);

CREATE INDEX IF NOT EXISTS idx_importacao_produto_auth0_sub   ON importacao_produto (auth0_sub);
CREATE INDEX IF NOT EXISTS idx_importacao_produto_cd_produto  ON importacao_produto (cd_produto_mv);

COMMENT ON TABLE  importacao_produto             IS 'De-para: linha da planilha → CD_PRODUTO no MV';
COMMENT ON COLUMN importacao_produto.cd_produto_mv IS 'Código gerado pelo Oracle (SEQ_PRODUTO)';
COMMENT ON COLUMN importacao_produto.ds_produto    IS 'Descrição original conforme planilha';
