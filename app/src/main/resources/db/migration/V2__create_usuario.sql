-- =============================================================
-- V2 — Tabela USUARIO
--
-- Armazena o perfil do usuário provisionado via Auth0.
-- O campo auth0_sub é o identificador único do Auth0 (sub claim).
-- =============================================================

CREATE TABLE IF NOT EXISTS usuario (
    id              BIGSERIAL    NOT NULL,
    auth0_sub       VARCHAR(128) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    nm_usuario      VARCHAR(200) NULL,
    picture         VARCHAR(500) NULL,
    dt_criacao      TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_atualizacao  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_usuario       PRIMARY KEY (id),
    CONSTRAINT uq_usuario_sub   UNIQUE (auth0_sub)
);

COMMENT ON TABLE  usuario              IS 'Usuários autenticados via Auth0';
COMMENT ON COLUMN usuario.auth0_sub    IS 'Claim "sub" do token Auth0 — identificador único global do usuário';
COMMENT ON COLUMN usuario.email        IS 'E-mail provisionado pelo Auth0';
COMMENT ON COLUMN usuario.nm_usuario   IS 'Nome completo do usuário (claim "name" do Auth0)';
COMMENT ON COLUMN usuario.picture      IS 'URL do avatar (claim "picture" do Auth0)';
