package com.GuildSpamFilter.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PersonalBestEnum
{
    INCLUDE_ALL_EXCEPT("Include all except", 0),
    EXCLUDE_ALL_EXCEPT("Exclude all except", 1);

    private final String name;
    private final int mode;

    @Override
    public String toString()
    {
        return name;
    }
}
