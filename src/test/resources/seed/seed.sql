INSERT INTO users (id, username, email, password, created_at, updated_at, roles)
VALUES ('01992abc-3c1c-7305-bf26-c5fdb14ff811'::uuid,
        'testuser1',
        'testuser1@example.com',
        '$argon2id$v=19$m=16384,t=2,p=1$uln4SvRxxoS8f5IygQRLCQ$M8va3TEH3DzeMiBP8aAkImT3yKQHKwxCDeNu1WofnX8',
        '2025-09-08T19:10:03.480678+00'::timestamptz,
        '2025-09-08T19:10:03.480678+00'::timestamptz,
        'USER');

INSERT INTO users (id, username, email, password, created_at, updated_at, roles)
VALUES ('01992abc-3c1c-7305-bf26-c5fdb14ff812'::uuid,
        'testuser2',
        'testuser2@example.com',
        '$argon2id$v=19$m=16384,t=2,p=1$uln4SvRxxoS8f5IygQRLCQ$M8va3TEH3DzeMiBP8aAkImT3yKQHKwxCDeNu1WofnX8',
        '2025-09-08T19:10:03.480678+00'::timestamptz,
        '2025-09-08T19:10:03.480678+00'::timestamptz,
        'USER');

INSERT INTO users (id, username, email, password, created_at, updated_at, roles)
VALUES ('01992abc-3c1c-7305-bf26-c5fdb14ff813'::uuid,
        'testadmin',
        'testadmin@example.com',
        '$argon2id$v=19$m=16384,t=2,p=1$uln4SvRxxoS8f5IygQRLCQ$M8va3TEH3DzeMiBP8aAkImT3yKQHKwxCDeNu1WofnX8',
        '2025-09-08T19:10:03.480678+00'::timestamptz,
        '2025-09-08T19:10:03.480678+00'::timestamptz,
        'ADMIN');