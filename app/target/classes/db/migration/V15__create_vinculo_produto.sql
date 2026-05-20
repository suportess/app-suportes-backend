-- =============================================================
-- V15 — Tabela VINCULO_PRODUTO
-- Sessão (cabeçalho) de um vínculo SUS gerado por transferência
-- =============================================================

CREATE TABLE IF NOT EXISTS vinculo_produto (
    id              BIGSERIAL     PRIMARY KEY,
    auth0_sub       VARCHAR(255)  NOT NULL,
    nm_usuario      VARCHAR(200),
    email_usuario   VARCHAR(150),
    nm_empresa      VARCHAR(150),
    dt_vinculo      TIMESTAMP     NOT NULL DEFAULT NOW(),
    qt_vinculos     INTEGER       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_vinc_prod_auth0_sub ON vinculo_produto (auth0_sub);
CREATE INDEX IF NOT EXISTS idx_vinc_prod_dt        ON vinculo_produto (dt_vinculo DESC);

COMMENT ON TABLE  vinculo_produto               IS 'Sessão de vínculo de CD_PROCEDIMENTO_SUS gerado por transferência de produto';
COMMENT ON COLUMN vinculo_produto.auth0_sub      IS 'Sub do usuário Auth0 que executou a transferência';
COMMENT ON COLUMN vinculo_produto.qt_vinculos    IS 'Quantidade de itens vinculados nesta sessão';
