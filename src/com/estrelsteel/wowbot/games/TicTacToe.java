package com.estrelsteel.wowbot.games;

import net.dv8tion.jda.core.entities.User;

public class TicTacToe {
	private String[][] board;
	private User x;
	private User o;
	private int turn;
	
	public TicTacToe() {
		board = new String[3][3];
		turn = 0;
		
		for(int y = 0; y < board.length; y++) {
			for(int x = 0; x < board[y].length; x++) {
				board[y][x] = ".";
			}
		}
	}
	
	public String[][] getBoard() {
		return board;
	}
	
	public User getPlayerX() {
		return x;
	}
	
	public User getPlayerO() {
		return o;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public void setBoard(String[][] board) {
		this.board = board;
	}
	
	public void setPlayerX(User x) {
		this.x = x;
	}
	
	public void setPlayerO(User o) {
		this.o = o;
	}
	
	public void setTurn(int turn) {
		this.turn = turn;
	}
	
	public String printGame() {
		String game = "`";
		for(int y = 0; y < board.length; y++) {
			for(int x = 0; x < board[y].length; x++) {
				game = game + board[y][x];
				if(x == 0) {
					game = game + " | ";
				}
				else if(x == 1) {
					game = game + " | ";
				}
			}
			if(y < 2) {
				game = game + "\n---------\n";
			}
		}
		game = game + "`";
		System.out.println(game);
		return game;
	}
}
