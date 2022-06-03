package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.PluginController;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToolBreakPreventionCommand implements CommandExecutor, TabCompleter {
    private final PluginController controller;
    private final String COMMAND = "tellraw %s [{\"text\":\"[Server]\",\"color\":\"gold\",\"bold\":true,\"italic\":true,\"underlined\":true,\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Boo\"}]}},{\"text\":\" I see what you tried... and i don't like it\",\"color\":\"white\",\"bold\":false,\"italic\":false,\"underlined\":false,\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"i appreciate the effort tho\"}]}}]";

    public ToolBreakPreventionCommand(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = ((Player) sender).getPlayer();
        if (player == null) return true;
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "You have to have a tool/armor in your hand to use this command.");
            return true;
        }
        if (player.getInventory().getItemInMainHand().getType().equals(Material.ENCHANTED_BOOK)) {
            Util.executeCommand(String.format(COMMAND, Util.componentToString(player.displayName())));
            return true;
        }
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.MENDING)) {
            sender.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Your item doesn't have mending, please apply mending and execute the Command again.");
            return true;
        }

        List<Component> lore = player.getInventory().getItemInMainHand().lore()==null ? new ArrayList<>() {{
            add(Component.text().content(controller.getConfig().getToolBreakPreventionDetectString()).build());
        }} : new ArrayList<>();
        player.getInventory().getItemInMainHand().lore(lore);
        player.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Break Prevention updated");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
