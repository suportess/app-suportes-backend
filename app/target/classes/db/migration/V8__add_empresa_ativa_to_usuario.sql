ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS cd_empresa_ativa BIGINT
        REFERENCES conf_empresa(id) ON DELETE SET NULL;
