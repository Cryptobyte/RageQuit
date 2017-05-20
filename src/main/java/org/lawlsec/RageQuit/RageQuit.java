package org.lawlsec.RageQuit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.lawlsec.RageQuit.Actions.RageQuitAction;
import org.lawlsec.RageQuit.Actions.RageQuitAnnouncementAction;
import org.lawlsec.RageQuit.Actions.RageQuitConsoleAction;
import org.lawlsec.RageQuit.Events.RageQuitAnnouncementEvent;
import org.lawlsec.RageQuit.Events.RageQuitConsoleEvent;
import org.lawlsec.RageQuit.Events.RageQuitEvent;
import org.lawlsec.RageQuit.Listeners.RageQuitAnnouncementListener;
import org.lawlsec.RageQuit.Listeners.RageQuitConsoleListener;
import org.lawlsec.RageQuit.Listeners.RageQuitListener;

import java.util.List;
import java.util.Random;

/*
  Main class for the plugin. This is what
  controls the entire plugin lifecycle.
 */
public class RageQuit extends JavaPlugin {

    /*
      Create a global instance of the
      random class to use while the
      plugin is running.
    */
    private Random rand;

    /*
      Hold a static instance of the plugin
      so we can access plugin specific
      variables from outside of this class.
    */
    private static RageQuit instance;

    @Override
    public void onEnable() {

        // Save Default Config (config.yml)
        saveDefaultConfig();

        // Set the instance to this
        instance = this;

        //Register "ragequit" command to be handled by this class
        getCommand("ragequit").setExecutor(this);

        // Register our listener for RageQuit events
        getServer().getPluginManager().registerEvents(new RageQuitListener(), this);

        // Register our listener for RageQuitAnnouncementEvents
        getServer().getPluginManager().registerEvents(new RageQuitAnnouncementListener(), this);

        // Register our listener for RageQuitConsoleEvents
        getServer().getPluginManager().registerEvents(new RageQuitConsoleListener(), this);

        /*
          Initialize Random in a global
          state so that the generated values
          are chosen from the same generator
          the whole time the plugin is active
        */
        rand = new Random();

        // Log that the Event system is enabled
        if (getConfig().getBoolean("Events.Enabled"))
            getLogger().info("Event system is enabled");

    }

    @Override
    public void onDisable() { }

    /***
     * Gets a random message from the configuration file's
     * list of messages.
     * @return Reasonably random message.
     */
    private String getRandomMessage() {

        // Get messages list from the Config.
        List<String> messages =
            getConfig().getStringList("Messages");

        /*
          Return a random string from
          "Messages" list in Config
        */
        return messages.get(

            /*
              Call the Random generator
              for a random integer that
              conforms to the size of
              our string list "Messages"
            */
            rand.nextInt(

                /*
                  Pass in the string lists
                  size to use as a maximum
                  number for the random
                  integer. We're doing this
                  here so that if the Config
                  changes we don't need to reload
                  the whole plugin.
                */
                messages.size()
            )
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        /*
          If the command comes from something other
          than a "Player" we can safely assume it
          was run from Console.
        */
        if (!(sender instanceof Player)) {

            // If Events are enable then fire the event
            if (getConfig().getBoolean("Events.Enabled")) {

                /*
                  Call the RageQuitConsoleEvent, setting
                  it up on the event bus so that it travels
                  along the handler list before firing.
                */
                Bukkit.getServer().getPluginManager().callEvent(

                    /*
                      Create the new RageQuitConsoleEvent
                      for calling so that it passes
                      through all of the event handlers.
                    */
                    new RageQuitConsoleEvent()
                );

            } else {

                // If Events are not enabled then just run the action
                new RageQuitConsoleAction().run(null, null);
            }

            /*
              Return here, since we don't need
              to do any more for console..
            */
            return true;
        }

        // If Events are enable then fire the event
        if (getConfig().getBoolean("Events.Enabled")) {

            /*
              Call the RageQuitEvent, setting it up
              on the event bus so that it travels
              along the handler list before firing.
            */
            Bukkit.getServer().getPluginManager().callEvent(

                /*
                  Create the new RageQuitEvent for
                  calling so that it passes through
                  all of the event handlers.
                */
                new RageQuitEvent((Player) sender, getRandomMessage())
            );

        } else {

            // If Events are not enabled then just run the action
            new RageQuitAction().run((Player)sender, getRandomMessage());
        }

        /*
          Get announcement message from the config
          using our instance defined in the main
          class.
        */
        String announcement =
            getConfig().getString("Announcement.Message");

        // Replace {player} with the player's name
        announcement =
            announcement.replace("{player}", sender.getName());


        // If Events are enable then fire the event
        if (getConfig().getBoolean("Events.Enabled")) {

            /*
              Call the RageQuitAnnouncementEvent,
              setting it up on the event bus so
              that it travels along the handler list
              before firing.
            */
            Bukkit.getServer().getPluginManager().callEvent(

                /*
                  Create the new RageQuitAnnouncementEvent
                  for calling so that it passes through
                  all of the event handlers.
                */
                new RageQuitAnnouncementEvent((Player) sender, announcement)
            );

        } else {

            // If Events are not enabled then just run the action
            new RageQuitAnnouncementAction().run((Player)sender, announcement);
        }

        /*
          Return true to indicate that
          the command was run successfully.
        */
        return true;
    }

    public static RageQuit getInstance() {
        return instance;
    }
}
