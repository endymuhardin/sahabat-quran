-- Permissions
MERGE INTO permissions (id, name) KEY(id) VALUES ('7d2d8f49-2e11-452a-92e4-2b634e2f6a8e', 'user:read');
MERGE INTO permissions (id, name) KEY(id) VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'user:write');

-- Roles
MERGE INTO roles (id, name) KEY(id) VALUES ('a2b3c4d5-6e7f-8a9b-0c1d-2e3f4a5b6c7d', 'ROLE_USER');
MERGE INTO roles (id, name) KEY(id) VALUES ('b3c4d5e6-7f8a-9b0c-1d2e-3f4a5b6c7d8e', 'ROLE_ADMIN');

-- Role-Permission mappings
MERGE INTO roles_permissions (id_role, id_permission) KEY(id_role, id_permission) VALUES ('a2b3c4d5-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '7d2d8f49-2e11-452a-92e4-2b634e2f6a8e');
MERGE INTO roles_permissions (id_role, id_permission) KEY(id_role, id_permission) VALUES ('b3c4d5e6-7f8a-9b0c-1d2e-3f4a5b6c7d8e', '7d2d8f49-2e11-452a-92e4-2b634e2f6a8e');
MERGE INTO roles_permissions (id_role, id_permission) KEY(id_role, id_permission) VALUES ('b3c4d5e6-7f8a-9b0c-1d2e-3f4a5b6c7d8e', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');
