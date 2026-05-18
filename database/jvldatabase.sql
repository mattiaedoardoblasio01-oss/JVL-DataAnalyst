CREATE DATABASE  IF NOT EXISTS `jvl` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `jvl`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: jvl
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `admin_user` (`admin_user`),
  UNIQUE KEY `email` (`email`)
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
  `anno` year NOT NULL,
  `data_inizio` date NOT NULL,
  `data_fine` date NOT NULL,
  `stato` enum('Config','Attivo','Chiuso') DEFAULT 'Config',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2527 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campionati`
--

LOCK TABLES `campionati` WRITE;
/*!40000 ALTER TABLE `campionati` DISABLE KEYS */;
INSERT INTO `campionati` VALUES (2526,'LBA 25/26',2025,'2025-10-05','2026-06-17','Attivo');
/*!40000 ALTER TABLE `campionati` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `giocatori`
--

DROP TABLE IF EXISTS `giocatori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `giocatori` (
  `id` varchar(20) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(50) NOT NULL,
  `ruolo` varchar(20) DEFAULT NULL,
  `n_maglia` int DEFAULT NULL,
  `squadra_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `squadra_id` (`squadra_id`),
  CONSTRAINT `fk_giocatori_squadra` FOREIGN KEY (`squadra_id`) REFERENCES `squadre` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `giocatori`
--

LOCK TABLES `giocatori` WRITE;
/*!40000 ALTER TABLE `giocatori` DISABLE KEYS */;
INSERT INTO `giocatori` VALUES ('AB12','Armoni','Brooks','Guardia',12,'MI'),('AC0','Alessandro','Cappelletti','Playmaker',0,'TV'),('AC31','Andrea','Calzavara','Playmaker',31,'UD'),('AD24','Aubrey','Dawkins','Guardia',24,'UD'),('AD31','Aliou','Diarra','Centro',31,'BO'),('AD42','Aljami','Durham','Playmaker',42,'CR'),('ADN10','Andrea','De Nicolao','Playmaker',10,'CA'),('ADV8','Amedeo','Della Valle','Guardia',8,'BS'),('AF55','Allerik','Freeman','Guardia',55,'VA'),('AH8','Anthony','Hickey','Playmaker',8,'UD'),('AJ23','Andrej','Jakimovski','Ala',23,'TN'),('AL4','Alessandro','Lever','Ala',4,'VE'),('AM24','Andrea','Mezzanotte','Ala',24,'SS'),('AnP6','Andrea','Pecchia','Ala',6,'TR'),('AP6','Alessandro','Pajola','Playmaker',6,'BO'),('AS12','Arturs','Strautins','Ala',12,'TR'),('AS9','Alen','Smailagic','Centro',9,'BO'),('AT00','Amedeo','Tessitori','Centro',0,'VE'),('AZ6','Alessandro ','Zanelli','Guardia',6,'SS'),('BC7','Brekkott','Chapman','Ala',7,'TR'),('BD42','Bryant','Dunston','Centro',42,'MI'),('BW11','Bryson','Williams','Centro',11,'RE'),('BW2','Briante','Weber','Playmaker',2,'TV'),('CB23','Christian','Burns','Centro',23,'CR'),('CC3','Christopher','Chiozza','Playmaker',3,'CA'),('CE3','Carsen','Edwards','Guardia',3,'BO'),('CH2','Chris','Horton','Centro',2,'VE'),('CJM5','CJ','Massinburg','Playmaker',5,'BS'),('CM17','Christian','Mekowulu','Centro',17,'UD'),('CM2','Carlos','Marshall','Ala',2,'SS'),('CN7','Cheickh','Niang','Ala',7,'TN'),('CR4','Colbey','Ross','Playmaker',4,'TS'),('CS1','Carlos ','Stewart','Playmaker',1,'VA'),('CV1','Christian','Vital','Guardia',1,'TR'),('CW10','Carl','Wheatle','Ala',10,'VE'),('DA2','Davide','Alviti','Ala',2,'VA'),('DA21','Derrick','Alston','Ala',21,'BO'),('DB4','Desure','Buie','Guardia',4,'SS'),('DB6','Devin','Booker','Centro',6,'MI'),('DC25','David','Cournooh','Guardia',25,'BS'),('DC7','Davide','Casarin','Playmaker',7,'CR'),('DF21','Diego','Flaccadori','Playmaker',21,'MI'),('DH23','Daniel','Hackett','Playmaker',23,'BO'),('DJS1','DJ','Steward','Playmaker',1,'TN'),('DM25','Davide','Moretti','Guardia',25,'TS'),('DM5','Daryl','Macon','Guardia',5,'SS'),('DO17','Dominik','Olejniczak','Centro',17,'TR'),('DR23','Demetre','Rivers','Ala',23,'BS'),('DT9','David','Torresani','Playmaker',9,'TV'),('DV45','Denzel','Valentine','Guardia',45,'VE'),('DVJ3','DeVante','Jones','Playmaker',3,'TN'),('EA7','Elisee','Assui','Ala',7,'VA'),('EB22','Eimantas','Bendzius','Ala',22,'UD'),('EC7','Ed','Croswell','Centro',7,'TV'),('EG32','Erick','Green','Guardia',32,'CA'),('EM5','Ezra','Manjon','Playmaker',5,'TR'),('FC13','Francesco','Candussi','Centro',13,'TS'),('FF24','Francesco','Ferrari','Ala',24,'BO'),('FG12','Filippo ','Galli','Ala',12,'CR'),('FM11','Federico','Miaschi','Playmaker',11,'TV'),('FP29','Francesco','Pellegrino','Centro',29,'TV'),('FS7','Fadilou','Seck','Centro',7,'SS'),('GB12','Giordano','Bortolani','Guardia',12,'CA'),('GB21','Grant','Basile','Ala',21,'CA'),('GC30','Guglielmo','Caruso','Centro',30,'NA'),('GDN5','Giovanni','De Nicolao','Playmaker',5,'VE'),('GF3','Giancarlo','Ferrero','Ala',3,'BS'),('GJ14','Giga','Janelidze','Centro',14,'VE'),('GR17','Giampaolo','Ricci','Ala',17,'MI'),('GR22','Gerals','Robinson','Playmaker',22,'CA'),('GV16','Giovanni','Veronesi','Ala',16,'CR'),('HW20','Hason','Ward','Centro',20,'VE'),('IA0','Ike','Anigbogu','Centro',0,'CR'),('IEA5','Ishmael','El-Amin','Playmaker',5,'NA'),('IF23','Ivan','Fevrier','Ala',23,'CA'),('II11','Ike','Iroegbu','Playmaker',11,'VA'),('II35','Iris','Ikangi','Ala',35,'UD'),('IW0','Isaiah','Whaley','Ala',0,'NA'),('JB0','Jaylen','Barford','Guardia',0,'RE'),('JB10','Jason','Burnell','Ala',10,'BS'),('JB23','Jeff','Brooks','Ala',23,'TS'),('JB26','Jordan','Bayehe','Centro',26,'TN'),('JB33','Joshua','Bannan','Ala',33,'TS'),('JE26','Jaime','Echenique','Centro',26,'RE'),('JG4','Justin','Gorham','Ala',4,'TR'),('JM21','Joseph','Mobio','Ala',21,'BS'),('JN32','Josh','Nebo','Centro',32,'MI'),('JN55','Jayden','Nunn','Guardia',55,'BS'),('JP22','Jordan','Parks','Ala',22,'VE'),('JPM55','JP','Macura','Ala',55,'TV'),('JR25','Joonas','Riismaa','Ala',25,'TR'),('JR37','Jahmius','Ramsey','Guardia',37,'TS'),('JT4','JT','Thor','Ala',4,'RE'),('JTA1','Juan','Toscano-Anderson','Ala',1,'TS'),('JU9','Jarrod','Uthoff','Ala',9,'TS'),('KB8','Ky','Bowman','Playmaker',8,'VE'),('KB99','Khalif','Battle','Guardia',99,'TN'),('KC55','Kwan','Cheatam','Ala',55,'RE'),('KJ34','Karim','Jallow','Ala',34,'BO'),('KP12','Kruize','Pinkins','Ala',12,'TV'),('KT18','Kaspar','Treier','Ala',18,'NA'),('KW33','Kyle','Wiltjer','Ala',33,'VE'),('LB10','Leandro','Bolmaro','Playmaker',10,'MI'),('LB2','Lorenzo','Brown','Playmaker',2,'MI'),('LB9','Laurynas','Beliauskas','Guardia',9,'SS'),('LC7','Leonardo','Candi','Playmaker',7,'VE'),('LD8','Lodovico','Deangeli','Guardia',8,'TS'),('LF10','Leonardo','Faggian','Guardia',10,'NA'),('LO90','Leonardo','Okeke','Centro',90,'CA'),('LR43','Leon','Radosevic','Centro',43,'TV'),('LS20','Luca','Severini','Ala',20,'RE'),('LT35','Leonardo','Totè','Centro',35,'NA'),('LT9','Lamine','Tandia','Centro',9,'TR'),('LU16','Lorenzo','Uglietti','Playmaker',16,'RE'),('LV1','Luca ','Vildoza','Playmaker',1,'BO'),('LV22','Luca','Vincini','Centro',22,'SS'),('MA5','Mirza','Alibegovic','Guardia',5,'UD'),('MA8','Matteo','Accorsi','Guardia',8,'BO'),('MAAR5','Muhammad-Ali','Abdur-Rahkman','Guardia',5,'TV'),('MB2','Matteo','Baiocchi','Playmaker',2,'BO'),('MB22','Markel','Brown','Guardia',22,'TS'),('MC11','Marco','Ceron','Ala',11,'SS'),('MC15','Matteo','Chillo','Ala',15,'TV'),('MD25','Milton','Doyle','Ala',25,'NA'),('MD35','Mouhamet','Diouf','Centro',35,'BO'),('MDR20','Matteo','Da Ros','Ala',20,'UD'),('MG23','Marko','Guduric','Guardia',23,'MI'),('MiB2','Miro','Bilan','Centro',2,'BS'),('MJ8','Matas','Jogela','Ala',8,'TN'),('ML13','Matteo','Librizzi','Playmaker',13,'VA'),('ML34','Maximilian','Ladurner','Centro',34,'VA'),('MM30','Matt','Morgan','Playmaker',30,'BO'),('MN9','Maurice','Ndour','Ala',9,'BS'),('MR10','Michele','Ruzzier','Playmaker',10,'TS'),('MS11','Mady','Sissoko','Centro',11,'TS'),('MU17','Mattia','Udom','Ala',17,'CR'),('MV31','Michele','Vitali','Guardia',31,'RE'),('NA45','Nicola','Akele','Ala',45,'BO'),('NI18','Nikola','Ivanovic','Playmaker',18,'BS'),('NM1','Niccolo','Mannion','Playmaker',1,'MI'),('NM11','Nick','Marshall','Playmaker',11,'NA'),('NMG29','Nick','McGlynn','Centro',29,'SS'),('NML3','Nazareth','Mitrou-Long','Playmaker',3,'NA'),('NR15','Nate','Renfro','Centro',15,'VA'),('NS77','Nathan','Sestina','Ala',77,'MI'),('OB11','Oumar','Ballo','Centro',11,'CA'),('OD25','Ousmane','Diop','Centro',25,'MI'),('ON46','Ousmane','Ndiaye','Ala',46,'CR'),('ON8','Olivier','Nkamhoua','Ala',8,'VA'),('OO31','Osvaldas','Olisevicius','Ala',31,'TV'),('PA22','Peyton','Aldridge','Ala',22,'TN'),('PB19','Paul','Biligha','Centro',19,'TR'),('PH3','Prentiss','Hubb','Playamker',3,'TR'),('PH68','Patrick','Hassan','Playmaker',68,'TN'),('PI18','Pietro','Iannuzzi','Guardia',18,'TS'),('PW2','Payton','Willis','Playmaker',2,'CR'),('QE3','Quinn','Ellis','Playmaker',3,'MI'),('RB21','Raequan','Battle','Guardia',21,'CR'),('RB45','Rasir','Bolton','Playmaker',45,'NA'),('RJC1','RJ','Cole','Playmaker',1,'VE'),('RM9','Riccardo','Moraschini','Ala',9,'CA'),('RR6','Riccardo','Rossato','Guardia',6,'RE'),('RT25','Rashawn','Thomas','Ala',25,'SS'),('RV36','Riccardo','Visconti','Guardia',36,'SS'),('SB2','Stephen','Brown','Playmaker',2,'RE'),('SC0','Semaj','Christon','Playmaker',0,'UD'),('SF1','Savion','Flagg','Ala',1,'NA'),('SG8','Sasha ','Grant','Ala',8,'CR'),('SM21','Selom','Mawugbe','Centro',21,'TN'),('SN11','Stefan','Nikolic','Ala',11,'VE'),('SN7','Saliou','Niang','Ala',7,'BO'),('SS31','Shavon','Shields','Ala',31,'MI'),('SS6','Skylar','Spencer','Centro',6,'UD'),('ST7','Stefano','Tonut','Guardia',7,'MI'),('TA11','Theo','Airhienbuwa','Ala',11,'TN'),('TB11','Tommaso','Baldasso','Playmaker',11,'TR'),('TC10','Troy','Caupain','Playmaker',10,'RE'),('TF10','Toto','Forray','Playmaker',10,'TN'),('TM4','Taze','Moore','Guardia',4,'VA'),('TW7','Tomas','Woldetensae','Ala',7,'RE'),('XS20','Xavier','Sneed','Ala',20,'CA'),('YDS99','Yago','Dos Santos','Playmaker',99,'BO'),('ZL16','Zach','LeDay','Ala',16,'MI');
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
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `azione` varchar(255) NOT NULL,
  `dettagli` text,
  PRIMARY KEY (`id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `log_operazioni_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `amministratori` (`id`) ON DELETE RESTRICT
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
  `camp_id` int NOT NULL,
  `fase` enum('RS','PO') NOT NULL,
  `giornata` int DEFAULT NULL,
  `casa_id` varchar(20) NOT NULL,
  `ospite_id` varchar(20) NOT NULL,
  `data_ora` datetime NOT NULL,
  `luogo` varchar(100) DEFAULT NULL,
  `score_casa` int DEFAULT '0',
  `score_osp` int DEFAULT '0',
  `stato` enum('Programmata','Conclusa') DEFAULT 'Programmata',
  PRIMARY KEY (`id`),
  KEY `camp_id` (`camp_id`),
  KEY `casa_id` (`casa_id`),
  KEY `ospite_id` (`ospite_id`),
  CONSTRAINT `fk_partite_casa` FOREIGN KEY (`casa_id`) REFERENCES `squadre` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_partite_ospite` FOREIGN KEY (`ospite_id`) REFERENCES `squadre` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `partite_ibfk_1` FOREIGN KEY (`camp_id`) REFERENCES `campionati` (`id`) ON DELETE RESTRICT
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
  `giocatore_id` varchar(20) NOT NULL,
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
  `squadra_id` varchar(20) NOT NULL,
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
  `id` varchar(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `sede` varchar(100) NOT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `allenatore` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `squadre`
--

LOCK TABLES `squadre` WRITE;
/*!40000 ALTER TABLE `squadre` DISABLE KEYS */;
INSERT INTO `squadre` VALUES ('BO','Virtus Bologna','Bologna',NULL,'Nenad Jakovljevic'),('BS','Germani Brescia','Brescia',NULL,'Matteo Cotelli'),('CA','Acqua S.Bernardo Cantù','Como',NULL,'Walter De Raffaele'),('CR','Vanoli Cremona','Cremona',NULL,'Pierluigi Brotto'),('MI','EA7 Olimpia Milano','Milano',NULL,'Giuseppe Poeta'),('NA','Guerri Napoli','Napoli',NULL,'Jasmin Repesa'),('RE','UNAHOTELS Reggio Emilia','Reggio Emilia',NULL,'Dimitris Prifitis'),('SS','Dianmo Sassari','Sassari',NULL,'Veljko Mrsic'),('TN','Dolomiti Energia Trento','Trento',NULL,'Massimo Cancellieri'),('TR','Bertram Derthona','Tortona',NULL,'Mario Fioretti'),('TS','Pallacanestro Trieste','Trieste',NULL,'Francesco Taccetti'),('TV','Nutribullet Treviso','Treviso',NULL,'Marcelo Nicola'),('UD','APU OWW Udine','Udine',NULL,'Adriano Vertemati'),('VA','Openjobmetis Varese','Varese',NULL,'Ioannis Kastritis'),('VE','UMANA Reyer Venezia','Venezia',NULL,'Neven Spahija');
/*!40000 ALTER TABLE `squadre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistiche`
--

DROP TABLE IF EXISTS `statistiche`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistiche` (
  `id` int NOT NULL AUTO_INCREMENT,
  `gioc_id` varchar(20) NOT NULL,
  `punti` int NOT NULL DEFAULT '0',
  `rimbalzi` int NOT NULL DEFAULT '0',
  `assist` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `gioc_id` (`gioc_id`),
  CONSTRAINT `fk_statistiche_giocatore` FOREIGN KEY (`gioc_id`) REFERENCES `giocatori` (`id`) ON DELETE CASCADE,
  CONSTRAINT `statistiche_chk_1` CHECK (((`punti` >= 0) and (`rimbalzi` >= 0) and (`assist` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistiche`
--

LOCK TABLES `statistiche` WRITE;
/*!40000 ALTER TABLE `statistiche` DISABLE KEYS */;
INSERT INTO `statistiche` VALUES (1,'MiB2',16,9,2),(2,'JB10',14,7,3),(3,'ADV8',17,3,5),(4,'TC10',15,4,6),(5,'RJC1',16,3,5),(6,'CV1',18,4,3),(7,'BW2',13,5,4),(8,'II11',17,3,5),(9,'ON8',16,6,2),(10,'OB11',11,8,1),(11,'JR37',19,2,2),(12,'JP22',14,5,2),(13,'NI18',13,3,4),(14,'DO17',10,7,1),(15,'KP12',11,7,1),(16,'JN32',10,7,0),(17,'RB45',14,4,4),(18,'MB22',11,4,3),(19,'AD42',14,3,3),(20,'EC7',10,5,0),(21,'DB4',14,3,7),(22,'SS31',13,4,3),(23,'AB12',16,4,2),(24,'XS20',14,4,2),(25,'KW33',14,4,1),(26,'PW2',14,4,4),(27,'JE26',11,5,1),(28,'SC0',14,3,5),(29,'EG32',15,2,2),(30,'MS11',10,7,0);
/*!40000 ALTER TABLE `statistiche` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tabellone_po`
--

DROP TABLE IF EXISTS `tabellone_po`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tabellone_po` (
  `partita_id` int NOT NULL,
  `turno` enum('Quarti','Semi','Finale') NOT NULL,
  `serie_n` int NOT NULL,
  PRIMARY KEY (`partita_id`),
  CONSTRAINT `tabellone_po_ibfk_1` FOREIGN KEY (`partita_id`) REFERENCES `partite` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tabellone_po`
--

LOCK TABLES `tabellone_po` WRITE;
/*!40000 ALTER TABLE `tabellone_po` DISABLE KEYS */;
/*!40000 ALTER TABLE `tabellone_po` ENABLE KEYS */;
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
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(50) NOT NULL,
  `indirizzo` varchar(100) DEFAULT NULL,
  `cap` varchar(5) DEFAULT NULL,
  `provincia` varchar(2) DEFAULT NULL,
  `data_nascita` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
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

-- Dump completed on 2026-05-18 19:37:21
