-- Cabeçalho da operação de baixa de consignados
CREATE TABLE operacao_baixa_consignado (
    id               BIGSERIAL    PRIMARY KEY,
    auth0_sub        VARCHAR(255) NOT NULL,
    empresa_id       BIGINT       NOT NULL,
    cd_multi_empresa BIGINT       NOT NULL,
    cd_estoque       BIGINT       NOT NULL,
    ds_estoque       VARCHAR(255),
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
    dt_criacao       TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_conclusao     TIMESTAMP
);

-- Produtos de origem selecionados na Etapa 1
CREATE TABLE operacao_baixa_consignado_origem (
    id               BIGSERIAL    PRIMARY KEY,
    operacao_id      BIGINT       NOT NULL REFERENCES operacao_baixa_consignado(id) ON DELETE CASCADE,
    cd_produto       BIGINT       NOT NULL,
    ds_produto       VARCHAR(255),
    qt_estoque_atual NUMERIC(18,4)
);

-- Produtos de destino distribuídos na Etapa 2
CREATE TABLE operacao_baixa_consignado_item (
    id          BIGSERIAL   PRIMARY KEY,
    operacao_id BIGINT      NOT NULL REFERENCES operacao_baixa_consignado(id) ON DELETE CASCADE,
    cd_produto  BIGINT      NOT NULL,
    ds_produto  VARCHAR(255),
    sn_lote     CHAR(1)     NOT NULL DEFAULT 'N',
    sn_validade CHAR(1)     NOT NULL DEFAULT 'N'
);

-- Linhas de fornecedor por item de destino
CREATE TABLE operacao_baixa_consignado_linha (
    id            BIGSERIAL    PRIMARY KEY,
    item_id       BIGINT       NOT NULL REFERENCES operacao_baixa_consignado_item(id) ON DELETE CASCADE,
    cd_fornecedor BIGINT       NOT NULL,
    nm_fornecedor VARCHAR(255),
    quantidade    NUMERIC(18,4) NOT NULL,
    lote          VARCHAR(500),
    validade      VARCHAR(20)
);

CREATE INDEX idx_op_baixa_consig_empresa ON operacao_baixa_consignado (empresa_id);
CREATE INDEX idx_op_baixa_consig_status  ON operacao_baixa_consignado (status);
CREATE INDEX idx_op_baixa_consig_criacao ON operacao_baixa_consignado (dt_criacao DESC);
