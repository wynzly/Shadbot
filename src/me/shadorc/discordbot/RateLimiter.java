package me.shadorc.discordbot;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.utils.BotUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class RateLimiter {

	private final Map<IGuild, HashMap<IUser, Long>> guildsRateLimiter;
	private final Map<IGuild, HashMap<IUser, Boolean>> warningsRateLimiter;
	private final long timeout;

	public RateLimiter(int timeout, ChronoUnit unit) {
		this.timeout = Duration.of(timeout, unit).toMillis();
		this.guildsRateLimiter = new HashMap<>();
		this.warningsRateLimiter = new HashMap<>();
	}

	public boolean isLimited(IGuild guild, IUser user) {
		if(!guildsRateLimiter.containsKey(guild)) {
			guildsRateLimiter.put(guild, new HashMap<IUser, Long>());
			warningsRateLimiter.put(guild, new HashMap<IUser, Boolean>());
			return false;
		}

		long currentTime = System.currentTimeMillis();

		if(!guildsRateLimiter.get(guild).containsKey(user)) {
			guildsRateLimiter.get(guild).put(user, currentTime);
			warningsRateLimiter.get(guild).put(user, false);
			return false;
		}

		long lastTime = guildsRateLimiter.get(guild).get(user);
		long diff = currentTime - lastTime;
		if(diff > timeout) {
			guildsRateLimiter.get(guild).remove(user);
			warningsRateLimiter.get(guild).remove(user);
			return false;
		}

		return true;
	}

	public boolean isWarned(IGuild guild, IUser user) {
		return warningsRateLimiter.get(guild).get(user);
	}

	public float getRemainingTime(IGuild guild, IUser user) {
		long remainingMillis = timeout - (System.currentTimeMillis() - guildsRateLimiter.get(guild).get(user));
		return remainingMillis / 1000f;
	}

	public long getTimeout() {
		return Duration.of(timeout, ChronoUnit.MILLIS).getSeconds();
	}

	public void warn(String message, Context context) {
		BotUtils.sendMessage(Emoji.STOPWATCH + " " + message, context.getChannel());
		warningsRateLimiter.get(context.getGuild()).put(context.getAuthor(), true);
	}
}