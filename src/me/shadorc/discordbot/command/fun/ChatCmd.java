package me.shadorc.discordbot.command.fun;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import me.shadorc.discordbot.Config;
import me.shadorc.discordbot.Emoji;
import me.shadorc.discordbot.MissingArgumentException;
import me.shadorc.discordbot.command.Command;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.HtmlUtils;
import me.shadorc.discordbot.utils.Log;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;

public class ChatCmd extends Command {

	private static final Map<IGuild, String> GUILDS_CUSTID = new HashMap<>();

	public ChatCmd() {
		super(false, "chat");
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(context.getArg() == null) {
			throw new MissingArgumentException();
		}

		if(!GUILDS_CUSTID.containsKey(context.getGuild())) {
			GUILDS_CUSTID.put(context.getGuild(), null);
		}

		try {
			String aliceState = GUILDS_CUSTID.get(context.getGuild());
			String xmlString = HtmlUtils.getHTML(new URL("http://sheepridge.pandorabots.com/pandora/talk-xml?"
					+ "botid=b69b8d517e345aba"
					+ "&input=" + URLEncoder.encode(context.getArg(), "UTF-8")
					+ (aliceState != null ? "&custid=" + aliceState : "")));
			JSONObject result = XML.toJSONObject(xmlString).getJSONObject("result");
			String response = result.getString("that").replace("<br>", "\n").trim();
			GUILDS_CUSTID.put(context.getChannel().getGuild(), result.getString("custid"));
			BotUtils.sendMessage(Emoji.SPEECH + " " + response, context.getChannel());

		} catch (SocketTimeoutException e) {
			BotUtils.sendMessage(Emoji.WARNING + " Chat isn't currently available.", context.getChannel());
			Log.warn("A.L.I.C.E. doesn't respond (" + e.getMessage() + ").");

		} catch (IOException e) {
			Log.error("An error occured while discussing with A.L.I.C.E.", e, context.getChannel());
		}
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = new EmbedBuilder()
				.withAuthorName("Help for /" + this.getNames()[0])
				.withAuthorIcon(context.getClient().getOurUser().getAvatarURL())
				.withColor(Config.BOT_COLOR)
				.appendDescription("**Chat with the artificial intelligence A.L.I.C.E.**")
				.appendField("Usage", "/chat <message>", false);
		BotUtils.sendEmbed(builder.build(), context.getChannel());
	}
}
