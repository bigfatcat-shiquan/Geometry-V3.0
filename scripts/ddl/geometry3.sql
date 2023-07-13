CREATE DATABASE geometry3 DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'bigfatcat'@'localhost' IDENTIFIED BY 'bigfatcat';
GRANT ALL ON geometry3.* TO 'bigfatcat'@'localhost';
USE geometry3;