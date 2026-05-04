-- =============================================================
-- V1 — Tabela CONF_EMPRESA
--
-- Armazena a configuração da empresa que opera o portal.
-- Regra: apenas um registro permitido (singleton).
--
-- APIKEY: hash de 64 chars (hex SHA-256 / SecureRandom)
--         usada para autenticar requisições ao portal.
-- =============================================================

CREATE TABLE IF NOT EXISTS conf_empresa (
    id              BIGSERIAL    NOT NULL,
    nm_empresa      VARCHAR(150) NOT NULL,
    ds_razao_social VARCHAR(200) NULL,
    nr_cnpj         VARCHAR(18)  NULL,
    ds_email        VARCHAR(150) NULL,
    nr_telefone     VARCHAR(20)  NULL,
    apikey          VARCHAR(64)  NOT NULL,
    sn_ativo        VARCHAR(1)   NOT NULL DEFAULT 'S',
    dt_criacao      TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_atualizacao  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_conf_empresa   PRIMARY KEY (id),
    CONSTRAINT ck_conf_sn_ativo  CHECK (sn_ativo IN ('S', 'N'))
);

-- Garante no banco que só pode existir uma linha (singleton)
CREATE UNIQUE INDEX IF NOT EXISTS uq_conf_empresa_singleton ON conf_empresa ((true));

COMMENT ON TABLE  conf_empresa           IS 'Configuração singleton da empresa operadora do portal';
COMMENT ON COLUMN conf_empresa.apikey    IS 'Hash hexadecimal de 64 chars gerado pelo BFF para autenticar o portal';
COMMENT ON COLUMN conf_empresa.sn_ativo  IS 'S = ativo / N = inativo';
