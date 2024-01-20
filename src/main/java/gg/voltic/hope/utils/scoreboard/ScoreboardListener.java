package gg.voltic.hope.utils.scoreboard;

import gg.voltic.hope.Hope;
import gg.voltic.hope.providers.ScoreboardProvider;
import gg.voltic.hope.utils.ConfigCursor;
import gg.voltic.hope.utils.FileConfig;
import gg.voltic.hope.utils.scoreboard.events.ScoreboardCreateEvent;
import gg.voltic.hope.utils.scoreboard.events.ScoreboardDestroyEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class ScoreboardListener implements Listener {

	private final Scoreboard assemble;

	public ScoreboardListener(Scoreboard assemble) {
		this.assemble = assemble;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ScoreboardCreateEvent createEvent = new ScoreboardCreateEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) return;

		Player player = event.getPlayer();

		FileConfig playersConfig = Hope.getInstance().getPlayersFile();

		ConfigCursor configCursor = new ConfigCursor(playersConfig, "PLAYERS." + player.getUniqueId());
		if (configCursor.getBoolean("board")) {
			getAssemble().getBoards().put(event.getPlayer().getUniqueId(), new ScoreboardManager(event.getPlayer(), getAssemble()));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ScoreboardDestroyEvent destroyEvent = new ScoreboardDestroyEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(destroyEvent);
		if (destroyEvent.isCancelled()) {
			return;
		}

		if (getAssemble().getBoards().containsKey(event.getPlayer().getUniqueId())) {
			getAssemble().getBoards().remove(event.getPlayer().getUniqueId());
			event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}

    public Scoreboard getAssemble() {
        return this.assemble;
    }
}
