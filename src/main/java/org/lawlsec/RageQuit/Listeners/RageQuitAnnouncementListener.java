package org.lawlsec.RageQuit.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.lawlsec.RageQuit.Actions.RageQuitAnnouncementAction;
import org.lawlsec.RageQuit.Events.RageQuitAnnouncementEvent;
import org.lawlsec.RageQuit.RageQuit;

/*
  Listener for RageQuitAnnouncement events
 */
public class RageQuitAnnouncementListener implements Listener {

    /*
      Listen for RageQuitEvents internally
      the event is not acted upon until it
      is picked up by this handler to allow
      cancellation and calling from other
      plugins.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRageQuitAnnouncement(RageQuitAnnouncementEvent event) {

        // If events are disabled in config then ignore this event
        if (!RageQuit.getInstance().getConfig().getBoolean("Events.Enabled"))
            return;

        // If event is cancelled, ignore it
        if (event.isCancelled())
            return;

        // Log that we received the RageQuitAnnouncement event
        RageQuit.getInstance().getLogger().info(
            String.format("Received RageQuitAnnouncement from %s",
                event.getPlayer().getName())
        );

        /*
          If Announcement is enabled in the config then
          announce the players rage quit to the server.
        */
        if (RageQuit.getInstance().getConfig().getBoolean("Announcement.Enabled")) {

            // Fire the action for this event
            new RageQuitAnnouncementAction().run(
                event.getPlayer(),
                event.getMessage()
            );
        }
    }
}
