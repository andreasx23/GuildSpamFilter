package com.GuildSpamFilter.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CombatDiariesEnum
{
    ALL("All", 6),
    GRANDMASTER("Grandmaster", 5),
    MASTER("Master", 4),
    ELITE("Elite", 3),
    HARD("Hard", 2),
    MEDIUM("Medium", 1);
//    EASY("Easy", 0);

    private final String name;
    private final int mode;

    @Override
    public String toString()
    {
        return name;
    }

    public int getId()
    {
        return mode;
    }
}