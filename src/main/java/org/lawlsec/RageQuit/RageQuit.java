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
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static Random rand;

    /*
      Executor service to fill the
      buffer concurrently.
    */
    private static ExecutorService executor;

    /*
      This provides a small buffer for
      our random messages. Is this really
      needed? No. But we're overdeveloping
      this plugin for fun.
    */
    private static Stack<String> buffer;

    /*
      Size of the buffer. We're defining
      this here because it needs to be
      a static variable although it is
      read from the config.
    */
    private static int bufferSize;

    /*
      If the buffer is enabled we need to
      put the messages from the config into
      a static variable like #bufferSize
    */
    private static List<String> messages;

    /*
      Boolean value indicating whether or not
      the buffer is currently filling in our
      executor. This is to prevent scheduling
      multiple background tasks to fill the
      buffer which can be an issue if the
      command is spammed by players.
    */
    private static boolean isBufferFilling = false;

    /*
      Hold a static instance of the plugin
      so we can access plugin specific
      variables from outside of this class.
    */
    private static RageQuit instance;

    @Override
    public void onEnable() {

        // Save Default Config (config.yml)
        this.saveDefaultConfig();

        // Set the instance to this
        instance = this;

        /*
          Register "ragequit" command
          to be handled by this class
        */
        this.getCommand("ragequit").setExecutor(this);

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

        /*
          If the config is set to have the
          buffer enabled, we have to initialize
          it and fill it for the first time.
        */
        if (this.getConfig().getBoolean("Buffer.Enabled")) {

            // Initialize the buffer.
            buffer = new Stack<>();

            // Initialize the executor service
            executor = Executors.newSingleThreadExecutor();

            // Get the desired buffer size from the Config
            bufferSize = this.getConfig().getInt("Buffer.Size");

            // Get the messages list from the Config
            messages = this.getConfig().getStringList("Messages");

            /*
              Log that the buffer is enabled along
              with its defined size.
            */
            this.getLogger().info(

                /*
                  Using String.format so we can include
                  an integer value the proper way.
                */
                String.format("Buffer enabled with a size of %d", bufferSize)
            );

            // Fill the buffer
            fillBuffer();
        }

        // Log that the Event system is enabled
        if (this.getConfig().getBoolean("Events.Enabled")) {
            this.getLogger().info("Event system is enabled");
        }
    }

    @Override
    public void onDisable() {

        /*
          Check if the buffer is enabled, if it is
          shut the executor and all its tasks down.
          http://i.imgur.com/hjmBqpI.jpg
        */
        if (this.getConfig().getBoolean("Buffer.Enabled")) {

            /*
              Executors keep running in the
              background after a task has
              completed, listening for new
              tasks. So here we stop all tasks
              and shut down the executor.
            */
            executor.shutdownNow();
        }
    }

    /***
     * Fills the buffer to the size defined
     * in #bufferSize concurrently.
     */
    private static void fillBuffer() {

        /*
          Submit the task of filling the
          buffer to our executor so that
          it will take place on a separate
          thread.
        */
        executor.submit(() -> {

            /*
              Set our variable telling the
              rest of the program that the
              buffer is being filled.
            */
            isBufferFilling = true;

            /*
              While the buffer size is less than
              the buffer size defined in the config
              (and set in our variable) we need to
              push another value. Once the sizes
              match, the while loop will exit and
              our buffer will be full.
            */
            while (buffer.size() < bufferSize) {

                /*
                  Since we're using a Stack<T> for
                  our buffer, it works like RAM in
                  which we push and pop things off
                  of the Stack<T>, so here we are
                  pushing a randomly chosen string
                  from our "messages" list into the
                  buffer.
                */
                buffer.push(

                     /*
                       Get a random message string from
                       our list of messages from the Config.
                     */
                     messages.get(

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
                    )
                );
            }

            /*
              Now that the buffer is full
              we reset the variable so that
              the task can be scheduled again.
            */
            isBufferFilling = false;
        });
    }

    /***
     * Gets a message from the buffer and tells the
     * executor to refill the buffer if it is not
     * already filling.
     * @return Reasonably random message from the buffer.
     */
    private String getRandomMessageFromBuffer() {

        /*
          In Stack<T> the .pop function returns
          the element at the top of the stack
          and removes it at the same time. So,
          we move that element (String) into a
          variable for returning at the end of
          the function and remove it from the
          buffer at the same time.
        */
        String message = buffer.pop();

        /*
          If the buffer is not already filling
          tell the executor to refill it.
        */
        if (!isBufferFilling) {

            /*
              Echo to console that we are refilling the buffer.
              This is more for testing purposes but it's useful
              information anyways.
            */
            this.getLogger().info(

                /*
                  Using String.format so we can include
                  an integer value the proper way.
                */
                String.format("Filling buffer. Current size %d", buffer.size())
            );

            // Fill the buffer
            fillBuffer();
        }

        /*
          Return the String that we retrieved
          with the pop function earlier.
        */
        return message;
    }

    /***
     * Gets a random message from the configuration file's
     * list of messages.
     * @return Reasonably random message.
     */
    private String getRandomMessage() {

        // Get messages list from the Config.
        List<String> messages =
                this.getConfig().getStringList("Messages");

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
            if (this.getConfig().getBoolean("Events.Enabled")) {

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

        /*
          Define a variable to hold our
          rage quit message so that we
          can use it when the player is
          kicked.
        */
        String randomMessage;

        // Check if the buffer is enabled
        if (this.getConfig().getBoolean("Buffer.Enabled")) {

            // If the buffer is enabled, get the message from it
            randomMessage = getRandomMessageFromBuffer();

        } else {

            /*
              If the buffer is disabled, get the message from
              the config.
            */
            randomMessage = getRandomMessage();
        }

        // If Events are enable then fire the event
        if (this.getConfig().getBoolean("Events.Enabled")) {

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
                new RageQuitEvent((Player) sender, randomMessage)
            );

        } else {

            // If Events are not enabled then just run the action
            new RageQuitAction().run((Player)sender, randomMessage);
        }

        /*
          Get announcement message from the config
          using our instance defined in the main
          class.
        */
        String announcement =
            this.getConfig().getString("Announcement.Message");

        // Replace {player} with the player's name
        announcement =
            announcement.replace("{player}", sender.getName());


        // If Events are enable then fire the event
        if (this.getConfig().getBoolean("Events.Enabled")) {

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
