package org.lawlsec.RageQuit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.lawlsec.RageQuit.Actions.RageQuitConsoleAction;
import org.lawlsec.RageQuit.Events.RageQuitConsoleEvent;
import org.lawlsec.RageQuit.RageQuit;

/*
  Listener class for RageQuitConsole events
 */
public class RageQuitConsoleListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRageQuitConsole(RageQuitConsoleEvent event) {

        // If events are disabled in config then ignore this event
        if (!RageQuit.getInstance().getConfig().getBoolean("Events.Enabled"))
            return;

        // If event is cancelled, ignore it
        if (event.isCancelled())
            return;

        // Log that we received the RageQuitConsole event
        RageQuit.getInstance().getLogger().info(
            "Received RageQuitConsoleEvent"
        );

        /*
          Fire the action for this event since
          this event doesn't take arguments
          we will just pass null values to
          work with #RageQuitActionInterface
        */
        new RageQuitConsoleAction().run(
            null,
            null
        );
    }
}
