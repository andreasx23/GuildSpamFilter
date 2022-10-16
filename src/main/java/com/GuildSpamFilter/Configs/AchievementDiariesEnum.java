package com.GuildSpamFilter.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementDiariesEnum
{
    ALL("All", 0),
    ELITE("Elite", 1),
    HARD("Hard", 2),
    MEDIUM("Medium", 3);
//    EASY("Easy", 4);

    private final String name;
    private final int mode;

    @Override
    public String toString()
    {
        return name;
    }
}
