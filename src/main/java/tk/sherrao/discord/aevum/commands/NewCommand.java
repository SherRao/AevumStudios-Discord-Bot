package tk.sherrao.discord.aevum.commands;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import tk.sherrao.discord.aevum.Bot;
import tk.sherrao.discord.aevum.BotInformation;

public class NewCommand extends Command {

    private final Bot bot;

    private net.dv8tion.jda.core.entities.Category ticketsCategory;
    private TextChannel commandsChannel;
    private TextChannel commissionChannel;
    
    private Role developmentRepartment;
    private Role designDepartment;
    private Role buildingDepartment;

    private String mainIcon;
    private String footerIcon;
    
    private MessageEmbed tosQuestion;
    private MessageEmbed departmentQuestion;
    private MessageEmbed deadlineQuestion;
    private MessageEmbed budgetQuestion;
    private MessageEmbed descriptionQuestion;
    private MessageEmbed completeQuestion;
    
    private EmbedBuilder commissionMessage;
    
    public NewCommand( final Bot bot ) {
        this.bot = bot;
        
        this.ticketsCategory = bot.getApi().getCategoryById( BotInformation.TICKETS_CATEGORY_ID );
        this.commandsChannel = bot.getApi().getTextChannelById( BotInformation.COMMANDS_CHANNEL_ID );
        this.commissionChannel = bot.getApi().getTextChannelById( BotInformation.COMMISSIONS_TICKET_CHANNEL_ID );
        
        this.developmentRepartment = bot.getApi().getRoleById( BotInformation.DEVELOPMENT_ROLE_ID );
        this.designDepartment = bot.getApi().getRoleById( BotInformation.DESIGN_ROLE_ID );
        this.buildingDepartment = bot.getApi().getRoleById( BotInformation.BUILD_ROLE_ID );

        this.mainIcon = bot.getApi().getGuildById( BotInformation.SERVER_ID ).getIconUrl();
        this.footerIcon = bot.getApi().getUserById( BotInformation.SHERRAO_ID ).getAvatarUrl();
        
        this.tosQuestion = new EmbedBuilder()
                .setAuthor( "Elysian Services Terms of Service", null, mainIcon )
                .setTitle( "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris est lectus, pretium molestie neque a, mattis rhoncus nunc. Duis tellus felis, maximus ut consequat et, dictum in sem. Curabitur vehicula tellus quis porttitor lacinia. Class aptent cras amet." )
                .setDescription( "this is the description" )
                .setThumbnail( mainIcon )
                .setFooter( "Developed by SherRao", footerIcon )
                .build();
        
        this.departmentQuestion = new EmbedBuilder()
                .setAuthor( "Department", null, mainIcon )
                .setTitle( "What department is this ticket for? Enter a number between 1 and 5!" )
                .setDescription( "1 - Development\n\t2 - Design\n\t3 - Minecraft Building" )
                .setThumbnail( mainIcon )
                .setFooter( "Developed by SherRao", footerIcon )
                .build();
        
        this.deadlineQuestion = new EmbedBuilder()
                .setAuthor( "Deadline", null, mainIcon )
                .setTitle( "Enter a rough deadline you need this service completed by" )
                .setDescription( "Examples: 'A week or two', '2nd of February', or 'A few days'" )
                .setThumbnail( mainIcon )
                .setFooter( "Developed by SherRao", footerIcon )
                .build();
       
        this.budgetQuestion = new EmbedBuilder()
                .setAuthor( "Budget", null, mainIcon )
                .setTitle( "Enter a rough budget that you have for this service" )
                .setDescription( "Examples: 'Around 100 dollars', '25 USD - 75 USD', or 'just looking for a quote'" )
                .setThumbnail( mainIcon )
                .setFooter( "Developed by SherRao", footerIcon )
                .build();
        
        this.descriptionQuestion = new EmbedBuilder()
                .setAuthor( "Description", null, mainIcon )
                .setTitle( "Enter a description of what you would like this service to be" )
                .setDescription( "Examples: 'I want a Discord bot that plays music', 'I want a program that lets you maange passwords', or 'I want a logo for my new upcoming company'" )
                .setThumbnail( mainIcon )
                .setFooter( "Developed by SherRao", footerIcon )
                .build();
        
        this.completeQuestion = new EmbedBuilder()
                .setAuthor( "Budget", null, mainIcon )
                .setTitle( "You have finished creatimg your ticket! Please wait for a member of the team to be with you!" )
                .setDescription( "Waiting time is around a few minutes" )
                .setThumbnail( mainIcon )
                .setFooter( "Developed by SherRao", footerIcon )
                .build();
        
        this.commissionMessage = new EmbedBuilder()
                .setFooter( "Developed by SherRao", footerIcon );
        
        super.name = "new";
        super.aliases = new String[] { "ticket" };
        super.guildOnly = true;
        super.help = "Used to create a new ticket.\n Usage: _'!new'_";
        super.cooldown = 2;
        
    }
    
