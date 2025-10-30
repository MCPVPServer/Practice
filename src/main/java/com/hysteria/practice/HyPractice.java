package com.hysteria.practice;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.hysteria.practice.api.rank.RankManager;
import com.hysteria.practice.chunk.ChunkRestorationManager;
import com.hysteria.practice.database.MongoConnection;
import com.hysteria.practice.essentials.Essentials;
import com.hysteria.practice.essentials.EssentialsListener;
import com.hysteria.practice.essentials.abilities.AbilityManager;
import com.hysteria.practice.essentials.abilities.command.AbilityCommand;
import com.hysteria.practice.essentials.chat.HyPracticeChatFormat;
import com.hysteria.practice.essentials.chat.impl.Chat;
import com.hysteria.practice.essentials.chat.impl.ChatListener;
import com.hysteria.practice.essentials.chat.impl.command.ClearChatCommand;
import com.hysteria.practice.essentials.chat.impl.command.MuteChatCommand;
import com.hysteria.practice.essentials.chat.impl.command.SlowChatCommand;
import com.hysteria.practice.essentials.command.donator.*;
import com.hysteria.practice.essentials.command.management.HyPracticeCommand;
import com.hysteria.practice.essentials.command.management.SetSlotsCommand;
import com.hysteria.practice.essentials.command.management.SetSpawnCommand;
import com.hysteria.practice.essentials.command.player.*;
import com.hysteria.practice.essentials.command.staff.*;
import com.hysteria.practice.game.arena.ArenaListener;
import com.hysteria.practice.game.arena.command.ArenasCommand;
import com.hysteria.practice.game.event.Event;
import com.hysteria.practice.game.event.command.EventCommand;
import com.hysteria.practice.game.event.command.EventsCommand;
import com.hysteria.practice.game.event.game.EventGame;
import com.hysteria.practice.game.event.game.EventGameListener;
import com.hysteria.practice.game.event.game.map.EventGameMap;
import com.hysteria.practice.game.event.game.map.vote.command.EventMapVoteCommand;
import com.hysteria.practice.game.ffa.FFAListener;
import com.hysteria.practice.game.ffa.FFAManager;
import com.hysteria.practice.game.ffa.command.FFACommand;
import com.hysteria.practice.game.kit.KitEditorListener;
import com.hysteria.practice.game.kit.KitRepository;
import com.hysteria.practice.game.kit.command.HCFClassCommand;
import com.hysteria.practice.game.kit.command.KitCommand;
import com.hysteria.practice.game.kit.command.KitsCommand;
import com.hysteria.practice.game.knockback.Knockback;
import com.hysteria.practice.game.tournament.commands.TournamentCommand;
import com.hysteria.practice.game.tournament.listeners.TournamentListener;
import com.hysteria.practice.game.tournament.managers.TournamentManager;
import com.hysteria.practice.match.command.*;
import com.hysteria.practice.match.duel.command.*;
import com.hysteria.practice.match.listeners.MatchListener;
import com.hysteria.practice.match.listeners.impl.MatchBuildListener;
import com.hysteria.practice.match.listeners.impl.MatchPearlListener;
import com.hysteria.practice.match.listeners.impl.MatchPlayerListener;
import com.hysteria.practice.match.listeners.impl.MatchSpecialListener;
import com.hysteria.practice.player.clan.Clan;
import com.hysteria.practice.player.clan.ClanListener;
import com.hysteria.practice.player.clan.commands.ClanCommand;
import com.hysteria.practice.player.cosmetics.command.CosmeticsCommand;
import com.hysteria.practice.player.cosmetics.impl.killeffects.command.KillEffectCommand;
import com.hysteria.practice.player.party.classes.ClassTask;
import com.hysteria.practice.player.party.classes.archer.ArcherClass;
import com.hysteria.practice.player.party.classes.bard.BardEnergyTask;
import com.hysteria.practice.player.party.classes.bard.BardListener;
import com.hysteria.practice.player.party.classes.rogue.RogueClass;
import com.hysteria.practice.player.party.command.PartyCommand;
import com.hysteria.practice.player.party.listeners.PartyListener;
import com.hysteria.practice.player.profile.Profile;
import com.hysteria.practice.player.profile.ProfileListener;
import com.hysteria.practice.player.profile.conversation.command.MessageCommand;
import com.hysteria.practice.player.profile.conversation.command.ReplyCommand;
import com.hysteria.practice.player.profile.file.impl.FlatFileIProfile;
import com.hysteria.practice.player.profile.file.impl.MongoDBIProfile;
import com.hysteria.practice.player.profile.hotbar.Hotbar;
import com.hysteria.practice.player.profile.meta.option.command.*;
import com.hysteria.practice.player.profile.modmode.ModmodeListener;
import com.hysteria.practice.player.queue.Queue;
import com.hysteria.practice.player.queue.QueueListener;
import com.hysteria.practice.utilities.*;
import com.hysteria.practice.utilities.file.language.LanguageConfigurationFile;
import com.hysteria.practice.utilities.file.type.BasicConfigurationFile;
import com.hysteria.practice.utilities.lag.LagRunnable;
import com.hysteria.practice.utilities.menu.MenuListener;
import com.hysteria.practice.utilities.playerversion.PlayerVersionHandler;
import com.hysteria.practice.visual.leaderboard.Leaderboard;
import com.hysteria.practice.visual.leaderboard.LeaderboardListener;
import com.hysteria.practice.visual.leaderboard.PlaceholderAPI;
import com.hysteria.practice.visual.leaderboard.commands.*;
import com.hysteria.practice.visual.tablist.TabAdapter;
import com.hysteria.practice.visual.tablist.impl.TabList;
import com.hysteria.practice.game.arena.Arena;
import com.hysteria.practice.game.arena.command.ArenaCommand;
import com.hysteria.practice.game.event.game.command.EventHostCommand;
import com.hysteria.practice.game.event.impl.spleef.SpleefGameLogic;
import com.hysteria.practice.game.event.impl.tntrun.TNTRunGameLogic;
import com.hysteria.practice.match.Match;
import com.hysteria.practice.player.party.Party;
import com.hysteria.practice.visual.scoreboard.BoardAdapter;
import com.hysteria.practice.shop.ShopSystem;
import com.hysteria.practice.shop.command.CoinsCommand;
import com.hysteria.practice.shop.command.ShopCommand;
import com.hysteria.practice.shop.command.staff.CoinsStaffCommand;
import com.hysteria.practice.utilities.chat.CC;
import com.hysteria.practice.api.command.CommandManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter @Setter
public class HyPractice extends JavaPlugin {

