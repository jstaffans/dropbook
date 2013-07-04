--liquibase formatted sql

--changeset jstaffans:1
INSERT INTO ROLES ( ROLE_NAME ) VALUES ('admin');
INSERT INTO ROLES ( ROLE_NAME ) VALUES ('user');
INSERT INTO ROLES ( ROLE_NAME ) VALUES ('other');
--rollback TRUNCATE TABLE ROLES AND COMMIT;

--changeset jstaffans:2
insert into roles_permissions ( ROLE_NAME, PERMISSION ) values ('admin', 'super');
insert into roles_permissions ( ROLE_NAME, PERMISSION ) values ('user', 'bar');
insert into roles_permissions ( ROLE_NAME, PERMISSION ) values ('admin', 'foo');
insert into roles_permissions ( ROLE_NAME, PERMISSION ) values ('other', 'gee');
--rollback TRUNCATE TABLE roles_permissions AND COMMIT;

--changeset jstaffans:3
INSERT INTO USERS ( USER_ID, USERNAME , PASSWORD ) VALUES (101, 'TEST', 'PASSWORD');
INSERT INTO USERS ( USER_ID, USERNAME , PASSWORD ) VALUES (102, 'Arnold', '12345678');
INSERT INTO USERS ( USERNAME , PASSWORD ) VALUES ('EXISTS', '12345678');
--rollback TRUNCATE TABLE USERS RESTART IDENTITY AND COMMIT;

--changeset jstaffans:4
insert into users_roles ( USER_ID, ROLE_NAME ) values (101, 'admin');
insert into users_roles ( USER_ID, ROLE_NAME ) values (101, 'user');
insert into users_roles ( USER_ID, ROLE_NAME ) values (102, 'user');
--rollback TRUNCATE TABLE users_roles AND COMMIT;