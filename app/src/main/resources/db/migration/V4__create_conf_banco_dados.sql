-- =============================================================
-- V4 — Tabela CONF_BANCO_DADOS
--
-- Armazena a configuração do banco de dados cliente por empresa.
-- Regra: no máximo 1 registro por empresa (1:1 com conf_empresa).
-- =============================================================

CREATE TABLE IF NOT EXISTS conf_banco_dados (
    id                    BIGSERIAL    NOT NULL,
    id_empresa            BIGINT       NOT NULL,
    ds_driver             VARCHAR(20)  NOT NULL,
    ds_host               VARCHAR(255) NOT NULL,
    nr_porta              INTEGER      NOT NULL,
    nm_banco              VARCHAR(150) NOT NULL,
    nm_usuario            VARCHAR(150) NOT NULL,
    ds_senha              VARCHAR(255) NOT NULL,
    nr_max_open_conns     INTEGER      NOT NULL DEFAULT 10,
    nr_max_idle_conns     INTEGER      NOT NULL DEFAULT 5,
    nr_conn_max_lifetime  INTEGER      NOT NULL DEFAULT 90,
    nr_conn_max_idle_time INTEGER      NOT NULL DEFAULT 30,
    ds_portal_key         VARCHAR(100) NOT NULL,
    dt_criacao            TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_atualizacao        TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_conf_banco_dados         PRIMARY KEY (id),
    CONSTRAINT fk_conf_banco_empresa       FOREIGN KEY (id_empresa) REFERENCES conf_empresa(id) ON DELETE CASCADE,
    CONSTRAINT uq_conf_banco_por_empresa   UNIQUE (id_empresa)
);

COMMENT ON TABLE  conf_banco_dados          IS 'Configuração do banco de dados cliente — 1 por empresa';
COMMENT ON COLUMN conf_banco_dados.ds_driver IS 'Driver: postgres | oracle | mysql';
COMMENT ON COLUMN conf_banco_dados.ds_portal_key IS 'Chave registrada no portal (ex: postgres-prod, oracle-prod)';