    private String prefix = "&7[&bHyPractice&7] &7";

    private LanguageConfigurationFile lang;
    private BasicConfigurationFile mainConfig, databaseConfig, arenasConfig, kitsConfig, eventsConfig,
            scoreboardConfig, coloredRanksConfig, tabLobbyConfig, tabEventConfig, tabSingleFFAFightConfig,
            tabSingleTeamFightConfig, tabPartyFFAFightConfig, tabPartyTeamFightConfig, leaderboardConfig,
            langConfig, hotbarConfig, playersConfig, clansConfig, categoriesConfig, abilityConfig, kitEditorConfig,
            npcConfig, queueConfig, lunarConfig, tabFFAConfig, potionConfig, menuConfig, ffaConfig, pearlConfig;

    private ChunkRestorationManager chunkRestorationManager;
    private TournamentManager tournamentManager;
    private AbilityManager abilityManager;
    private KitRepository kitRepository;
    private RankManager rankManager;
    private FFAManager ffaManager;
    private ShopSystem shopSystem;
    private Hotbar hotbar;

    private EntityHider entityHider;
    private Essentials essentials;

    private MongoConnection mongoConnection;
    private ProtocolManager protocolManager;

    public boolean runningPlaceholderAPI = false;
    public boolean runningAlonsoLeagues = false;
    public int inQueues, inFights, bridgeRounds, rankedSumoRounds;

