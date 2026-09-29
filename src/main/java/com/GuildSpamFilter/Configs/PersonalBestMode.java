package com.GuildSpamFilter.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// The constant names are what users' settings are saved as, so they must never be renamed
@Getter
@RequiredArgsConstructor
public enum PersonalBestMode
{
    INCLUDE_ALL_EXCEPT("Include all except", 0),
    EXCLUDE_ALL_EXCEPT("Exclude all except", 1);

    private final String name;
    private final int id;

    @Override
    public String toString()
    {
        return name;
    }
}
