/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun;

import java.util.HashSet;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;
import tntrun.utils.Utils;

public class TNTRunPlaceholders extends PlaceholderExpansion {
	private final TNTRun plugin;

	public TNTRunPlaceholders(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist(){
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String getIdentifier() {
		return "tntrun";
	}

	@Override
	public String onRequest(OfflinePlayer p, String identifier) {

		if (p == null) {
			return "";
		}
		if (identifier.equals("version")) {
			return String.valueOf(plugin.getDescription().getVersion());

		} else if (identifier.equals("arena_count")) {
			return String.valueOf(plugin.amanager.getArenas().size());

		} else if (identifier.equals("pvp_arena_count")) {
			return String.valueOf(plugin.amanager.getPvpArenas().size());

		} else if (identifier.equals("nopvp_arena_count")) {
			return String.valueOf(plugin.amanager.getNonPvpArenas().size());

		} else if (identifier.equals("played")) {
			return String.valueOf(plugin.stats.getPlayedGames(p));

		} else if (identifier.equals("wins")) {
			return String.valueOf(plugin.stats.getWins(p));

		} else if (identifier.equals("losses")) {
			return String.valueOf(plugin.stats.getLosses(p));

		} else if (identifier.equals("current_arena")) {
			Arena arena = plugin.amanager.getPlayerArena(p.getName());
			return arena != null ? arena.getArenaName() : FormattingCodesParser.parseFormattingCodes(Messages.playernotinarena);

		} else if (identifier.equals("player_count")) {
			return String.valueOf(Utils.playerCount());

		} else if (identifier.equals("pvp_player_count")) {
			return String.valueOf(Utils.pvpPlayerCount());

		} else if (identifier.equals("nopvp_player_count")) {
			return String.valueOf(Utils.nonPvpPlayerCount());

		} else if (identifier.startsWith("allplayers")) {
			String[] temp = identifier.split("_");
			if (temp.length != 2) {
				return null;
			}
			Arena arena = plugin.amanager.getArenaByName(temp[1]);
			if (arena == null) {
				return null;
			}
			return getNames(arena.getPlayersManager().getAllParticipantsCopy());

		} else if (identifier.startsWith("players")) {
			String[] temp = identifier.split("_");
			if (temp.length != 2) {
				return null;
			}
			Arena arena = plugin.amanager.getArenaByName(temp[1]);
			if (arena == null) {
				return null;
			}
			return getNames(arena.getPlayersManager().getPlayersCopy());

		} else if (identifier.startsWith("spectators")) {
			String[] temp = identifier.split("_");
			if (temp.length != 2) {
				return null;
			}
			Arena arena = plugin.amanager.getArenaByName(temp[1]);
			if (arena == null) {
				return null;
			}
			return getNames(arena.getPlayersManager().getSpectatorsCopy());

		} else if (identifier.startsWith("player_count")) {
			String[] temp = identifier.split("_");
			if (temp.length != 3) {
				return null;
			}
			Arena arena = plugin.amanager.getArenaByName(temp[2]);
			return arena != null ? String.valueOf(arena.getPlayersManager().getPlayersCount()) : null;

		} else if (identifier.equals("doublejumps")) {
			int amount = 0;
			Arena arena = plugin.amanager.getPlayerArena(p.getName());
			if (arena == null) {
				if (plugin.getConfig().getBoolean("freedoublejumps.enabled")) {
					amount = plugin.shop.getAllowedDoubleJumps((Player) p, plugin.getConfig().getInt("freedoublejumps.amount", 0));
				} else {
					amount = plugin.getPData().getDoubleJumpsFromFile(p);
				}
			} else {
				amount = arena.getPlayerHandler().getDoubleJumps((Player) p);
			}
			return String.valueOf(amount);

		} else if (identifier.startsWith("joinfee") || identifier.startsWith("currency")) {
			String[] temp = identifier.split("_");
			if (temp.length != 2) {
				return null;
			}
			Arena arena = plugin.amanager.getArenaByName(temp[1]);
			if (arena == null) {
				return null;
			}
			if (identifier.startsWith("joinfee")) {
				return arena.getStructureManager().hasFee() ? String.valueOf(arena.getStructureManager().getFee()) : "0";
			}
			return arena.getStructureManager().isCurrencyEnabled() ? arena.getStructureManager().getCurrency().toString() : null;

		} else if (identifier.startsWith("leaderboard")) {
			if (!isValidIdentifier(identifier)) {
				return null;
			}
			String[] temp = identifier.split("_");
			String type = temp[1];
			String entry = temp[2];
			int pos = Integer.parseInt(temp[3]);

			return plugin.stats.getLeaderboardPosition(pos, type, entry);

		} else if (identifier.startsWith("status")) {
			String[] temp = identifier.split("_");
			if (temp.length != 2) {
				return null;
			}
			Arena arena = plugin.amanager.getArenaByName(temp[1]);
			return arena != null ? arena.getStatusManager().getArenaStatus() : null;
		}
		return null;
	}

	private boolean isValidIdentifier(String identifier) {
		String[] temp = identifier.split("_");
		if (temp.length != 4) {
			return false;
		}
		if (!Utils.isNumber(temp[3]) || Integer.parseInt(temp[3]) < 1) {
			return false;
		}
		if (!temp[2].equalsIgnoreCase("player") && !temp[2].equalsIgnoreCase("score")) {
			return false;
		}
		if (!Stream.of("wins", "played", "losses").anyMatch(temp[1]::equalsIgnoreCase)) {
			return false;
		}
		return true;
	}

	private String getNames(HashSet<Player> playerSet) {
		StringJoiner names = new StringJoiner(", ");
		playerSet.stream().forEach(player -> {
			names.add(player.getName());
		});
		return names.toString();
	}
}
