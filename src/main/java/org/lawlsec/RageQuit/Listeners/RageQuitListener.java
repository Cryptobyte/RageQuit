package org.lawlsec.RageQuit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.lawlsec.RageQuit.Actions.RageQuitAction;
import org.lawlsec.RageQuit.Events.RageQuitEvent;
import org.lawlsec.RageQuit.RageQuit;

/*
  Listener class for RageQuit events
 */
public class RageQuitListener implements Listener {

    /*
      Listen for RageQuitEvents internally
      the event is not acted upon until it
      is picked up by this handler to allow
      cancellation and calling from other
      plugins.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRageQuit(RageQuitEvent event) {

        // If events are disabled in config then ignore this event
        if (!RageQuit.getInstance().getConfig().getBoolean("Events.Enabled"))
            return;

        // If event is cancelled, ignore it
        if (event.isCancelled())
            return;

        // Log that we received the RageQuit event
        RageQuit.getInstance().getLogger().info(
            String.format("Received RageQuitEvent from %s",
                event.getPlayer().getName())
        );

        // Fire the action for this event
        new RageQuitAction().run(
            event.getPlayer(),
            event.getMessage()
        );
    }
}
