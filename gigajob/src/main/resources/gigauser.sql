CREATE TABLE users (
	id INT NOT NULL,
	dob DATETIME,
	email VARCHAR(255) NOT NULL,
	firstName VARCHAR(255),
	lastName VARCHAR(255),
	password VARCHAR(255) NOT NULL,
	login VARCHAR(255),
	imageUrl VARCHAR(512),
	PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
	user_id INT NOT NULL,
	role_id INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE roles (
	id INT NOT NULL,
	role VARCHAR(255),
	PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE UserConnection (
	userId VARCHAR(255) NOT NULL,
	providerId VARCHAR(255) NOT NULL,
	providerUserId VARCHAR(255) NOT NULL,
	rank INT NOT NULL,
	displayName VARCHAR(255),
	profileUrl VARCHAR(512),
	imageUrl VARCHAR(512),
	accessToken VARCHAR(512) NOT NULL,
	secret VARCHAR(512),
	refreshToken VARCHAR(512),
	expireTime BIGINT,
	PRIMARY KEY (userId,providerId,providerUserId)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX UserConnectionRank ON UserConnection (userId ASC);

CREATE INDEX role ON user_roles (role_id ASC);

CREATE UNIQUE INDEX UserConnectionRank ON UserConnection (providerId ASC);

CREATE INDEX user ON user_roles (user_id ASC);

CREATE UNIQUE INDEX UserConnectionRank ON UserConnection (rank ASC);

CREATE UNIQUE INDEX UK_avh1b2ec82audum2lyjx2p1ws ON users (email ASC);

ALTER TABLE users ADD PRIMARY KEY (id);

ALTER TABLE UserConnection ADD PRIMARY KEY (userId, providerId, providerUserId);

ALTER TABLE user_roles ADD CONSTRAINT FK_g1uebn6mqk9qiaw45vnacmyo2 FOREIGN KEY (user_id)
	REFERENCES users (id);

ALTER TABLE user_roles ADD CONSTRAINT FK_5q4rc4fh1on6567qk69uesvyf FOREIGN KEY (role_id)
	REFERENCES roles (id);

