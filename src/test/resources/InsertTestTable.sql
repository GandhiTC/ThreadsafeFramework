DROP TABLE IF EXISTS `pomCredentials`;

CREATE TABLE `pomCredentials`
(
	`baseURL` varchar(150) NOT NULL,
	`username` varchar(50) NOT NULL,
	`password` varchar(50) NOT NULL,
	PRIMARY KEY (`baseURL`)
);

INSERT INTO `pomCredentials`
	(`baseURL`, `username`, `password`) 
VALUES 
	('http://demo.guru99.com/v4/','mngr250218','vezYgad');