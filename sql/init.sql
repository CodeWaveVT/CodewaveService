create database if not exists codewavedb;

use codewavedb;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `userAccount` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'account',
                        `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'password',
                        `userName` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'usernickname',
                        `userRole` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT 'user role：user/admin',
                        `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                        `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                        `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT 'logic delete',
                        PRIMARY KEY (`id`),
                        KEY `idx_userAccount` (`userAccount`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='user';

CREATE TABLE `task` (
                        `id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'id',
                        `ebookname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'upload ebook name',
                        `ebookOriginData` longblob COMMENT 'user upload origin ebook',
                        `bookType` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ebook type',
                        `ebookTextData` text COLLATE utf8mb4_unicode_ci COMMENT 'user upload ebook text',
                        `genAudioUrl` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'generated audio url',
                        `genAduioData` longblob COMMENT 'generated audio file',
                        `status` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'wait' COMMENT 'wait,running,succeed,failed',
                        `execMessage` text COLLATE utf8mb4_unicode_ci COMMENT 'execute message',
                        `userId` bigint DEFAULT NULL COMMENT 'user id who start this task',
                        `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                        `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                        `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT 'logical delete',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='task info table'