package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.Date;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {

		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository) {
		return (args) -> {
			Game game1 = new Game();
			gameRepository.save(game1);

			Date date = new Date();

			Game game2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			gameRepository.save(game2);

			Game game3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));
			gameRepository.save(game3);

			Player player1 = new Player("stevewozniak@aol.com");
			Player player1_2 = new Player("leonardeuler@gmail.com");
			playerRepository.save(player1);
			playerRepository.save(player1_2);

			Player player2 = new Player("konradzuse@aol.com");
			playerRepository.save(player2);

			Player player3 = new Player("charlesbabbage@live.com");
			playerRepository.save(player3);

			gamePlayerRepository.save(new GamePlayer(game1, player1, new Date()));
			gamePlayerRepository.save(new GamePlayer(game1, player1_2, new Date()));
			gamePlayerRepository.save(new GamePlayer(game2, player2, new Date()));
			gamePlayerRepository.save(new GamePlayer(game3, player3, new Date()));
		};
	}
}
