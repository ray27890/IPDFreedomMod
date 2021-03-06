package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "For the mighty senior admins that weild this ban hammer, you cannot be stopped.", usage = "/<command> <username> [reason] [-nrb]")
public class Command_banhammer extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final String username;
        final List<String> ips = new ArrayList<>();

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            final PlayerData entry = plugin.pl.getData(args[0]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            username = entry.getUsername();
            ips.addAll(entry.getIps());
        }
        else
        {
            final PlayerData entry = plugin.pl.getData(player);
            username = player.getName();
            ips.addAll(entry.getIps());

            // Deop
            player.setOp(false);

            // Gamemode survival
            player.setGameMode(GameMode.SURVIVAL);

            // Clear inventory
            player.getInventory().clear();

            // Strike with lightning
            final Location targetPos = player.getLocation();
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                    targetPos.getWorld().strikeLightning(strike_pos);
                    targetPos.getWorld().strikeLightning(strike_pos);
                    targetPos.getWorld().strikeLightning(strike_pos);
                    targetPos.getWorld().strikeLightning(strike_pos);
                }
            }

            // generate explosion
            player.getWorld().createExplosion(player.getLocation(), 0F, false);
                
            // Kill player
            player.setHealth(0.0);
        }

        String reason = null;
        Boolean cancelRollback = false;
        if (args.length >= 2)
        {
            if (args[args.length - 1].equalsIgnoreCase("-nrb"))
            {
                cancelRollback = true;
                if (args.length >= 3)
                {
                    reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 1), " ");
                }
            }
            else
            {
                reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
            }
        }

        // Checks if CoreProtect is loaded and installed, and skips the rollback and uses CoreProtect directly
        if (!cancelRollback)
        {
            if (!plugin.cpb.isEnabled())
            {
                // Undo WorldEdits
                try
                {
                    plugin.web.undo(player, 15);
                }
                catch (NoClassDefFoundError | NullPointerException ex)
                {
                }

                // Rollback
                plugin.rb.rollback(username);
            }
            else
            {
                plugin.cpb.rollback(username);
            }
        }

        if (player != null)
        {
        FUtil.adminAction(sender.getName(), "Unleashed the senior's all mighty ban hammer on " + player.getName(), true);
        FUtil.bcastMsg(player.getName() + " was struck with the ban hammer.", ChatColor.WHITE);
        }

        // Ban player
        Ban ban = Ban.forPlayerName(username, sender, null, reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
        }
        plugin.bm.addBan(ban);

        // Broadcast
        String ip = FUtil.getFuzzyIp(player.getAddress().getAddress().getHostAddress());
        final StringBuilder bcast = new StringBuilder()
                .append(ChatColor.RED)
                .append("Banning: ")
                .append(username)
                .append(", IP: ")
                .append(ip);
        if (reason != null)
        {

        }
        FUtil.bcastMsg(bcast.toString());
        final StringBuilder adminNotice = new StringBuilder()
                .append(ChatColor.DARK_AQUA)
                .append("[STAFF] ")
                .append(ChatColor.WHITE)
                .append(sender.getName())
                .append(" has struck the banhammer on ")
                .append(username)
                .append(" for ")
                .append(reason);
        plugin.al.messageAllAdmins(adminNotice.toString());

        // Kick player
        if (player != null)
        {
            player.kickPlayer(ban.bakeKickMessage());
        }

        // Log ban
        plugin.pul.logPunishment(new Punishment(username, ips.get(0), sender.getName(), PunishmentType.BANHAMMER, reason));

        return true;
    }
}
