package com.GuildSpamFilter;

import com.GuildSpamFilter.Configs.AchievementDiaryTier;
import com.GuildSpamFilter.Configs.CombatAchievementTier;
import com.GuildSpamFilter.Configs.PersonalBestMode;
import org.junit.Test;

import static org.mockito.Mockito.when;

public class GeneralFiltersTest extends FilterTestBase
{
    private static final String VORKATH_PERSONAL_BEST = "Biceps Btw has achieved a new Vorkath personal best: 1:05.40";
    private static final String ZULRAH_PERSONAL_BEST = "Biceps Btw has achieved a new Zulrah personal best: 0:58.20";
    private static final String CHAMBERS_OF_XERIC_PERSONAL_BEST = "Biceps Btw has achieved a new Chambers of Xeric (Team Size: 3 players) personal best: 18:03";

    private static final String MEDIUM_DIARY = "Biceps Btw has completed the Medium Ardougne diary.";
    private static final String HARD_DIARY = "Biceps Btw has completed the Hard Ardougne diary.";
    private static final String ELITE_DIARY = "Biceps Btw has completed the Elite Ardougne diary.";

    private static final String ELITE_COMBAT_ACHIEVEMENT_TIER = "Biceps Btw has unlocked the Elite tier of rewards from Combat Achievements!";
    private static final String MASTER_COMBAT_ACHIEVEMENT_TIER = "Biceps Btw has unlocked the Master tier of rewards from Combat Achievements!";
    private static final String GRANDMASTER_COMBAT_ACHIEVEMENT_TIER = "Biceps Btw has unlocked the Grandmaster tier of rewards from Combat Achievements!";

    // Personal bests

    @Test
    public void hidesEveryPersonalBestWhenTheListIsEmpty()
    {
        when(config.filterPersonalBests()).thenReturn(true);

        assertHidden(VORKATH_PERSONAL_BEST);
        assertHidden(ZULRAH_PERSONAL_BEST);
    }

    @Test
    public void excludeAllExceptModeKeepsOnlyListedPersonalBests()
    {
        when(config.filterPersonalBests()).thenReturn(true);
        setPersonalBestList("vorkath");

        assertShown(VORKATH_PERSONAL_BEST);
        assertHidden(ZULRAH_PERSONAL_BEST);
    }

    @Test
    public void includeAllExceptModeHidesOnlyListedPersonalBests()
    {
        when(config.filterPersonalBests()).thenReturn(true);
        when(config.personalBestMode()).thenReturn(PersonalBestMode.INCLUDE_ALL_EXCEPT);
        setPersonalBestList("vorkath");

        assertHidden(VORKATH_PERSONAL_BEST);
        assertShown(ZULRAH_PERSONAL_BEST);
    }

    @Test
    public void personalBestListAcceptsSeveralCommaSeparatedEntries()
    {
        when(config.filterPersonalBests()).thenReturn(true);
        setPersonalBestList("Chambers of Xeric, zulrah");

        assertShown(CHAMBERS_OF_XERIC_PERSONAL_BEST);
        assertShown(ZULRAH_PERSONAL_BEST);
        assertHidden(VORKATH_PERSONAL_BEST);
    }

    // Pets, clan members, quests, hardcore deaths and the login message

    @Test
    public void hidesEveryKindOfPetBroadcast()
    {
        when(config.filterPets()).thenReturn(true);

        assertHidden("Biceps Btw has a funny feeling like they're being followed: Vorki at 50 killcount.");
        assertHidden("Biceps Btw has a funny feeling like they would have been followed: Vorki at 50 killcount.");
        assertHidden("Biceps Btw feels something weird sneaking into their backpack: Heron at 5,000,000 XP.");
        assertHidden("Biceps Btw has acquired something special: Olmlet at 50 killcount.");
    }

    @Test
    public void hidesNewClanMembers()
    {
        when(config.filterNewClanMember()).thenReturn(true);

        assertHidden("Store Biceps has been invited into the clan by Biceps Btw.");
    }

    @Test
    public void hidesKickedClanMembers()
    {
        when(config.filterClanMemberKicked()).thenReturn(true);

        assertHidden("Biceps Btw has expelled Store Biceps from the clan.");
    }

    @Test
    public void hidesQuestCompletions()
    {
        when(config.filterQuestComplete()).thenReturn(true);

        assertHidden("Biceps Btw has completed a quest: Dragon Slayer II");
    }

    @Test
    public void hidesHardcoreDeaths()
    {
        when(config.filterHardcoreDeath()).thenReturn(true);

        assertHidden("Biceps Btw has died and lost their hardcore ironman status!");
    }

    @Test
    public void hidesTheClanLoginMessage()
    {
        when(config.filterDefaultMessage()).thenReturn(true);

        assertHidden("To talk in your clan's channel, start each line of chat with // or /c.");
    }

