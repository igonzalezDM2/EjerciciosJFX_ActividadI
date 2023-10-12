DROP SCHEMA IF EXISTS `personas` ;
CREATE SCHEMA IF NOT EXISTS `personas` DEFAULT CHARACTER SET latin1 COLLATE latin1_spanish_ci;

USE `personas` ;

DROP TABLE IF EXISTS `personas`.`Persona` ;

CREATE TABLE
    IF NOT EXISTS `personas`.`Persona` (
        `id` INT NOT NULL AUTO_INCREMENT,
        `nombre` VARCHAR(250) NULL DEFAULT NULL,
        `apellidos` VARCHAR(250) NULL DEFAULT NULL,
        `edad` INT NULL DEFAULT NULL,
        PRIMARY KEY (`id`)
    ) ENGINE = InnoDB DEFAULT CHARSET = latin1;