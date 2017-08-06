package com.estrelsteel.wowbot.games;

public class GameCentre {
	
	private TicTacToe ttt;
	
	public GameCentre() {
		ttt = new TicTacToe();
	}
	
	public TicTacToe getTicTacToe() {
		return ttt;
	}
	
	public void setTicTacToe(TicTacToe ttt) {
		this.ttt = ttt;
	}
}
