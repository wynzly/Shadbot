package com.shadorc.shadbot.command.admin.member;

import com.shadorc.shadbot.core.command.Context;
import com.shadorc.shadbot.object.help.HelpBuilder;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Permission;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class BanCmd extends RemoveMemberCmd {

    public BanCmd() {
        super("ban", "banned", Permission.BAN_MEMBERS);
    }

    @Override
    public Mono<Void> action(Member member, String reason) {
        return member.ban(spec -> spec.setReason(reason).setDeleteMessageDays(7));
    }

    @Override
    public Consumer<EmbedCreateSpec> getHelp(Context context) {
        return new HelpBuilder(this, context)
                .setDescription("Ban user and delete his messages from the last 7 days.")
                .addArg("@user", false)
                .addArg("reason", true)
                .build();
    }

}
