package com.GuildSpamFilter.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CombatDiariesEnum
{
    ALL("All", 0),
    GRANDMASTER("Grandmaster", 1),
    MASTER("Master", 2),
    ELITE("Elite", 3),
    HARD("Hard", 4),
    MEDIUM("Medium", 5);
//    EASY("Easy", 6);

    private final String name;
    private final int mode;

    @Override
    public String toString()
    {
        return name;
    }
}
