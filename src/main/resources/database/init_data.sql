# user
# login:jkowalski haslo:Kowal123!
INSERT INTO user (id, login, password, email, is_active, is_verified)
VALUES (-1, 'jkowalski', '$2y$12$ealytKG0z6p3hipT2XpxfeDDL7fPS7Dy5aDKkOzKaMnSUfoYnCuPu', 'kowal@example.com', true,
        true);
# login:anowak haslo:Nowak123!
INSERT INTO user (id, login, password, email, is_active, is_verified)
VALUES (-2, 'anowak', '$2y$12$Rv/0rpWtKOKcoM2k2R7zbOyNjSx2hVDceU8EE9RXJRl6URTQehw/a', 'nowak@example.com', true, true);
# login:msipinski haslo:Sipin123!
INSERT INTO user (id, login, password, email, is_active, is_verified)
VALUES (-3, 'msipinski', '$2y$12$q1rYhhNHlYr2Mq.vZuIklO31YsVliLfii2ug4WkvB7D.CtFUUhnNy', 'sipin@example.com', true,
        true);
# login:aremplewicz haslo:Rempek123!
INSERT INTO user (id, login, password, email, is_active, is_verified)
VALUES (-4, 'aremplewicz', '$2y$12$yyzobmoHaPCC87aPF27I2.4IjNRPLo12KNB8HE27u15dcPczlRls6', 'rempek@example.com', true,
        true);
# login:adrajling haslo:Drajling123!
INSERT INTO user (id, login, password, email, is_active, is_verified)
VALUES (-5, 'adrajling', '$2y$12$g66Xs5jGrjGiSQRI.ikuMeadyAHwpgF4noiofSG09j47CQV.EnDK6', 'drajling@example.com', true,
        true);

# personal_data
INSERT INTO personal_data (user_id, name, surname, phone_number, is_man)
VALUES (-1, 'Jan', 'Kowalski', '111111111', true);
INSERT INTO personal_data (user_id, name, surname, phone_number, is_man)
VALUES (-2, 'Anna', 'Nowak', '222222222', false);
INSERT INTO personal_data (user_id, name, surname, phone_number, is_man)
VALUES (-3, 'Mateusz', 'Sipiński', '333333333', true);
INSERT INTO personal_data (user_id, name, surname, phone_number, is_man, language)
VALUES (-4, 'Arkadiusz', 'Remplewicz', '444444444', true, 'ENG');
INSERT INTO personal_data (user_id, name, surname, phone_number, is_man)
VALUES (-5, 'Aleksander', 'Drajling', '555555555', true);

# access_level
INSERT INTO access_level (id, user_id, access_level,is_active)
VALUES (-11, -1, 'ENTERTAINER',true);
INSERT INTO access_level (id, user_id, access_level,is_active)
VALUES (-21, -1, 'CLIENT',false);
INSERT INTO access_level (id, user_id, access_level,is_active)
VALUES (-31, -1, 'MANAGEMENT',false);
INSERT INTO access_level (id, user_id, access_level,is_active)
VALUES (-12, -2, 'CLIENT',true);
INSERT INTO access_level (id, user_id, access_level,is_active)
VALUES (-22, -2, 'ENTERTAINER',false);
INSERT INTO access_level (id, user_id, access_level,is_active)
VALUES (-32, -2, 'MANAGEMENT',false);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-13, -3, 'MANAGEMENT', true);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-23, -3, 'CLIENT', false);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-33, -3, 'ENTERTAINER', true);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-14, -4, 'ENTERTAINER', false);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-24, -4, 'CLIENT', false);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-34, -4, 'MANAGEMENT', false);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-15, -5, 'MANAGEMENT', true);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-25, -5, 'CLIENT', false);
INSERT INTO access_level (id, user_id, access_level, is_active)
VALUES (-35, -5, 'ENTERTAINER', false);

# entertainer
INSERT INTO entertainer (access_level_id, description, avg_rating)
VALUES (-11, 'Nasz pierwszy pracownik', 4.2);
INSERT INTO entertainer (access_level_id, description, avg_rating)
VALUES (-14, 'Nasz najlepszy pracownik', 9.7);
INSERT INTO entertainer (access_level_id)
VALUES (-22);
INSERT INTO entertainer (access_level_id, description, avg_rating)
VALUES (-33, 'Nasz najgorszy pracownik', 2.1);
INSERT INTO entertainer (access_level_id)
VALUES (-35);


#  entertainer_unavailability
INSERT INTO entertainer_unavailability (id, entertainer_id, date_time_from, date_time_to, description)
VALUES (-91, -11, '2021-10-01 06:00:00', '2021-10-14 20:00:00', 'Będę chory');

# management
INSERT INTO management (access_level_id)
VALUES (-13);
INSERT INTO management (access_level_id)
VALUES (-15);
INSERT INTO management (access_level_id)
VALUES (-32);
INSERT INTO management (access_level_id)
VALUES (-34);
INSERT INTO management (access_level_id)
VALUES (-31);


# client
INSERT INTO client (access_level_id)
VALUES (-12);
INSERT INTO client (access_level_id)
VALUES (-21);
INSERT INTO client (access_level_id)
VALUES (-23);
INSERT INTO client (access_level_id)
VALUES (-24);
INSERT INTO client (access_level_id)
VALUES (-25);

