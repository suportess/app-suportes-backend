CREATE TABLE consulta_salva (
    id          BIGSERIAL    PRIMARY KEY,
    auth0_sub   VARCHAR(255) NOT NULL,
    nome        VARCHAR(200) NOT NULL,
    sql_texto   TEXT         NOT NULL,
    dt_criacao  TIMESTAMP    NOT NULL DEFAULT NOW(),
    dt_atualizacao TIMESTAMP
);

CREATE INDEX idx_consulta_salva_auth0 ON consulta_salva (auth0_sub);
CREATE INDEX idx_consulta_salva_criacao ON consulta_salva (auth0_sub, dt_criacao DESC);
