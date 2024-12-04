package me.autobot.playerdoll.api;

import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.constant.AbsServerVersion;

public final class BuiltinImpl {

    private static BuiltinImpl instance = null;

    public static void init() {
        if (instance == null) {
            instance = new BuiltinImpl();
        }
    }

    public BuiltinImpl() {
        registerServerBranch();
        registerServerVersion();
    }

    private void registerServerBranch() {
        AbsServerBranch.SPIGOT = new AbsServerBranch("me.autobot.playerdoll.scheduler.BukkitScheduler") {
            @Override
            public String registerName() {
                return "SPIGOT";
            }

            @Override
            public boolean match() {
                return ReflectionUtil.hasClass("org.spigotmc.SpigotConfig");
            }
        };
        AbsServerBranch.PAPER = new AbsServerBranch("me.autobot.playerdoll.scheduler.BukkitScheduler") {
            @Override
            public String registerName() {
                return "PAPERSERIES";
            }

            @Override
            public boolean match() {
                return ReflectionUtil.hasClass("com.destroystokyo.paper.PaperConfig") ||
                        ReflectionUtil.hasClass("io.papermc.paper.configuration.Configuration");
            }
        };

        AbsServerBranch.FOLIA = new AbsServerBranch("me.autobot.playerdoll.addon.FoliaScheduler") {
            @Override
            public String registerName() {
                return "FOLIA";
            }

            @Override
            public boolean match() {
                return ReflectionUtil.getFoliaRegoinizedServerClass() != null;
            }
        };
    }

    private void registerServerVersion() {
        AbsServerVersion.v1_20_R2 = new AbsServerVersion(764) {
            @Override
            public String registerVersion() {
                return "v1_20_R2";
            }

            @Override
            public boolean match(String ver) {
                return ver.equals("1.20.2");
            }

        };

        AbsServerVersion.v1_20_R3 = new AbsServerVersion(765) {
            @Override
            public String registerVersion() {
                return "v1_20_R3";
            }

            @Override
            public boolean match(String ver) {
                return ver.matches("1\\.20\\.3|1\\.20\\.4");
            }
        };

        AbsServerVersion.v1_20_R4 = new AbsServerVersion(766) {
            @Override
            public String registerVersion() {
                return "v1_20_R4";
            }

            @Override
            public boolean match(String ver) {
                return ver.matches("1\\.20\\.5|1\\.20\\.6");
            }
        };

        AbsServerVersion.v1_21_R1 = new AbsServerVersion(767) {
            @Override
            public String registerVersion() {
                return "v1_21_R1";
            }

            @Override
            public boolean match(String ver) {
                return ver.matches("1\\.21|1\\.21\\.1");
            }
        };

        AbsServerVersion.v1_21_R2 = new AbsServerVersion(768) {
            @Override
            public String registerVersion() {
                return "v1_21_R2";
            }

            @Override
            public boolean match(String ver) {
                return ver.matches("1\\.21\\.2|1\\.21\\.3");
            }
        };

        AbsServerVersion.v1_21_R3 = new AbsServerVersion(769) {
            @Override
            public String registerVersion() {
                return "v1_21_R3";
            }

            @Override
            public boolean match(String ver) {
                return ver.matches("1\\.21\\.4");
            }
        };
    }
}
