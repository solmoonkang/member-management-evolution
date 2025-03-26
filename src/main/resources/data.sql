INSERT INTO TBL_MEMBERS (email,
                         password,
                         nickname,
                         registration_number,
                         address,
                         role,
                         created_at)
VALUES ('admin@gmail.com',
        '$2a$10$aMM.1eIvrfIwxox7BoSoy.3pgi4jOm.Dp25Nkk21xFZ1XoScKnnIe',
        'admin',
        'XYSgR3w0IeBoMH4DvXK8g+BZwuQrxvXC6cfDGr1qgTk=',
        'Inchang-dong, Guri-si, Gyeonggi-do, Republic of Korea',
        'ADMIN',
        CURRENT_TIMESTAMP);
