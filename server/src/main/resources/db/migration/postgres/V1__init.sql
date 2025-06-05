CREATE TABLE IF NOT EXISTS users
(
    id          CHAR(26) PRIMARY KEY,
    user_id     VARCHAR(255) UNIQUE NOT NULL,
    pwd         VARCHAR(255)        NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    profile_img VARCHAR(500) DEFAULT '',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    is_deleted  BOOLEAN      DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS users_details
(
    users_pk   CHAR(26),
    name       VARCHAR(500),
    birth_day  DATE,
    gender     VARCHAR(10),
    phone      VARCHAR(50),
    address    VARCHAR(500),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users_roles
(
    id       BIGSERIAL PRIMARY KEY,
    users_pk CHAR(26),
    role     VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS users_refresh_token
(
    id            BIGSERIAL PRIMARY KEY,
    users_pk      CHAR(26),
    refresh_token VARCHAR(500),
    ip            VARCHAR(50),
    user_agent    VARCHAR(500),
    expired_at    TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users_verify_email
(
    id         BIGSERIAL PRIMARY KEY,
    users_pk   CHAR(26),
    verify_key VARCHAR(500),
    available  BOOLEAN DEFAULT TRUE,
    expired_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS system_settings
(
    id         SERIAL PRIMARY KEY,
    key        VARCHAR(255) NOT NULL,
    value      JSONB        NOT NULL,
    init_value JSONB        NOT NULL,
    public     BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS system_logs
(
    id         BIGSERIAL PRIMARY KEY,
    message    VARCHAR(500),
    is_read    BOOLEAN   DEFAULT FALSE,
    level      VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users_webhooks
(
    id         BIGSERIAL PRIMARY KEY,
    users_pk   CHAR(26),
    type       VARCHAR(50),
    url        VARCHAR(500),
    secret     VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN   DEFAULT FALSE,
    deleted_at TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS users_webhooks_message_logs
(
    id                BIGSERIAL PRIMARY KEY,
    users_pk          CHAR(26),
    users_webhooks_pk BIGINT,
    message           JSONB,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users_passkeys
(
    id           CHAR(26) PRIMARY KEY,
    users_pk     CHAR(26),
    user_handle  BYTEA,
    credential   BYTEA,
    public_key   BYTEA,
    device_name  VARCHAR(255),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted   BOOLEAN   DEFAULT FALSE,
    last_used_at TIMESTAMP DEFAULT NULL,
    deleted_at   TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS users_passkeys_pending_registrations
(
    id         CHAR(26) PRIMARY KEY,
    users_pk   CHAR(26),
    options    JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS users_passkeys_pending_assertion
(
    id         CHAR(26) PRIMARY KEY,
    users_pk   CHAR(26),
    request    JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP DEFAULT NULL
);


-- CREATE FOREIGN KEY
ALTER TABLE users_details
    ADD CONSTRAINT fk_users_details_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );


ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_refresh_token
    ADD CONSTRAINT fk_users_refresh_token_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_verify_email
    ADD CONSTRAINT fk_users_verify_email_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_webhooks
    ADD CONSTRAINT fk_users_webhooks_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_webhooks_message_logs
    ADD CONSTRAINT fk_users_webhooks_message_logs_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_webhooks_message_logs
    ADD CONSTRAINT fk_users_webhooks_message_logs_users_webhooks
        FOREIGN KEY ( users_webhooks_pk ) REFERENCES users_webhooks ( id );

ALTER TABLE users_passkeys
    ADD CONSTRAINT fk_users_passkeys_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_passkeys_pending_registrations
    ADD CONSTRAINT fk_users_passkeys_pending_registrations_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_passkeys_pending_assertion
    ADD CONSTRAINT fk_users_passkeys_pending_assertion_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );


-- CREATE UNIQUE
ALTER TABLE users_roles
    ADD UNIQUE ( users_pk, role );

ALTER TABLE users_refresh_token
    ADD UNIQUE ( users_pk );

ALTER TABLE system_settings
    ADD UNIQUE ( key );


-- INSERT DEFAULT DATA
INSERT INTO users (id, user_id, pwd, email, profile_img, created_at, deleted_at, is_deleted)
VALUES ('00000000000000000000000000', 'master', '', '', '', NOW(), NULL, FALSE);

INSERT INTO users_roles (users_pk, role)
VALUES ('00000000000000000000000000', 'MASTER'),
       ('00000000000000000000000000', 'ADMIN'),
       ('00000000000000000000000000', 'USER');


INSERT INTO system_settings (key, value, init_value, public)
VALUES ('INIT', '{
  "initialized": false,
  "isUpdatedMasterPwd": false,
  "homeUrl": "http://localhost:3000"
}', '{
  "initialized": false,
  "isUpdatedMasterPwd": false,
  "homeUrl": "http://localhost:3000"
}', TRUE);

INSERT INTO system_settings (key, value, init_value, public)
VALUES ('SMTP', '{
  "enabled": false,
  "host": "",
  "port": 587,
  "username": "",
  "password": ""
}', '{
  "enabled": false,
  "host": "",
  "port": 587,
  "username": "",
  "password": ""
}', FALSE);

INSERT INTO system_settings (key, value, init_value, public)
VALUES ('SIGN_UP', '{
  "enabled": false
}', '{
  "enabled": false
}', TRUE);

INSERT INTO system_settings (key, value, init_value, public)
VALUES ('WEBHOOK', '{
  "enabled": false,
  "hasRole": [
    "USER",
    "ADMIN",
    "MASTER"
  ],
  "coverage": [
    "SIGNIN",
    "SIGNOUT",
    "SIGNUP",
    "PASSWORD",
    "ROLE"
  ]
}', '{
  "enabled": false,
  "hasRole": [
    "USER",
    "ADMIN",
    "MASTER"
  ],
  "coverage": [
    "SIGNIN",
    "SIGNOUT",
    "SIGNUP",
    "PASSWORD",
    "ROLE"
  ]
}', TRUE);


INSERT INTO system_settings (key, value, init_value, public)
VALUES ('PASSKEY', '{
  "enabled": false,
  "domain": "localhost",
  "port": 433,
  "displayName": "afarensis"
}', '{
  "enabled": false,
  "domain": "localhost",
  "port": 443,
  "displayName": "afarensis"
}', TRUE);