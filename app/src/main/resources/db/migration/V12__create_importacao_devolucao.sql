-- =============================================================
-- V12 — Tabela IMPORTACAO_DEVOLUCAO
-- Sessão/lote de importação de devoluções de saldo consignado
-- =============================================================

CREATE TABLE IF NOT EXISTS importacao_devolucao (
    id              BIGSERIAL     PRIMARY KEY,
    auth0_sub       VARCHAR(255)  NOT NULL,
    nm_usuario      VARCHAR(200),
    email_usuario   VARCHAR(150),
    nm_empresa      VARCHAR(150),
    dt_importacao   TIMESTAMP     NOT NULL DEFAULT NOW(),
    qt_itens        INTEGER       NOT NULL DEFAULT 0,
    qt_sucesso      INTEGER       NOT NULL DEFAULT 0,
    qt_erro         INTEGER       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_imp_dev_auth0_sub ON importacao_devolucao (auth0_sub);
CREATE INDEX IF NOT EXISTS idx_imp_dev_dt        ON importacao_devolucao (dt_importacao DESC);

COMMENT ON TABLE  importacao_devolucao             IS 'Sessão (lote) de importação de devoluções de saldo consignado';
COMMENT ON COLUMN importacao_devolucao.auth0_sub   IS 'Sub do usuário Auth0 que iniciou a importação';
COMMENT ON COLUMN importacao_devolucao.qt_itens    IS 'Total de itens processados nesta sessão';
COMMENT ON COLUMN importacao_devolucao.qt_sucesso  IS 'Itens que resultaram em devolução OK no Oracle';
COMMENT ON COLUMN importacao_devolucao.qt_erro     IS 'Itens que falharam na execução Oracle';
