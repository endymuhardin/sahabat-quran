-- Create permissions table
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Create roles_permissions join table
CREATE TABLE roles_permissions (
    id_role UUID NOT NULL,
    id_permission UUID NOT NULL,
    PRIMARY KEY (id_role, id_permission),
    FOREIGN KEY (id_role) REFERENCES roles (id),
    FOREIGN KEY (id_permission) REFERENCES permissions (id)
);

-- Create users_roles join table
CREATE TABLE users_roles (
    id_user UUID NOT NULL,
    id_role UUID NOT NULL,
    PRIMARY KEY (id_user, id_role),
    FOREIGN KEY (id_user) REFERENCES users (id),
    FOREIGN KEY (id_role) REFERENCES roles (id)
);
