package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Collection;
import java.util.stream.Collectors;

public class NetherPortalListener implements QoLListener {
    private final PluginController controller;

    public NetherPortalListener(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreate(PortalCreateEvent event) {
        if (event.isCancelled()) return;
        if (event.getReason().equals(PortalCreateEvent.CreateReason.END_PLATFORM)) return;
        if (event.getWorld().getEnvironment().equals(World.Environment.CUSTOM)) return;

        World destination;
        World origin = event.getWorld();

        if (event.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            destination = Bukkit.getWorlds().stream().filter(x -> x.getEnvironment().equals(World.Environment.NETHER)).collect(Collectors.toList()).get(0);
        } else {
            destination = Bukkit.getWorlds().stream().filter(x -> x.getEnvironment().equals(World.Environment.NORMAL)).collect(Collectors.toList()).get(0);
        }

        Location averageLocation = new Location(origin, 0, 0, 0);
        boolean northSouth = true;

        for (BlockState block : event.getBlocks()) {
            averageLocation.add(block.getLocation());
            if (block.getType().equals(Material.NETHER_PORTAL) && block.getBlockData() instanceof Orientable directional) {
                northSouth = directional.getAxis().equals(Axis.X);
            }
        }

        averageLocation.multiply(1d / event.getBlocks().size());

        Collection<Player> nearbyPlayers = averageLocation.getNearbyPlayers(10);

        if (destination.getEnvironment().equals(World.Environment.NETHER)) {
            averageLocation.multiply(1d / 8);
        } else {
            averageLocation.multiply(8d);
        }

        for (Player player : nearbyPlayers) {
            player.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + String.format("A linked Portal should be at: (%s, ~, %s) and when looking through the portal, it should%sface the north pole", averageLocation.getX(), averageLocation.getZ(), northSouth ? " " : " not "));
        }

    }
}
