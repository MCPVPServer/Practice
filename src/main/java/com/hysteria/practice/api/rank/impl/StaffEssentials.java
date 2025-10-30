package com.hysteria.practice.api.rank.impl;

import com.hysteria.practice.api.rank.Rank;
import me.kaleb.staffEssentials.profile.Profile;

import java.util.UUID;

public class StaffEssentials implements Rank {
    private Profile getProfile(UUID uuid) {
        Profile profile = Profile.get(uuid);
        if (profile == null) profile = Profile.reloadFromDatabase(uuid);
        return profile;
    }

    @Override
    public String getName(UUID uuid) {
        Profile profile = getProfile(uuid);
        if (profile == null || profile.getRank() == null)
            return "Default";
        return profile.getRank().getName();
    }

    @Override
    public String getPrefix(UUID uuid) {
        Profile profile = getProfile(uuid);
        if (profile == null || profile.getRank() == null)
            return "";
        return profile.getRank().getPrefix();
    }

    @Override
    public String getSuffix(UUID uuid) {
        return "";
    }

    @Override
    public String getColor(UUID uuid) {
        Profile profile = getProfile(uuid);
        if (profile == null || profile.getRank() == null)
            return "§a";
        return profile.getRank().getColor();
    }

    @Override
    public int getWeight(UUID uuid) {
        Profile profile = getProfile(uuid);
        if (profile == null || profile.getRank() == null)
            return 0;
        return profile.getRank().getPriority();
    }
}
