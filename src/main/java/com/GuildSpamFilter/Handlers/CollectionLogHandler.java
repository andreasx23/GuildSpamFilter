package com.GuildSpamFilter.Handlers;

import com.GuildSpamFilter.Models.Categori;
import com.GuildSpamFilter.Models.Section;
import java.util.ArrayList;
import java.util.Arrays;

public class CollectionLogHandler
{
    private final String colLogData = "Bosses#Abyssal Sire?Abyssal orphan,Unsired,Abyssal head,Bludgeon spine,Bludgeon claw,Bludgeon axon,Jar of miasma,Abyssal dagger,Abyssal whip§Alchemical Hydra?Ikkle hydra,Hydra's claw,Hydra tail,Hydra leather,Hydra's fang,Hydra's eye,Hydra's heart,Dragon knife,Dragon thrownaxe,Jar of chemicals,Alchemical hydra heads§Barrows Chests?Karil's coif,Karil's leathertop,Karil's leatherskirt,Karil's crossbow,Ahrim's hood,Ahrim's robetop,Ahrim's robeskirt,Ahrim's staff,Dharok's helm,Dharok's platebody,Dharok's platelegs,Dharok's greataxe,Guthan's helm,Guthan's platebody,Guthan's chainskirt,Guthan's warspear,Torag's helm,Torag's platebody,Torag's platelegs,Torag's hammers,Verac's helm,Verac's brassard,Verac's plateskirt,Verac's flail,Bolt rack§Bryophyta?Bryophyta's essence§Callisto and Artio?Callisto cub,Tyrannical ring,Dragon pickaxe,Dragon 2h sword,Claws of callisto,Voidwaker hilt§Cerberus?Hellpuppy,Eternal crystal,Pegasian crystal,Primordial crystal,Jar of souls,Smouldering stone,Key master teleport§Chaos Elemental?Pet chaos elemental,Dragon pickaxe,Dragon 2h sword§Chaos Fanatic?Pet chaos elemental,Odium shard 1,Malediction shard 1§Commander Zilyana?Pet zilyana,Armadyl crossbow,Saradomin hilt,Saradomin sword,Saradomin's light,Godsword shard 1,Godsword shard 2,Godsword shard 3§Corporeal Beast?Pet dark core,Elysian sigil,Spectral sigil,Arcane sigil,Holy elixir,Spirit shield,Jar of spirits§Crazy archaeologist?Odium shard 2,Malediction shard 2,Fedora§Dagannoth Kings?Pet dagannoth prime,Pet dagannoth supreme,Pet dagannoth rex,Berserker ring,Archers ring,Seers ring,Warrior ring,Dragon axe,Seercull,Mud battlestaff§Duke Sucellus?Baron,Eye of the duke,Virtus mask,Virtus robe top,Virtus robe bottom,Magus vestige,Ice quartz,Frozen tablet,Chromium ingot,Awakener's orb§The Fight Caves?Tzrek-jad,Fire cape§Fortis Colosseum?Smol heredit,Dizana's quiver (uncharged),Sunfire fanatic cuirass,Sunfire fanatic chausses,Sunfire fanatic helm,Echo crystal,Tonalztics of ralos (uncharged),Sunfire splinters§The Gauntlet?Youngllef,Crystal armour seed,Crystal weapon seed,Enhanced crystal weapon seed,Gauntlet cape§General Graardor?Pet general graardor,Bandos chestplate,Bandos tassets,Bandos boots,Bandos hilt,Godsword shard 1,Godsword shard 2,Godsword shard 3§Giant Mole?Baby mole,Mole skin,Mole claw§Grotesque Guardians?Noon,Black tourmaline core,Granite gloves,Granite ring,Granite hammer,Jar of stone,Granite dust§Hespori?Bottomless compost bucket,Iasor seed,Kronos seed,Attas seed§The Inferno?Jal-nib-rek,Infernal cape§Kalphite Queen?Kalphite princess,Kq head,Jar of sand,Dragon 2h sword,Dragon chainbody,Dragon pickaxe§King Black Dragon?Prince black dragon,Kbd heads,Dragon pickaxe,Draconic visage§Kraken?Pet kraken,Kraken tentacle,Trident of the seas,Jar of dirt§Kree'arra?Pet kree'arra,Armadyl helmet,Armadyl chestplate,Armadyl chainskirt,Armadyl hilt,Godsword shard 1,Godsword shard 2,Godsword shard 3§K'ril Tsutsaroth?Pet k'ril tsutsaroth,Staff of the dead,Zamorakian spear,Steam battlestaff,Zamorak hilt,Godsword shard 1,Godsword shard 2,Godsword shard 3§The Leviathan?Lil'viathan,Leviathan's lure,Virtus mask,Virtus robe top,Virtus robe bottom,Venator vestige,Smoke quartz,Scarred tablet,Chromium ingot,Awakener's orb§Moons of Peril?Eclipse moon chestplate,Eclipse moon tassets,Eclipse moon helm,Eclipse atlatl,Blue moon chestplate,Blue moon tassets,Blue moon helm,Blue moon spear,Blood moon chestplate,Blood moon tassets,Blood moon helm,Dual macuahuitl,Atlatl dart§Nex?Nexling,Ancient hilt,Nihil horn,Zaryte vambraces,Torva full helm (damaged),Torva platebody (damaged),Torva platelegs (damaged),Nihil shard§The Nightmare?Little nightmare,Inquisitor's mace,Inquisitor's great helm,Inquisitor's hauberk,Inquisitor's plateskirt,Nightmare staff,Volatile orb,Harmonised orb,Eldritch orb,Jar of dreams,Slepey tablet,Parasitic egg§Obor?Hill giant club§Phantom Muspah?Muphin,Venator shard,Ancient icon,Charged ice,Frozen cache,Ancient essence§Sarachnis?Sraracha,Jar of eyes,Giant egg sac(full),Sarachnis cudgel§Scorpia?Scorpia's offspring,Odium shard 3,Malediction shard 3§Scurrius?Scurry,Scurrius' spine§Skotizo?Skotos,Jar of darkness,Dark claw,Dark totem,Uncut onyx,Ancient shard§Tempoross?Tiny tempor,Big harpoonfish,Spirit angler headband,Spirit angler top,Spirit angler waders,Spirit angler boots,Tome of water (empty),Soaked page,Tackle box,Fish barrel,Dragon harpoon,Spirit flakes§Thermonuclear smoke devil?Pet smoke devil,Occult necklace,Smoke battlestaff,Dragon chainbody,Jar of smoke§Vardorvis?Butch,Executioner's axe head,Virtus mask,Virtus robe top,Virtus robe bottom,Ultor vestige,Blood quartz,Strangled tablet,Chromium ingot,Awakener's orb§Venenatis and Spindel?Venenatis spiderling,Treasonous ring,Dragon pickaxe,Dragon 2h sword,Fangs of venenatis,Voidwaker gem§Vet'ion and Calvar'ion?Vet'ion jr.,Ring of the gods,Dragon pickaxe,Dragon 2h sword,Skull of vet'ion,Voidwaker blade§Vorkath?Vorki,Vorkath's head,Draconic visage,Skeletal visage,Jar of decay,Dragonbone necklace§The Whisperer?Wisp,Siren's staff,Virtus mask,Virtus robe top,Virtus robe bottom,Bellator vestige,Shadow quartz,Sirenic tablet,Chromium ingot,Awakener's orb§Wintertodt?Phoenix,Tome of fire (empty),Burnt page,Pyromancer garb,Pyromancer hood,Pyromancer robe,Pyromancer boots,Warm gloves,Bruma torch,Dragon axe§Zalcano?Smolcano,Crystal tool seed,Zalcano shard,Uncut onyx§Zulrah?Pet snakeling,Tanzanite mutagen,Magma mutagen,Jar of swamp,Magic fang,Serpentine visage,Tanzanite fang,Zul-andra teleport,Uncut onyx,Zulrah's scales§@Raids#Chambers of Xeric?Olmlet,Metamorphic dust,Twisted bow,Elder maul,Kodai insignia,Dragon claws,Ancestral hat,Ancestral robe top,Ancestral robe bottom,Dinh's bulwark,Dexterous prayer scroll,Arcane prayer scroll,Dragon hunter crossbow,Twisted buckler,Torn prayer scroll,Dark relic,Onyx,Twisted ancestral colour kit,Xeric's guard,Xeric's warrior,Xeric's sentinel,Xeric's general,Xeric's champion§Theatre of Blood?Lil' zik,Scythe of vitur (uncharged),Ghrazi rapier,Sanguinesti staff (uncharged),Justiciar faceguard,Justiciar chestguard,Justiciar legguards,Avernic defender hilt,Vial of blood,Sinhaza shroud tier 1,Sinhaza shroud tier 2,Sinhaza shroud tier 3,Sinhaza shroud tier 4,Sinhaza shroud tier 5,Sanguine dust,Holy ornament kit,Sanguine ornament kit§Tombs of Amascut?Tumeken's guardian,Tumeken's shadow (uncharged),Elidinis' ward,Masori mask,Masori body,Masori chaps,Lightbearer,Osmumten's fang,Thread of elidinis,Breach of the scarab,Eye of the corruptor,Jewel of the sun,Menaphite ornament kit,Cursed phalanx,Masori crafting kit,Cache of runes,Icthlarin's shroud (tier 1),Icthlarin's shroud (tier 2),Icthlarin's shroud (tier 3),Icthlarin's shroud (tier 4),Icthlarin's shroud (tier 5),Remnant of akkha,Remnant of ba-ba,Remnant of kephri,Remnant of zebak,Ancient remnant§@Clues#Beginner Treasure Trails?Mole slippers,Frog slippers,Bear feet,Demon feet,Jester cape,Shoulder parrot,Monk's robe top (t),Monk's robe (t),Amulet of defence (t),Sandwich lady hat,Sandwich lady top,Sandwich lady bottom,Rune scimitar ornament kit (guthix),Rune scimitar ornament kit (saradomin),Rune scimitar ornament kit (zamorak),Black pickaxe§Easy Treasure Trails?Team cape zero,Team cape i,Team cape x,Cape of skulls,Golden chef's hat,Golden apron,Wooden shield (g),Black full helm (t),Black platebody (t),Black platelegs (t),Black plateskirt (t),Black kiteshield (t),Black full helm (g),Black platebody (g),Black platelegs (g),Black plateskirt (g),Black kiteshield (g),Black shield (h1),Black shield (h2),Black shield (h3),Black shield (h4),Black shield (h5),Black helm (h1),Black helm (h2),Black helm (h3),Black helm (h4),Black helm (h5),Black platebody (h1),Black platebody (h2),Black platebody (h3),Black platebody (h4),Black platebody (h5),Steel full helm (t),Steel platebody (t),Steel platelegs (t),Steel plateskirt (t),Steel kiteshield (t),Steel full helm (g),Steel platebody (g),Steel platelegs (g),Steel plateskirt (g),Steel kiteshield (g),Iron platebody (t),Iron platelegs (t),Iron plateskirt (t),Iron kiteshield (t),Iron full helm (t),Iron platebody (g),Iron platelegs (g),Iron plateskirt (g),Iron kiteshield (g),Iron full helm (g),Bronze platebody (t),Bronze platelegs (t),Bronze plateskirt (t),Bronze kiteshield (t),Bronze full helm (t),Bronze platebody (g),Bronze platelegs (g),Bronze plateskirt (g),Bronze kiteshield (g),Bronze full helm (g),Studded body (g),Studded chaps (g),Studded body (t),Studded chaps (t),Leather body (g),Leather chaps (g),Blue wizard hat (g),Blue wizard robe (g),Blue skirt (g),Blue wizard hat (t),Blue wizard robe (t),Blue skirt (t),Black wizard hat (g),Black wizard robe (g),Black skirt (g),Black wizard hat (t),Black wizard robe (t),Black skirt (t),Monk's robe top (g),Monk's robe (g),Saradomin robe top,Saradomin robe legs,Guthix robe top,Guthix robe legs,Zamorak robe top,Zamorak robe legs,Ancient robe top,Ancient robe legs,Armadyl robe top,Armadyl robe legs,Bandos robe top,Bandos robe legs,Bob's red shirt,Bob's green shirt,Bob's blue shirt,Bob's black shirt,Bob's purple shirt,Highwayman mask,Blue beret,Black beret,White beret,Red beret,A powdered wig,Beanie,Imp mask,Goblin mask,Sleeping cap,Flared trousers,Pantaloons,Black cane,Staff of bob the cat,Red elegant shirt,Red elegant blouse,Red elegant legs,Red elegant skirt,Green elegant shirt,Green elegant blouse,Green elegant legs,Green elegant skirt,Blue elegant shirt,Blue elegant blouse,Blue elegant legs,Blue elegant skirt,Amulet of magic (t),Amulet of power (t),Black pickaxe,Ham joint,Rain bow,Willow comp bow§Medium Treasure Trails?Ranger boots,Wizard boots,Holy sandals,Climbing boots (g),Spiked manacles,Adamant full helm (t),Adamant platebody (t),Adamant platelegs (t),Adamant plateskirt (t),Adamant kiteshield (t),Adamant full helm (g),Adamant platebody (g),Adamant platelegs (g),Adamant plateskirt (g),Adamant kiteshield (g),Adamant shield (h1),Adamant shield (h2),Adamant shield (h3),Adamant shield (h4),Adamant shield (h5),Adamant helm (h1),Adamant helm (h2),Adamant helm (h3),Adamant helm (h4),Adamant helm (h5),Adamant platebody (h1),Adamant platebody (h2),Adamant platebody (h3),Adamant platebody (h4),Adamant platebody (h5),Mithril full helm (t),Mithril platebody (t),Mithril platelegs (t),Mithril plateskirt (t),Mithril kiteshield (t),Mithril full helm (g),Mithril platebody (g),Mithril platelegs (g),Mithril plateskirt (g),Mithril kiteshield (g),Green d'hide body (g),Green d'hide body (t),Green d'hide chaps (g),Green d'hide chaps (t),Saradomin mitre,Saradomin cloak,Guthix mitre,Guthix cloak,Zamorak mitre,Zamorak cloak,Ancient mitre,Ancient cloak,Ancient stole,Ancient crozier,Armadyl mitre,Armadyl cloak,Armadyl stole,Armadyl crozier,Bandos mitre,Bandos cloak,Bandos stole,Bandos crozier,Red boater,Green boater,Orange boater,Black boater,Blue boater,Pink boater,Purple boater,White boater,Red headband,Black headband,Brown headband,White headband,Blue headband,Gold headband,Pink headband,Green headband,Crier hat,Crier coat,Crier bell,Adamant cane,Arceuus banner,Piscarilius banner,Hosidius banner,Shayzien banner,Lovakengj banner,Cabbage round shield,Black unicorn mask,White unicorn mask,Cat mask,Penguin mask,Leprechaun hat,Black leprechaun hat,Wolf mask,Wolf cloak,Purple elegant shirt,Purple elegant blouse,Purple elegant legs,Purple elegant skirt,Black elegant shirt,White elegant blouse,Black elegant legs,White elegant skirt,Pink elegant shirt,Pink elegant blouse,Pink elegant legs,Pink elegant skirt,Gold elegant shirt,Gold elegant blouse,Gold elegant legs,Gold elegant skirt,Gnomish firelighter,Strength amulet (t),Yew comp bow§Hard Treasure Trails?Robin hood hat,Dragon boots ornament kit,Rune defender ornament kit,Tzhaar-ket-om ornament kit,Berserker necklace ornament kit,Rune full helm (t),Rune platebody (t),Rune platelegs (t),Rune plateskirt (t),Rune kiteshield (t),Rune full helm (g),Rune platebody (g),Rune platelegs (g),Rune plateskirt (g),Rune kiteshield (g),Zamorak full helm,Zamorak platebody,Zamorak platelegs,Zamorak plateskirt,Zamorak kiteshield,Guthix full helm,Guthix platebody,Guthix platelegs,Guthix plateskirt,Guthix kiteshield,Saradomin full helm,Saradomin platebody,Saradomin platelegs,Saradomin plateskirt,Saradomin kiteshield,Ancient full helm,Ancient platebody,Ancient platelegs,Ancient plateskirt,Ancient kiteshield,Armadyl full helm,Armadyl platebody,Armadyl platelegs,Armadyl plateskirt,Armadyl kiteshield,Bandos full helm,Bandos platebody,Bandos platelegs,Bandos plateskirt,Bandos kiteshield,Rune shield (h1),Rune shield (h2),Rune shield (h3),Rune shield (h4),Rune shield (h5),Rune helm (h1),Rune helm (h2),Rune helm (h3),Rune helm (h4),Rune helm (h5),Rune platebody (h1),Rune platebody (h2),Rune platebody (h3),Rune platebody (h4),Rune platebody (h5),Saradomin coif,Saradomin d'hide body,Saradomin chaps,Saradomin bracers,Saradomin d'hide boots,Saradomin d'hide shield,Guthix coif,Guthix d'hide body,Guthix chaps,Guthix bracers,Guthix d'hide boots,Guthix d'hide shield,Zamorak coif,Zamorak d'hide body,Zamorak chaps,Zamorak bracers,Zamorak d'hide boots,Zamorak d'hide shield,Bandos coif,Bandos d'hide body,Bandos chaps,Bandos bracers,Bandos d'hide boots,Bandos d'hide shield,Armadyl coif,Armadyl d'hide body,Armadyl chaps,Armadyl bracers,Armadyl d'hide boots,Armadyl d'hide shield,Ancient coif,Ancient d'hide body,Ancient chaps,Ancient bracers,Ancient d'hide boots,Ancient d'hide shield,Red d'hide body (t),Red d'hide chaps (t),Red d'hide body (g),Red d'hide chaps (g),Blue d'hide body (t),Blue d'hide chaps (t),Blue d'hide body (g),Blue d'hide chaps (g),Enchanted hat,Enchanted top,Enchanted robe,Saradomin stole,Saradomin crozier,Guthix stole,Guthix crozier,Zamorak stole,Zamorak crozier,Zombie head,Cyclops head,Pirate's hat,Red cavalier,White cavalier,Navy cavalier,Tan cavalier,Dark cavalier,Black cavalier,Pith helmet,Explorer backpack,Thieving bag,Green dragon mask,Blue dragon mask,Red dragon mask,Black dragon mask,Nunchaku,Dual sai,Rune cane,Amulet of glory (t),Magic comp bow§Elite Treasure Trails?Ring of 3rd age,Fury ornament kit,Dragon chainbody ornament kit,Dragon legs/skirt ornament kit,Dragon sq shield ornament kit,Dragon full helm ornament kit,Dragon scimitar ornament kit,Light infinity colour kit,Dark infinity colour kit,Holy wraps,Ranger gloves,Rangers' tunic,Rangers' tights,Black d'hide body (g),Black d'hide chaps (g),Black d'hide body (t),Black d'hide chaps (t),Royal crown,Royal sceptre,Royal gown top,Royal gown bottom,Musketeer hat,Musketeer tabard,Musketeer pants,Dark tuxedo jacket,Dark trousers,Dark tuxedo shoes,Dark tuxedo cuffs,Dark bow tie,Light tuxedo jacket,Light trousers,Light tuxedo shoes,Light tuxedo cuffs,Light bow tie,Arceuus scarf,Hosidius scarf,Piscarilius scarf,Shayzien scarf,Lovakengj scarf,Bronze dragon mask,Iron dragon mask,Steel dragon mask,Mithril dragon mask,Adamant dragon mask,Rune dragon mask,Katana,Dragon cane,Briefcase,Bucket helm,Blacksmith's helm,Deerstalker,Afro,Big pirate hat,Top hat,Monocle,Sagacious spectacles,Fremennik kilt,Giant boot,Uri's hat§Master Treasure Trails?Bloodhound,Ring of 3rd age,Armadyl godsword ornament kit,Bandos godsword ornament kit,Saradomin godsword ornament kit,Zamorak godsword ornament kit,Occult ornament kit,Torture ornament kit,Anguish ornament kit,Dragon defender ornament kit,Dragon kiteshield ornament kit,Dragon platebody ornament kit,Tormented ornament kit,Hood of darkness,Robe top of darkness,Robe bottom of darkness,Gloves of darkness,Boots of darkness,Samurai kasa,Samurai shirt,Samurai greaves,Samurai boots,Samurai gloves,Ankou mask,Ankou top,Ankou gloves,Ankou socks,Ankou's leggings,Mummy's head,Mummy's feet,Mummy's hands,Mummy's legs,Mummy's body,Shayzien hood,Hosidius hood,Arceuus hood,Piscarilius hood,Lovakengj hood,Lesser demon mask,Greater demon mask,Black demon mask,Jungle demon mask,Old demon mask,Left eye patch,Bowl wig,Ale of the gods,Obsidian cape (r),Half moon spectacles,Fancy tiara§Hard Treasure Trail Rewards (Rare)?3rd age range coif,3rd age range top,3rd age range legs,3rd age vambraces,3rd age robe top,3rd age robe,3rd age mage hat,3rd age amulet,3rd age plateskirt,3rd age platelegs,3rd age platebody,3rd age full helmet,3rd age kiteshield,Gilded platebody,Gilded platelegs,Gilded plateskirt,Gilded full helm,Gilded kiteshield,Gilded med helm,Gilded chainbody,Gilded sq shield,Gilded 2h sword,Gilded spear,Gilded hasta§Elite Treasure Trail Rewards (Rare)?3rd age longsword,3rd age wand,3rd age cloak,3rd age bow,3rd age range coif,3rd age range top,3rd age range legs,3rd age vambraces,3rd age robe top,3rd age robe,3rd age mage hat,3rd age amulet,3rd age plateskirt,3rd age platelegs,3rd age platebody,3rd age full helmet,3rd age kiteshield,Gilded scimitar,Gilded boots,Gilded platebody,Gilded platelegs,Gilded plateskirt,Gilded full helm,Gilded kiteshield,Gilded med helm,Gilded chainbody,Gilded sq shield,Gilded 2h sword,Gilded spear,Gilded hasta,Gilded coif,Gilded d'hide vambraces,Gilded d'hide body,Gilded d'hide chaps,Gilded pickaxe,Gilded axe,Gilded spade,Ring of nature,Lava dragon mask§Master Treasure Trail Rewards (Rare)?3rd age pickaxe,3rd age axe,3rd age longsword,3rd age wand,3rd age cloak,3rd age bow,3rd age range coif,3rd age range top,3rd age range legs,3rd age vambraces,3rd age robe top,3rd age robe,3rd age mage hat,3rd age amulet,3rd age plateskirt,3rd age platelegs,3rd age platebody,3rd age full helmet,3rd age kiteshield,3rd age druidic robe bottoms,3rd age druidic robe top,3rd age druidic staff,3rd age druidic cloak,Gilded scimitar,Gilded boots,Gilded platebody,Gilded platelegs,Gilded plateskirt,Gilded full helm,Gilded kiteshield,Gilded med helm,Gilded chainbody,Gilded sq shield,Gilded 2h sword,Gilded spear,Gilded hasta,Gilded coif,Gilded d'hide vambraces,Gilded d'hide body,Gilded d'hide chaps,Gilded pickaxe,Gilded axe,Gilded spade,Bucket helm (g),Ring of coins§Shared Treasure Trail Rewards?Saradomin page 1,Saradomin page 2,Saradomin page 3,Saradomin page 4,Zamorak page 1,Zamorak page 2,Zamorak page 3,Zamorak page 4,Guthix page 1,Guthix page 2,Guthix page 3,Guthix page 4,Bandos page 1,Bandos page 2,Bandos page 3,Bandos page 4,Armadyl page 1,Armadyl page 2,Armadyl page 3,Armadyl page 4,Ancient page 1,Ancient page 2,Ancient page 3,Ancient page 4,Holy blessing,Unholy blessing,Peaceful blessing,War blessing,Honourable blessing,Ancient blessing,Nardah teleport,Mos le'harmless teleport,Mort'ton teleport,Feldip hills teleport,Lunar isle teleport,Digsite teleport,Piscatoris teleport,Pest control teleport,Tai bwo wannai teleport,Lumberyard teleport,Iorwerth camp teleport,Master scroll book,Red firelighter,Green firelighter,Blue firelighter,Purple firelighter,White firelighter,Charge dragonstone jewellery scroll,Purple sweets§@Minigames#Barbarian Assault?Pet penance queen,Fighter hat,Ranger hat,Runner hat,Healer hat,Fighter torso,Penance skirt,Runner boots,Penance gloves,Granite helm,Granite body§Brimhaven Agility Arena?Agility arena ticket,Pirate's hook,Graceful hood,Graceful top,Graceful legs,Graceful gloves,Graceful boots,Graceful cape§Castle Wars?Decorative helm (red),Decorative full helm (red),Decorative armour (red platebody),Decorative sword (red),Decorative shield (red),Decorative armour (red platelegs),Decorative armour (red plateskirt),Decorative boots (red),Decorative helm (white),Decorative full helm (white),Decorative armour (white platebody),Decorative sword (white),Decorative shield (white),Decorative armour (white platelegs),Decorative armour (white plateskirt),Decorative boots (white),Decorative helm (gold),Decorative full helm (gold),Decorative armour (gold platebody),Decorative sword (gold),Decorative shield (gold),Decorative armour (gold platelegs),Decorative armour (gold plateskirt),Decorative boots (gold),Castlewars hood,Castlewars cloak,Castlewars hood,Castlewars cloak,Saradomin banner,Zamorak banner,Decorative armour (magic hat),Decorative armour (magic top),Decorative armour (magic legs),Decorative armour (ranged top),Decorative armour (ranged legs),Decorative armour (quiver),Saradomin halo,Zamorak halo,Guthix halo§Fishing Trawler?Angler hat,Angler top,Angler waders,Angler boots§Giants' Foundry?Smiths tunic,Smiths trousers,Smiths boots,Smiths gloves,Colossal blade,Double ammo mould,Kovac's grog,Smithing catalyst,Ore pack§Gnome Restaurant?Grand seed pod,Gnome scarf,Gnome goggles,Mint cake§Guardians of the Rift?Abyssal protector,Abyssal pearls,Catalytic talisman,Abyssal needle,Abyssal green dye,Abyssal blue dye,Abyssal red dye,Hat of the eye,Robe top of the eye,Robe bottoms of the eye,Boots of the eye,Ring of the elements,Abyssal lantern,Guardian's eye,Intricate pouch,Lost bag,Tarnished locket§Hallowed Sepulchre?Hallowed mark,Hallowed token,Hallowed grapple,Hallowed focus,Hallowed symbol,Hallowed hammer,Hallowed ring,Dark dye,Dark acorn,Strange old lockpick,Ring of endurance,Mysterious page,Mysterious page,Mysterious page,Mysterious page,Mysterious page§Last Man Standing?Deadman's chest,Deadman's legs,Deadman's cape,Armadyl halo,Bandos halo,Seren halo,Ancient halo,Brassica halo,Golden armadyl special attack,Golden bandos special attack,Golden saradomin special attack,Golden zamorak special attack,Victor's cape (1),Victor's cape (10),Victor's cape (50),Victor's cape (100),Victor's cape (500),Victor's cape (1000),Granite clamp,Ornate maul handle,Steam staff upgrade kit,Lava staff upgrade kit,Dragon pickaxe upgrade kit,Ward upgrade kit,Green dark bow paint,Yellow dark bow paint,White dark bow paint,Blue dark bow paint,Volcanic whip mix,Frozen whip mix,Guthixian icon,Swift blade§Magic Training Arena?Beginner wand,Apprentice wand,Teacher wand,Master wand,Infinity hat,Infinity top,Infinity bottoms,Infinity boots,Infinity gloves,Mage's book,Bones to peaches§Mahogany Homes?Supply crate,Carpenter's helmet,Carpenter's shirt,Carpenter's trousers,Carpenter's boots,Amy's saw,Plank sack,Hosidius blueprints§Pest Control?Void knight mace,Void knight top,Void knight robe,Void knight gloves,Void mage helm,Void melee helm,Void ranger helm,Void seal(8),Elite void top,Elite void robe§Rogues' Den?Rogue mask,Rogue top,Rogue trousers,Rogue boots,Rogue gloves§Shades of Mort'ton?Amulet of the damned,Flamtaer bag,Fine cloth,Bronze locks,Steel locks,Black locks,Silver locks,Gold locks,Zealot's helm,Zealot's robe top,Zealot's robe bottom,Zealot's boots,Tree wizards' journal,Bloody notes§Soul Wars?Lil' creator,Soul cape,Ectoplasmator§Temple Trekking?Lumberjack hat,Lumberjack top,Lumberjack legs,Lumberjack boots§Tithe Farm?Farmer's strawhat,Farmer's jacket,Farmer's boro trousers,Farmer's boots,Seed box,Gricoller's can,Herb sack§Trouble Brewing?Blue naval shirt,Blue tricorn hat,Blue navy slacks,Green naval shirt,Green tricorn hat,Green navy slacks,Red naval shirt,Red tricorn hat,Red navy slacks,Brown naval shirt,Brown tricorn hat,Brown navy slacks,Black naval shirt,Black tricorn hat,Black navy slacks,Purple naval shirt,Purple tricorn hat,Purple navy slacks,Grey naval shirt,Grey tricorn hat,Grey navy slacks,Cutthroat flag,Gilded smile flag,Bronze fist flag,Lucky shot flag,Treasure flag,Phasmatys flag,The stuff,Rum,Rum§Volcanic Mine?Ash covered tome,Large water container,Volcanic mine teleport,Dragon pickaxe (broken)§@Other#Aerial Fishing?Golden tench,Pearl fishing rod,Pearl fly fishing rod,Pearl barbarian rod,Fish sack,Angler hat,Angler top,Angler waders,Angler boots§All Pets?Abyssal orphan,Ikkle hydra,Callisto cub,Hellpuppy,Pet chaos elemental,Pet zilyana,Pet dark core,Pet dagannoth prime,Pet dagannoth supreme,Pet dagannoth rex,Tzrek-jad,Pet general graardor,Baby mole,Noon,Jal-nib-rek,Kalphite princess,Prince black dragon,Pet kraken,Pet kree'arra,Pet k'ril tsutsaroth,Scorpia's offspring,Skotos,Pet smoke devil,Venenatis spiderling,Vet'ion jr.,Vorki,Phoenix,Pet snakeling,Olmlet,Lil' zik,Bloodhound,Pet penance queen,Heron,Rock golem,Beaver,Baby chinchompa,Giant squirrel,Tangleroot,Rocky,Rift guardian,Herbi,Chompy chick,Sraracha,Smolcano,Youngllef,Little nightmare,Lil' creator,Tiny tempor,Nexling,Abyssal protector,Tumeken's guardian,Muphin,Wisp,Baron,Butch,Lil'viathan,Scurry,Smol heredit,Quetzin§Camdozaal?Barronite mace,Barronite head,Barronite handle,Barronite guard,Ancient globe,Ancient ledger,Ancient astroscope,Ancient treatise,Ancient carcanet,Imcando hammer§Champion's Challenge?Earth warrior champion scroll,Ghoul champion scroll,Giant champion scroll,Goblin champion scroll,Hobgoblin champion scroll,Imp champion scroll,Jogre champion scroll,Lesser demon champion scroll,Skeleton champion scroll,Zombie champion scroll,Champion's cape§Chaos Druids?Elder chaos top,Elder chaos robe,Elder chaos hood§Chompy Birds?Chompy chick,Chompy bird hat (ogre bowman),Chompy bird hat (bowman),Chompy bird hat (ogre yeoman),Chompy bird hat (yeoman),Chompy bird hat (ogre marksman),Chompy bird hat (marksman),Chompy bird hat (ogre woodsman),Chompy bird hat (woodsman),Chompy bird hat (ogre forester),Chompy bird hat (forester),Chompy bird hat (ogre bowmaster),Chompy bird hat (bowmaster),Chompy bird hat (ogre expert),Chompy bird hat (expert),Chompy bird hat (ogre dragon archer),Chompy bird hat (dragon archer),Chompy bird hat (expert ogre dragon archer),Chompy bird hat (expert dragon archer)§Creature Creation?Tea flask,Plain satchel,Green satchel,Red satchel,Black satchel,Gold satchel,Rune satchel§Cyclopes?Bronze defender,Iron defender,Steel defender,Black defender,Mithril defender,Adamant defender,Rune defender,Dragon defender§Forestry?Fox whistle,Golden pheasant egg,Lumberjack hat,Lumberjack top,Lumberjack legs,Lumberjack boots,Forestry hat,Forestry top,Forestry legs,Forestry boots,Twitcher's gloves,Funky shaped log,Log basket,Log brace,Clothes pouch blueprint,Cape pouch,Felling axe handle,Pheasant hat,Pheasant legs,Pheasant boots,Pheasant cape,Petal garland,Sturdy beehive parts§Fossil Island Notes?Scribbled note,Partial note,Ancient note,Ancient writings,Experimental note,Paragraph of text,Musty smelling note,Hastily scrawled note,Old writing,Short note§Glough's Experiments?Zenyte shard,Light frame,Heavy frame,Ballista limbs,Monkey tail,Ballista spring§Hunter Guild?Quetzin,Huntsman's kit,Guild hunter headwear,Guild hunter top,Guild hunter legs,Guild hunter boots§Monkey Backpacks?Karamjan monkey,Kruk jr,Maniacal monkey,Princely monkey,Skeleton monkey,Zombie monkey§Motherlode Mine?Coal bag,Gem bag,Prospector helmet,Prospector jacket,Prospector legs,Prospector boots§My Notes?Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page,Ancient page§Random Events?Camo top,Camo bottoms,Camo helmet,Lederhosen top,Lederhosen shorts,Lederhosen hat,Zombie shirt,Zombie trousers,Zombie mask,Zombie gloves,Zombie boots,Mime mask,Mime top,Mime legs,Mime gloves,Mime boots,Frog token,Stale baguette,Beekeeper's hat,Beekeeper's top,Beekeeper's legs,Beekeeper's gloves,Beekeeper's boots§Revenants?Viggora's chainmace (u),Craw's bow (u),Thammaron's sceptre (u),Amulet of avarice,Bracelet of ethereum (uncharged),Ancient crystal,Ancient relic,Ancient effigy,Ancient medallion,Ancient statuette,Ancient totem,Ancient emblem,Revenant cave teleport,Revenant ether§Rooftop Agility?Mark of grace,Graceful hood,Graceful cape,Graceful top,Graceful legs,Graceful gloves,Graceful boots§Shayzien Armour?Shayzien gloves (1),Shayzien boots (1),Shayzien helm (1),Shayzien greaves (1),Shayzien platebody (1),Shayzien gloves (2),Shayzien boots (2),Shayzien helm (2),Shayzien greaves (2),Shayzien platebody (2),Shayzien gloves (3),Shayzien boots (3),Shayzien helm (3),Shayzien greaves (3),Shayzien platebody (3),Shayzien gloves (4),Shayzien boots (4),Shayzien helm (4),Shayzien greaves (4),Shayzien platebody (4),Shayzien gloves (5),Shayzien boots (5),Shayzien helm (5),Shayzien greaves (5),Shayzien body (5)§Shooting Stars?Celestial ring (uncharged),Star fragment§Skilling Pets?Heron,Rock golem,Beaver,Baby chinchompa,Giant squirrel,Tangleroot,Rocky,Rift guardian§Slayer?Crawling hand,Cockatrice head,Basilisk head,Kurask head,Abyssal head,Imbued heart,Eternal gem,Dust battlestaff,Mist battlestaff,Abyssal whip,Granite maul,Mudskipper hat,Flippers,Brine sabre,Leaf-bladed sword,Leaf-bladed battleaxe,Black mask,Granite longsword,Granite boots,Wyvern visage,Granite legs,Granite helm,Draconic visage,Bronze boots,Iron boots,Steel boots,Black boots,Mithril boots,Adamant boots,Rune boots,Dragon boots,Abyssal dagger,Uncharged trident,Kraken tentacle,Dark bow,Occult necklace,Dragon chainbody,Dragon thrownaxe,Dragon harpoon,Dragon sword,Dragon knife,Broken dragon hasta,Drake's tooth,Drake's claw,Hydra tail,Hydra's fang,Hydra's eye,Hydra's heart,Mystic hat (light),Mystic robe top (light),Mystic robe bottom (light),Mystic gloves (light),Mystic boots (light),Mystic hat (dark),Mystic robe top (dark),Mystic robe bottom (dark),Mystic gloves (dark),Mystic boots (dark),Mystic hat (dusk),Mystic robe top (dusk),Mystic robe bottom (dusk),Mystic gloves (dusk),Mystic boots (dusk),Basilisk jaw,Dagon'hai hat,Dagon'hai robe top,Dagon'hai robe bottom,Blood shard,Ancient ceremonial mask,Ancient ceremonial top,Ancient ceremonial legs,Ancient ceremonial gloves,Ancient ceremonial boots,Warped sceptre,Sulphur blades,Teleport anchoring scroll§TzHaar?Obsidian cape,Toktz-ket-xil,Tzhaar-ket-om,Toktz-xil-ak,Toktz-xil-ek,Toktz-mej-tal,Toktz-xil-ul,Obsidian helmet,Obsidian platebody,Obsidian platelegs§Miscellaneous?Herbi,Chompy chick,Dragon warhammer,Big swordfish,Big shark,Big bass,Long bone,Curved bone,Ecumenical key,Pharaoh's sceptre (3),Dark totem base,Dark totem middle,Dark totem top,Chewed bones,Dragon full helm,Shield left half,Dragon metal slice,Dragon metal lump,Dragon limbs,Dragon spear,Amulet of eternal glory,Shaman mask,Evil chicken head,Evil chicken wings,Evil chicken legs,Evil chicken feet,Mining gloves,Superior mining gloves,Expert mining gloves,Right skull half,Left skull half,Top of sceptre,Bottom of sceptre,Mossy key,Giant key,Hespori seed,Fresh crab claw,Fresh crab shell,Xeric's talisman (inert),Mask of ranul,Elven signet,Crystal grail,Enhanced crystal teleport seed,Dragonstone full helm,Dragonstone platebody,Dragonstone platelegs,Dragonstone gauntlets,Dragonstone boots,Uncut onyx,Merfolk trident,Orange egg sac,Blue egg sac§@";
    private final String categoriSeperator = "@";
    private final String categoriNameSeperator = "#";
    private final String sectionSeperator = "§";
    private final String sectionDataSeperator = "?";

