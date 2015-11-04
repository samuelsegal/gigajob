--<ScriptOptions statementTerminator=";"/>

ALTER TABLE `database`.`roles` DROP PRIMARY KEY;

ALTER TABLE `database`.`UserConnection` DROP PRIMARY KEY;

ALTER TABLE `database`.`users` DROP PRIMARY KEY;

ALTER TABLE `database`.`UserConnection` DROP INDEX `database`.`UserConnectionRank`;

ALTER TABLE `database`.`user_roles` DROP INDEX `database`.`role`;

ALTER TABLE `database`.`user_roles` DROP INDEX `database`.`FK_g1uebn6mqk9qiaw45vnacmyo2`;

DROP TABLE `database`.`users`;

DROP TABLE `database`.`UserConnection`;

DROP TABLE `database`.`user_roles`;

DROP TABLE `database`.`roles`;

CREATE TABLE `database`.`users` (
	`id` INT NOT NULL,
	`dob` DATETIME,
	`email` VARCHAR(255) NOT NULL,
	`firstName` VARCHAR(255),
	`lastName` VARCHAR(255),
	`password` VARCHAR(255) NOT NULL,
	`login` VARCHAR(255),
	`imageUrl` VARCHAR(512),
	`first_name` VARCHAR(255),
	`image_url` VARCHAR(255),
	`last_name` VARCHAR(255),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `gigajob`.`UserConnection` (
	`userId` VARCHAR(255) NOT NULL,
	`providerId` VARCHAR(255) NOT NULL,
	`providerUserId` VARCHAR(255) NOT NULL,
	`rank` INT NOT NULL,
	`displayName` VARCHAR(255),
	`profileUrl` VARCHAR(512),
	`imageUrl` VARCHAR(512),
	`accessToken` VARCHAR(512) NOT NULL,
	`secret` VARCHAR(512),
	`refreshToken` VARCHAR(512),
	`expireTime` BIGINT,
	PRIMARY KEY (`userId`,`providerId`,`providerUserId`)
) ENGINE=InnoDB;

CREATE TABLE `database`.`user_roles` (
	`user_id` INT NOT NULL,
	`role_id` INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE `database`.`roles` (
	`id` INT NOT NULL,
	`role` VARCHAR(255),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX `UserConnectionRank` ON `database`.`UserConnection` (`userId` ASC);

CREATE INDEX `role` ON `database`.`user_roles` (`role_id` ASC);

CREATE INDEX `FK_g1uebn6mqk9qiaw45vnacmyo2` ON `database`.`user_roles` (`user_id` ASC);

