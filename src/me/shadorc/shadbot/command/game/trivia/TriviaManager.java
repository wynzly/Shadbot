package me.shadorc.shadbot.command.game.trivia;

import java.io.IOException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import me.shadorc.shadbot.api.trivia.TriviaResponse;
import me.shadorc.shadbot.api.trivia.TriviaResult;
import me.shadorc.shadbot.core.ExceptionHandler;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.game.AbstractGameManager;
import me.shadorc.shadbot.data.db.DatabaseManager;
import me.shadorc.shadbot.data.stats.StatsManager;
import me.shadorc.shadbot.data.stats.enums.MoneyEnum;
import me.shadorc.shadbot.listener.interceptor.MessageInterceptor;
import me.shadorc.shadbot.listener.interceptor.MessageInterceptorManager;
import me.shadorc.shadbot.utils.BotUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.NumberUtils;
import me.shadorc.shadbot.utils.TimeUtils;
import me.shadorc.shadbot.utils.Utils;
import me.shadorc.shadbot.utils.command.Emoji;
import me.shadorc.shadbot.utils.embed.EmbedUtils;
import me.shadorc.shadbot.utils.embed.log.LogUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

public class TriviaManager extends AbstractGameManager implements MessageInterceptor {

	protected static final int MIN_GAINS = 100;
	protected static final int MAX_BONUS = 100;
	protected static final int LIMITED_TIME = 30;

	private final Integer categoryId;
	private final ConcurrentHashMap<Snowflake, Boolean> alreadyAnswered;

	private TriviaResult trivia;
	private long startTime;
	private List<String> answers;

	public TriviaManager(Context context, Integer categoryId) {
		super(context);
		this.categoryId = categoryId;
		this.alreadyAnswered = new ConcurrentHashMap<>();
	}

	// Trivia API doc : https://opentdb.com/api_config.php
	@Override
	public void start() {
		try {
			final URL url = new URL(String.format("https://opentdb.com/api.php?amount=1&category=%s", Objects.toString(categoryId, "")));
			TriviaResponse response = Utils.MAPPER.readValue(url, TriviaResponse.class);
			this.trivia = response.getResults().get(0);
		} catch (IOException err) {
			throw Exceptions.propagate(err);
		}

		MessageInterceptorManager.addInterceptor(this.getContext().getChannelId(), this);
		this.schedule(Mono.fromRunnable(this::stop)
				.then(BotUtils.sendMessage(String.format(Emoji.HOURGLASS + " Time elapsed, the correct answer was **%s**.", trivia.getCorrectAnswer()),
						this.getContext().getChannel()))
				.doOnError(ExceptionHandler::isForbidden, err -> LogUtils.cannotSpeak(this.getClass(), this.getContext().getGuildId())),
				LIMITED_TIME, ChronoUnit.SECONDS);
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void stop() {
		this.cancelScheduledTask();
		MessageInterceptorManager.removeInterceptor(this.getContext().getChannelId(), this);
		TriviaCmd.MANAGERS.remove(this.getContext().getChannelId());
	}

	@Override
	public Mono<Void> show() {
		final String description = String.format("**%s**%n%s",
				trivia.getQuestion(),
				FormatUtils.numberedList(answers.size(), answers.size(),
						count -> String.format("\t**%d**. %s", count, answers.get(count - 1))));

		return this.getContext().getAvatarUrl()
				.map(avatarUrl -> EmbedUtils.getDefaultEmbed()
						.setAuthor("Trivia", null, avatarUrl)
						.setDescription(description)
						.addField("Category", String.format("`%s`", trivia.getCategory()), true)
						.addField("Type", String.format("`%s`", trivia.getType()), true)
						.addField("Difficulty", String.format("`%s`", trivia.getDifficulty()), true)
						.setFooter(String.format("You have %d seconds to answer.", LIMITED_TIME), null))
				.flatMap(embed -> BotUtils.sendMessage(embed, this.getContext().getChannel()))
				.then();
	}

	private Mono<Message> win(Member member) {
		final float coinsPerSec = (float) MAX_BONUS / LIMITED_TIME;
		final long remainingSec = LIMITED_TIME - TimeUnit.MILLISECONDS.toSeconds(TimeUtils.getMillisUntil(startTime));
		final int gains = MIN_GAINS + (int) Math.ceil(remainingSec * coinsPerSec);

		DatabaseManager.getDBMember(member.getGuildId(), member.getId()).addCoins(gains);
		StatsManager.MONEY_STATS.log(MoneyEnum.MONEY_GAINED, this.getContext().getCommandName(), gains);

		this.stop();
		return BotUtils.sendMessage(String.format(Emoji.CLAP + " (**%s**) Correct ! You won **%d coins**.",
				member.getUsername(), gains), this.getContext().getChannel());
	}

	@Override
	public Mono<Boolean> isIntercepted(MessageCreateEvent event) {
		final Member member = event.getMember().get();
		return this.cancelOrDo(event.getMessage(), Mono.just(event.getMessage().getContent().get())
				.flatMap(content -> {
					// It's a number or a text
					Integer choice = NumberUtils.asIntBetween(content, 1, answers.size());

					// Message is a text and doesn't match any answers, ignore it
					if(choice == null && !answers.stream().anyMatch(content::equalsIgnoreCase)) {
						return Mono.just(false);
					}

					// If the user has already answered and has been warned, ignore him
					if(alreadyAnswered.containsKey(member.getId()) && alreadyAnswered.get(member.getId())) {
						return Mono.just(false);
					}

					final String answer = choice == null ? content : answers.get(choice - 1);

					Mono<Message> monoMessage;
					if(alreadyAnswered.containsKey(member.getId())) {
						monoMessage = BotUtils.sendMessage(String.format(Emoji.GREY_EXCLAMATION + " (**%s**) You can only answer once.",
								member.getUsername()), event.getMessage().getChannel());
						alreadyAnswered.put(member.getId(), true);
					} else if(answer.equalsIgnoreCase(trivia.getCorrectAnswer())) {
						monoMessage = this.win(member);

					} else {
						monoMessage = BotUtils.sendMessage(String.format(Emoji.THUMBSDOWN + " (**%s**) Wrong answer.",
								member.getUsername()), event.getMessage().getChannel());
						alreadyAnswered.put(member.getId(), false);
					}

					return monoMessage.thenReturn(true);
				}));
	}

}
