package me.shadorc.shadbot.command.music;

import java.util.List;
import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;

import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.BaseCmd;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.music.TrackScheduler;
import me.shadorc.shadbot.object.Emoji;
import me.shadorc.shadbot.utils.DiscordUtils;
import reactor.core.publisher.Mono;

public class BoostBassCmd extends BaseCmd {

	private static final float[] BASS_BOOST = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f,
			-0.05f, -0.1f, -0.1f, -0.1f, -0.1f,
			-0.1f, -0.1f, -0.1f, -0.1f, -0.1f };
	private final EqualizerFactory equalizer;

	public BoostBassCmd() {
		super(CommandCategory.MUSIC, List.of("boost_bass", "boost-bass", "boostbass"));
		this.setDefaultRateLimiter();

		this.equalizer = new EqualizerFactory();
		for(int i = 0; i < BASS_BOOST.length; i++) {
			this.equalizer.setGain(i, BASS_BOOST[i]);
		}
	}

	@Override
	public Mono<Void> execute(Context context) {
		final TrackScheduler trackScheduler = context.requireGuildMusic().getTrackScheduler();

		trackScheduler.setFilterFactory(trackScheduler.isFilterApplied() ? null : this.equalizer);

		return context.getChannel()
				.flatMap(channel -> DiscordUtils.sendMessage(String.format(Emoji.INFO + " (**%s**) Bass boost %s.",
						context.getUsername(), trackScheduler.isFilterApplied() ? "disabled" : "enabled"), channel))
				.then();
	}

	@Override
	public Consumer<EmbedCreateSpec> getHelp(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
