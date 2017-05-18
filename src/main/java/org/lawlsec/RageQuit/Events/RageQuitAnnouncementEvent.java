package org.lawlsec.RageQuit.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.sql.Timestamp;
import java.util.Calendar;


/*
  RageQuitEvent for other plugins to
  trigger or listen for. This makes
  integration with other plugins a lot
  easier.
*/
public class RageQuitAnnouncementEvent extends Event implements Cancellable {

    /*
      Global variable for the player
      that has rage quit. Used to
      pass data to listeners.
    */
    private Player player;

    /*
      Timestamp of the event used to
      accurately track announcements
      and pass data to listeners.
    */
    private Timestamp time;

    /*
      Global variable to hold the
      message that the server is sent
      when they a player is kicked
      after running the /ragequit
      command.
    */
    private String message;

    /*
      Global variable for event
      cancellation. This allows
      listeners to cancel the event
      therefore preventing it from
      occurring.
    */
    private boolean isCancelled;

    /*
      A HandlerList is required by
      Bukkit in order for custom
      events to work. This list will
      contain all registered handlers
      for this event.
    */
    private static final HandlerList handlers = new HandlerList();

    /*
      Returns the player object set
      by the initializer, used for
      passing data to listeners.
    */
    public Player getPlayer() {
        return player;
    }

    /*
      Returns the message set by the
      initializer, used for passing
      data to listeners.
    */
    public String getMessage() {
        return message;
    }

    /*
      Returns the timestamp of the
      event. Used for passing data
      to listeners.
    */
    public Timestamp getTime() {
        return time;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /*
      Used by Bukkit to return a list
      of handlers for this event. This
      is required by Bukkit for custom
      events to work.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public RageQuitAnnouncementEvent(Player player, String message) {
        /*
          Set the player variable in our event
          from the calling class, used to pass
          the player object as data to listeners.
        */
        this.player = player;

        /*
          Set the message variable in our event
          from the calling class, used to pass
          the message as data to listeners.
         */
        this.message = message;

        /*
          We set the time of the event here
          instead of having the calling class
          do it. This way the timestamp is
          less likely to be inaccurate.
        */
        this.time = new Timestamp(Calendar.getInstance().getTime().getTime());
    }
}
