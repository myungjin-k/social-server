DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       seq               bigint NOT NULL AUTO_INCREMENT,
                       name              varchar(10) NOT NULL,
                       email             varchar(50) NOT NULL,
                       passwd            varchar(80) NOT NULL,
                       profile_image_url varchar(255) DEFAULT NULL,
                       login_count       int NOT NULL DEFAULT 0,
                       last_login_at     datetime DEFAULT NULL,
                       create_at         datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                       role              varchar(10) DEFAULT 'USER',
                       PRIMARY KEY (seq),
                       CONSTRAINT unq_user_email UNIQUE (email)
);

CREATE TABLE posts (
                       seq                bigint NOT NULL AUTO_INCREMENT,
                       user_seq           bigint NOT NULL,
                       contents           varchar(500) NOT NULL,
                       post_image_url     varchar(255) DEFAULT  NULL,
                       like_count         int NOT NULL DEFAULT 0,
                       comment_count      int NOT NULL DEFAULT 0,
                       create_at          datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                       PRIMARY KEY (seq),
                       CONSTRAINT fk_post_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE comments (
                          seq       bigint NOT NULL AUTO_INCREMENT,
                          user_seq  bigint NOT NULL,
                          post_seq  bigint NOT NULL,
                          contents  varchar(500) NOT NULL,
                          create_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                          PRIMARY KEY (seq),
                          CONSTRAINT fk_comment_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,
                          CONSTRAINT fk_comment_to_post FOREIGN KEY (post_seq) REFERENCES posts (seq) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE connections (
                             seq         bigint NOT NULL AUTO_INCREMENT,
                             user_seq    bigint NOT NULL,
                             target_seq  bigint NOT NULL,
                             granted_at  datetime DEFAULT NULL,
                             create_at   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                             PRIMARY KEY (seq),
                             CONSTRAINT fk_connection_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,
                             CONSTRAINT fk_connection_to_user2 FOREIGN KEY (target_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE likes (
                       seq       bigint NOT NULL AUTO_INCREMENT,
                       user_seq  bigint NOT NULL,
                       post_seq  bigint NOT NULL,
                       create_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                       PRIMARY KEY (seq),
                       CONSTRAINT unq_likes_user_post UNIQUE (user_seq, post_seq),
                       CONSTRAINT fk_likes_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT,
                       CONSTRAINT fk_likes_to_post FOREIGN KEY (post_seq) REFERENCES posts (seq) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE subscriptions (
                               seq         bigint NOT NULL AUTO_INCREMENT,
                               user_seq    bigint NOT NULL,
                               endpoint    varchar(200) NOT NULL,
                               public_key  varchar(200) NOT NULL,
                               auth        varchar(200) NOT NULL,
                               create_at   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                               CONSTRAINT unq_subscriptions UNIQUE (user_seq, endpoint),
                               CONSTRAINT fk_subscriptions_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE notifications (
                               seq          bigint NOT NULL AUTO_INCREMENT,
                               user_seq     bigint NOT NULL,
                               message      varchar(200) NOT NULL,
                               click_target varchar(200) DEFAULT NULL,
                               create_at    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                               PRIMARY KEY  (seq),
                               CONSTRAINT   fk_notifications_to_user FOREIGN KEY (user_seq) REFERENCES users (seq) ON DELETE RESTRICT ON UPDATE RESTRICT
);