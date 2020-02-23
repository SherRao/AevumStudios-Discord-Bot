package tk.sherrao.discord.aevum.listeners;

import java.util.EventListener;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import tk.sherrao.discord.aevum.Bot;
import tk.sherrao.discord.aevum.BotInformation;

public class NewMemberJoinListener implements EventListener {

    private final Bot bot;
    
    private Role newRole;
    private EmbedBuilder message;
    
    public NewMemberJoinListener( final Bot bot ) {
        this.bot = bot;
        
        this.newRole = bot.getApi().getRoleById( BotInformation.MEMBER_ROLE_ID );
        this.message = new EmbedBuilder()
                .setTitle( "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris est lectus, pretium molestie neque a, mattis rhoncus nunc. Duis tellus felis, maximus ut consequat et, dictum in sem. Curabitur vehicula tellus quis porttitor lacinia. Class aptent cras amet." )
                .setDescription( "this is the description" )
                .setFooter( "Developed by SherRao", bot.getFooterIcon() );
        
    }
    
    @SubscribeEvent
    public void onNewMemberJoin( GuildMemberJoinEvent event ) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        guild.getController().addSingleRoleToMember( member, newRole ).complete();

        PrivateChannel channel = member.getUser().openPrivateChannel().complete();
        message.setAuthor( "Elysian Services - Welcome " + member.getEffectiveName(), null, bot.getTitleIcon() );
        message.setThumbnail( member.getUser().getAvatarUrl() );
        channel.sendMessage( message.build() ).complete();
        
    }


}
