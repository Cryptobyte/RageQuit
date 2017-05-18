package org.lawlsec.RageQuit.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.sql.Timestamp;
import java.util.Calendar;

/*
  RageQuitConsoleEvent for other plugins to
  trigger or listen for. This makes
  integration with other plugins a lot
  easier.
*/
public class RageQuitConsoleEvent extends Event implements Cancellable {
    /*
      Timestamp of the event used
      to accurately track rage quits
      and pass data to listeners.
    */
    private Timestamp time;

    /*
      Returns the timestamp of the
      event. Used for passing data
      to listeners.
    */
    public Timestamp getTime() {
        return time;
    }

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

    public RageQuitConsoleEvent() {
        /*
          We set the time of the event here
          instead of having the calling class
          do it. This way the timestamp is
          less likely to be inaccurate.
        */
        this.time = new Timestamp(Calendar.getInstance().getTime().getTime());
    }
}