    @Override
    protected void execute( CommandEvent event ) {
        Member member = event.getMember();
        if( event.getChannel().equals( commandsChannel ) ) {
            event.getMessage().addReaction( BotInformation.SUCCESS_UNICODE ).complete();
            TextChannel channel = (TextChannel) ticketsCategory.createTextChannel( "ticket-" + member.getEffectiveName() + "-" + ticketsCategory.getChannels().size() ).complete();
            channel.createPermissionOverride( member )
                .setAllow( Permission.VIEW_CHANNEL, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE )
                .complete();
            
            channel.sendMessage( tosQuestion ).complete();
            channel.sendMessage( departmentQuestion ).completeAfter( 5, TimeUnit.SECONDS );
            EventWaiter departmentWaiter = new EventWaiter();
            departmentWaiter.waitForEvent( MessageReceivedEvent.class, new Predicate<MessageReceivedEvent>() {

                @Override
                public boolean test( MessageReceivedEvent t ) {
                    if( t.getTextChannel().equals( channel ) && t.getMember().equals( member ) ) {
                        try {
                            int i = Integer.parseInt( t.getMessage().getContentRaw() ); 
                            if( i >= 1 && i <= 3 )
                                return true;
                        
                            else throw new NumberFormatException();
                        
                        } catch( NumberFormatException e ) {
                            channel.sendMessage( " :warning: " + member.getAsMention() + ", please input a valid number between 1 and 3!" ).complete();
                            t.getMessage().addReaction( BotInformation.WARNING_UNICODE ).complete();
                            return false;
                        
                        }
                 
                    } else 
                        return false;
                
                }
                
            }, new Consumer<MessageReceivedEvent>() {

                @Override
                public void accept( MessageReceivedEvent t ) {
                    t.getMessage().addReaction( BotInformation.SUCCESS_UNICODE ).complete();
                    String department;
                    switch ( Integer.parseInt( t.getMessage().getContentRaw() ) ) {
                        case 1:
                            department = developmentRepartment.getAsMention();
                            break;
                            
                        case 2:
                            department = designDepartment.getAsMention();
                            break;

                        case 3:
                            department = buildingDepartment.getAsMention();
                            break;

                        default:
                            department = "null";

                    }
                    
                    channel.sendMessage( deadlineQuestion ).complete();
                    EventWaiter deadlineWaiter = new EventWaiter();
                    deadlineWaiter.waitForEvent( MessageReceivedEvent.class, new Predicate<MessageReceivedEvent>() {

                        @Override
                        public boolean test( MessageReceivedEvent t ) {
                            return t.getTextChannel().equals( channel ) && t.getMember().equals( member );

                        }
                        
                    }, new Consumer<MessageReceivedEvent>() {

                        @Override
                        public void accept( MessageReceivedEvent t ) {
                            String deadline = t.getMessage().getContentRaw();
                            t.getMessage().addReaction( BotInformation.SUCCESS_UNICODE ).complete();
                            channel.sendMessage( budgetQuestion ).complete();
                            EventWaiter budgetWaiter = new EventWaiter();
                            budgetWaiter.waitForEvent( MessageReceivedEvent.class, new Predicate<MessageReceivedEvent>() {

                                @Override
                                public boolean test( MessageReceivedEvent t ) {
                                    return t.getTextChannel().equals( channel ) && t.getMember().equals( member );
                                    
                                }
                                
                            }, new Consumer<MessageReceivedEvent>() {

                                @Override
                                public void accept( MessageReceivedEvent t ) {
                                    String budget = t.getMessage().getContentRaw();
                                    t.getMessage().addReaction( BotInformation.SUCCESS_UNICODE ).complete();
                                    channel.sendMessage( descriptionQuestion ).complete();
                                    EventWaiter descriptionWaiter = new EventWaiter();
                                    descriptionWaiter.waitForEvent( MessageReceivedEvent.class, new Predicate<MessageReceivedEvent>() {
                                        
                                        @Override
                                        public boolean test( MessageReceivedEvent t ) {
                                            return t.getTextChannel().equals( channel ) && t.getMember().equals( member );

                                        }
                                                
                                        }, new Consumer<MessageReceivedEvent>() {

                                            @Override
                                            public void accept( MessageReceivedEvent t ) {
                                                String description = t.getMessage().getContentRaw();
                                                t.getMessage().addReaction( BotInformation.SUCCESS_UNICODE ).complete();
                                                channel.sendMessage( completeQuestion ).complete();
                                                bot.getApi().removeEventListener( departmentWaiter, deadlineWaiter, budgetWaiter, descriptionWaiter );
                                                publishCommission( member, channel, department, deadline, budget, description );
                                                
                                                EventWaiter reactionWaiter = new EventWaiter();
                                                reactionWaiter.waitForEvent( MessageReactionAddEvent.class, new Predicate<MessageReactionAddEvent>() {
                                                    
                                                    @Override
                                                    public boolean test( MessageReactionAddEvent t ) {
                                                        return t.getTextChannel().equals( commissionChannel );
                                                        
                                                    }
                                                    
                                                }, new Consumer<MessageReactionAddEvent>() {
                                                    
                                                    @Override
                                                    public void accept( MessageReactionAddEvent t ) {
                                                        //DO STUFF
                                                        
                                                    }
                                                    
                                                } );
                                                
                                                bot.getApi().addEventListener( reactionWaiter );
                                                
                                            }
                                                
                                        } );
                                            
                                    bot.getApi().addEventListener( descriptionWaiter );
                                            
                                }
                                
                            } );
                            
                            bot.getApi().addEventListener( budgetWaiter );
                            
                        }
                        
                    } );
                    
                    bot.getApi().addEventListener( deadlineWaiter );
                    
                }
                
            } );
            
            bot.getApi().addEventListener( departmentWaiter );
            
        } else {
            commandsChannel.sendMessage( member.getAsMention() + ", you are only allowed to use that command in this channel!" ).complete();
            event.getMessage().addReaction( BotInformation.ERROR_UNICODE ).complete();
            
        }
        
    }
    
    public void publishCommission( Member client, TextChannel ticket, String department, String deadline, String budget, String description ) {
        commissionMessage.setAuthor( "Elysian Services - Commission for " + client.getEffectiveName(), null, mainIcon )
            .setTitle( "Requested at " + LocalDateTime.now().toString() )
            .addField( "Department", department, true )
            .addField( "Deadline", deadline, true )
            .addField( "Budget", budget, true )
            .addField( "Description", description, false )
            .setThumbnail( client.getUser().getAvatarUrl() )
            .setDescription( "React below to be added to the ticket!" );
        
        commissionChannel.sendMessage( commissionMessage.build() ).complete();
        commissionMessage.clearFields();
        
    }
    
    public final Bot getBot() {
        return bot;
        
    }
    
}