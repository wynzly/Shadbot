package me.shadorc.discordbot.command;

import me.shadorc.discordbot.Command;
import me.shadorc.discordbot.Context;
import me.shadorc.discordbot.Storage;
import me.shadorc.discordbot.utility.BotUtils;

public class CoinsCommand extends Command {

	public CoinsCommand() {
		super("coins", "coin");
	}

	@Override
	public void execute(Context context) {
		BotUtils.sendMessage("Vous avez " + Storage.get(context.getAuthor().getName()) + " coins.", context.getChannel());
	}
}