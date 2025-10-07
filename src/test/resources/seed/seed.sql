INSERT INTO history (id, linux_user, ext, path, result, created_at)
VALUES ('01992abc-3c1c-7305-bf26-c5fdb14ff811'::uuid,
        'testuser1',
        'txt',
        '/path/to/dir1',
        '["file1.txt","file2.txt","file3.txt"]',
        '2025-09-08T19:10:03.480678+00'::timestamptz);

INSERT INTO history (id, linux_user, ext, path, result, created_at)
VALUES ('01992abc-3c1c-7305-bf26-c5fdb14ff812'::uuid,
        'testuser2',
        'txt',
        '/path/to/dir3',
        '["file4.txt","file5.txt","file6.txt"]',
        '2025-09-08T19:10:04.480678+00'::timestamptz);