    public ArrayList<Categori> ReadData()
    {
        ArrayList<Categori> categoris = new ArrayList<>();

        String[] part1 = colLogData.split(categoriSeperator);
        for (String item : part1)
        {
            String[] part2 = item.split(categoriNameSeperator);
            Categori categori = new Categori();
            categori.name = part2[0];
            String remaining = GetRemaining(part2);
            StringBuilder sectionName = new StringBuilder();
            for (int i = 0; i < remaining.length(); i++)
            {
                String current = "" + remaining.charAt(i);
                boolean isDone = false;
                switch (current)
                {
                    case sectionDataSeperator:
                        i++;
                        current = "" + remaining.charAt(i);
                        StringBuilder collectionLogData = new StringBuilder();
                        while (!current.equals(sectionSeperator))
                        {
                            collectionLogData.append(current);
                            i++;
                            current = "" + remaining.charAt(i);
                        }
                        Section section = new Section();
                        section.name = sectionName.toString();
                        String[] part3 = collectionLogData.toString().split(",");
                        section.collectionLogs.addAll(Arrays.asList(part3));
                        categori.sections.add(section);
                        sectionName = new StringBuilder();
                        break;
                    case categoriSeperator:
                        isDone = true;
                        break;
                    default:
                        sectionName.append(current);
                        break;
                }

                if (isDone)
                {
                    break;
                }
            }

            categoris.add(categori);
        }

        for (Categori categori: categoris)
        {
            for (Section section : categori.sections)
            {
                categori.allItems.addAll(section.collectionLogs);
            }
        }

        return categoris;
    }

    private String GetRemaining(String[] part)
    {
        StringBuilder remaining = new StringBuilder();
        for (int i = 1; i < part.length; i++)
        {
            remaining.append(part[i]);
        }
        return remaining.toString();
    }
}
