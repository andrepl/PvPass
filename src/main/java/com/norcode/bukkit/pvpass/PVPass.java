package com.norcode.bukkit.pvpass;

import com.norcode.bukkit.playerid.PlayerID;
import com.norcode.bukkit.pvpass.commands.PVPCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class PVPass extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PVPListener(this), this);
        getServer().getPluginCommand("pvp").setExecutor(new PVPCommand(this));
    }

    /***
     * Enables pvp for player
     * @param player - Player to have PVP enabled.
     */
    public void EnablePvP(Player player) {
        ConfigurationSection cfg = PlayerID.getPlayerData(this.getName(), player);
        if (IsPvPEnabled(player)) {
            cfg.set("pvp-cooldown", System.currentTimeMillis() + (5*60*1000));
            PlayerID.savePlayerData(this.getName(), player, cfg);
            return;
        }

        cfg.set("pvp-cooldown", System.currentTimeMillis() + (5*60*1000));
        cfg.set("pvp-enabled", true);
        player.setDisplayName(ChatColor.RED + player.getName() + ChatColor.RESET);
        /* TO-DO:  CHOP THIS AT 16 */
        //player.setPlayerListName(ChatColor.RED + player.getName() + ChatColor.RESET);
        player.sendMessage("Enabling PVP");
        this.getServer().broadcastMessage("PVP has been enabled for " + player.getName() + ".");
        PlayerID.savePlayerData(this.getName(), player, cfg);
        player.setMetadata("pvpass-pvp-enabled", new FixedMetadataValue(this, true));
    }

    /***
     * Disables PVP for a player
     * @param player - Player to have PVP disabled.
     */
    public void DisablePvP(Player player) {
        if (!IsPvPEnabled(player)) {
            return;
        }

        Long cooldown = GetPvPCooldown(player);
        if (cooldown > 0) {
            player.sendMessage("You need to wait " + cooldown + "s before you can disable pvp.");
            return;
        }

        ConfigurationSection cfg = PlayerID.getPlayerData(this.getName(), player);
        cfg.set("pvp-enabled", false);
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        player.sendMessage("Disabling PVP");
        PlayerID.savePlayerData(this.getName(), player, cfg);
        player.setMetadata("pvpass-pvp-enabled", new FixedMetadataValue(this, false));
    }

    /***
     * Checks for pvp-enabled on the player and creates it if needed, defaulting to false.
     * @param player - Player to be looked up
     * @return bool - True if PVP is enabled, False if PVP is disabled
     */
    public boolean IsPvPEnabled(Player player) {
        if (!player.hasMetadata("pvpass-pvp-enabled")) {
            ConfigurationSection cfg = PlayerID.getPlayerData(this.getName(), player);
            boolean enabled = cfg.getBoolean("pvp-enabled", false);
            player.setMetadata("pvpass-pvp-enabled", new FixedMetadataValue(this, enabled));
        }
        return player.getMetadata("pvpass-pvp-enabled").get(0).asBoolean();
    }

    /***
     * Gets the remaining time in seconds for players PVP Cooldown
     * @param player
     * @return long that is the number of seconds before the cooldown is up.
     */
    public long GetPvPCooldown(Player player) {
        ConfigurationSection cfg = PlayerID.getPlayerData(this.getName(), player);
        Long cooldown =  cfg.getLong("pvp-cooldown", System.currentTimeMillis()-60000);
        if (cooldown < System.currentTimeMillis()) {
            return 0;
        } else {
            return (cooldown - System.currentTimeMillis()) / 1000;
        }
    }
}
