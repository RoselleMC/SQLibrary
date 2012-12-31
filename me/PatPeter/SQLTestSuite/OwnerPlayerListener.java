package me.PatPeter.SQLTestSuite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

/**
 * Player Listener for the deprecated plugin example.<br>
 * Date Created: 2011-05-28 13:56.
 * 
 * @author alta189 (Stephen Williams)
 */
public class OwnerPlayerListener implements Listener {
	private OwnerCore plugin;
	
	public OwnerPlayerListener(OwnerCore plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (!plugin.commandUsers.containsKey(player.getName().toLowerCase())) return;
		
		Block block = event.getClickedBlock();
		
		if (plugin.commandUsers.get(player.getName().toLowerCase()) == 2) { // INFO MODE
			String query = "SELECT * FROM blocks WHERE x = " + block.getX() + " AND y = " + block.getY() + " AND z = " + block.getZ() + ";";
			ResultSet result = null;

			try {
				if (plugin.mySQL) {
					result = plugin.mysql.query(query);
				} else {
					result = plugin.sqlite.query(query);
				}
			} catch (SQLException e) {
				this.plugin.log.severe(e.getMessage());
			}
			
			try {
				
				if (result != null && result.next()) {
					String owner = result.getString("owner");
					int id = result.getInt("id");
					int x =  result.getInt("x");
					int y =  result.getInt("y");
					int z =  result.getInt("z");
					
					player.sendMessage(ChatColor.GREEN + "Block ID " + ChatColor.RED + id + ChatColor.GREEN + "at (x, y, z) = (" + ChatColor.RED + x + ChatColor.GREEN + ", " + ChatColor.RED + y + ChatColor.GREEN + ", " + ChatColor.RED + z + ChatColor.GREEN + ")");
					player.sendMessage(ChatColor.GREEN + "is owned by " + ChatColor.RED + owner);
				} else {
					player.sendMessage(ChatColor.GREEN + "This block is not owned");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (plugin.commandUsers.get(player.getName().toLowerCase()) == 1){ //Create Mode
			try {
				if (!checkBlock(block)) return;
				String query = "INSERT INTO blocks (owner, x, y, z) VALUES ('" + player.getName().toLowerCase() +"', " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ");";
				
				if (plugin.mySQL) {
					this.plugin.mysql.query(query);
					player.sendMessage(ChatColor.GREEN + "This block is now owned by alta189");
				} else {
					this.plugin.sqlite.query(query);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Boolean checkBlock(Block block) throws SQLException {
		String query = "SELECT * FROM blocks WHERE x = " + block.getX() + " AND y = " + block.getY() + " AND z = " + block.getZ() + ";";
		ResultSet result = null;
		
		if (plugin.mySQL) {
			result = this.plugin.mysql.query(query);
		} else {
			result = this.plugin.sqlite.query(query);
		}
		
		if (result != null && result.next()) {
			return false;
		} else {
			return true;
		}
	}
}