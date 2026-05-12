CREATE DATABASE  IF NOT EXISTS `jvldatabase` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `jvldatabase`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: jvldatabase
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `amministratori`
--

DROP TABLE IF EXISTS `amministratori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `amministratori` (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_user` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `stato` enum('Attivo','Sospeso') DEFAULT 'Attivo',
  `ultimo_accesso` datetime DEFAULT NULL,
  `data_creazione` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_admin_user_unico` (`admin_user`),
  UNIQUE KEY `idx_admin_email_unica` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `amministratori`
--

LOCK TABLES `amministratori` WRITE;
/*!40000 ALTER TABLE `amministratori` DISABLE KEYS */;
/*!40000 ALTER TABLE `amministratori` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campionati`
--

DROP TABLE IF EXISTS `campionati`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campionati` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `stagione` varchar(9) NOT NULL,
  `data_inizio` date NOT NULL,
  `data_fine` date NOT NULL,
  `stato` enum('Programmato','Attivo','Concluso') DEFAULT 'Programmato',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campionati`
--

LOCK TABLES `campionati` WRITE;
/*!40000 ALTER TABLE `campionati` DISABLE KEYS */;
/*!40000 ALTER TABLE `campionati` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `giocatori`
--

DROP TABLE IF EXISTS `giocatori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `giocatori` (
  `id` varchar(6) NOT NULL,
  `squadra_id` int DEFAULT NULL,
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(50) NOT NULL,
  `ruolo` varchar(20) DEFAULT NULL,
  `numero_maglia` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_giocatori_squadra` (`squadra_id`),
  CONSTRAINT `fk_giocatori_squadra` FOREIGN KEY (`squadra_id`) REFERENCES `squadre` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `giocatori`
--

LOCK TABLES `giocatori` WRITE;
/*!40000 ALTER TABLE `giocatori` DISABLE KEYS */;
/*!40000 ALTER TABLE `giocatori` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_operazioni`
--

DROP TABLE IF EXISTS `log_operazioni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_operazioni` (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_id` int NOT NULL,
  `azione` varchar(255) NOT NULL,
  `dettagli` json DEFAULT NULL,
  `indirizzo_ip` varchar(45) DEFAULT NULL,
  `data_ora` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_log_admin` (`admin_id`),
  CONSTRAINT `fk_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `amministratori` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_operazioni`
--

LOCK TABLES `log_operazioni` WRITE;
/*!40000 ALTER TABLE `log_operazioni` DISABLE KEYS */;
/*!40000 ALTER TABLE `log_operazioni` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partite`
--

DROP TABLE IF EXISTS `partite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partite` (
  `id` int NOT NULL AUTO_INCREMENT,
  `campionato_id` int NOT NULL,
  `squadra_casa_id` int NOT NULL,
  `squadra_ospite_id` int NOT NULL,
  `data_ora` datetime NOT NULL,
  `luogo` varchar(100) DEFAULT NULL,
  `punti_casa` int DEFAULT '0',
  `punti_ospite` int DEFAULT '0',
  `stato` enum('Da giocare','In corso','Finita','Rinviata') DEFAULT 'Da giocare',
  PRIMARY KEY (`id`),
  KEY `fk_partite_camp` (`campionato_id`),
  KEY `fk_partite_casa` (`squadra_casa_id`),
  KEY `fk_partite_ospite` (`squadra_ospite_id`),
  CONSTRAINT `fk_partite_camp` FOREIGN KEY (`campionato_id`) REFERENCES `campionati` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_partite_casa` FOREIGN KEY (`squadra_casa_id`) REFERENCES `squadre` (`id`),
  CONSTRAINT `fk_partite_ospite` FOREIGN KEY (`squadra_ospite_id`) REFERENCES `squadre` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partite`
--

LOCK TABLES `partite` WRITE;
/*!40000 ALTER TABLE `partite` DISABLE KEYS */;
/*!40000 ALTER TABLE `partite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preferenze_giocatori`
--

DROP TABLE IF EXISTS `preferenze_giocatori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `preferenze_giocatori` (
  `utente_id` int NOT NULL,
  `giocatore_id` varchar(6) NOT NULL,
  PRIMARY KEY (`utente_id`,`giocatore_id`),
  KEY `fk_pref_gioc_giocatore` (`giocatore_id`),
  CONSTRAINT `fk_pref_gioc_giocatore` FOREIGN KEY (`giocatore_id`) REFERENCES `giocatori` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pref_gioc_utente` FOREIGN KEY (`utente_id`) REFERENCES `utenti` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preferenze_giocatori`
--

LOCK TABLES `preferenze_giocatori` WRITE;
/*!40000 ALTER TABLE `preferenze_giocatori` DISABLE KEYS */;
/*!40000 ALTER TABLE `preferenze_giocatori` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preferenze_squadre`
--

DROP TABLE IF EXISTS `preferenze_squadre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `preferenze_squadre` (
  `utente_id` int NOT NULL,
  `squadra_id` int NOT NULL,
  PRIMARY KEY (`utente_id`,`squadra_id`),
  KEY `fk_pref_sq_squadra` (`squadra_id`),
  CONSTRAINT `fk_pref_sq_squadra` FOREIGN KEY (`squadra_id`) REFERENCES `squadre` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pref_sq_utente` FOREIGN KEY (`utente_id`) REFERENCES `utenti` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preferenze_squadre`
--

LOCK TABLES `preferenze_squadre` WRITE;
/*!40000 ALTER TABLE `preferenze_squadre` DISABLE KEYS */;
/*!40000 ALTER TABLE `preferenze_squadre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `squadre`
--

DROP TABLE IF EXISTS `squadre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `squadre` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `citta` varchar(100) NOT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `allenatore` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_nome_unico` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `squadre`
--

LOCK TABLES `squadre` WRITE;
/*!40000 ALTER TABLE `squadre` DISABLE KEYS */;
/*!40000 ALTER TABLE `squadre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistiche_partite`
--

DROP TABLE IF EXISTS `statistiche_partite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistiche_partite` (
  `id` int NOT NULL AUTO_INCREMENT,
  `partita_id` int NOT NULL,
  `giocatore_id` varchar(6) NOT NULL,
  `punti` int DEFAULT '0',
  `rimbalzi` int DEFAULT '0',
  `assist` int DEFAULT '0',
  `falli` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_stats_partita` (`partita_id`),
  KEY `fk_stats_giocatore` (`giocatore_id`),
  CONSTRAINT `fk_stats_giocatore` FOREIGN KEY (`giocatore_id`) REFERENCES `giocatori` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_stats_partita` FOREIGN KEY (`partita_id`) REFERENCES `partite` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistiche_partite`
--

LOCK TABLES `statistiche_partite` WRITE;
/*!40000 ALTER TABLE `statistiche_partite` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistiche_partite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utenti`
--

DROP TABLE IF EXISTS `utenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utenti` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `cognome` varchar(50) DEFAULT NULL,
  `data_registrazione` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utenti`
--

LOCK TABLES `utenti` WRITE;
/*!40000 ALTER TABLE `utenti` DISABLE KEYS */;
/*!40000 ALTER TABLE `utenti` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-12  9:32:30
