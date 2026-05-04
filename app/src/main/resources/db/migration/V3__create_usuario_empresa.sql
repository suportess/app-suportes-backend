-- =============================================================
-- V3 — Tabela USUARIO_EMPRESA + remover singleton de CONF_EMPRESA
--
-- Remove o índice singleton que limitava conf_empresa a 1 linha.
-- Cria a tabela de vínculo usuario <-> empresa.
-- =============================================================

-- Remove restrição de singleton (empresa agora é por usuário)
DROP INDEX IF EXISTS uq_conf_empresa_singleton;

-- Tabela de vínculo usuario <-> conf_empresa
CREATE TABLE IF NOT EXISTS usuario_empresa (
    id          BIGSERIAL   NOT NULL,
    usuario_id  BIGINT      NOT NULL,
    empresa_id  BIGINT      NOT NULL,
    dt_vinculo  TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_usuario_empresa       PRIMARY KEY (id),
    CONSTRAINT fk_ue_usuario            FOREIGN KEY (usuario_id) REFERENCES usuario(id)   ON DELETE CASCADE,
    CONSTRAINT fk_ue_empresa            FOREIGN KEY (empresa_id) REFERENCES conf_empresa(id) ON DELETE CASCADE,
    CONSTRAINT uq_usuario_empresa       UNIQUE (usuario_id, empresa_id)
);

COMMENT ON TABLE  usuario_empresa            IS 'Vínculo N:N entre usuário e empresa';
COMMENT ON COLUMN usuario_empresa.dt_vinculo IS 'Data em que o usuário registrou a empresa';
