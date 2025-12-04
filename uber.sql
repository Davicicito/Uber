-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 04-12-2025 a las 10:27:24
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `uber`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estacion`
--

CREATE TABLE `estacion` (
  `id_estacion` int(11) NOT NULL,
  `ciudad` varchar(50) NOT NULL,
  `nombre_estacion` varchar(80) NOT NULL,
  `direccion` varchar(120) NOT NULL,
  `capacidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estacion`
--

INSERT INTO `estacion` (`id_estacion`, `ciudad`, `nombre_estacion`, `direccion`, `capacidad`) VALUES
(1, 'Madrid', 'Estación Atocha', 'Calle Atocha 23', 10),
(2, 'Barcelona', 'Estación Sants', 'Carrer de Tarragona 52', 12),
(3, 'Sevilla', 'Estación Santa Justa', 'Av. Kansas City 1', 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mantenimiento`
--

CREATE TABLE `mantenimiento` (
  `id_mantenimiento` int(11) NOT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `descripcion` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mantenimiento`
--

INSERT INTO `mantenimiento` (`id_mantenimiento`, `tipo`, `descripcion`) VALUES
(1, 'CAMBIO_RUEDAS', 'Sustitución de ruedas'),
(2, 'REVISION_GENERAL', 'Revisión completa del vehículo'),
(3, 'CAMBIO_BATERIA', 'Sustitución de batería'),
(4, 'FRENOS', 'Ajuste o cambio de frenos'),
(5, 'DIAGNOSTICO', 'Diagnóstico eléctrico');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva`
--

CREATE TABLE `reserva` (
  `id_reserva` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_vehiculo` int(11) NOT NULL,
  `fecha_hora_inicio` datetime NOT NULL,
  `fecha_hora_fin` datetime DEFAULT NULL,
  `coste` double DEFAULT NULL,
  `estado` enum('ACTIVA','FINALIZADA','CANCELADA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reserva`
--

INSERT INTO `reserva` (`id_reserva`, `id_usuario`, `id_vehiculo`, `fecha_hora_inicio`, `fecha_hora_fin`, `coste`, `estado`) VALUES
(1, 1, 1, '2025-01-10 10:00:00', '2025-01-10 11:15:00', 12.5, 'FINALIZADA'),
(2, 2, 3, '2025-01-11 09:30:00', NULL, NULL, 'ACTIVA'),
(3, 4, 5, '2025-01-12 17:20:00', '2025-01-12 18:10:00', 9.2, 'FINALIZADA'),
(4, 9, 1, '2025-11-27 10:45:00', '2025-11-27 12:10:25', 5, 'FINALIZADA'),
(5, 9, 1, '2025-11-27 11:13:00', '2025-11-27 12:14:01', 15, 'FINALIZADA'),
(6, 9, 5, '2025-12-02 18:07:00', '2025-12-02 19:08:04', 5, 'FINALIZADA'),
(7, 9, 3, '2025-12-03 08:10:00', '2025-12-03 13:33:59', 15, 'FINALIZADA'),
(8, 9, 1, '2025-12-03 12:41:00', '2025-12-03 13:41:35', 5, 'FINALIZADA'),
(9, 11, 1, '2025-12-03 13:10:00', '2025-12-03 14:11:32', 5, 'CANCELADA'),
(10, 1, 1, '2025-12-03 18:20:56', '2025-12-03 19:20:56', 7.5, 'ACTIVA'),
(11, 1, 1, '2025-12-03 18:25:09', '2025-12-03 19:25:09', 7.5, 'ACTIVA'),
(12, 9, 3, '2025-12-04 08:00:00', '2025-12-04 09:00:58', 5, 'FINALIZADA'),
(13, 9, 3, '2025-12-04 08:28:00', '2025-12-04 09:28:14', 10, 'FINALIZADA'),
(14, 14, 5, '2025-12-04 08:56:00', '2025-12-04 09:57:16', 5, 'FINALIZADA');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiene`
--

CREATE TABLE `tiene` (
  `id_vehiculo` int(11) NOT NULL,
  `id_mantenimiento` int(11) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `coste` double DEFAULT NULL,
  `notas` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tiene`
--

INSERT INTO `tiene` (`id_vehiculo`, `id_mantenimiento`, `fecha_hora`, `coste`, `notas`) VALUES
(1, 1, '2025-12-03 18:20:56', 55, 'Prueba inserción mantenimiento'),
(1, 1, '2025-12-03 18:25:09', 55, 'Prueba inserción mantenimiento'),
(2, 2, '2025-01-08 09:10:00', 40, 'Revisión rutinaria'),
(4, 1, '2025-01-05 12:00:00', 25, 'Ruedas desgastadas'),
(4, 3, '2025-12-02 19:16:02', 50, 'Cambio de bateria por fuerte golpe'),
(4, 4, '2025-01-05 12:30:00', 15, 'Ajuste de frenos'),
(6, 5, '2025-01-15 16:00:00', 10, 'Diagnóstico por apagado inesperado'),
(7, 1, '2025-12-04 09:00:22', 20, 'pinchazo');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellidos` varchar(80) NOT NULL,
  `email` varchar(80) NOT NULL,
  `contrasena` varchar(200) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `metodo_pago` varchar(50) DEFAULT NULL,
  `saldo` double DEFAULT 0,
  `estado_cuenta` enum('ACTIVO','SUSPENDIDO','BLOQUEADO') NOT NULL,
  `rol` varchar(20) DEFAULT 'USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `apellidos`, `email`, `contrasena`, `telefono`, `metodo_pago`, `saldo`, `estado_cuenta`, `rol`) VALUES
(1, 'Juan', 'García López', 'juan@example.com', 'pass123', '612345678', 'Tarjeta', 25.5, 'ACTIVO', 'CLIENTE'),
(2, 'María', 'Santos Ruiz', 'maria@example.com', 'pass123', '698745321', 'PayPal', 40, 'ACTIVO', 'CLIENTE'),
(3, 'Luis', 'Pérez Soto', 'luis@example.com', 'pass123', '666555444', 'Tarjeta', 10, 'ACTIVO', 'CLIENTE'),
(4, 'Ana', 'Martín Díaz', 'ana@example.com', 'pass123', '677889900', 'Bizum', 90, 'ACTIVO', 'CLIENTE'),
(7, 'Admin', 'Master', 'admin@uber.com', '1234', '600000000', 'TARJETA', 0, 'ACTIVO', 'ADMIN'),
(8, 'Juan', 'Cliente', 'juan@ejemplo.com', '1234', '611111111', 'PAYPAL', 20, 'BLOQUEADO', 'CLIENTE'),
(9, 'David', 'Montoro Guillén', 'david@gmail.com', 'Usuario_1', '651970888', 'EFECTIVO', 500, 'ACTIVO', 'CLIENTE'),
(11, 'Jesus', 'Galisteo', 'jesus@gmail.com', 'Usuario_1', NULL, 'SIN DEFINIR', 0, 'ACTIVO', 'CLIENTE'),
(14, 'Juan', 'Martinez', 'juan@gmail.com', 'Usuario_1', '651870235', 'TARJETA', 5, 'ACTIVO', 'CLIENTE');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculo`
--

CREATE TABLE `vehiculo` (
  `id_vehiculo` int(11) NOT NULL,
  `tipo` enum('COCHE','MOTO','PATINETE') NOT NULL,
  `marca` varchar(50) NOT NULL,
  `modelo` varchar(50) NOT NULL,
  `estado_vehiculo` enum('DISPONIBLE','EN_USO','MANTENIMIENTO','INACTIVO') NOT NULL,
  `id_estacion` int(11) NOT NULL,
  `nivel_bateria` double DEFAULT 100,
  `kilometraje` double DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `vehiculo`
--

INSERT INTO `vehiculo` (`id_vehiculo`, `tipo`, `marca`, `modelo`, `estado_vehiculo`, `id_estacion`, `nivel_bateria`, `kilometraje`) VALUES
(1, 'COCHE', 'Renault', 'Zoe', 'EN_USO', 2, 87, 12000),
(2, 'COCHE', 'Nissan', 'Leaf', 'EN_USO', 1, 45, 30000),
(3, 'MOTO', 'Silence', 'S02', 'DISPONIBLE', 2, 95, 5000),
(4, 'PATINETE', 'Xiaomi', 'Mi Pro 2', 'MANTENIMIENTO', 2, 10, 800),
(5, 'COCHE', 'Tesla', 'Model 3', 'DISPONIBLE', 3, 98, 15000),
(6, 'PATINETE', 'Segway', 'Ninebot', 'INACTIVO', 3, 0, 2000),
(7, 'COCHE', 'Tesla', 'Model Y', 'MANTENIMIENTO', 3, 100, 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `estacion`
--
ALTER TABLE `estacion`
  ADD PRIMARY KEY (`id_estacion`);

--
-- Indices de la tabla `mantenimiento`
--
ALTER TABLE `mantenimiento`
  ADD PRIMARY KEY (`id_mantenimiento`);

--
-- Indices de la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD PRIMARY KEY (`id_reserva`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_vehiculo` (`id_vehiculo`);

--
-- Indices de la tabla `tiene`
--
ALTER TABLE `tiene`
  ADD PRIMARY KEY (`id_vehiculo`,`id_mantenimiento`,`fecha_hora`),
  ADD KEY `id_mantenimiento` (`id_mantenimiento`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indices de la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  ADD PRIMARY KEY (`id_vehiculo`),
  ADD KEY `id_estacion` (`id_estacion`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `estacion`
--
ALTER TABLE `estacion`
  MODIFY `id_estacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `mantenimiento`
--
ALTER TABLE `mantenimiento`
  MODIFY `id_mantenimiento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `reserva`
--
ALTER TABLE `reserva`
  MODIFY `id_reserva` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  MODIFY `id_vehiculo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD CONSTRAINT `reserva_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `reserva_ibfk_2` FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculo` (`id_vehiculo`);

--
-- Filtros para la tabla `tiene`
--
ALTER TABLE `tiene`
  ADD CONSTRAINT `tiene_ibfk_1` FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculo` (`id_vehiculo`),
  ADD CONSTRAINT `tiene_ibfk_2` FOREIGN KEY (`id_mantenimiento`) REFERENCES `mantenimiento` (`id_mantenimiento`);

--
-- Filtros para la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  ADD CONSTRAINT `vehiculo_ibfk_1` FOREIGN KEY (`id_estacion`) REFERENCES `estacion` (`id_estacion`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
