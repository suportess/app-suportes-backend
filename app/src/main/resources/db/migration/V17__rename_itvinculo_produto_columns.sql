-- =============================================================
-- V17 — Renomeia colunas de itvinculo_produto
-- pai/filho -> antigo/novo para refletir a terminologia correta
-- =============================================================

ALTER TABLE itvinculo_produto
    RENAME COLUMN cd_produto      TO cd_produto_antigo;

ALTER TABLE itvinculo_produto
    RENAME COLUMN ds_produto      TO ds_produto_antigo;

ALTER TABLE itvinculo_produto
    RENAME COLUMN cd_produto_filho TO cd_produto_novo;

ALTER TABLE itvinculo_produto
    RENAME COLUMN ds_produto_filho TO ds_produto_novo;

-- Recria índices com nomes adequados
DROP INDEX IF EXISTS idx_itvp_produto_filho;
CREATE INDEX IF NOT EXISTS idx_itvp_produto_novo ON itvinculo_produto (cd_produto_novo);

-- Atualiza comentários
COMMENT ON TABLE  itvinculo_produto                              IS 'Item de vínculo SUS: par produto antigo → produto novo';
COMMENT ON COLUMN itvinculo_produto.cd_produto_antigo            IS 'Código do produto antigo (que doou o SUS)';
COMMENT ON COLUMN itvinculo_produto.ds_produto_antigo            IS 'Descrição do produto antigo';
COMMENT ON COLUMN itvinculo_produto.cd_produto_novo              IS 'Código do produto novo (que recebeu o SUS)';
COMMENT ON COLUMN itvinculo_produto.ds_produto_novo              IS 'Descrição do produto novo';
