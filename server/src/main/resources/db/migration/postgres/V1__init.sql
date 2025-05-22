CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(255) UNIQUE NOT NULL,
    pwd         VARCHAR(255)        NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    profile_img VARCHAR(500) DEFAULT '',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
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


ALTER TABLE users_details
    ADD CONSTRAINT fk_users_details_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );


ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_roles
    ADD UNIQUE ( users_pk, role );

ALTER TABLE users_refresh_token
    ADD CONSTRAINT fk_users_refresh_token_users
        FOREIGN KEY ( users_pk ) REFERENCES users ( id );

ALTER TABLE users_refresh_token
    ADD UNIQUE ( users_pk );
