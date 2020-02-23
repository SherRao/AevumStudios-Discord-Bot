
package tk.sherrao.discord.aevum;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import tk.sherrao.discord.aevum.commands.NewCommand;
import tk.sherrao.logging.Logger;

public class Bot {

    private final Logger log;
    private JDA api;
    private ScheduledExecutorService executor;
    private CommandClient commands;

    private String titleIcon;
    private String footerIcon;
    
    public Bot() {
        this.log = new Logger( "Aevum Studios" );
        try {
            this.api = new JDABuilder( AccountType.BOT ).setAudioEnabled( false )
                    .setToken( BotInformation.DISCORD_TOKEN )
                    .setGame( Game.of( GameType.DEFAULT, "Welcome - Use '!help'!" ) )
                    .build();

            api.awaitReady();

        } catch ( LoginException e ) {
            log.error( "Failed to connect to Discord authentication servers. The internet connection on this machine, or Discord might be down at the moment", e );
            System.exit( 0 );

        } catch ( InterruptedException e ) {
            log.error( "Failed to load the Discord JDA API!", e );
            System.exit( 0 );

        }

        this.executor = Executors.newScheduledThreadPool( 3 );
        this.commands = new CommandClientBuilder()
                .setPrefix( "!" )
                // .setEmojis( ":white_check_mark:", ":warning:", ":no_entry:" )
                .setOwnerId( "190984801929396224" )
                .setScheduleExecutor( executor )
                .setShutdownAutomatically( true )
                .addCommand( new NewCommand( this ) )
                .build();

        this.titleIcon = api.getGuildById( BotInformation.SERVER_ID ).getIconUrl();
        this.footerIcon = api.getUserById( BotInformation.SHERRAO_ID ).getAvatarUrl();
        
        api.addEventListener( commands );
        //api.addEventListener( new NewMemberJoinListener( this ) ); //CHANGE ASAP 
        System.out.println( "done" );
    }

    public Logger getLog() {
        return log;

    }

    public JDA getApi() {
        return api;

    }
    
    public String getTitleIcon() {
        return titleIcon;
        
    }
    
    public String getFooterIcon() {
        return footerIcon;
        
    }

}
