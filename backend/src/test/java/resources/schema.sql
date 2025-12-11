
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'COMMON')),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE TABLE resource_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    icon VARCHAR(50)
);

INSERT INTO resource_types (name, description, icon) VALUES
('LANGUAGE', 'Linguagens de programação', 'code'),
('SERVER', 'Servidores de aplicação', 'server'),
('DATABASE', 'Bancos de dados', 'database'),
('FRAMEWORK', 'Frameworks e bibliotecas', 'layers');

CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    resource_type_id BIGINT NOT NULL REFERENCES resource_types(id),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    version VARCHAR(50),
    code_snippet TEXT,
    docker_image VARCHAR(255),
    config_template JSONB,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE INDEX idx_resources_type ON resources(resource_type_id);

CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    last_deployed_at TIMESTAMP
);


CREATE TABLE application_resources (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    resource_id BIGINT NOT NULL REFERENCES resources(id),
    config_override JSONB,
    added_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(application_id, resource_id)
);


CREATE TABLE deployment_logs (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);