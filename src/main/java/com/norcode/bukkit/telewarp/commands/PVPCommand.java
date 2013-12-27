package com.norcode.bukkit.telewarp.commands;

import com.norcode.bukkit.telewarp.PVPass;

public class PVPCommand extends BaseCommand {
    public PVPCommand(PVPass plugin) {
        super(plugin, "pvp", new String[] {}, "pvpass.command", null);
        registerSubcommand(new OnCommand(plugin));
        registerSubcommand(new OffCommand(plugin));
    }
}
