package me.shadorc.discordbot.command.french;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import me.shadorc.discordbot.Config;
import me.shadorc.discordbot.Emoji;
import me.shadorc.discordbot.MissingArgumentException;
import me.shadorc.discordbot.RateLimiter;
import me.shadorc.discordbot.Shadbot;
import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.LogUtils;
import me.shadorc.discordbot.utils.TwitterUtils;
import sx.blah.discord.util.EmbedBuilder;
import twitter4j.TwitterException;

public class HolidaysCmd extends AbstractCommand {

	private final RateLimiter rateLimiter;

	private static final List<String> ZONES = Arrays.asList("A", "B", "C");

	public HolidaysCmd() {
		super(false, "vacs", "vacances");
		this.rateLimiter = new RateLimiter(2, ChronoUnit.SECONDS);
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(!context.hasArg()) {
			throw new MissingArgumentException();
		}

		if(rateLimiter.isLimited(context.getGuild(), context.getAuthor())) {
			if(!rateLimiter.isWarned(context.getGuild(), context.getAuthor())) {
				rateLimiter.warn("Take it easy, don't spam :)", context);
			}
			return;
		}

		String zone = context.getArg().toUpperCase();
		if(!ZONES.contains(zone)) {
			throw new MissingArgumentException();
		}

		try {
			TwitterUtils.connection();
			String holidays = TwitterUtils.getInstance().getUserTimeline("Vacances_Zone" + zone).get(0).getText().replaceAll("#", "");
			BotUtils.sendMessage(Emoji.BEACH + " " + holidays, context.getChannel());
		} catch (TwitterException e) {
			LogUtils.error("An error occured while getting holidays information.", e, context.getChannel());
		}
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = new EmbedBuilder()
				.withAuthorName("Help for " + this.getNames()[0] + " command")
				.withAuthorIcon(Shadbot.getClient().getOurUser().getAvatarURL())
				.withColor(Config.BOT_COLOR)
				.appendDescription("**Show the number of remaining days before the next school holidays for the indicated zone.**")
				.appendField("Usage", context.getPrefix() + "vacs <A|B|C>", false);
		BotUtils.sendEmbed(builder.build(), context.getChannel());
	}

}
