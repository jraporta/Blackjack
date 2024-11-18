package com.cat.itacademy.s05.blackjack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Configuration
public class MySqlDatabaseConfig {

    private final DatabaseClient databaseClient;

    public MySqlDatabaseConfig(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Bean
    public Mono<Void> initializeDatabase() {
        return databaseClient.sql("""
                CREATE SCHEMA IF NOT EXISTS `s5_blackjack` DEFAULT CHARACTER SET utf8mb4 ;
                USE `s5_blackjack` ;
                CREATE TABLE IF NOT EXISTS `s5_blackjack`.`player` (
                  `id` INT NOT NULL AUTO_INCREMENT,
                  `name` VARCHAR(45) NOT NULL,
                  `money` INT NOT NULL DEFAULT 0,
                  `games_played` INT NOT NULL DEFAULT 0,
                  PRIMARY KEY (`id`),
                  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);
                """)
                .then();
    }
}
