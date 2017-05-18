package org.lawlsec.RageQuit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RageQuit extends JavaPlugin {

    // Create a global instance of the
    // random class to use while the
    // plugin is running.
    private static Random rand;

    // Executor service to fill the
    // buffer concurrently.
    private static ExecutorService executor;

    // This provides a small buffer for
    // our random messages. Is this really
    // needed? No. But we're overdeveloping
    // this plugin for fun.
    private static Stack<String> buffer;

    // Size of the buffer. We're defining
    // this here because it needs to be
    // a static variable although it is
    // read from the config.
    private static int bufferSize;

    // If the buffer is enabled we need to
    // put the messages from the config into
    // a static variable like #bufferSize
    private static List<String> messages;

    // Boolean value indicating whether or not
    // the buffer is currently filling in our
    // executor. This is to prevent scheduling
    // multiple background tasks to fill the
    // buffer which can be an issue if the
    // command is spammed by players.
    private static boolean isBufferFilling = false;

    @Override
    public void onEnable() {

        // Save Default Config (config.yml)
        this.saveDefaultConfig();

        // Register "ragequit" command
        // to be handled by this class
        this.getCommand("ragequit").setExecutor(this);

        // Initialize Random in a global
        // state so that the generated values
        // are chosen from the same generator
        // the whole time the plugin is active
        rand = new Random();

        // If the config is set to have the
        // buffer enabled, we have to initialize
        // it and fill it for the first time.
        if (this.getConfig().getBoolean("Buffer.Enabled")) {

            // Initialize the buffer.
            buffer = new Stack<>();

            // Initialize the executor service
            executor = Executors.newSingleThreadExecutor();

            // Get the desired buffer size from the Config
            bufferSize = this.getConfig().getInt("Buffer.Size");

            // Get the messages list from the Config
            messages = this.getConfig().getStringList("Messages");

            // Log that the buffer is enabled along
            // with its defined size.
            this.getLogger().info(

                // Using String.format so we can include
                // an integer value the proper way.
                String.format("Buffer enabled with a size of %d", bufferSize)
            );

            // Fill the buffer
            fillBuffer();
        }
    }

    @Override
    public void onDisable() {

        // Check if the buffer is enabled, if it is
        // shut the executor and all its tasks down.
        // http://i.imgur.com/hjmBqpI.jpg
        if (this.getConfig().getBoolean("Buffer.Enabled")) {

            // Executors keep running in the
            // background after a task has
            // completed, listening for new
            // tasks. So here we stop all tasks
            // and shut down the executor.
            executor.shutdownNow();
        }
    }

    /***
     * Fills the buffer to the size defined
     * in #bufferSize concurrently.
     */
    private static void fillBuffer() {

        // Submit the task of filling the
        // buffer to our executor so that
        // it will take place on a separate
        // thread.
        executor.submit(() -> {

            // Set our variable telling the
            // rest of the program that the
            // buffer is being filled.
            isBufferFilling = true;

            // While the buffer size is less than
            // the buffer size defined in the config
            // (and set in our variable) we need to
            // push another value. Once the sizes
            // match, the while loop will exit and
            // our buffer will be full.
            while (buffer.size() < bufferSize) {

                // Since we're using a Stack<T> for
                // our buffer, it works like RAM in
                // which we push and pop things off
                // of the Stack<T>, so here we are
                // pushing a randomly chosen string
                // from our "messages" list into the
                // buffer.
                buffer.push(

                        // Get a random message string from
                        // our list of messages from the Config.
                        messages.get(

                                // Call the Random generator
                                // for a random integer that
                                // conforms to the size of
                                // our string list "Messages"
                                rand.nextInt(

                                        // Pass in the string lists
                                        // size to use as a maximum
                                        // number for the random
                                        // integer. We're doing this
                                        // here so that if the Config
                                        // changes we don't need to reload
                                        // the whole plugin.
                                        messages.size()
                                )
                        )
                );
            }

            // Now that the buffer is full
            // we reset the variable so that
            // the task can be scheduled again.
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
        // In Stack<T> the .pop function returns
        // the element at the top of the stack
        // and removes it at the same time. So,
        // we move that element (String) into a
        // variable for returning at the end of
        // the function and remove it from the
        // buffer at the same time.
        String message = buffer.pop();

        // If the buffer is not already filling
        // tell the executor to refill it.
        if (!isBufferFilling) {

            // Echo to console that we are refilling the buffer.
            // This is more for testing purposes but it's useful
            // information anyways.
            this.getLogger().info(

                // Using String.format so we can include
                // an integer value the proper way.
                String.format("Filling buffer. Current size %d", buffer.size())
            );

            // Fill the buffer
            fillBuffer();
        }

        // Return the String that we retrieved
        // with the pop function earlier.
        return message;
    }

    /***
     * Gets a random message from the configuration file's
     * list of messages.
     * @return Reasonably random message.
     */
    private String getRandomMessage() {
        List<String> Messages =
                this.getConfig().getStringList("Messages");

        // Return a random string from
        // "Messages" list in Config
        return Messages.get(

            // Call the Random generator
            // for a random integer that
            // conforms to the size of
            // our string list "Messages"
            rand.nextInt(

                // Pass in the string lists
                // size to use as a maximum
                // number for the random
                // integer. We're doing this
                // here so that if the Config
                // changes we don't need to reload
                // the whole plugin.
                Messages.size()
            )
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // If the command comes from something other
        // than a "Player" we can safely assume it
        // was run from Console.
        if (!(sender instanceof Player)) {

            // Rage quitting is no joke, console..
            Bukkit.shutdown();

            // Return here, since we don't need
            // to do any more for console..
            return true;
        }

        // Define a variable to hold our
        // rage quit message so that we
        // can use it when the player is
        // kicked.
        String randomMessage;

        // Check if the buffer is enabled
        if (this.getConfig().getBoolean("Buffer.Enabled")) {

            // If the buffer is enabled, get the message from it
            randomMessage = getRandomMessageFromBuffer();

        } else {

            // If the buffer is disabled, get the message from
            // the config.
            randomMessage = getRandomMessage();
        }

        // Kick the sender (Player) with a random
        // message.
        ((Player)sender).kickPlayer(

            // Use the random string as a kick
            // message
            randomMessage
        );

        // If Announcement is enabled in the config then
        // announce the players rage quit to the server.
        if (this.getConfig().getBoolean("Announcement.Enabled")) {

            // Get announcement message from the config
            String announcement =
                    this.getConfig().getString("Announcement.Message");

            // Replace {player} with the player's name
            announcement =
                    announcement.replace("{player}", sender.getName());

            // Broadcast the message defined in
            // the Config.
            Bukkit.broadcastMessage(

                // Call on Bukkit's ChatColor to translate &
                // color codes that may be in the announcement
                // message defined in the Config.
                ChatColor.translateAlternateColorCodes('&',

                    // Broadcast announcement message
                    announcement
                )
            );
        }

        // Return true to indicate that
        // the command was run successfully.
        return true;
    }
}