    @Override
    public void onEnable() {
        loadConfig();

        loadSaveMethod();
        loadEssentials();
        initManagers();


        registerCommands();
        registerListeners();

        removeCrafting();

        setUpWorld();
        runTasks();

        CC.loadPlugin();
        
        if(!HyPractice.get().getDescription().getAuthors().contains("Hysteria Development")) {
            Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "You edited the plugin.yml, please don't do that"));
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Please check your plugin.yml and try again."));
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "            &cDisabling HyPractice"));
            Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!HyPractice.get().getDescription().getName().contains("HyPractice")) {
            Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "You edited the plugin.yml, please don't do that"));
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Please check your plugin.yml and try again."));
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "            &cDisabling HyPractice"));
            Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        Profile.getProfiles().values().stream().filter(Profile::isOnline).forEach(Profile::save);
        if (EventGame.getActiveGame() != null) {
            if (EventGame.getActiveGame().getGameLogic() instanceof SpleefGameLogic) {
                SpleefGameLogic event = (SpleefGameLogic) EventGame.getActiveGame().getGameLogic();
                event.endEvent();
            } else if (EventGame.getActiveGame().getGameLogic() instanceof TNTRunGameLogic) {
                TNTRunGameLogic event = (TNTRunGameLogic) EventGame.getActiveGame().getGameLogic();
                event.endEvent();
            }
        }
        Match.cleanup();
        Clan.getClans().values().forEach(clan -> clan.save(false));
        kitRepository.getKits().forEach(kit -> kitRepository.saveKit(kit));
        Arena.getArenas().forEach(Arena::save);
    }

    private void initManagers() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Initializing all managers for HyPractice"));
        this.essentials = new Essentials(this);
        this.rankManager = new RankManager(this);
        this.rankManager.loadRank();

        this.abilityManager = new AbilityManager();
        this.abilityManager.load();

        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.ffaManager = new FFAManager();
        this.ffaManager.initTasks();

        this.shopSystem = new ShopSystem();
        this.chunkRestorationManager = new ChunkRestorationManager();

        this.hotbar = new Hotbar();

        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        this.entityHider.init();

        this.tournamentManager = new TournamentManager();

        this.kitRepository = new KitRepository();
        this.kitRepository.loadKits();

        Arena.init();
        Profile.init();
        Match.init();
        Party.init();
        Knockback.init();

        Event.init();
        EventGameMap.init();

        Clan.init();
        Queue.init();
        Animation.init();
        BoardAdapter.hook();
        Leaderboard.init();
        PlayerVersionHandler.init();
        Chat.setChatFormat(new HyPracticeChatFormat());
        if (mainConfig.getBoolean("TABLIST_ENABLE")) {
            new TabList(this, new TabAdapter());
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Tablist expansion successfully registered."));
        }
        runningAlonsoLeagues = getServer().getPluginManager().getPlugin("AlonsoLeagues") != null;
        if(runningAlonsoLeagues) {
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "AlonsoLeagues API expansion successfully registered."));
        }

        runningPlaceholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (runningPlaceholderAPI) {
            new PlaceholderAPI().register();
            Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Placeholder API expansion successfully registered."));
        }
    }

    private void loadConfig() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
        Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Initializing all configs for HyPractice"));

        this.mainConfig = new BasicConfigurationFile(this, "config");
        this.databaseConfig = new BasicConfigurationFile(this, "database");

        this.arenasConfig = new BasicConfigurationFile(this, "cache/arenas");
        this.kitsConfig = new BasicConfigurationFile(this, "cache/kits");

        this.langConfig = new BasicConfigurationFile(this, "lang/global-lang");
        this.lang = new LanguageConfigurationFile(this, "lang/lang");

        this.hotbarConfig = new BasicConfigurationFile(this, "features/hotbar");
        this.abilityConfig = new BasicConfigurationFile(this, "features/ability");
        this.eventsConfig = new BasicConfigurationFile(this, "features/events");

        this.menuConfig = new BasicConfigurationFile(this, "settings/menu");
        this.kitEditorConfig = new BasicConfigurationFile(this, "settings/kiteditor");
        this.coloredRanksConfig = new BasicConfigurationFile(this, "settings/colored-ranks");
        this.scoreboardConfig = new BasicConfigurationFile(this, "settings/scoreboard");
        this.leaderboardConfig = new BasicConfigurationFile(this, "settings/leaderboard");

        this.tabEventConfig = new BasicConfigurationFile(this, "tablist/event");
        this.tabLobbyConfig = new BasicConfigurationFile(this, "tablist/lobby");
        this.tabFFAConfig = new BasicConfigurationFile(this, "tablist/ffa");
        this.tabSingleFFAFightConfig = new BasicConfigurationFile(this, "tablist/SingleFFAFight");
        this.tabSingleTeamFightConfig = new BasicConfigurationFile(this, "tablist/SingleTeamFight");
        this.tabPartyFFAFightConfig = new BasicConfigurationFile(this, "tablist/PartyFFAFight");
        this.tabPartyTeamFightConfig = new BasicConfigurationFile(this, "tablist/PartyTeamFight");

        if (mainConfig.getString("SAVE_METHOD").equals("FILE") || mainConfig.getString("SAVE_METHOD").equals("FLATFILE")) {
            this.playersConfig = new BasicConfigurationFile(this, "cache/players");
            this.clansConfig = new BasicConfigurationFile(this, "cache/clans");
        }
    }



    private void loadSaveMethod() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Initializing save method for HyPractice"));
        switch (mainConfig.getString("SAVE_METHOD")) {
            case "MONGO": case "MONGODB":
                Profile.iProfile = new MongoDBIProfile();
                break;
            case "FLATFILE": case "FILE":
                Profile.iProfile = new FlatFileIProfile();
                break;
        }

        if (Profile.getIProfile() instanceof MongoDBIProfile) {
            try {
                if (databaseConfig.getBoolean("MONGO.URI")) {
                    this.mongoConnection = new MongoConnection(databaseConfig.getString("MONGO.URI_LINK"), "practice");
                } else if (databaseConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
                    this.mongoConnection = new MongoConnection(
                            databaseConfig.getString("MONGO.HOST"),
                            databaseConfig.getInteger("MONGO.PORT"),
                            databaseConfig.getString("MONGO.AUTHENTICATION.USERNAME"),
                            databaseConfig.getString("MONGO.AUTHENTICATION.PASSWORD"),
                            databaseConfig.getString("MONGO.AUTHENTICATION.DATABASE"));
                } else {
                    this.mongoConnection = new MongoConnection(
                            databaseConfig.getString("MONGO.HOST"),
                            databaseConfig.getInteger("MONGO.PORT"),
                            databaseConfig.getString("MONGO.DATABASE"));
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
                Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "            &c&lMongo Internal Error"));
                Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "        &cMongo is not setup correctly!"));
                Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "      &cPlease check your mongo and try again."));
                Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "              &c&lDisabling HyPractice"));
                Bukkit.getConsoleSender().sendMessage(CC.translate(CC.CHAT_BAR));
                Bukkit.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
    }

    private void runTasks() {
        TaskUtil.runTimer(new ClassTask(), 5L, 5L);
        TaskUtil.runTimer(new BardEnergyTask(), 15L, 20L);
        TaskUtil.runTimer(() ->
            Profile.getProfiles().values().stream()
                    .filter(profile -> profile.getPlayer() != null && profile.getPlayer().isOnline())
                    .filter(profile -> profile.getRematchData() != null)
                    .forEach(profile -> profile.getRematchData().validate())
                , 20L, 20L);
    }

    private void setUpWorld() {
        // Set the difficulty for each world to HARD
        // Clear the droppedItems for each world
        getServer().getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRuleValue("doDaylightCycle", "false");
            getEssentials().clearEntities(world);
        });
    }

    private void removeCrafting() {
        Arrays.asList(
                Material.WORKBENCH,
                Material.STICK,
                Material.WOOD_PLATE,
                Material.WOOD_BUTTON,
                Material.SNOW_BLOCK,
                Material.STONE_BUTTON
        ).forEach(InventoryUtil::removeCrafting);
    }

    private void registerListeners() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Initializing listeners for HyPractice"));

        Arrays.asList(
                new KitEditorListener(),
                new PartyListener(),
                new MatchPearlListener(),
                new EventGameListener(),
                new BardListener(),
                new ArcherClass(),
                new RogueClass(),
                new ClanListener(),
                new EssentialsListener(),
                new MenuListener(),
                new ChatListener(),
                new LeaderboardListener(),
                new TournamentListener(),
                new MatchSpecialListener(),
                new ArenaListener(),
                new MatchBuildListener(),
                new MatchPlayerListener(),
                new MatchListener(),
                new QueueListener(),
                new ProfileListener(),
                new FFAListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagRunnable(), 100L, 1L);

        if (getMainConfig().getBoolean("MOD_MODE")) getServer().getPluginManager().registerEvents(new ModmodeListener(), this);
    }
    public void registerCommands() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(prefix + "Initializing commands for HyPractice"));

        new CommandManager(this);
        if (mainConfig.getBoolean("MESSAGE-REPLY-BOOLEAN")) {
            new MessageCommand();
            new ReplyCommand();
        }
        new DivisionCommand();
        new CancelAllMatchesCommand();
        new StreamingCommand();
        new CosmeticsCommand();
        new ShopCommand();
        new CoinsCommand();
        new CoinsStaffCommand();
        new KillEffectCommand();
        new MatchListCommand();
        new TrollCommand();
        new FFACommand();
        new ArenaCommand();
        new ArenasCommand();
        new DuelCommand();
        new DuelRoundCommand();
        new DuelAcceptCommand();
        new EventCommand();
        new EventHostCommand();
        new EventsCommand();
        new EventMapVoteCommand();
        new RematchCommand();
        new SpectateCommand();
        new CancelMatchCommand();
        new StopSpectatingCommand();
        new FlyCommand();
        new ViewMatchCommand();
        new PartyCommand();
        new KitCommand();
        new KitsCommand();
        new ViewInventoryCommand();
        new ToggleScoreboardCommand();
        new ToggleSpectatorsCommand();
        new ToggleDuelRequestsCommand();
        new ClanCommand();
        new TournamentCommand();
        new ClearCommand();
        new DayCommand();
        new GameModeCommand();
        new AbilityCommand();
        new HyPracticeCommand();
        new HealCommand();
        new LangCommand();
        new LocationCommand();
        new MoreCommand();
        new NightCommand();
        new PingCommand();
        new RenameCommand();
        new SetSlotsCommand();
        new SetSpawnCommand();
        new ShowAllPlayersCommand();
        new ShowPlayerCommand();
        new SpawnCommand();
        new SudoAllCommand();
        new SudoCommand();
        new SunsetCommand();
        new TeleportWorldCommand();
        new OptionsCommand();
        new ClearChatCommand();
        new SlowChatCommand();
        new MuteChatCommand();
        new EloCommand();
        new SetEloCommand();
        new ResetEloCommand();
        new CreateWorldCommand();
        new StatsCommand();
        new LeaderboardCommand();
        new RankedCommand();
        new UnRankedCommand();
        new HCFClassCommand();
        new ResetCommand();
        new ToggleGlobalChatCommand();
        new TogglePrivateMessagesCommand();
        new ToggleScoreboardCommand();
        new ToggleSoundsCommand();
        new ToggleSpectatorsCommand();
        new BotFight();
    }

    private void loadEssentials() {
        this.bridgeRounds = getMainConfig().getInteger("MATCH.ROUNDS_BRIDGE");
        this.rankedSumoRounds = getMainConfig().getInteger("MATCH.ROUNDS_RANKED_SUMO");
    }

    public static HyPractice get(){
        return getPlugin(HyPractice.class);
    }
}
