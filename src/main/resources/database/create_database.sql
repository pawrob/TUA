-- czesc skryptu usuwajaca stare struktury, jezeli takowe istnieja
DROP VIEW IF EXISTS ssbd05.authentication_view;

DROP TABLE IF EXISTS ssbd05.favourites;
DROP TABLE IF EXISTS ssbd05.reservation;
DROP TABLE IF EXISTS ssbd05.offer_availability;
DROP TABLE IF EXISTS ssbd05.offer;

DROP TABLE IF EXISTS ssbd05.access_level_change_log;
DROP TABLE IF EXISTS ssbd05.session_log;
DROP TABLE IF EXISTS ssbd05.query_log;

DROP TABLE IF EXISTS ssbd05.entertainer_unavailability;

DROP TABLE IF EXISTS ssbd05.client;
DROP TABLE IF EXISTS ssbd05.management;
DROP TABLE IF EXISTS ssbd05.entertainer;
DROP TABLE IF EXISTS ssbd05.access_level;

DROP TABLE IF EXISTS ssbd05.personal_data;
DROP TABLE IF EXISTS ssbd05.user;


CREATE TABLE user
(

    id                   BIGINT             NOT NULL auto_increment,
    login                CHARACTER VARYING(16) NOT NULL,
    password             CHARACTER(60)         NOT NULL,
    email                CHARACTER VARYING(64) NOT NULL,
    is_active            BOOLEAN               NOT NULL DEFAULT TRUE,
    is_verified          BOOLEAN               NOT NULL DEFAULT FALSE,
    password_reset_token CHARACTER(64),
    token_timestamp      DATETIME,
    failed_login         SMALLINT              NOT NULL DEFAULT 0,
    version              BIGINT                NOT NULL DEFAULT 1,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT user_login_key UNIQUE (login),
    CONSTRAINT user_email_key UNIQUE (email)
#     CONSTRAINT user_email_correctness CHECK ( email ~* '^[-!#$%&*+-/=?^_`{|}~a-z0-9]+@[a-z]+.[a-z]{2,5}$'),
#     CONSTRAINT user_password_bcrypt_form CHECK ( password ~*
#                                                  '^[$]2[abxy][$](?:0[4-9]|[12][0-9]|3[01])[$][./0-9a-zA-Z]{53}$'
#         )
);

GRANT ALL PRIVILEGES ON ssbd05.user TO 'ssbd05admin'@'%';


CREATE TABLE personal_data
(
    user_id      BIGINT               NOT NULL,
    name         CHARACTER VARYING(30),
    surname      CHARACTER VARYING(30),
    phone_number CHARACTER VARYING(15),
    is_man       BOOLEAN,
    language     CHARACTER VARYING(3) NOT NULL DEFAULT 'PL',
    version      BIGINT               NOT NULL DEFAULT 1,

    CONSTRAINT personal_data_pkey PRIMARY KEY (user_id),
    CONSTRAINT personal_data_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES ssbd05.user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
#     CONSTRAINT phone_number_correctness CHECK ( phone_number ~ '^[+]?[-\s0-9]{9,15}$'),
#     CONSTRAINT name_correctness CHECK ( name ~* '^[a-z\sżźćńółęąś]+$' ),
#     CONSTRAINT surname_correctness CHECK ( surname ~* '^[a-z\sżźćńółęąś]+$' )
);

GRANT ALL PRIVILEGES ON ssbd05.personal_data TO 'ssbd05admin'@'%';


