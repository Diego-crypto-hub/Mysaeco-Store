-- Dados de exemplo para testar a API (rode depois do loja_velas.sql)
USE loja_velas;

INSERT INTO categoria (nome, descricao) VALUES
  ('Aromáticas', 'Velas perfumadas para ambientes'),
  ('Decorativas', 'Velas com foco em design'),
  ('Kits e Presentes', 'Combos prontos para presentear');
INSERT INTO categoria (nome, descricao, id_categoria_pai) VALUES
  ('Velas em Pote', 'Aromáticas em pote de vidro', 1);

INSERT INTO aroma (nome, familia_olfativa) VALUES
  ('Lavanda', 'Floral'), ('Baunilha', 'Gourmand'), ('Sândalo', 'Amadeirado'),
  ('Capim-limão', 'Cítrico'), ('Flor de Laranjeira', 'Floral');

INSERT INTO produto (id_categoria, nome, descricao, tipo_cera, tipo_pavio) VALUES
  (4, 'Vela em Pote Âmbar', 'Pote de vidro âmbar reutilizável com tampa de bambu.', 'SOJA', 'ALGODAO'),
  (4, 'Vela Crepitante', 'Pavio de madeira que estala como uma lareira.', 'COCO', 'MADEIRA'),
  (2, 'Vela Pilar Rústica', 'Vela pilar sem aroma, acabamento texturizado.', 'PARAFINA', 'ALGODAO'),
  (3, 'Kit Relax', 'Duas velas em pote + caixa de presente.', 'SOJA', 'ALGODAO');

INSERT INTO variacao_produto (id_produto, id_aroma, sku, tamanho, peso_gramas, tempo_queima_horas, preco, preco_promocional, qtd_estoque) VALUES
  (1, 1, 'POTE-LAV-P', 'P', 120, 25, 49.90, NULL, 30),
  (1, 1, 'POTE-LAV-G', 'G', 300, 60, 89.90, 79.90, 15),
  (1, 2, 'POTE-BAU-P', 'P', 120, 25, 49.90, NULL, 25),
  (1, 4, 'POTE-CAP-G', 'G', 300, 60, 89.90, NULL, 10),
  (2, 3, 'CREP-SAN-M', 'M', 200, 40, 99.90, NULL, 12),
  (2, 5, 'CREP-FLO-M', 'M', 200, 40, 99.90, 89.90, 8),
  (4, 1, 'KIT-RELAX', NULL, 240, 50, 119.90, NULL, 6);
INSERT INTO variacao_produto (id_produto, id_aroma, sku, tamanho, cor, peso_gramas, tempo_queima_horas, preco, qtd_estoque) VALUES
  (3, NULL, 'PILAR-BRANCA-M', 'M', 'Branca', 400, 70, 59.90, 20),
  (3, NULL, 'PILAR-TERRA-M', 'M', 'Terracota', 400, 70, 59.90, 14);

INSERT INTO imagem_produto (id_produto, url, ordem) VALUES
  (1, 'https://exemplo.com/img/pote-ambar-1.jpg', 1),
  (1, 'https://exemplo.com/img/pote-ambar-2.jpg', 2),
  (2, 'https://exemplo.com/img/crepitante-1.jpg', 1),
  (3, 'https://exemplo.com/img/pilar-1.jpg', 1),
  (4, 'https://exemplo.com/img/kit-relax-1.jpg', 1);

INSERT INTO cupom (codigo, tipo_desconto, valor_desconto, valor_minimo_pedido, data_inicio, data_fim, limite_uso) VALUES
  ('BEMVINDO10', 'PERCENTUAL', 10.00, NULL, '2026-01-01 00:00:00', '2027-12-31 23:59:59', NULL),
  ('VELA20', 'VALOR', 20.00, 150.00, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 100);
