package org.lawlsec.RageQuit.Actions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RageQuitAnnouncementAction implements RageQuitActionInterface {
    @Override
    public void run(Player player, String message) {

        /*
          Broadcast the message defined in
          the Config.
        */
        Bukkit.broadcastMessage(

            /*
              Call on Bukkit's ChatColor to translate &
              color codes that may be in the announcement
              message defined in the Config.
            */
            ChatColor.translateAlternateColorCodes('&',

                // Broadcast announcement message
                message
            )
        );
    }
}
