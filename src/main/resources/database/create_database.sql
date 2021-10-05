-- czesc skryptu usuwajaca stare struktury, jezeli takowe istnieja
DROP VIEW IF EXISTS public.authentication_view;

DROP TABLE IF EXISTS public.favourites;
DROP TABLE IF EXISTS public.reservation;
DROP TABLE IF EXISTS public.offer_availability;
DROP TABLE IF EXISTS public.offer;

DROP TABLE IF EXISTS public.access_level_change_log;
DROP TABLE IF EXISTS public.session_log;
DROP TABLE IF EXISTS public.query_log;

DROP TABLE IF EXISTS public.entertainer_unavailability;

DROP TABLE IF EXISTS public.client;
DROP TABLE IF EXISTS public.management;
DROP TABLE IF EXISTS public.entertainer;
DROP TABLE IF EXISTS public.access_level;

DROP TABLE IF EXISTS public.personal_data;
DROP TABLE IF EXISTS public."user";

-- reprezentuje konto uzytkownika
CREATE TABLE public."user"
(
-- klucz glowny identyfikujacy rekord w tabeli
    id                   BIGSERIAL             NOT NULL,
-- login identyfikujacy konto uzytkownika w aplikacji
    login                CHARACTER VARYING(16) NOT NULL,
-- wynik uzycia funkcji Bcrypt na hasle
    password             CHARACTER(60)         NOT NULL,
-- adres email do kontaktu
    email                CHARACTER VARYING(64) NOT NULL,
-- czy konto jest niezablokowane
    is_active            BOOLEAN               NOT NULL DEFAULT TRUE,
-- czy adres email zostal zweryfikowany
    is_verified          BOOLEAN               NOT NULL DEFAULT FALSE,
-- zeton wysylany na adres email przy resetowaniu hasla
    password_reset_token CHARACTER(64),
-- czas wystawienia tokenu
    token_timestamp      TIMESTAMP WITH TIME ZONE,
--  ilosc blednych prob logowania
    failed_login         SMALLINT              NOT NULL DEFAULT 0,
-- wersja uzywana do blokad optymistycznych
    version              BIGINT                NOT NULL DEFAULT 1,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT user_login_key UNIQUE (login),
    CONSTRAINT user_email_key UNIQUE (email),
    CONSTRAINT user_email_correctness CHECK ( email ~* '^[-!#$%&*+-/=?^_`{|}~a-z0-9]+@[a-z]+.[a-z]{2,5}$'
        ),
    CONSTRAINT user_password_bcrypt_form CHECK ( password ~*
                                                 '^[$]2[abxy][$](?:0[4-9]|[12][0-9]|3[01])[$][./0-9a-zA-Z]{53}$'
        )
);

ALTER TABLE public."user"
    OWNER TO ssbd05admin;


