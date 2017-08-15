package me.shadorc.discordbot;

public enum Emoji {

	ACCESS_DENIED(":no_entry_sign:"),
	CHECK_MARK(":white_check_mark:"),
	EXCLAMATION(":grey_exclamation:"),
	RED_FLAG(":triangular_flag_on_post:"),
	INFO(":information_source:"),
	STOPWATCH(":stopwatch:"),

	MONEY_WINGS(":money_with_wings:"),
	HOURGLASS(":hourglass:"),
	PURSE(":purse:"),
	BANK(":bank:"),
	DICE(":game_die:"),

	THERMOMETER(":thermometer:"),
	BEACH(":beach_umbrella:"),
	CLOUD(":cloud:"),
	WIND(":wind_blowing_face:"),
	RAIN(":cloud_rain:"),
	DROPLET(":droplet:"),
	MAP(":map:"),

	MUSICAL_NOTE(":musical_note:"),
	REPEAT(":repeat:"),
	PAUSE(":pause_button:"),
	PLAY(":arrow_forward:"),

	THUMBSDOWN(":thumbsdown:"),
	SPEECH(":speech_balloon:"),
	CLAP(":clap:"),
	GEAR(":gear:"),

	SCISSORS(":scissors:"),
	GEM(":gem:"),
	LEAF(":leaves:");

	private final String discordNotation;

	Emoji(String discordNotation) {
		this.discordNotation = discordNotation;
	}

	@Override
	public String toString() {
		return discordNotation;
	}
}
