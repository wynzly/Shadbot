package me.shadorc.discordbot.command.owner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.CommandCategory;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.command.Role;
import me.shadorc.discordbot.data.StatCategory;
import me.shadorc.discordbot.data.Stats;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.StringUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.Emoji;
import me.shadorc.discordbot.utils.command.MissingArgumentException;
import sx.blah.discord.util.EmbedBuilder;

public class StatsCmd extends AbstractCommand {

	private static final int ROW_SIZE = 25;

	public StatsCmd() {
		super(CommandCategory.OWNER, Role.OWNER, "stats");
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(!context.hasArg()) {
			throw new MissingArgumentException();
		}

		if(!Arrays.stream(StatCategory.values()).anyMatch(category -> category.toString().equals(context.getArg()))) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Category unknown. (Options: "
					+ Arrays.stream(StatCategory.values()).map(cat -> cat.toString()).collect(Collectors.toList()) + ")", context.getChannel());
			return;
		}

		StatCategory category = StatCategory.valueOf(context.getArg().toUpperCase());

		JSONObject statsObj = Stats.getCategory(category);
		if(statsObj.length() == 0) {
			BotUtils.sendMessage(Emoji.MAGNIFYING_GLASS + " This category is empty.", context.getChannel());
			return;
		}

		final Map<String, Integer> statsMap = new HashMap<>();
		for(Object key : statsObj.keySet()) {
			statsMap.put(key.toString(), statsObj.getInt(key.toString()));
		}

		List<String> statsList = Utils.sortByValue(statsMap).keySet().stream()
				.map(key -> "**" + key + "**: " + statsMap.get(key))
				.collect(Collectors.toList());

		EmbedBuilder builder = Utils.getDefaultEmbed()
				.setLenient(true)
				.withAuthorName(StringUtils.capitalize(category.toString()) + "'s Stats");

		for(int i = 0; i < Math.ceil((float) statsMap.keySet().size() / ROW_SIZE); i++) {
			int index = i * ROW_SIZE;
			builder.appendField("Row N°" + (i + 1),
					StringUtils.formatList(
							statsList.subList(index, index + Math.min(ROW_SIZE, statsList.size() - index)), str -> str, "\n"), true);
		}

		BotUtils.sendMessage(builder.build(), context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed(this)
				.appendDescription("**Show stats for the specified category.**")
				.appendField("Usage", "`" + context.getPrefix() + this.getNames()[0] + " <category>`", false)
				.appendField("Argument", "**category** - " + StringUtils.formatArray(StatCategory.values(), cat -> cat.toString(), ", "), false);
		BotUtils.sendMessage(builder.build(), context.getChannel());

	}
}