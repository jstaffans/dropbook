--liquibase formatted sql

--changeset jstaffans:1
INSERT INTO ROLES ( ROLE_NAME ) VALUES ('admin');
INSERT INTO ROLES ( ROLE_NAME ) VALUES ('user');
INSERT INTO ROLES ( ROLE_NAME ) VALUES ('other');
--rollback TRUNCATE TABLE ROLES AND COMMIT;