CREATE TABLE access_level
(
    id           BIGINT             NOT NULL auto_increment,
    user_id      BIGINT                NOT NULL,
    access_level CHARACTER VARYING(16) NOT NULL,
    is_active    BOOLEAN               NOT NULL DEFAULT TRUE,
    version      BIGINT                NOT NULL DEFAULT 1,
    CONSTRAINT access_level_correctness CHECK ( access_level.access_level IN ('ENTERTAINER', 'CLIENT', 'MANAGEMENT')),
    CONSTRAINT access_level_pkey PRIMARY KEY (id),
    CONSTRAINT access_level_user_id_access_level_key UNIQUE (user_id, access_level),
    CONSTRAINT access_level_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES ssbd05.user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.access_level TO 'ssbd05admin'@'%';


CREATE TABLE entertainer
(
    access_level_id BIGINT NOT NULL,
    description     CHARACTER VARYING(2048),
    avg_rating      DOUBLE PRECISION,
    version         BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT entertainer_pkey PRIMARY KEY (access_level_id),
    CONSTRAINT entertainer_access_level_id_fkey FOREIGN KEY (access_level_id)
        REFERENCES ssbd05.access_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.entertainer TO 'ssbd05admin'@'%';



CREATE TABLE management
(
    access_level_id BIGINT NOT NULL,
    version         BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT management_pkey PRIMARY KEY (access_level_id),
    CONSTRAINT management_access_level_id_fkey FOREIGN KEY (access_level_id)
        REFERENCES ssbd05.access_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.management TO 'ssbd05admin'@'%';


CREATE TABLE client
(
    access_level_id BIGINT NOT NULL,
    version         BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT client_pkey PRIMARY KEY (access_level_id),
    CONSTRAINT client_access_level_id_fkey FOREIGN KEY (access_level_id)
        REFERENCES ssbd05.access_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.client TO 'ssbd05admin'@'%';


CREATE TABLE entertainer_unavailability
(
    id             BIGINT                NOT NULL auto_increment,
    entertainer_id BIGINT                   NOT NULL,
    date_time_from DATETIME NOT NULL,
    date_time_to   DATETIME NOT NULL,
    description    CHARACTER VARYING(350),
    is_valid       BOOLEAN                  NOT NULL DEFAULT TRUE,
    version        BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT entertainer_unavailability_pkey PRIMARY KEY (id),
    CONSTRAINT entertainer_unavailability_entertainer_id_fkey FOREIGN KEY (entertainer_id)
        REFERENCES ssbd05.entertainer (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT entertainer_unavailability_date_check CHECK (date_time_from < date_time_to)
);

GRANT ALL PRIVILEGES ON ssbd05.entertainer_unavailability TO 'ssbd05admin'@'%';


CREATE TABLE query_log
(
    id               BIGINT                NOT NULL auto_increment,
    user_id          BIGINT,
    action_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    query            CHARACTER VARYING(512)   NOT NULL,
    module           CHARACTER VARYING(64)    NOT NULL,
    affected_table   CHARACTER VARYING(64),
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT module_correctness CHECK ( module IN ('mok', 'moo', 'auth')
        ),
    CONSTRAINT query_log_pkey PRIMARY KEY (id),
    CONSTRAINT query_log_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES ssbd05.user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


GRANT ALL PRIVILEGES ON ssbd05.query_log TO 'ssbd05admin'@'%';


CREATE TABLE session_log
(
    id               BIGINT                NOT NULL auto_increment,
    user_id          BIGINT,
    action_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address       CHARACTER VARYING(15)    NOT NULL,
    is_successful    BOOLEAN                  NOT NULL,
    version          BIGINT                   NOT NULL DEFAULT 1,
#     CONSTRAINT ip_address_correctness CHECK (ip_address ~
#                                              '^(([0-2]?[0-9]?[0-9])?\s?([.]||[:])){3,7}([0-2]?[0-9]?[0-9])?\s?$'
#         ),
    CONSTRAINT session_log_pkey PRIMARY KEY (id),
    CONSTRAINT session_log_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES ssbd05.user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.session_log TO 'ssbd05admin'@'%';


CREATE TABLE access_level_change_log
(
    id               BIGINT                NOT NULL auto_increment,
    user_id          BIGINT                   NOT NULL,
    action_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    access_level     CHARACTER VARYING(15)    NOT NULL,
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT access_level_change_log_pkey PRIMARY KEY (id),
    CONSTRAINT access_level_change_log_fkey FOREIGN KEY (user_id)
        REFERENCES ssbd05.user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.access_level_change_log TO 'ssbd05admin'@'%';


CREATE TABLE offer
(
    id             BIGINT                NOT NULL auto_increment,
    entertainer_id BIGINT                   NOT NULL,
    title          CHARACTER VARYING(100)   NOT NULL,
    description    CHARACTER VARYING(350),
    is_active      BOOLEAN                  NOT NULL DEFAULT TRUE,
    valid_from     DATETIME NOT NULL,
    valid_to       DATETIME NOT NULL,
    avg_rating     DOUBLE PRECISION,
    version        BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT offer_pkey PRIMARY KEY (id),
    CONSTRAINT offer_entertainer_id_fkey FOREIGN KEY (entertainer_id)
        REFERENCES ssbd05.entertainer (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT offer_date_check CHECK (valid_from < valid_to)
);

GRANT ALL PRIVILEGES ON ssbd05.offer TO 'ssbd05admin'@'%';


CREATE TABLE offer_availability
(
    id         BIGINT           NOT NULL auto_increment,
    offer_id   BIGINT              NOT NULL,
    week_day   INTEGER             NOT NULL,
    hours_from DATETIME NOT NULL,
    hours_to   DATETIME NOT NULL,
    version    BIGINT              NOT NULL DEFAULT 1,
    CONSTRAINT offer_availability_pkey PRIMARY KEY (id),
    CONSTRAINT offer_availability_offer_id_fkey FOREIGN KEY (offer_id)
        REFERENCES ssbd05.offer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT week_day_check CHECK (week_day >= 0 AND week_day <= 6)
);

GRANT ALL PRIVILEGES ON ssbd05.offer_availability TO 'ssbd05admin'@'%';


CREATE TABLE reservation
(
    id               BIGINT                NOT NULL auto_increment,
    client_id        BIGINT                   NOT NULL,
    offer_id         BIGINT                   NOT NULL,
    reservation_from DATETIME NOT NULL,
    reservation_to   DATETIME NOT NULL,
    status           CHARACTER VARYING(20),
    rating           INTEGER,
    comment          CHARACTER VARYING(250),
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT reservation_pkey PRIMARY KEY (id),
    CONSTRAINT reservation_client_id_fkey FOREIGN KEY (client_id)
        REFERENCES ssbd05.client (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT reservation_offer_id_fkey FOREIGN KEY (offer_id)
        REFERENCES ssbd05.offer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT reservation_date_check CHECK (reservation_from < reservation_to)
);

GRANT ALL PRIVILEGES ON ssbd05.reservation TO 'ssbd05admin'@'%';


CREATE TABLE favourites
(
    client_id BIGINT NOT NULL,
    offer_id  BIGINT NOT NULL,
    version   BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT favourites_pkey PRIMARY KEY (client_id, offer_id),
    CONSTRAINT favourites_client_id_fkey FOREIGN KEY (client_id)
        REFERENCES ssbd05.client (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT favourites_offer_id_fkey FOREIGN KEY (offer_id)
        REFERENCES ssbd05.offer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

GRANT ALL PRIVILEGES ON ssbd05.favourites TO 'ssbd05admin'@'%';


-- widok laczący uzytkownikow, z przypisanymi im poziomami dostepow
CREATE OR REPLACE VIEW authentication_view
AS
SELECT al.id,
       u.login,
       u.password,
       al.access_level
FROM ssbd05.user u
         JOIN access_level al
              ON u.id = al.user_id
WHERE u.is_active
  AND u.is_verified
  AND u.password_reset_token IS NULL
  AND al.is_active;

GRANT ALL PRIVILEGES ON ssbd05.authentication_view TO 'ssbd05admin'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'ssbd05auth'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'ssbd05admin'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'ssbd05mok'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'ssbd05moo'@'%';
-- uprawnienia dla uzytkowników bazodanowych do poszczegolnych tabel
# GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE ssbd05.access_level TO ssbd05mok;
# GRANT SELECT, UPDATE ON TABLE ssbd05.access_level TO ssbd05moo;
# GRANT SELECT ON TABLE ssbd05.authentication_view TO ssbd05auth;
# GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE ssbd05.user TO ssbd05mok;
# GRANT SELECT ON TABLE ssbd05.user TO ssbd05moo;
# GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE ssbd05.session_log TO ssbd05mok;
# GRANT INSERT, SELECT, UPDATE ON TABLE ssbd05.reservation TO ssbd05moo;
# GRANT INSERT ON TABLE ssbd05.query_log TO ssbd05moo;
# GRANT INSERT ON TABLE ssbd05.query_log TO ssbd05mok;
# GRANT INSERT, UPDATE, SELECT, DELETE ON TABLE ssbd05.personal_data TO ssbd05mok;
# GRANT SELECT ON TABLE ssbd05.personal_data TO ssbd05moo;
# GRANT INSERT, SELECT, UPDATE ON TABLE ssbd05.offer_availability TO ssbd05moo;
# GRANT INSERT, SELECT, UPDATE ON TABLE ssbd05.offer TO ssbd05moo;
# GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE ssbd05.management TO ssbd05mok;
# GRANT SELECT ON TABLE ssbd05.management TO ssbd05moo;
# GRANT INSERT, SELECT, DELETE ON TABLE ssbd05.favourites TO ssbd05moo;
# GRANT INSERT, SELECT, UPDATE ON TABLE ssbd05.entertainer_unavailability TO ssbd05moo;
# GRANT SELECT, UPDATE, DELETE ON TABLE ssbd05.entertainer_unavailability TO ssbd05mok;
# GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE ssbd05.entertainer TO ssbd05mok;
# GRANT INSERT, SELECT, UPDATE ON TABLE ssbd05.entertainer TO ssbd05moo;
# GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE ssbd05.client TO ssbd05mok;
# GRANT SELECT ON TABLE ssbd05.client TO ssbd05moo;
# GRANT INSERT ON TABLE ssbd05.access_level_change_log TO ssbd05mok;

# -- uprawnienia do poszczegolnych sekwencji
# GRANT USAGE ON SEQUENCE ssbd05.access_level_change_log_id_seq TO ssbd05mok;
# GRANT USAGE ON SEQUENCE public.entertainer_unavailability_id_seq TO ssbd05moo;
# GRANT USAGE ON SEQUENCE public.offer_id_seq TO ssbd05moo;
# GRANT USAGE ON SEQUENCE public.offer_availability_id_seq TO ssbd05moo;
# GRANT USAGE ON SEQUENCE public.query_log_id_seq TO ssbd05moo;
# GRANT USAGE ON SEQUENCE public.query_log_id_seq TO ssbd05mok;
# GRANT USAGE ON SEQUENCE public.reservation_id_seq TO ssbd05moo;
# GRANT USAGE ON SEQUENCE public.session_log_id_seq TO ssbd05mok;
# GRANT USAGE ON SEQUENCE public.session_log_id_seq TO ssbd05auth;
# GRANT USAGE ON SEQUENCE public.user_id_seq TO ssbd05mok;
# GRANT USAGE ON SEQUENCE public.access_level_id_seq TO ssbd05mok;

-- indeksy dla kluczy obcych
DROP
    INDEX IF EXISTS personal_data_user_id on personal_data;
CREATE
    INDEX personal_data_user_id
    ON personal_data(user_id);

DROP
    INDEX IF EXISTS query_log_user_id on query_log;
CREATE
    INDEX query_log_user_id
    ON query_log(user_id);

DROP
    INDEX IF EXISTS access_level_user_id on access_level;
CREATE
    INDEX access_level_user_id
    ON access_level(user_id);

DROP
    INDEX IF EXISTS access_level_change_log_user_id on access_level_change_log;
CREATE
    INDEX access_level_change_log_user_id
    ON access_level_change_log(user_id);

DROP
    INDEX IF EXISTS session_log_user_id on session_log;
CREATE
    INDEX session_log_user_id
    ON session_log(user_id);

DROP
    INDEX IF EXISTS entertainer_access_level_id on entertainer;
CREATE
    INDEX entertainer_access_level_id
    ON entertainer(access_level_id);

DROP
    INDEX IF EXISTS management_access_level_id on management;
CREATE
    INDEX management_access_level_id
    ON management(access_level_id);

DROP
    INDEX IF EXISTS client_access_level_id on client;
CREATE
    INDEX client_access_level_id
    ON client(access_level_id);

DROP
    INDEX IF EXISTS reservation_client_id on reservation;
CREATE
    INDEX reservation_client_id
    ON reservation(client_id);

DROP
    INDEX IF EXISTS reservation_offer_id on reservation;
CREATE
    INDEX reservation_offer_id
    ON reservation(offer_id);

DROP
    INDEX IF EXISTS entertainer_unavailability_entertainer_id on entertainer_unavailability;
CREATE
    INDEX entertainer_unavailability_entertainer_id
    ON entertainer_unavailability(entertainer_id);

DROP
    INDEX IF EXISTS offer_entertainer_id on offer;
CREATE
    INDEX offer_entertainer_id
    ON offer(entertainer_id);

DROP
    INDEX IF EXISTS favourites_client_id on favourites;
CREATE
    INDEX favourites_client_id
    ON favourites(client_id);

DROP
    INDEX IF EXISTS favourites_offer_id on favourites;
CREATE
    INDEX favourites_offer_id
    ON favourites(offer_id);

DROP
    INDEX IF EXISTS offer_availability_offer_id on offer_availability;
CREATE
    INDEX offer_availability_offer_id
    ON offer_availability(offer_id);
