package me.shadorc.shadbot.shard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import me.shadorc.shadbot.utils.Utils;
import sx.blah.discord.api.IShard;

public class ShadbotShard {

	private final IShard shard;
	private final int shardID;
	private final ExecutorService threadPool;
	private final AtomicLong lastUsed;

	public ShadbotShard(IShard shard) {
		this.shard = shard;
		this.shardID = shard.getInfo()[0];
		this.threadPool = Executors.newCachedThreadPool(Utils.getThreadFactoryNamed("ShadbotShard-" + this.getID() + "-%d"));
		this.lastUsed = new AtomicLong();
	}

	public final IShard getShard() {
		return shard;
	}

	public final int getID() {
		return shardID;
	}

	public final ExecutorService getThreadPool() {
		return threadPool;
	}

	public final long getLastUsed() {
		return lastUsed.get();
	}

	public final void used() {
		lastUsed.set(System.currentTimeMillis());
	}

	public final void restart() {
		shard.logout();
		shard.login();
	}
}
