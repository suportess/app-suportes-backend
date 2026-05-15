-- =============================================================
-- V11 — Tabela IMPORTACAO_LOTE
--
-- Cada execução do wizard de importação de produtos gera um
-- "lote" (cabeçalho). Os produtos importados nessa sessão
-- ficam vinculados ao lote via FK em importacao_produto.
-- =============================================================

CREATE TABLE IF NOT EXISTS importacao_lote (
    id              BIGSERIAL     PRIMARY KEY,
    auth0_sub       VARCHAR(255)  NOT NULL,
    nm_usuario      VARCHAR(200),
    email_usuario   VARCHAR(150),
    nm_empresa      VARCHAR(150),
    dt_importacao   TIMESTAMP     NOT NULL DEFAULT NOW(),
    qt_produtos     INTEGER       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_importacao_lote_auth0_sub ON importacao_lote (auth0_sub);

COMMENT ON TABLE  importacao_lote                 IS 'Cabeçalho de cada sessão de importação de produtos';
COMMENT ON COLUMN importacao_lote.auth0_sub       IS 'Sub do usuário Auth0 que realizou a importação';
COMMENT ON COLUMN importacao_lote.nm_usuario      IS 'Nome do usuário no momento da importação';
COMMENT ON COLUMN importacao_lote.email_usuario   IS 'E-mail do usuário no momento da importação';
COMMENT ON COLUMN importacao_lote.nm_empresa      IS 'Nome da empresa ativa no momento da importação';
COMMENT ON COLUMN importacao_lote.qt_produtos     IS 'Quantidade de produtos cadastrados neste lote';

-- ── Adiciona FK em importacao_produto ─────────────────────────────────────────
ALTER TABLE importacao_produto
    ADD COLUMN IF NOT EXISTS cd_lote BIGINT REFERENCES importacao_lote(id);

CREATE INDEX IF NOT EXISTS idx_importacao_produto_cd_lote ON importacao_produto (cd_lote);