    // Achievement diaries

    @Test
    public void hidesEveryAchievementDiaryWithTheDefaultThreshold()
    {
        when(config.filterAchievementDiaries()).thenReturn(true);

        assertHidden(MEDIUM_DIARY);
        assertHidden(HARD_DIARY);
        assertHidden(ELITE_DIARY);
    }

    @Test
    public void achievementDiaryThresholdKeepsThatTierAndHarder()
    {
        when(config.filterAchievementDiaries()).thenReturn(true);
        when(config.achievementDiariesThreshold()).thenReturn(AchievementDiaryTier.HARD);

        assertHidden(MEDIUM_DIARY);
        assertShown(HARD_DIARY);
        assertShown(ELITE_DIARY);
    }

    @Test
    public void hidesEasyAchievementDiaries()
    {
        when(config.filterAchievementDiaries()).thenReturn(true);

        assertHidden("Biceps Btw has completed the Easy Lumbridge & Draynor diary.");
    }

    @Test
    public void achievementDiariesHandlePlayerNamesContainingThe()
    {
        when(config.filterAchievementDiaries()).thenReturn(true);

        assertHidden("Heather has completed the Hard Ardougne diary.");
    }

    @Test
    public void achievementDiaryFilterLeavesCombatAchievementsAlone()
    {
        when(config.filterAchievementDiaries()).thenReturn(true);

        assertShown(ELITE_COMBAT_ACHIEVEMENT_TIER);
    }

    // Combat Achievements

    @Test
    public void hidesEveryCombatAchievementTierWithTheDefaultThreshold()
    {
        when(config.filterCombatAchievementTiers()).thenReturn(true);

        assertHidden("Biceps Btw has unlocked the Easy tier of rewards from Combat Achievements!");
        assertHidden(ELITE_COMBAT_ACHIEVEMENT_TIER);
        assertHidden(MASTER_COMBAT_ACHIEVEMENT_TIER);
        assertHidden(GRANDMASTER_COMBAT_ACHIEVEMENT_TIER);
    }

    @Test
    public void combatAchievementsHandlePlayerNamesContainingThe()
    {
        when(config.filterCombatAchievementTiers()).thenReturn(true);
        when(config.combatAchievementThreshold()).thenReturn(CombatAchievementTier.MASTER);

        assertHidden("Heather has unlocked the Elite tier of rewards from Combat Achievements!");
        assertShown("Heather has unlocked the Master tier of rewards from Combat Achievements!");
    }

    @Test
    public void combatAchievementThresholdKeepsThatTierAndHarder()
    {
        when(config.filterCombatAchievementTiers()).thenReturn(true);
        when(config.combatAchievementThreshold()).thenReturn(CombatAchievementTier.MASTER);

        assertHidden(ELITE_COMBAT_ACHIEVEMENT_TIER);
        assertShown(MASTER_COMBAT_ACHIEVEMENT_TIER);
        assertShown(GRANDMASTER_COMBAT_ACHIEVEMENT_TIER);
    }

    @Test
    public void combatTaskThresholdKeepsThatTierAndHarder()
    {
        when(config.filterCombatAchievementTasks()).thenReturn(true);
        when(config.combatAchievementThreshold()).thenReturn(CombatAchievementTier.ELITE);

        assertHidden("Biceps Btw has completed a Hard combat task: Whack-a-Mole.");
        assertShown("Biceps Btw has completed an Elite combat task: Perfect Zulrah.");
    }

    @Test
    public void hidesEasyCombatTasks()
    {
        when(config.filterCombatAchievementTasks()).thenReturn(true);

        assertHidden("Biceps Btw has completed an Easy combat task: Noxious Foe.");
    }

    // Combat level

    @Test
    public void combatLevelThresholdKeepsThatLevelAndHigher()
    {
        when(config.filterCombatLevelUps()).thenReturn(true);
        when(config.combatLevelUpThreshold()).thenReturn(100);

        assertHidden("Biceps Btw has reached combat level 99.");
        assertShown("Biceps Btw has reached combat level 100.");
    }

    @Test
    public void readsCombatLevelsWithoutAFullStop()
    {
        when(config.filterCombatLevelUps()).thenReturn(true);
        when(config.combatLevelUpThreshold()).thenReturn(100);

        assertShown("Biceps Btw has reached combat level 100");
    }

    @Test
    public void maxCombatIsHiddenOnlyWhenTheThresholdIsAbove126()
    {
        String maxCombat = "Biceps Btw has reached the highest possible combat level of 126!";
        when(config.filterCombatLevelUps()).thenReturn(true);

        assertHidden(maxCombat);

        when(config.combatLevelUpThreshold()).thenReturn(126);
        assertShown(maxCombat);
    }
}
