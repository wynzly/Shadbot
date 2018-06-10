package me.shadorc.shadbot.command.owner;

import java.util.Map;
import java.util.function.Supplier;

import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.AbstractCommand;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.CommandPermission;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.command.annotation.Command;
import me.shadorc.shadbot.data.stats.StatsManager;
import me.shadorc.shadbot.exception.IllegalCmdArgumentException;
import me.shadorc.shadbot.utils.BotUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.embed.HelpBuilder;

@Command(category = CommandCategory.OWNER, permission = CommandPermission.OWNER, names = { "stats" })
public class StatsCmd extends AbstractCommand {

	@Override
	public void execute(Context context) {
		context.requireArg();

		Map<String, Supplier<EmbedCreateSpec>> map = StatsManager.getStats();

		if(!map.containsKey(context.getArg().get().toLowerCase())) {
			throw new IllegalCmdArgumentException(String.format("`%s` is not a valid category. Options: %s",
					context.getArg(), FormatUtils.format(map.keySet().stream(), value -> String.format("`%s`", value), ", ")));
		}

		BotUtils.sendMessage(map.get(context.getArg().get().toLowerCase()).get(), context.getChannel());
	}

	@Override
	public EmbedCreateSpec getHelp(String prefix) {
		return new HelpBuilder(this, prefix)
				.setDescription("Show statistics for the specified category.")
				.addArg("category", FormatUtils.format(StatsManager.getStats().keySet().stream(),
						name -> String.format("`%s`", name.toString().toLowerCase()), ", "), false)
				.build();
	}
}
