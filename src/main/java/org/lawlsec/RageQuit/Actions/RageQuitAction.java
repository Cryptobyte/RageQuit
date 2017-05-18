package org.lawlsec.RageQuit.Actions;

import org.bukkit.entity.Player;

public class RageQuitAction implements RageQuitActionInterface {
    @Override
    public void run(Player player, String message) {

        /*
          Kick the sender (Player) with
          the message defined in the
          event which is set by the calling
          class.
        */
        player.getPlayer().kickPlayer(

            /*
              Kick the player with
              the defined message.
            */
            message
        );
    }
}
