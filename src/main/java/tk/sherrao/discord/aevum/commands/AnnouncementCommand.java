package tk.sherrao.discord.aevum.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import tk.sherrao.discord.aevum.Bot;

public class AnnouncementCommand extends Command {

    private final Bot bot;
    
    public AnnouncementCommand( final Bot bot ) {
        this.bot = bot;
        
    }
    
    @Override
    protected void execute( CommandEvent event ) {
        
        
    }
    
    public final Bot getBot() {
        return bot;
        
    }

}
