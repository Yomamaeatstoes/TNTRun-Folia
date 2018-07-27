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

package tntrun.commands.setup.arena;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.utils.Bars;

public class FinishArena implements CommandHandlerInterface {

	private TNTRun plugin;
	public FinishArena(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getArenaByName(args[0]);
		if (arena != null) {
			if (!arena.getStatusManager().isArenaEnabled()) {
				if (arena.getStructureManager().isArenaConfigured()) {
					arena.getStructureManager().saveToConfig();
					plugin.amanager.registerArena(arena);
					arena.getStatusManager().enableArena();
					Bars.createBar(args[0]);
					player.sendMessage("§7[§6TNTRun§7] §7Arena §6" + args[0] + "§7 saved and enabled");
				} else {
					player.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[0] + "§c isn't configured. Reason: " + arena.getStructureManager().isArenaConfiguredString());
				}
			} else {
				player.sendMessage("§7[§6TNTRun§7] §cPlease disable arena §6/trsetup disable " + args[0]);
			}
		} else {
			player.sendMessage("§7[§6TNTRun§7] §cArena §6" + args[0] + "§c doesn't exist");
		}
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}

}