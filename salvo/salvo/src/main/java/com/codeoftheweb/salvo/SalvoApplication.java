package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {

		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
		return (args) -> {

			Date d = new Date();

			Game game1 = new Game();
			Game game2 = new Game(Date.from(d.toInstant().plusSeconds(3600)));
			Game game3 = new Game(Date.from(d.toInstant().plusSeconds(7200)));
			Game game4 = new Game(Date.from(d.toInstant().plusSeconds(10800)));
			Game game5 = new Game(Date.from(d.toInstant().plusSeconds(14400)));


			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);

			Player player1 = new Player("stevewozniak@aol.com");
			Player player1_2 = new Player("leonardeuler@gmail.com");
			Player player2 = new Player("konradzuse@aol.com");
			Player player3 = new Player("charlesbabbage@live.com");
			Player player4 = new Player("margarethamilton@live.com");
			Player player5 = new Player("timbernerslee@gmail.com");


			playerRepository.save(player1);
			playerRepository.save(player1_2);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);

			GamePlayer gamePlayer1 = new GamePlayer(game1, player1, Date.from(d.toInstant()));
			GamePlayer gamePlayer2 = new GamePlayer(game1, player1_2, Date.from(d.toInstant()));
			GamePlayer gamePlayer3 = new GamePlayer(game2, player2, Date.from(d.toInstant()));
			GamePlayer gamePlayer4 = new GamePlayer(game2, player3, Date.from(d.toInstant()));
			GamePlayer gamePlayer5 = new GamePlayer(game3, player4, Date.from(d.toInstant()));
			GamePlayer gamePlayer6 = new GamePlayer(game3, player5, Date.from(d.toInstant()));

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);

			List<String> locations1 = List.of("A2", "B2", "F2", "F3");
			List<String> locations2 = List.of("D3", "E3", "H7", "H8");
			List<String> locations3 = List.of("G9", "H9");
			List<String> locations4 = List.of("B1", "C1");
			List<String> locations5 = List.of("G5", "H5");
			List<String> locations6 = List.of("I7", "I8");



			Ship ship1 = new Ship("cruiser", locations1, gamePlayer1);
			Ship ship2 = new Ship("battleship", locations2, gamePlayer2);
			Ship ship3 = new Ship("submarine", locations3, gamePlayer3);
			Ship ship4 = new Ship("destroyer", locations4, gamePlayer4);
			Ship ship5 = new Ship("patrolBoat", locations5, gamePlayer5);
			Ship ship6 = new Ship("patrolBoat", locations6, gamePlayer6);


			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
		};
	}
}
