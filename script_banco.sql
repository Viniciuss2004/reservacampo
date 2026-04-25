-- Script de Criação do Banco de Dados PostgreSQL - Reserva Campo
-- Gerado a partir das Requisições DTO e Entidades do Backend

-- 1. Criação da Tabela de Usuários (Baseada em UsuarioRequestDTO / Usuario)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(50) NOT NULL
);

-- 2. Criação da Tabela de Quadras/Campos (Baseada em QuadraRequestDTO / Quadra)
-- Os tipos esperados geralmente em Enum: 'FUTSAL', 'SOCIETY', 'VOLEI', 'BASQUETE', etc.
CREATE TABLE IF NOT EXISTS quadras (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    localizacao VARCHAR(255) NOT NULL,
    preco_hora DECIMAL(10, 2) NOT NULL
);

-- 3. Criação da Tabela de Reservas (Baseada em ReservaRequestDTO / Reserva)
-- O status esperado em Enum: 'CONFIRMADA', 'CANCELADA', 'PENDENTE', etc.
CREATE TABLE IF NOT EXISTS reservas (
    id BIGSERIAL PRIMARY KEY,
    quadra_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_hora_inicio TIMESTAMP NOT NULL,
    data_hora_fim TIMESTAMP NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMADA',

    -- Chaves Estrangeiras relacionando o Agendamento com o a Quadra e o Usuário
    CONSTRAINT fk_reserva_quadra FOREIGN KEY (quadra_id) REFERENCES quadras(id) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ===========================================
-- (Opcional) Script para inserir alguns dados de teste:
-- ===========================================

-- INSERT INTO usuarios (nome, email, telefone) VALUES ('Administrador', 'admin@reservacampo.com.br', '11999999999');
-- INSERT INTO quadras (nome, tipo, localizacao, preco_hora) VALUES ('Quadra Principal', 'FUTSAL', 'Centro', 150.00);
-- INSERT INTO quadras (nome, tipo, localizacao, preco_hora) VALUES ('Campo Society A', 'SOCIETY', 'Zona Sul', 200.00);
