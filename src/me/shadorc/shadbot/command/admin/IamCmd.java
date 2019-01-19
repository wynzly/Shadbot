package me.shadorc.shadbot.command.admin;

import java.util.List;
import java.util.Map;

import discord4j.core.object.entity.Role;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;
import discord4j.core.object.util.Permission;
import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.Shadbot;
import me.shadorc.shadbot.core.command.AbstractCommand;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.CommandPermission;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.command.annotation.Command;
import me.shadorc.shadbot.core.setting.SettingEnum;
import me.shadorc.shadbot.data.database.DBGuild;
import me.shadorc.shadbot.exception.CommandException;
import me.shadorc.shadbot.exception.MissingArgumentException;
import me.shadorc.shadbot.exception.MissingPermissionException.UserType;
import me.shadorc.shadbot.utils.DiscordUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.StringUtils;
import me.shadorc.shadbot.utils.embed.EmbedUtils;
import me.shadorc.shadbot.utils.embed.help.HelpBuilder;
import me.shadorc.shadbot.utils.object.message.ReactionMessage;
import reactor.core.publisher.Mono;

@Command(category = CommandCategory.ADMIN, permission = CommandPermission.ADMIN, names = { "iam" })
public class IamCmd extends AbstractCommand {

	public static final Unicode REACTION = ReactionEmoji.unicode("✅");

	@Override
	public Mono<Void> execute(Context context) {
		final String arg = context.requireArg();

		final List<String> quotedElements = StringUtils.getQuotedElements(arg);
		if(quotedElements.isEmpty() && arg.contains("\"")) {
			throw new CommandException("One quotation mark is missing.");
		}
		if(quotedElements.size() > 1) {
			throw new CommandException("You should specify only one text in quotation marks.");
		}

		final Mono<List<Role>> rolesMono = context.getGuild()
				.flatMapMany(guild -> DiscordUtils.extractRoles(guild, StringUtils.remove(arg, quotedElements)))
				.flatMap(roleId -> context.getClient().getRoleById(context.getGuildId(), roleId))
				.collectList();

		return context.getChannel()
				.flatMap(channel -> DiscordUtils.requirePermissions(channel, context.getSelfId(), UserType.BOT,
						Permission.MANAGE_ROLES, Permission.ADD_REACTIONS)
						.then(Mono.zip(rolesMono, context.getAvatarUrl()))
						.flatMap(tuple -> {
							final List<Role> roles = tuple.getT1();
							final String avatarUrl = tuple.getT2();

							if(roles.isEmpty()) {
								throw new MissingArgumentException();
							}

							final StringBuilder description = new StringBuilder();
							if(quotedElements.isEmpty()) {
								description.append(String.format("Click on %s to get role(s): %s", REACTION.getRaw(),
										FormatUtils.format(roles, role -> String.format("`@%s`", role.getName()), "\n")));
							} else {
								description.append(quotedElements.get(0));
							}

							final EmbedCreateSpec embed = EmbedUtils.getDefaultEmbed()
									.setAuthor(String.format("Iam: %s",
											FormatUtils.format(roles, role -> String.format("@%s", role.getName()), ", ")),
											null, avatarUrl)
									.setDescription(description.toString());

							return new ReactionMessage(context.getClient(), context.getChannelId(), List.of(REACTION))
									.sendMessage(embed)
									.doOnNext(message -> {
										final DBGuild dbGuild = Shadbot.getDatabase().getDBGuild(context.getGuildId());
										final Map<String, Long> setting = dbGuild.getIamMessages();
										roles.stream().map(Role::getId)
												.forEach(roleId -> setting.put(message.getId().asString(), roleId.asLong()));
										dbGuild.setSetting(SettingEnum.IAM_MESSAGES, setting);
									});
						}))
				.then();
	}

	@Override
	public Mono<EmbedCreateSpec> getHelp(Context context) {
		return new HelpBuilder(this, context)
				.setDescription(String.format("Send a message with a reaction, users will be able to get the role(s) "
						+ "associated with the message by clicking on %s", REACTION.getRaw()))
				.addArg("@role(s)", false).addArg("\"text\"", "Replace the default text", true).build();
	}

}
