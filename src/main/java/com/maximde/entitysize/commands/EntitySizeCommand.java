package com.maximde.entitysize.commands;

import com.maximde.entitysize.EntitySize;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.function.Predicate;

public class EntitySizeCommand implements CommandExecutor, TabCompleter {

    private final EntitySize entitySize;

    public EntitySizeCommand(EntitySize entitySize) {
        this.entitySize = entitySize;
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return !(sender instanceof Player) || sender.hasPermission(permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!hasPermission(sender, entitySize.getPermission("commands"))) {
            sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.plugin_info")
                    .replace("%version%", entitySize.getDescription().getVersion()));
            return false;
        }
        if(args.length < 1) return sendCommands(sender);

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (!hasPermission(sender, entitySize.getPermission("add"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player_only"));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.usage.add"));
                    return false;
                }
                try {
                    double currentSize = entitySize.getSize(player);
                    double deltaSize = Double.parseDouble(args[1]);
                    double newSize = currentSize + deltaSize;

                    setSize(sender, player, newSize, -1);
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size_updated")
                            .replace("%size%", String.valueOf(newSize)));
                } catch (NumberFormatException e) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.invalid_format"));
                }
            }
        case "player" -> {
                if(!hasPermission(sender, entitySize.getPermission("player"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if(args.length < 3) return sendCommands(sender);
                if(args[1].equalsIgnoreCase("@a")) {
                    try {
                        double size = Double.parseDouble(args[2]);
                        int time = -1;
                        if (args.length >= 4) {
                            time = Integer.parseInt(args[3]);
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            setSize(sender, player, size, time);
                        }

                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player.all_changed_time")
                                .replace("%count%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%time%", String.valueOf(time)));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.invalid_format"));
                        e.printStackTrace();
                    } catch (Exception e) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.error_changing_size"));
                        e.printStackTrace();
                    }
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target == null || !target.isOnline()) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player.not_found")
                                .replace("%player%", args[1]));
                        return false;
                    }

                    try {
                        double size = Double.parseDouble(args[2]);
                        int time = -1;
                        if (args.length >= 4) {
                            time = Integer.parseInt(args[3]);
                        }
                        setSize(sender, target, size, time);
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player.all_changed_time")
                                .replace("%count%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                .replace("%time%", String.valueOf(time)));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.invalid_format"));
                        e.printStackTrace();
                    } catch (Exception e) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.error_changing_size"));
                        e.printStackTrace();
                    }
                }

            }

            case "entity" -> {
                if(!hasPermission(sender, entitySize.getPermission("entity"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if(args.length < 2) return sendCommands(sender);
                boolean success = handleEntityArg(sender, args);
                if(!success) return false;
            }

            case "reload" -> {
                if(!hasPermission(sender, entitySize.getPermission("reload"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                entitySize.getConfiguration().reload();
                entitySize.getLanguage().reload();
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.config.reloaded"));
            }

            case "reset" -> {
                if(!hasPermission(sender, entitySize.getPermission("reset"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if(args.length == 1) {
                    entitySize.getConfiguration().reload();
                    entitySize.resetSize((Player)sender);
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size.reset"));
                    return false;
                } else if (args.length == 2) {
                    if(args[1].equalsIgnoreCase("@a")) {
                        if(!hasPermission(sender, entitySize.getPermission("reset.all"))) {
                            sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                            return false;
                        }
                        Bukkit.getOnlinePlayers().forEach(entitySize::resetSize);
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size.reset_all")
                                .replace("%count%", String.valueOf(Bukkit.getOnlinePlayers().size())));
                        return true;
                    }
                    if(!hasPermission(sender, entitySize.getPermission("reset.player"))) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                        return false;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if(target == null || !target.isOnline()) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player.not_found")
                                .replace("%player%", args[1]));
                        return false;
                    }
                    entitySize.resetSize(target);
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size.reset_player")
                            .replace("%player%", target.getName()));
                    return true;
                }
                sendCommands(sender);
                return false;
            }

            default -> {
                try {
                    if(!hasPermission(sender, entitySize.getPermission("self"))) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                        return false;
                    }
                    double size = Double.parseDouble(args[0]);
                    int time = -1;
                    if (args.length >= 2) {
                        time = Integer.parseInt(args[1]);
                    }
                    Player player = (Player) sender;
                    setSize(sender, player, size, time);

                    if(time == -1) {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player.size_changed")
                                .replace("%player%", player.getName()));
                    } else {
                        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player.size_changed_time")
                                .replace("%player%", player.getName())
                                .replace("%time%", String.valueOf(time)));
                    }
                } catch (Exception exception) {
                    sendCommands(sender);
                }
            }
        }

        return false;
    }

    private boolean handleEntityArg(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendCommands(sender);
            return false;
        }

        switch (args[1].toLowerCase()) {
            case "looking" -> {
                if(!hasPermission(sender, entitySize.getPermission("entity.looking"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }

                double size = Double.parseDouble(args[2]);
                int time = -1;
                if (args.length >= 4) {
                    time = Integer.parseInt(args[3]);
                }

                if(!(sender instanceof Player player)) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player_only"));
                    return false;
                }

                Optional<LivingEntity> optionalEntity = entitySize.getEntity(player, 30);

                if(optionalEntity.isEmpty()) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.entity.no_entity"));
                    return false;
                }
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.entity.looking_changed")
                        .replace("%size%", String.valueOf(size)));
                handleEntities(entity -> optionalEntity.get() == entity, size, time, sender);

                return true;
            }
            case "tag" -> {
                if(!hasPermission(sender, entitySize.getPermission("entity.tag"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if (args.length < 4) {
                    sendCommands(sender);
                    return false;
                }
                String tag = args[2];
                double size = Double.parseDouble(args[3]);
                int time = -1;
                if (args.length >= 5) {
                    time = Integer.parseInt(args[4]);
                }
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.entity.tag_changed")
                        .replace("%tag%", tag));
                handleEntities(entity -> entity.getScoreboardTags().contains(tag), size, time, sender);

                return true;
            }
            case "name" -> {
                if(!hasPermission(sender, entitySize.getPermission("entity.name"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if (args.length < 4) {
                    sendCommands(sender);
                    return false;
                }
                String name = args[2];
                double size = Double.parseDouble(args[3]);
                int time = -1;
                if (args.length >= 5) {
                    time = Integer.parseInt(args[4]);
                }
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.entity.name_changed")
                        .replace("%name%", name));
                handleEntities(entity -> (entity.getType().name().equalsIgnoreCase(name)), size, time, sender);

                return true;
            }
            case "uuid" -> {
                if(!hasPermission(sender, entitySize.getPermission("entity.uuid"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if (args.length < 4) {
                    sendCommands(sender);
                    return false;
                }
                UUID uuid = UUID.fromString(args[2]);
                double size = Double.parseDouble(args[3]);
                int time = -1;
                if (args.length >= 5) {
                    time = Integer.parseInt(args[4]);
                }
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.entity.uuid_changed")
                        .replace("%uuid%", String.valueOf(uuid)));
                handleEntities(entity -> entity.getUniqueId().equals(uuid), size, time, sender);
                return true;
            }
            case "range" -> {
                if(!hasPermission(sender, entitySize.getPermission("entity.range"))) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.no_permission"));
                    return false;
                }
                if (args.length < 4) {
                    sendCommands(sender);
                    return false;
                }
                if(!(sender instanceof Player player)) {
                    sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.player_only"));
                    return false;
                }

                double range = Double.parseDouble(args[2]);
                double size = Double.parseDouble(args[3]);
                int time = -1;
                if (args.length >= 5) {
                    time = Integer.parseInt(args[4]);
                }
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.entity.range_changed")
                        .replace("%range%", String.valueOf(range)));
                handleEntities(entity -> isWithinRange(entity, player, range), size, time, sender);
                return true;
            }
            default -> {sendCommands(sender); return false;}
        }
    }

    private boolean isWithinRange(Entity entity, Player player, double range) {
        return player.getNearbyEntities(range, range, range).contains(entity) || entity == player;
    }

    private void handleEntities(Predicate<Entity> condition, double size, int time, CommandSender sender) {
        List<UUID> affectedEntities = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (condition.test(entity) && entity instanceof LivingEntity livingEntity) {
                    setSize(sender, livingEntity, size, time);
                    if(entity instanceof Player) {
                        affectedEntities.add(entity.getUniqueId());
                    }
                }
            }
        }
        if (time > 0) {
            scheduleReset(affectedEntities, time);
            sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size.reset_scheduled")
                    .replace("%count%", String.valueOf(affectedEntities.size()))
                    .replace("%time%", String.valueOf(time)));
        }
    }

    private void setSize(CommandSender sender, LivingEntity entity, double size, int time) {
        double minSize = 0.1;
        double maxSize = 10.0;

        if (!hasPermission(sender, "entitysize.sizelimit.bypass")) {
            if (sender instanceof Player player) {
                double lowestMin = -1;
                double highestMax = -1;

                for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
                    String permission = perm.getPermission();
                    if (perm.getValue()) {
                        if (permission.startsWith("entitysize.sizelimit.min.")) {
                            try {
                                String sizeStr = permission.substring("entitysize.sizelimit.min.".length());
                                double permMinSize = Double.parseDouble(sizeStr);
                                if (lowestMin < 0 || permMinSize < lowestMin) {
                                    lowestMin = permMinSize;
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                        else if (permission.startsWith("entitysize.sizelimit.max.")) {
                            try {
                                String sizeStr = permission.substring("entitysize.sizelimit.max.".length());
                                double permMaxSize = Double.parseDouble(sizeStr);

                                if (highestMax < 0 || permMaxSize > highestMax) {
                                    highestMax = permMaxSize;
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }

                if (lowestMin >= 0) {
                    minSize = lowestMin;
                }
                if (highestMax >= 0) {
                    maxSize = highestMax;
                }
            }

            if (size < minSize) {
                size = minSize;
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size.limited_min")
                        .replace("%size%", String.valueOf(minSize)));
            }
            if (size > maxSize) {
                size = maxSize;
                sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.size.limited_max")
                        .replace("%size%", String.valueOf(maxSize)));
            }
        }

        entitySize.setSize(entity, size);
        if (time > 0 && entity instanceof Player player) {
            scheduleReset(Collections.singletonList(player.getUniqueId()), time);
        }
    }

    private void scheduleReset(List<UUID> entityUUIDs, int minutes) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : entityUUIDs) {
                    Player player = Bukkit.getPlayer(uuid);
                    if(player == null || !player.isOnline()) {
                        entitySize.resetOfflinePlayerSize(uuid);
                    } else {
                        entitySize.resetSize(player);
                    }
                }
            }
        }.runTaskLater(entitySize, minutes * 60 * 20L);
    }

    private boolean sendCommands(CommandSender sender) {
        sender.sendMessage(entitySize.getPrimaryColor() + entitySize.getLanguage().getMessage("messages.help_message"));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            if(!hasPermission(sender, entitySize.getPermission("commands"))) {
                return new ArrayList<>();
            }
        }

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (hasPermission(sender, entitySize.getPermission("add"))) commands.add("add");
            if (hasPermission(sender, entitySize.getPermission("player"))) commands.add("player");
            if (hasPermission(sender, entitySize.getPermission("entity"))) commands.add("entity");
            if (hasPermission(sender, entitySize.getPermission("reload"))) commands.add("reload");
            if (hasPermission(sender, entitySize.getPermission("reset"))) commands.add("reset");
            if (hasPermission(sender, entitySize.getPermission("self")) && sender instanceof Player) commands.add("<size>");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        if (args.length == 2) {
            if ("add".equalsIgnoreCase(args[0]) && hasPermission(sender, entitySize.getPermission("add"))) {
                commands.add("<size>");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }

        if (args.length == 2) {
            if(args[0].equalsIgnoreCase("player") && hasPermission(sender, entitySize.getPermission("player"))) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    commands.add(player.getName());
                });
                commands.add("@a");
            }
            if(args[0].equalsIgnoreCase("entity") && hasPermission(sender, entitySize.getPermission("entity"))) {
                if (hasPermission(sender, entitySize.getPermission("entity.looking"))) commands.add("looking");
                if (hasPermission(sender, entitySize.getPermission("entity.tag"))) commands.add("tag");
                if (hasPermission(sender, entitySize.getPermission("entity.name"))) commands.add("name");
                if (hasPermission(sender, entitySize.getPermission("entity.uuid"))) commands.add("uuid");
                if (hasPermission(sender, entitySize.getPermission("entity.range"))) commands.add("range");
            }
            try {
                Integer.parseInt(args[0]);
                if (hasPermission(sender, entitySize.getPermission("entity.self"))) commands.add("<reset time in minutes>");
            } catch (NumberFormatException ignore){}
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }

        if (args.length == 3) {
            if(args[0].equalsIgnoreCase("player") && hasPermission(sender, entitySize.getPermission("player"))) commands.add("<size>");
            if(args[0].equalsIgnoreCase("entity") && hasPermission(sender, entitySize.getPermission("entity"))) {
                switch (args[1].toLowerCase()) {
                    case "tag" -> commands.add("<tag>");
                    case "name" -> commands.add("<name>");
                    case "looking" -> commands.add("<size>");
                    case "uuid" -> commands.add("<uuid>");
                    case "range" -> commands.add("<range>");
                }
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }

        if (args.length == 4) {
            if(args[0].equalsIgnoreCase("player") && hasPermission(sender, entitySize.getPermission("player"))) commands.add("<reset time in minutes>");
            if(args[0].equalsIgnoreCase("entity") && hasPermission(sender, entitySize.getPermission("entity"))) {
                if (args[1].toLowerCase().equals("looking")) {
                    commands.add("<reset time in minutes>");
                }
                switch (args[1].toLowerCase()) {
                    case "tag", "name", "uuid", "range" -> commands.add("<size>");
                }
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        }

        if (args.length == 5) {
            if(args[0].equalsIgnoreCase("entity") && hasPermission(sender, entitySize.getPermission("entity"))) {
                switch (args[1].toLowerCase()) {
                    case "tag", "name", "uuid", "range" -> commands.add("<reset time in minutes>");
                }
            }
            StringUtil.copyPartialMatches(args[4], commands, completions);
        }

        return completions;
    }
}