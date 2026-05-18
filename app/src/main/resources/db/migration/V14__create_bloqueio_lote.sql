-- =============================================================
-- V14 — Tabelas BLOQUEIO_LOTE e BLOQUEIO_ITEM
--
-- Cada execução do wizard de bloqueio/desbloqueio de produtos
-- gera um "lote" (cabeçalho). Os itens processados ficam
-- vinculados ao lote via FK em bloqueio_item.
-- =============================================================

CREATE TABLE IF NOT EXISTS bloqueio_lote (
    id              BIGSERIAL     PRIMARY KEY,
    auth0_sub       VARCHAR(255)  NOT NULL,
    nm_usuario      VARCHAR(200),
    email_usuario   VARCHAR(150),
    nm_empresa      VARCHAR(150),
    dt_bloqueio     TIMESTAMP     NOT NULL DEFAULT NOW(),
    qt_itens        INTEGER       NOT NULL DEFAULT 0,
    qt_sucesso      INTEGER       NOT NULL DEFAULT 0,
    qt_erro         INTEGER       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_bloqueio_lote_auth0_sub ON bloqueio_lote (auth0_sub);

CREATE TABLE IF NOT EXISTS bloqueio_item (
    id              BIGSERIAL     PRIMARY KEY,
    cd_lote         BIGINT        NOT NULL REFERENCES bloqueio_lote(id) ON DELETE CASCADE,
    cd_produto      BIGINT        NOT NULL,
    acao            VARCHAR(15)   NOT NULL,
    sn_sucesso      BOOLEAN       NOT NULL DEFAULT FALSE,
    ds_erro         TEXT,
    dt_operacao     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_bloqueio_item_cd_lote ON bloqueio_item (cd_lote);

COMMENT ON TABLE  bloqueio_lote              IS 'Cabeçalho de cada sessão de bloqueio/desbloqueio de produtos';
COMMENT ON COLUMN bloqueio_lote.auth0_sub    IS 'Sub do usuário Auth0 que realizou a operação';
COMMENT ON COLUMN bloqueio_lote.qt_itens     IS 'Total de itens processados';
COMMENT ON COLUMN bloqueio_lote.qt_sucesso   IS 'Itens executados com sucesso';
COMMENT ON COLUMN bloqueio_lote.qt_erro      IS 'Itens com erro';

COMMENT ON TABLE  bloqueio_item              IS 'Cada produto bloqueado/desbloqueado em uma sessão';
COMMENT ON COLUMN bloqueio_item.acao         IS 'BLOQUEIO ou DESBLOQUEIO';
COMMENT ON COLUMN bloqueio_item.sn_sucesso   IS 'TRUE se o portal retornou sucesso';
COMMENT ON COLUMN bloqueio_item.ds_erro      IS 'Mensagem de erro quando sn_sucesso = FALSE';
