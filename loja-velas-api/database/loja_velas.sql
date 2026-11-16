-- =============================================================
--  Banco de dados: Loja virtual de velas
--  SGBD: MySQL 8.0.16+ (necessário para as restrições CHECK)
-- =============================================================

CREATE DATABASE IF NOT EXISTS loja_velas
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE loja_velas;

-- -------------------------------------------------------------
-- CLIENTE
-- -------------------------------------------------------------
CREATE TABLE cliente (
  id_cliente       INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome             VARCHAR(100) NOT NULL,
  email            VARCHAR(150) NOT NULL,
  senha_hash       VARCHAR(255) NOT NULL,
  cpf              CHAR(11)     NULL,
  telefone         VARCHAR(20)  NULL,
  data_nascimento  DATE         NULL,
  data_cadastro    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id_cliente),
  UNIQUE KEY uk_cliente_email (email),
  UNIQUE KEY uk_cliente_cpf (cpf)
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- ENDERECO
-- -------------------------------------------------------------
CREATE TABLE endereco (
  id_endereco   INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_cliente    INT UNSIGNED NOT NULL,
  apelido       VARCHAR(50)  NULL,
  cep           CHAR(8)      NOT NULL,
  logradouro    VARCHAR(150) NOT NULL,
  numero        VARCHAR(10)  NOT NULL,
  complemento   VARCHAR(100) NULL,
  bairro        VARCHAR(80)  NOT NULL,
  cidade        VARCHAR(80)  NOT NULL,
  uf            CHAR(2)      NOT NULL,
  principal     BOOLEAN      NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id_endereco),
  KEY idx_endereco_cliente (id_cliente),
  CONSTRAINT fk_endereco_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- CATEGORIA (com autorrelacionamento para subcategorias)
