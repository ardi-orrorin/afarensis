CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(255) UNIQUE NOT NULL,
    pwd         VARCHAR(255)        NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    profile_img VARCHAR(500) DEFAULT '',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,
    is_deleted  BOOLEAN      DEFAULT FALSE
);

CREATE TABLE users_details
(
    users_pk   BIGINT,
    name       VARCHAR(500),
    birth_day  DATE,
    gender     VARCHAR(10),
    phone      VARCHAR(50),
    address    VARCHAR(500),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users_roles
(
    id       BIGSERIAL PRIMARY KEY,
    users_pk BIGINT,
    role     VARCHAR(50)
);

CREATE TABLE users_refresh_token
(
    id            BIGSERIAL PRIMARY KEY,
    users_pk      BIGINT,
    refresh_token VARCHAR(500),
    expired_at    TIMESTAMP NOT NULL
);

CREATE TABLE users_verify_email
(
    id         BIGSERIAL PRIMARY KEY,
    users_pk   BIGINT,
    verify_key VARCHAR(500),
    available  BOOLEAN DEFAULT TRUE,
    expired_at TIMESTAMP NOT NULL
);

CREATE TABLE system_settings
(
    id         SERIAL PRIMARY KEY,
    key        VARCHAR(255) NOT NULL,
    value      JSONB        NOT NULL,
    init_value JSONB        NOT NULL,
    public     BOOLEAN DEFAULT FALSE
);

CREATE TABLE system_logs
(
    id         BIGSERIAL PRIMARY KEY,
    message    VARCHAR(500),
    is_read    BOOLEAN   DEFAULT FALSE,
    level      VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

-- CREATE UNIQUE
ALTER TABLE users_roles
    ADD UNIQUE ( users_pk, role );

ALTER TABLE users_refresh_token
    ADD UNIQUE ( users_pk );

ALTER TABLE system_settings
    ADD UNIQUE ( key );


-- INSERT DEFAULT DATA
INSERT INTO users (id, user_id, pwd, email, profile_img, created_at, deleted_at, is_deleted)
VALUES (0, 'master', '', '', '', NOW(), NULL, FALSE);

INSERT INTO users_roles (users_pk, role)
VALUES (0, 'MASTER'),
       (0, 'ADMIN'),
       (0, 'USER');


INSERT INTO system_settings (key, value, init_value, public)
VALUES ('INIT', '{
  "initialized": false,
  "isUpdatedMasterPwd": false
}', '{
  "initialized": false,
  "isUpdatedMasterPwd": false
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
}', FALSE);
