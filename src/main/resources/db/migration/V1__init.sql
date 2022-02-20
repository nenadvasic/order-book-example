CREATE TABLE `orders` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `price` DECIMAL(16,4) NOT NULL,
    `side` ENUM('BUY', 'SELL') NOT NULL,
    `amount` DECIMAL(16,4) NOT NULL,
    `filled_amount` DECIMAL(16,4) NOT NULL DEFAULT 0,
    `active` TINYINT(1) UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    INDEX `active_idx` (`active` ASC),
    INDEX `price_asc_time_idx` (`price` ASC, `time` ASC),
    INDEX `price_desc_time_idx` (`price` DESC, `time` ASC)
)
ENGINE = InnoDB
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `trades` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `price` DECIMAL(16,4) NOT NULL,
    `side` ENUM('BUY', 'SELL') NOT NULL,
    `amount` DECIMAL(16,4) NOT NULL,
    PRIMARY KEY (`id`)
)
ENGINE = InnoDB
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
