-- =====================================================================
--  Probadores Rincón Sur - Base de datos MySQL (rinconsur_db)
--
--  Este script NO borra nada:
--    * CREATE ... IF NOT EXISTS  -> crea solo lo que falta.
--    * INSERT IGNORE             -> agrega los datos de ejemplo solo si
--                                   no existen (mismo id). Los datos que
--                                   ya tengas no se modifican.
--
--  Cómo usarlo (Laragon / MySQL 8):
--    1. Iniciar MySQL en Laragon.
--    2. Abrir HeidiSQL (o MySQL Workbench) y ejecutar este archivo completo.
--    3. Iniciar el backend. (Con spring.jpa.hibernate.ddl-auto=update el
--       backend también crea las tablas que falten, pero así el proyecto
--       parte con los mismos datos en cualquier computador.)
--
--  Usuarios de prueba (la clave está guardada con BCrypt):
--    admin@rinconsur.cl  (Administrador)
--    maria@rinconsur.cl  (Personal, con la sede 2 asignada)
-- =====================================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `rinconsur_db`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE `rinconsur_db`;

-- ---------------------------------------------------------------------
--  TABLAS
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `tipo_usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_usuario` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlx0blyydjomk7i1u81fgv3i31` (`tipo_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `sedes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `tiempo_alerta` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKli6ju353rjef6tcxlaxo43ao9` (`nombre`),
  CONSTRAINT `sedes_chk_1` CHECK (((`tiempo_alerta` <= 60) and (`tiempo_alerta` >= 5)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `tipo_usuario_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `fk_user_tipo_usuario` (`tipo_usuario_id`),
  CONSTRAINT `fk_user_tipo_usuario` FOREIGN KEY (`tipo_usuario_id`) REFERENCES `tipo_usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `probadores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cantidad_prendas` int NOT NULL,
  `estado` enum('FUERA_DE_SERVICIO','LIBRE','OCUPADO','PRENDA_OLVIDADA') NOT NULL,
  `inicio_ocupacion` bigint DEFAULT NULL,
  `numero` int NOT NULL,
  `sede_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_probador_sede_numero` (`sede_id`,`numero`),
  CONSTRAINT `fk_probador_sede` FOREIGN KEY (`sede_id`) REFERENCES `sedes` (`id`),
  CONSTRAINT `probadores_chk_1` CHECK (((`cantidad_prendas` >= 0) and (`cantidad_prendas` <= 4))),
  CONSTRAINT `probadores_chk_2` CHECK ((`numero` >= 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `registros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cantidad_prendas` int NOT NULL,
  `fin_millis` bigint NOT NULL,
  `inicio_millis` bigint NOT NULL,
  `numero_probador` int NOT NULL,
  `observacion` text NOT NULL,
  `prenda_olvidada` bit(1) NOT NULL,
  `sede_id` int NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `registros_chk_1` CHECK (((`cantidad_prendas` <= 4) and (`cantidad_prendas` >= 0))),
  CONSTRAINT `registros_chk_2` CHECK ((`numero_probador` >= 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `reportes_prenda` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descripcion` text NOT NULL,
  `fecha_millis` bigint NOT NULL,
  `foto_uri` text,
  `probador_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_reporte_probador` (`probador_id`),
  CONSTRAINT `fk_reporte_probador` FOREIGN KEY (`probador_id`) REFERENCES `probadores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Sedes que puede ver cada usuario de tipo Personal
CREATE TABLE IF NOT EXISTS `usuario_sede` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `sede_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_sede` (`usuario_id`,`sede_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
--  DATOS EXISTENTES (se agregan solo si no existen)
-- ---------------------------------------------------------------------

INSERT IGNORE INTO `tipo_usuarios` (`id`, `tipo_usuario`) VALUES
  (1, 'Administrador'),
  (3, 'Personal');

INSERT IGNORE INTO `sedes` (`id`, `nombre`, `tiempo_alerta`) VALUES
  (2, 'Rincon Sur Santiago', 15);

INSERT IGNORE INTO `users` (`id`, `email`, `name`, `password`, `tipo_usuario_id`) VALUES
  (2, 'admin@rinconsur.cl', 'Ana Castillo', '$2a$10$t8m5uVI0pKt6IBxdGBzxSud6cD7OXr15SfAlCQYXkBOu41c./QEse', 1),
  (3, 'maria@rinconsur.cl', 'María Pérez', '$2a$10$jxGLLPkQ5rawJWpIFQq6DeIVhHJs5gGIt29x2lc2vAyCb.QLLdc0q', 3);

INSERT IGNORE INTO `probadores` (`id`, `cantidad_prendas`, `estado`, `inicio_ocupacion`, `numero`, `sede_id`) VALUES
  (2, 0, 'PRENDA_OLVIDADA', NULL, 1, 2);

INSERT IGNORE INTO `registros` (`id`, `cantidad_prendas`, `fin_millis`, `inicio_millis`, `numero_probador`, `observacion`, `prenda_olvidada`, `sede_id`) VALUES
  (1, 3, 1795540823966, 1795540759802, 1, '', b'0', 2),
  (2, 2, 1795540885116, 1795540864771, 1, 'Prenda olvidada en el probador', b'1', 2),
  (3, 3, 1795545681924, 1795545681758, 1, 'Prenda olvidada en el probador', b'1', 2);

INSERT IGNORE INTO `reportes_prenda` (`id`, `descripcion`, `fecha_millis`, `foto_uri`, `probador_id`) VALUES
  (1, 'Polera negra talla M olvidada en el probador', 1795545408637, NULL, 2),
  (2, 'Chaqueta olvidada', 1795545681958, NULL, 2);

-- María (Personal) ve la sede 2. El administrador puede cambiarlo desde la app (Usuarios > Editar).
INSERT IGNORE INTO `usuario_sede` (`usuario_id`, `sede_id`) VALUES
  (3, 2);
