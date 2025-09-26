INSERT INTO "users" (id, username, password, active)
VALUES (1, 'admin', '{bcrypt}$2a$08$xbAkREmunqDyLeB975w43O1to/HZaQiPgAxNU88X25uKk3mMaaz0.', true);

INSERT INTO user_role (user_id, roles)
VALUES (1, 'ADMIN');
