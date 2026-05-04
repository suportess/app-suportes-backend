CREATE TABLE transferencia_consignado (
    id                  BIGSERIAL PRIMARY KEY,
    auth0_sub           VARCHAR(255)    NOT NULL,
    empresa_id          BIGINT          NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDENTE',

    -- Origem
    cd_multi_empresa    BIGINT          NOT NULL,
    cd_estoque          BIGINT          NOT NULL,
    ds_estoque          VARCHAR(255),
    cd_produto_dev      BIGINT          NOT NULL,
    ds_produto_dev      VARCHAR(255),
    cd_ent_pro          BIGINT          NOT NULL,
    cd_lote_dev         VARCHAR(500),
    dt_validade_dev     VARCHAR(20),
    qt_devolvida        NUMERIC(18,4)   NOT NULL,

    -- Destino
    cd_produto_ent      BIGINT          NOT NULL,
    ds_produto_ent      VARCHAR(255),
    cd_lote_ent         VARCHAR(500),
    dt_validade_ent     VARCHAR(20),
    qt_entrada          NUMERIC(18,4)   NOT NULL,

    -- Operação
    dt_devolucao        VARCHAR(20)     NOT NULL,

    -- Auditoria
    dt_criacao          TIMESTAMP       NOT NULL DEFAULT NOW(),
    dt_conclusao        TIMESTAMP
);

CREATE INDEX idx_transf_consig_auth0     ON transferencia_consignado (auth0_sub);
CREATE INDEX idx_transf_consig_empresa   ON transferencia_consignado (empresa_id);
CREATE INDEX idx_transf_consig_status    ON transferencia_consignado (status);
CREATE INDEX idx_transf_consig_criacao   ON transferencia_consignado (dt_criacao DESC);
