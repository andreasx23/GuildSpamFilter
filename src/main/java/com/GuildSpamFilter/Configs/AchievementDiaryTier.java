package com.GuildSpamFilter.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// The constant names are what users' settings are saved as, so they must never be renamed
@Getter
@RequiredArgsConstructor
public enum AchievementDiaryTier
{
    ALL("All", 4),
    ELITE("Elite", 3),
    HARD("Hard", 2),
    MEDIUM("Medium", 1);
    // Easy (id 0) isn't offered as a threshold, since it would hide nothing

    private final String name;
    private final int id;

    @Override
    public String toString()
    {
        return name;
    }
}
