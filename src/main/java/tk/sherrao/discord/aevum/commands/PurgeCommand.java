
package tk.sherrao.discord.aevum.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;
import tk.sherrao.discord.aevum.Bot;
import tk.sherrao.discord.aevum.BotInformation;

public class PurgeCommand extends Command {

    private final Bot bot;
    private TextChannel commandsChannel;

    public PurgeCommand( final Bot bot ) {
        this.bot = bot;
        this.commandsChannel = bot.getApi().getTextChannelById( BotInformation.COMMANDS_CHANNEL_ID );

        super.name = "purge";
        super.aliases = new String[] { "purge" };
        super.guildOnly = true;
        super.help = "Used to clear a channel of all its messages.\n Usage: _'!clear <amount>'_";
        super.cooldown = 2;
        
    }

    @Override
    protected void execute( CommandEvent event ) {
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        if( member.hasPermission( Permission.MESSAGE_MANAGE ) ) {
            MessagePaginationAction action = channel.getIterableHistory();
            if( event.getArgs().length() > 0 ) {
                try {
                    int limit = Integer.parseInt( event.getArgs().substring( 0, 1 ) ); 
                    action = action.limit( limit );
                    
                } catch( NumberFormatException ignored ) {}                      
            }  
                
            channel.deleteMessages( action.complete() ).complete();                
            commandsChannel.sendMessage( member.getAsMention() + ", you have cleared messages from this channel!" ).complete();
                
        } else 
            return;

    }
    
    public final Bot getBot() {
        return bot;
        
    }

}