package com.maximde.entitysize.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


@Getter
public class Config {

    private final File file = new File("plugins/EntitySize", "config.yml");
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    private boolean bStats;
    private boolean transition;
    private boolean isReachMultiplier;
    private boolean isStepHeightMultiplier;
    private boolean isSpeedMultiplier;
    private boolean isJumpMultiplier;
    private boolean isSaveFallDistanceMultiplier;
    private int transitionSteps;

    private boolean isReachReverted;
    private boolean isStepHeightReverted;
    private boolean isSpeedReverted;
    private boolean isJumpReverted;
    private boolean isSaveFallDistanceReverted;

    private double reachMultiplier;
    private double stepHeightMultiplier;
    private double speedMultiplier;
    private double jumpMultiplier;
    private double saveFallDistanceMultiplier;
    private String language;

    public Config() {
        setDefault("General.bStats", true);
        setDefault("General.language", "en_us");
        setDefault("Size.Transition", true);
        setDefault("Size.TransitionSteps", 30);
        setDefault("Size.IsReachMultiplier", true);
        setDefault("Size.IsStepHeightMultiplier", true);
        setDefault("Size.IsSpeedMultiplier", true);
        setDefault("Size.IsJumpMultiplier", true);
        setDefault("Size.IsSaveFallDistanceMultiplier", true);

        setDefault("Size.IsReachReverted", false);
        setDefault("Size.IsStepHeightReverted", false);
        setDefault("Size.IsSpeedReverted", false);
        setDefault("Size.IsJumpReverted", false);
        setDefault("Size.IsSaveFallDistanceReverted", false);

        setDefault("Size.ReachMultiplier", 1);
        setDefault("Size.StepHeightMultiplier", 1);
        setDefault("Size.SpeedMultiplier", 1);
        setDefault("Size.JumpMultiplier", 1);
        setDefault("Size.SaveFallDistanceMultiplier", 1);
        saveConfig();
        initValues();
    }

    private void initValues() {
        this.bStats = cfg.getBoolean("General.bStats");
        this.transition = cfg.getBoolean("Size.Transition");
        this.isReachMultiplier = cfg.getBoolean("Size.IsReachMultiplier");
        this.isStepHeightMultiplier = cfg.getBoolean("Size.IsStepHeightMultiplier");
        this.isSpeedMultiplier = cfg.getBoolean("Size.IsSpeedMultiplier");
        this.isJumpMultiplier = cfg.getBoolean("Size.IsJumpMultiplier");
        this.isSaveFallDistanceMultiplier = cfg.getBoolean("Size.IsSaveFallDistanceMultiplier");

        this.isReachReverted = cfg.getBoolean("Size.IsReachReverted");
        this.isStepHeightReverted = cfg.getBoolean("Size.IsStepHeightReverted");
        this.isSpeedReverted = cfg.getBoolean("Size.IsSpeedReverted");
        this.isJumpReverted = cfg.getBoolean("Size.IsJumpReverted");
        this.isSaveFallDistanceReverted = cfg.getBoolean("Size.IsSaveFallDistanceReverted");

        this.reachMultiplier = cfg.getDouble("Size.ReachMultiplier");
        this.stepHeightMultiplier = cfg.getDouble("Size.StepHeightMultiplier");
        this.speedMultiplier = cfg.getDouble("Size.SpeedMultiplier");
        this.jumpMultiplier = cfg.getDouble("Size.JumpMultiplier");
        this.saveFallDistanceMultiplier = cfg.getDouble("Size.SaveFallDistanceMultiplier");

        this.transitionSteps = cfg.getInt("Size.TransitionSteps");
        this.language = cfg.getString("General.language");
    }

    public void reload() {
        this.cfg = YamlConfiguration.loadConfiguration(file);
        initValues();
    }

    @SneakyThrows
    public void saveConfig() {
        cfg.save(file);
    }

    private void setDefault(String path, Object value) {
        if(!cfg.isSet(path)) setValue(path, value);
    }

    public void setValue(String path, Object value) {
        this.cfg.set(path, value);
        saveConfig();
        reload();
    }

    public Object getValue(String path) {
        return this.cfg.get(path);
    }

}

