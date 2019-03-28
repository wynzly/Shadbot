package me.shadorc.shadbot.command.music;

import java.util.List;
import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;

import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.BaseCmd;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import reactor.core.publisher.Mono;

public class BassBoostCmd extends BaseCmd {

	private static final float[] BASS_BOOST = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
			-0.1f, -0.1f, -0.1f, -0.1f };
	private final EqualizerFactory equalizer;

	public BassBoostCmd() {
		super(CommandCategory.MUSIC, List.of("bass_boost", "bass-boost", "bassboost"));
		this.setDefaultRateLimiter();

		this.equalizer = new EqualizerFactory();
	}

	@Override
	public Mono<Void> execute(Context context) {
		context.requireGuildMusic().getTrackScheduler()
				.getAudioPlayer().setFilterFactory(this.equalizer);

		final float diff = 0f;
		for(int i = 0; i < BASS_BOOST.length; i++) {
			this.equalizer.setGain(i, BASS_BOOST[i] + diff);
		}

		return Mono.empty();
	}

	@Override
	public Consumer<EmbedCreateSpec> getHelp(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