-- reprezentuje dane osobowe uzytkownika
CREATE TABLE public.personal_data
(
-- klucz glowny i jednoczesnie klucz obcy odnoszacy się do rekordu z tabeli user
    user_id      BIGINT               NOT NULL,
-- imie uzytkownika
    name         CHARACTER VARYING(30),
-- nazwisko uzytkownika
    surname      CHARACTER VARYING(30),
-- numer telefonu
    phone_number CHARACTER VARYING(15),
-- plec
    is_man       BOOLEAN,
-- preferowany jezyk aplikacji
    language     CHARACTER VARYING(3) NOT NULL DEFAULT 'PL',
-- wersja uzywana do blokad optymistycznych
    version      BIGINT               NOT NULL DEFAULT 1,
    CONSTRAINT phone_number_correctness CHECK ( phone_number ~ '^[+]?[-\s0-9]{9,15}$'
        ),
    CONSTRAINT name_correctness CHECK ( name ~* '^[a-z\sżźćńółęąś]+$' ),
    CONSTRAINT surname_correctness CHECK ( surname ~* '^[a-z\sżźćńółęąś]+$' ),
    CONSTRAINT personal_data_pkey PRIMARY KEY (user_id),
    CONSTRAINT personal_data_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.personal_data
    OWNER TO ssbd05admin;


-- reprezentuje poziomy dostępu dostepne dla danego uzytkownika
CREATE TABLE public.access_level
(
-- klucz glowny identyfikujacy rekord w tabeli
    id           BIGSERIAL             NOT NULL,
-- klucz obcy odnoszacy się do rekordu z tabeli user
    user_id      BIGINT                NOT NULL,
-- wyroznik poziomu dostępu
    access_level CHARACTER VARYING(16) NOT NULL,
-- czy poziom jest niezablokowany
    is_active    BOOLEAN               NOT NULL DEFAULT TRUE,
-- wersja uzywana do blokad optymistycznych
    version      BIGINT                NOT NULL DEFAULT 1,
    CONSTRAINT access_level_correctness CHECK ( access_level.access_level IN ('ENTERTAINER', 'CLIENT', 'MANAGEMENT')),
    CONSTRAINT access_level_pkey PRIMARY KEY (id),
    CONSTRAINT access_level_user_id_access_level_key UNIQUE (user_id, access_level),
    CONSTRAINT access_level_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.access_level
    OWNER TO ssbd05admin;


-- zawiera dane specyficzne dla poziomu dostepu pracownik dzialu rozrywki
CREATE TABLE public.entertainer
(
-- klucz glowny, bedący kluczem obcym odnoszacym się do rekordu w tabeli public_access
    access_level_id BIGINT NOT NULL,
-- opis w profilu widoczny dla innych uzytkowników
    description     CHARACTER VARYING(2048),
-- srednia ocena pracownika przez klientow
    avg_rating      DOUBLE PRECISION,
-- wersja uzywana do blokad optymistycznych
    version         BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT entertainer_pkey PRIMARY KEY (access_level_id),
    CONSTRAINT entertainer_access_level_id_fkey FOREIGN KEY (access_level_id)
        REFERENCES public.access_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.entertainer
    OWNER TO ssbd05admin;


-- zawiera dane specyficzne dla poziomu dostepu zarząd
CREATE TABLE public.management
(
-- klucz glowny, bedący kluczem obcym odnoszącym sie do rekordu w tabeli public_access
    access_level_id BIGINT NOT NULL,
-- wersja uzywana do blokad optymistycznych
    version         BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT management_pkey PRIMARY KEY (access_level_id),
    CONSTRAINT management_access_level_id_fkey FOREIGN KEY (access_level_id)
        REFERENCES public.access_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.management
    OWNER TO ssbd05admin;


-- zawiera dane specyficzne dla poziomu dostepu klient
CREATE TABLE public.client
(
-- klucz glowny, bedący kluczem obcym odnoszacym się do rekordu w tabeli public_access
    access_level_id BIGINT NOT NULL,
-- wersja uzywana do blokad optymistycznych
    version         BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT client_pkey PRIMARY KEY (access_level_id),
    CONSTRAINT client_access_level_id_fkey FOREIGN KEY (access_level_id)
        REFERENCES public.access_level (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.client
    OWNER TO ssbd05admin;


-- zawiera niedysponowalnosci pracownika dzialu rozrywki
CREATE TABLE public.entertainer_unavailability
(
-- klucz glowny identyfikujacy rekord w tabeli
    id             BIGSERIAL                NOT NULL,
-- klucz obcy odnoszacy się do rekordu w tabeli entertainer
    entertainer_id BIGINT                   NOT NULL,
-- poczatek niedysponowalnosci pracownika
    date_time_from TIMESTAMP WITH TIME ZONE NOT NULL,
-- koniec niedysponowalnosci pracownika
    date_time_to   TIMESTAMP WITH TIME ZONE NOT NULL,
-- opcjonalny opis (na przyklad powod nieobecnosci)
    description    CHARACTER VARYING(350),
-- czy niedysponowalnosc jest wazna
    is_valid       BOOLEAN                  NOT NULL DEFAULT TRUE,
-- wersja uzywana do blokad optymistycznych
    version        BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT entertainer_unavailability_pkey PRIMARY KEY (id),
    CONSTRAINT entertainer_unavailability_entertainer_id_fkey FOREIGN KEY (entertainer_id)
        REFERENCES public.entertainer (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT entertainer_unavailability_date_check CHECK (date_time_from < date_time_to)
);

ALTER TABLE public.entertainer_unavailability
    OWNER TO ssbd05admin;


-- dziennik zdarzen dotyczacy polecen bazodanowych
CREATE TABLE public.query_log
(
-- klucz glowny identyfikujacy rekord w tabeli
    id               BIGSERIAL                NOT NULL,
-- identyfikator uzytkownika, ktory spowodowal polecenie
    user_id          BIGINT,
-- identyfikator czasowy zdarzenia
    action_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
-- zawartosc polecenia
    query            CHARACTER VARYING(512)   NOT NULL,
-- modul zwiazany z danym poleceniem
    module           CHARACTER VARYING(64)    NOT NULL,
-- tabela, ktorej polecenie to dotyczylo
    affected_table   CHARACTER VARYING(64),
-- wersja uzywana do blokad optymistycznych
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT module_correctness CHECK ( module IN ('mok', 'moo', 'auth')
        ),
    CONSTRAINT query_log_pkey PRIMARY KEY (id),
    CONSTRAINT query_log_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.query_log
    OWNER TO ssbd05admin;


-- dziennik zdarzen dotyczacy logowania sie uzytkownika
CREATE TABLE public.session_log
(
-- klucz glowny identyfikujacy rekord w tabeli
    id               BIGSERIAL                NOT NULL,
-- identyfikator uzytkownika, ktory probował sie zalogowac
    user_id          BIGINT,
-- znacznik czasowy zdarzenia
    action_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
-- adres logiczny, z którego uzytkownik sie logował
    ip_address       CHARACTER VARYING(15)    NOT NULL,
-- czy proba logowania byla pomyslna
    is_successful    BOOLEAN                  NOT NULL,
-- wersja uzywana do blokad optymistycznych
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT ip_address_correctness CHECK (ip_address ~
                                             '^(([0-2]?[0-9]?[0-9])?\s?([.]||[:])){3,7}([0-2]?[0-9]?[0-9])?\s?$'
        ),
    CONSTRAINT session_log_pkey PRIMARY KEY (id),
    CONSTRAINT session_log_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
);

ALTER TABLE public.session_log
    OWNER TO ssbd05admin;


-- dziennik zdarzen dotyczacy zmiany poziomu dostepu
CREATE TABLE public.access_level_change_log
(
-- klucz glowny identyfikujacy rekord w tabeli
    id               BIGSERIAL                NOT NULL,
-- identyfikator uzytkownika, ktory zmienial poziom dostepu
    user_id          BIGINT                   NOT NULL,
-- identyfikator czasowy zdarzenia
    action_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
-- docelowy poziom dostepu
    access_level     CHARACTER VARYING(15)    NOT NULL,
-- wersja uzywana do blokad optymistycznych
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT access_level_change_log_pkey PRIMARY KEY (id),
    CONSTRAINT access_level_change_log_fkey FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.access_level_change_log
    OWNER TO ssbd05admin;


-- zawiera oferty udostepniane przez pracownika dzialu rozrywki
CREATE TABLE public.offer
(
-- klucz glowny identyfikujacy rekord w tabeli
    id             BIGSERIAL                NOT NULL,
-- identyfikator pracownika dzialu rozrywki
    entertainer_id BIGINT                   NOT NULL,
-- tytul oferty
    title          CHARACTER VARYING(100)   NOT NULL,
-- opis oferty
    description    CHARACTER VARYING(350),
-- czy oferta jest niezablokowana
    is_active      BOOLEAN                  NOT NULL DEFAULT TRUE,
-- poczatek dostepności oferty
    valid_from     TIMESTAMP WITH TIME ZONE NOT NULL,
-- koniec dostepności oferty
    valid_to       TIMESTAMP WITH TIME ZONE NOT NULL,
-- srednia ocena oferty przez klientow
    avg_rating     DOUBLE PRECISION,
-- wersja uzywana do blokad optymistycznych
    version        BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT offer_pkey PRIMARY KEY (id),
    CONSTRAINT offer_entertainer_id_fkey FOREIGN KEY (entertainer_id)
        REFERENCES public.entertainer (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT offer_date_check CHECK (valid_from < valid_to)
);

ALTER TABLE public.offer
    OWNER TO ssbd05admin;


-- dostepnosci oferty w ciagu tygodnia
CREATE TABLE public.offer_availability
(
-- klucz glowny identyfikujacy rekord w tabeli
    id         BIGSERIAL           NOT NULL,
-- identyfikator oferty
    offer_id   BIGINT              NOT NULL,
-- dzien tygodnia
    week_day   INTEGER             NOT NULL,
-- poczatek godzin dostepnosci
    hours_from TIME WITH TIME ZONE NOT NULL,
-- koniec godzin dostępnosci
    hours_to   TIME WITH TIME ZONE NOT NULL,
-- wersja uzywana do blokad optymistycznych
    version    BIGINT              NOT NULL DEFAULT 1,
    CONSTRAINT offer_availability_pkey PRIMARY KEY (id),
    CONSTRAINT offer_availability_offer_id_fkey FOREIGN KEY (offer_id)
        REFERENCES public.offer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT week_day_check CHECK (week_day >= 0 AND week_day <= 6)
);

ALTER TABLE public.offer_availability
    OWNER TO ssbd05admin;


-- reprezentuje rezerwacje ofert przez klientow
CREATE TABLE public.reservation
(
-- klucz glowny identyfikujacy rekord w tabeli
    id               BIGSERIAL                NOT NULL,
-- identyfikator klienta
    client_id        BIGINT                   NOT NULL,
-- identyfikator oferty
    offer_id         BIGINT                   NOT NULL,
-- poczatek rezerwacji
    reservation_from TIMESTAMP WITH TIME ZONE NOT NULL,
-- koniec rezerwacji
    reservation_to   TIMESTAMP WITH TIME ZONE NOT NULL,
-- status rezerwacji
    status           CHARACTER VARYING(20),
-- ocena zakonczonej rezerwacji przez klienta
    rating           INTEGER,
-- komentarz klienta do oceny
    comment          CHARACTER VARYING(250),
-- wersja uzywana do blokad optymistycznych
    version          BIGINT                   NOT NULL DEFAULT 1,
    CONSTRAINT reservation_pkey PRIMARY KEY (id),
    CONSTRAINT reservation_client_id_fkey FOREIGN KEY (client_id)
        REFERENCES public.client (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT reservation_offer_id_fkey FOREIGN KEY (offer_id)
        REFERENCES public.offer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT reservation_date_check CHECK (reservation_from < reservation_to)
);

ALTER TABLE public.reservation
    OWNER TO ssbd05admin;


-- ulubione oferty klienta
CREATE TABLE public.favourites
(
-- identyfikator klienta
    client_id BIGINT NOT NULL,
-- identyfikator oferty
    offer_id  BIGINT NOT NULL,
-- wersja uzywana do blokad optymistycznych
    version   BIGINT NOT NULL DEFAULT 1,
-- kluczem glównym jest para identyfikator klienta i identyfikator oferty
    CONSTRAINT favourites_pkey PRIMARY KEY (client_id, offer_id),
    CONSTRAINT favourites_client_id_fkey FOREIGN KEY (client_id)
        REFERENCES public.client (access_level_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT favourites_offer_id_fkey FOREIGN KEY (offer_id)
        REFERENCES public.offer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE public.favourites
    OWNER TO ssbd05admin;


-- widok laczący uzytkownikow, z przypisanymi im poziomami dostepow
CREATE OR REPLACE VIEW public.authentication_view
AS
SELECT al.id,
       u.login,
       u.password,
       al.access_level
FROM public."user" u
         JOIN access_level al
              ON u.id = al.user_id
WHERE u.is_active
  AND u.is_verified
  AND u.password_reset_token IS NULL
  AND al.is_active;

ALTER TABLE public.authentication_view
    OWNER TO ssbd05admin;


-- uprawnienia dla uzytkowników bazodanowych do poszczegolnych tabel
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.access_level TO ssbd05mok;
GRANT SELECT, UPDATE ON TABLE public.access_level TO ssbd05moo;
GRANT SELECT ON TABLE public.authentication_view TO ssbd05auth;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public."user" TO ssbd05mok;
GRANT SELECT ON TABLE public."user" TO ssbd05moo;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.session_log TO ssbd05mok;
GRANT INSERT, SELECT, UPDATE ON TABLE public.reservation TO ssbd05moo;
GRANT INSERT ON TABLE public.query_log TO ssbd05moo;
GRANT INSERT ON TABLE public.query_log TO ssbd05mok;
GRANT INSERT, UPDATE, SELECT, DELETE ON TABLE public.personal_data TO ssbd05mok;
GRANT SELECT ON TABLE public.personal_data TO ssbd05moo;
GRANT INSERT, SELECT, UPDATE ON TABLE public.offer_availability TO ssbd05moo;
GRANT INSERT, SELECT, UPDATE ON TABLE public.offer TO ssbd05moo;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.management TO ssbd05mok;
GRANT SELECT ON TABLE public.management TO ssbd05moo;
GRANT INSERT, SELECT, DELETE ON TABLE public.favourites TO ssbd05moo;
GRANT INSERT, SELECT, UPDATE ON TABLE public.entertainer_unavailability TO ssbd05moo;
GRANT SELECT, UPDATE, DELETE ON TABLE public.entertainer_unavailability TO ssbd05mok;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.entertainer TO ssbd05mok;
GRANT INSERT, SELECT, UPDATE ON TABLE public.entertainer TO ssbd05moo;
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE public.client TO ssbd05mok;
GRANT SELECT ON TABLE public.client TO ssbd05moo;
GRANT INSERT ON TABLE public.access_level_change_log TO ssbd05mok;

--uprawnienia do poszczegolnych sekwencji
GRANT USAGE ON SEQUENCE public.access_level_change_log_id_seq TO ssbd05mok;
GRANT USAGE ON SEQUENCE public.entertainer_unavailability_id_seq TO ssbd05moo;
GRANT USAGE ON SEQUENCE public.offer_id_seq TO ssbd05moo;
GRANT USAGE ON SEQUENCE public.offer_availability_id_seq TO ssbd05moo;
GRANT USAGE ON SEQUENCE public.query_log_id_seq TO ssbd05moo;
GRANT USAGE ON SEQUENCE public.query_log_id_seq TO ssbd05mok;
GRANT USAGE ON SEQUENCE public.reservation_id_seq TO ssbd05moo;
GRANT USAGE ON SEQUENCE public.session_log_id_seq TO ssbd05mok;
GRANT USAGE ON SEQUENCE public.session_log_id_seq TO ssbd05auth;
GRANT USAGE ON SEQUENCE public.user_id_seq TO ssbd05mok;
GRANT USAGE ON SEQUENCE public.access_level_id_seq TO ssbd05mok;

--indeksy dla kluczy obcych
DROP
    INDEX IF EXISTS personal_data_user_id;
CREATE
    INDEX personal_data_user_id
    ON public.personal_data USING btree
        (user_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS query_log_user_id;
CREATE
    INDEX query_log_user_id
    ON public.query_log USING btree
        (user_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS access_level_user_id;
CREATE
    INDEX access_level_user_id
    ON public.access_level USING btree
        (user_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS access_level_change_log_user_id;
CREATE
    INDEX access_level_change_log_user_id
    ON public.access_level_change_log USING btree
        (user_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS session_log_user_id;
CREATE
    INDEX session_log_user_id
    ON public.session_log USING btree
        (user_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS entertainer_access_level_id;
CREATE
    INDEX entertainer_access_level_id
    ON public.entertainer USING btree
        (access_level_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS management_access_level_id;
CREATE
    INDEX management_access_level_id
    ON public.management USING btree
        (access_level_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS client_access_level_id;
CREATE
    INDEX client_access_level_id
    ON public.client USING btree
        (access_level_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS reservation_client_id;
CREATE
    INDEX reservation_client_id
    ON public.reservation USING btree
        (client_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS reservation_offer_id;
CREATE
    INDEX reservation_offer_id
    ON public.reservation USING btree
        (offer_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS entertainer_unavailability_entertainer_id;
CREATE
    INDEX entertainer_unavailability_entertainer_id
    ON public.entertainer_unavailability USING btree
        (entertainer_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS offer_entertainer_id;
CREATE
    INDEX offer_entertainer_id
    ON public.offer USING btree
        (entertainer_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS favourites_client_id;
CREATE
    INDEX favourites_client_id
    ON public.favourites USING btree
        (client_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS favourites_offer_id;
CREATE
    INDEX favourites_offer_id
    ON public.favourites USING btree
        (offer_id ASC NULLS LAST)
    TABLESPACE pg_default;

DROP
    INDEX IF EXISTS offer_availability_offer_id;
CREATE
    INDEX offer_availability_offer_id
    ON public.offer_availability USING btree
        (offer_id ASC NULLS LAST)
    TABLESPACE pg_default;