-- -------------------------------------------------------------
CREATE TABLE categoria (
  id_categoria      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome              VARCHAR(60)  NOT NULL,
  descricao         TEXT         NULL,
  id_categoria_pai  INT UNSIGNED NULL,
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uk_categoria_nome (nome),
  KEY idx_categoria_pai (id_categoria_pai),
  CONSTRAINT fk_categoria_pai
    FOREIGN KEY (id_categoria_pai) REFERENCES categoria (id_categoria)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- AROMA
-- -------------------------------------------------------------
CREATE TABLE aroma (
  id_aroma          INT UNSIGNED NOT NULL AUTO_INCREMENT,
  nome              VARCHAR(60)  NOT NULL,
  familia_olfativa  VARCHAR(40)  NULL,
  PRIMARY KEY (id_aroma),
  UNIQUE KEY uk_aroma_nome (nome)
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- PRODUTO
-- -------------------------------------------------------------
CREATE TABLE produto (
  id_produto    INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_categoria  INT UNSIGNED NOT NULL,
  nome          VARCHAR(120) NOT NULL,
  descricao     TEXT         NULL,
  tipo_cera     ENUM('SOJA','PARAFINA','COCO','ABELHA','VEGETAL_MISTA') NULL,
  tipo_pavio    ENUM('ALGODAO','MADEIRA') NULL,
  ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
  data_criacao  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_produto),
  KEY idx_produto_categoria (id_categoria),
  FULLTEXT KEY ft_produto_busca (nome, descricao),
  CONSTRAINT fk_produto_categoria
    FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- VARIACAO_PRODUTO (aroma, tamanho, preço e estoque)
-- -------------------------------------------------------------
CREATE TABLE variacao_produto (
  id_variacao         INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  id_produto          INT UNSIGNED  NOT NULL,
  id_aroma            INT UNSIGNED  NULL,
  sku                 VARCHAR(30)   NOT NULL,
  tamanho             ENUM('P','M','G','GG') NULL,
  peso_gramas         INT UNSIGNED  NULL,
  cor                 VARCHAR(30)   NULL,
  tempo_queima_horas  INT UNSIGNED  NULL,
  preco               DECIMAL(10,2) NOT NULL,
  preco_promocional   DECIMAL(10,2) NULL,
  qtd_estoque         INT UNSIGNED  NOT NULL DEFAULT 0,
  ativo               BOOLEAN       NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id_variacao),
  UNIQUE KEY uk_variacao_sku (sku),
  KEY idx_variacao_produto (id_produto),
  KEY idx_variacao_aroma (id_aroma),
  CONSTRAINT chk_variacao_preco CHECK (preco > 0),
  CONSTRAINT chk_variacao_promo CHECK (preco_promocional IS NULL OR preco_promocional < preco),
  CONSTRAINT fk_variacao_produto
    FOREIGN KEY (id_produto) REFERENCES produto (id_produto)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_variacao_aroma
    FOREIGN KEY (id_aroma) REFERENCES aroma (id_aroma)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- IMAGEM_PRODUTO
-- -------------------------------------------------------------
CREATE TABLE imagem_produto (
  id_imagem    INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_produto   INT UNSIGNED NOT NULL,
  id_variacao  INT UNSIGNED NULL,
  url          VARCHAR(255) NOT NULL,
  ordem        INT UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (id_imagem),
  KEY idx_imagem_produto (id_produto),
  KEY idx_imagem_variacao (id_variacao),
  CONSTRAINT fk_imagem_produto
    FOREIGN KEY (id_produto) REFERENCES produto (id_produto)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_imagem_variacao
    FOREIGN KEY (id_variacao) REFERENCES variacao_produto (id_variacao)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- CARRINHO
-- -------------------------------------------------------------
CREATE TABLE carrinho (
  id_carrinho       INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_cliente        INT UNSIGNED NULL,          -- NULL = visitante
  token_sessao      VARCHAR(100) NULL,
  data_criacao      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  data_atualizacao  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrinho),
  UNIQUE KEY uk_carrinho_cliente (id_cliente),
  UNIQUE KEY uk_carrinho_token (token_sessao),
  CONSTRAINT fk_carrinho_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- ITEM_CARRINHO
-- -------------------------------------------------------------
CREATE TABLE item_carrinho (
  id_carrinho  INT UNSIGNED NOT NULL,
  id_variacao  INT UNSIGNED NOT NULL,
  quantidade   INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_carrinho, id_variacao),
  KEY idx_itemcar_variacao (id_variacao),
  CONSTRAINT chk_itemcar_qtd CHECK (quantidade > 0),
  CONSTRAINT fk_itemcar_carrinho
    FOREIGN KEY (id_carrinho) REFERENCES carrinho (id_carrinho)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_itemcar_variacao
    FOREIGN KEY (id_variacao) REFERENCES variacao_produto (id_variacao)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- CUPOM
-- -------------------------------------------------------------
CREATE TABLE cupom (
  id_cupom             INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  codigo               VARCHAR(30)   NOT NULL,
  tipo_desconto        ENUM('PERCENTUAL','VALOR') NOT NULL,
  valor_desconto       DECIMAL(10,2) NOT NULL,
  valor_minimo_pedido  DECIMAL(10,2) NULL,
  data_inicio          DATETIME      NOT NULL,
  data_fim             DATETIME      NOT NULL,
  limite_uso           INT UNSIGNED  NULL,
  qtd_usada            INT UNSIGNED  NOT NULL DEFAULT 0,
  PRIMARY KEY (id_cupom),
  UNIQUE KEY uk_cupom_codigo (codigo),
  CONSTRAINT chk_cupom_datas CHECK (data_fim > data_inicio),
  CONSTRAINT chk_cupom_valor CHECK (valor_desconto > 0)
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- PEDIDO
-- -------------------------------------------------------------
CREATE TABLE pedido (
  id_pedido           INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  id_cliente          INT UNSIGNED  NOT NULL,
  id_endereco         INT UNSIGNED  NOT NULL,
  id_cupom            INT UNSIGNED  NULL,
  data_pedido         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status              ENUM('AGUARDANDO_PAGAMENTO','PAGO','EM_SEPARACAO',
                           'ENVIADO','ENTREGUE','CANCELADO')
                      NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
  valor_subtotal      DECIMAL(10,2) NOT NULL,
  valor_desconto      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  valor_frete         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  valor_total         DECIMAL(10,2) NOT NULL,
  embalagem_presente  BOOLEAN       NOT NULL DEFAULT FALSE,
  mensagem_presente   VARCHAR(255)  NULL,
  PRIMARY KEY (id_pedido),
  KEY idx_pedido_cliente (id_cliente),
  KEY idx_pedido_endereco (id_endereco),
  KEY idx_pedido_cupom (id_cupom),
  KEY idx_pedido_status_data (status, data_pedido),
  CONSTRAINT fk_pedido_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_pedido_endereco
    FOREIGN KEY (id_endereco) REFERENCES endereco (id_endereco)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_pedido_cupom
    FOREIGN KEY (id_cupom) REFERENCES cupom (id_cupom)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- ITEM_PEDIDO
-- -------------------------------------------------------------
CREATE TABLE item_pedido (
  id_pedido       INT UNSIGNED  NOT NULL,
  id_variacao     INT UNSIGNED  NOT NULL,
  quantidade      INT UNSIGNED  NOT NULL,
  preco_unitario  DECIMAL(10,2) NOT NULL,   -- preço congelado na compra
  PRIMARY KEY (id_pedido, id_variacao),
  KEY idx_itemped_variacao (id_variacao),
  CONSTRAINT chk_itemped_qtd CHECK (quantidade > 0),
  CONSTRAINT fk_itemped_pedido
    FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_itemped_variacao
    FOREIGN KEY (id_variacao) REFERENCES variacao_produto (id_variacao)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- PAGAMENTO
-- -------------------------------------------------------------
CREATE TABLE pagamento (
  id_pagamento          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
  id_pedido             INT UNSIGNED  NOT NULL,
  forma_pagamento       ENUM('PIX','CARTAO_CREDITO','CARTAO_DEBITO','BOLETO') NOT NULL,
  parcelas              TINYINT UNSIGNED NOT NULL DEFAULT 1,
  valor                 DECIMAL(10,2) NOT NULL,
  status                ENUM('PENDENTE','APROVADO','RECUSADO','ESTORNADO')
                        NOT NULL DEFAULT 'PENDENTE',
  id_transacao_gateway  VARCHAR(100)  NULL,
  data_pagamento        DATETIME      NULL,
  PRIMARY KEY (id_pagamento),
  KEY idx_pagamento_pedido (id_pedido),
  CONSTRAINT chk_pagamento_parcelas CHECK (parcelas BETWEEN 1 AND 12),
  CONSTRAINT fk_pagamento_pedido
    FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- ENTREGA (1:1 com pedido)
-- -------------------------------------------------------------
CREATE TABLE entrega (
  id_entrega       INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_pedido        INT UNSIGNED NOT NULL,
  transportadora   VARCHAR(60)  NULL,
  modalidade       VARCHAR(30)  NULL,
  codigo_rastreio  VARCHAR(50)  NULL,
  prazo_dias       INT UNSIGNED NULL,
  data_envio       DATETIME     NULL,
  data_entrega     DATETIME     NULL,
  PRIMARY KEY (id_entrega),
  UNIQUE KEY uk_entrega_pedido (id_pedido),
  CONSTRAINT fk_entrega_pedido
    FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- AVALIACAO
-- -------------------------------------------------------------
CREATE TABLE avaliacao (
  id_avaliacao    INT UNSIGNED NOT NULL AUTO_INCREMENT,
  id_cliente      INT UNSIGNED NOT NULL,
  id_produto      INT UNSIGNED NOT NULL,
  nota            TINYINT UNSIGNED NOT NULL,
  comentario      TEXT         NULL,
  data_avaliacao  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  aprovada        BOOLEAN      NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id_avaliacao),
  UNIQUE KEY uk_avaliacao_cliente_produto (id_cliente, id_produto),
  KEY idx_avaliacao_produto (id_produto),
  CONSTRAINT chk_avaliacao_nota CHECK (nota BETWEEN 1 AND 5),
  CONSTRAINT fk_avaliacao_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_avaliacao_produto
    FOREIGN KEY (id_produto) REFERENCES produto (id_produto)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- LISTA_DESEJOS
-- -------------------------------------------------------------
CREATE TABLE lista_desejos (
  id_cliente   INT UNSIGNED NOT NULL,
  id_produto   INT UNSIGNED NOT NULL,
  data_adicao  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_cliente, id_produto),
  KEY idx_desejos_produto (id_produto),
  CONSTRAINT fk_desejos_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_desejos_produto
    FOREIGN KEY (id_produto) REFERENCES produto (id_produto)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