# query_log
INSERT INTO query_log (id, user_id, query, module)
VALUES (-101, -1, 'Select * FROM user', 'mok');
INSERT INTO query_log (id, user_id, query, module)
VALUES (-102, -1, 'Select * FROM entertainer', 'mok');
INSERT INTO query_log (id, user_id, query, module)
VALUES (-103, -2, 'Select * FROM access_level WHERE access_level == "CLIENT"', 'mok');

# session_log
INSERT INTO session_log (id, user_id, action_timestamp, ip_address, is_successful)
VALUES (-201, -1, '2021-02-02 19:10:25', '127.0.0.1', false);
INSERT INTO session_log (id, user_id, action_timestamp, ip_address, is_successful)
VALUES (-202, -2, '2021-04-02 16:20:00', '127.0.0.1', true);
INSERT INTO session_log (id, user_id, action_timestamp, ip_address, is_successful)
VALUES (-203, -4, '2021-04-02 16:25:01', '123.232.0.33', true);
INSERT INTO session_log (id, user_id, action_timestamp, ip_address, is_successful)
VALUES (-204, -4, '2021-04-03 21:01:41', '192.168.0.1', true);

# access_level_change_log
INSERT INTO access_level_change_log (id, user_id, action_timestamp, access_level)
VALUES (-301, -1, '2021-01-01 11:20:13', 'CLIENT');
INSERT INTO access_level_change_log (id, user_id, action_timestamp, access_level)
VALUES (-302, -1, '2021-01-02 8:12:13', 'ENTERTAINER');
INSERT INTO access_level_change_log (id, user_id, action_timestamp, access_level)
VALUES (-303, -5, '2021-03-12 17:02:13', 'MANAGEMENT');

# offer
INSERT INTO offer (id, entertainer_id, title, description, valid_from, valid_to)
VALUES (-51, -11, 'Picie wody na zawody', 'Pije wode duszkiem, a ty liczysz czas', '2021-06-01 11:20:13',
        '2021-08-20 23:00:00');
INSERT INTO offer (id, entertainer_id, title, description, valid_from, valid_to)
VALUES (-52, -11, 'Animator urodzinowy dla dzieci', 'Wszystkie dzieci nasze są',
        '2021-05-01 10:03:13', '2021-10-01 00:00:00');
INSERT INTO offer (id, entertainer_id, title, description, valid_from, valid_to)
VALUES (-53, -11, 'Rzeźbienie w styropianie', 'Tak jak w tytule, koszty materiału pokrywa klient',
        '2020-01-01 00:03:13', '2022-01-01 00:00:00');
INSERT INTO offer (id, entertainer_id, title, description, valid_from, valid_to)
VALUES (-54, -14, 'Rozbawię kazdego', 'Dowcipy i wygłupy, jeśli zachowasz powagę - zwracam pieniadzę',
        '2019-07-01 10:03:13', '2022-09-11 00:00:00');
INSERT INTO offer (id, entertainer_id, title, description, valid_from, valid_to)
VALUES (-55, -14, 'Wizualizacje świetlne latarką LED', 'Dla tych, którzy szukają mocnych wrażeń',
        '2021-02-24 00:05:13', '2021-12-23 00:00:00');

# offer_availability
INSERT INTO offer_availability (id, offer_id, week_day, hours_from, hours_to)
VALUES (-61, -51, 6, '10:00:00', '18:00:00');
INSERT INTO offer_availability (id, offer_id, week_day, hours_from, hours_to)
VALUES (-62, -52, 5, '10:00:00', '18:00:00');
INSERT INTO offer_availability (id, offer_id, week_day, hours_from, hours_to)
VALUES (-63, -53, 0, '08:00:00', '12:00:00');
INSERT INTO offer_availability (id, offer_id, week_day, hours_from, hours_to)
VALUES (-64, -54, 1, '06:00:00', '20:00:00');
INSERT INTO offer_availability (id, offer_id, week_day, hours_from, hours_to)
VALUES (-65, -55, 2, '12:00:00', '22:00:00');

# reservation
INSERT INTO reservation (id, client_id, offer_id, reservation_from, reservation_to, status)
VALUES (-71, -12, -51, '2021-06-12 11:00:00', '2021-06-12 11:15:00', 'ENDED');
INSERT INTO reservation (id, client_id, offer_id, reservation_from, reservation_to, status)
VALUES (-72, -12, -52, '2021-06-11 15:00:00', '2021-06-12 16:15:00', 'CANCELED');
INSERT INTO reservation (id, client_id, offer_id, reservation_from, reservation_to, status)
VALUES (-73, -12, -53, '2021-05-16 13:00:00', '2021-05-16 14:00:00', 'ACCEPTED');
INSERT INTO reservation (id, client_id, offer_id, reservation_from, reservation_to, status)
VALUES (-74, -12, -51, '2021-06-12 13:00:00', '2021-06-12 14:00:00', 'PENDING');

# favourites
INSERT INTO favourites (client_id, offer_id)
VALUES (-12, -51);