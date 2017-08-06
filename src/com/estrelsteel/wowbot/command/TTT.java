package com.estrelsteel.wowbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.games.GameCentre;
import com.estrelsteel.wowbot.games.TicTacToe;

public class TTT implements Command {
	
	private GameCentre gc;
	
	public TTT(GameCentre gc) {
		this.gc = gc;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(gc.getTicTacToe() == null) {
			gc.setTicTacToe(new TicTacToe());
		}
		if(gc.getTicTacToe().getPlayerX() == null) {
			e.getTextChannel().sendMessage("@here " + e.getAuthor().getName() + " joined TicTacToe as player X").queue();
			gc.getTicTacToe().setPlayerX(e.getAuthor());
		}
		else if(gc.getTicTacToe().getPlayerO() == null && gc.getTicTacToe().getPlayerX() != e.getAuthor()) {
			e.getTextChannel().sendMessage("@here " + e.getAuthor().getName() + " joined TicTacToe as player O").queue();
			gc.getTicTacToe().setPlayerO(e.getAuthor());
		}
//		else {
//			e.getTextChannel().sendMessage("@" + e.getAuthor().getName() + " this game is already running.").queue();
//		}
		
		if(gc.getTicTacToe().getPlayerX() != null && gc.getTicTacToe().getPlayerO() != null) {
			e.getTextChannel().sendMessage("\n" + gc.getTicTacToe().printGame()).queue();
			if(gc.getTicTacToe().getTurn() % 2 == 0) {
				e.getTextChannel().sendMessage("@" + gc.getTicTacToe().getPlayerX().getName() + " , X, it is your turn.").queue();
			}
			else {
				e.getTextChannel().sendMessage("@" + gc.getTicTacToe().getPlayerX().getName() + " , X, it is your turn.").queue();
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "ttt";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
