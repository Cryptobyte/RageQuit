package org.lawlsec.RageQuit.Actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RageQuitConsoleAction implements RageQuitActionInterface {
    @Override
    public void run(Player player, String message) {

        /*
          Shutdown the server since
          console sent this action.
        */
        Bukkit.shutdown();
    }
}
