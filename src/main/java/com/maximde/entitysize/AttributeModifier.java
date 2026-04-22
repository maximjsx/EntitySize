package com.maximde.entitysize;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

@Data
@AllArgsConstructor
public class AttributeModifier {
    private final Attribute attribute;
    private final double defaultValue;
    private final double maxValue;
    private final boolean enabled;
    private final boolean reverted;
    private final double multiplier;

    public AttributeModifier(Attribute attribute, double defaultValue) {
        this(attribute, defaultValue, 1024.0D, true, false, 1.0D);
    }

    public void apply(LivingEntity entity, double scale) {
        if (!enabled) return;

        double finalScale = reverted ? -scale : scale;
        double value = calculateValue(defaultValue, maxValue, finalScale * multiplier);

        EntitySize.scheduler().runAtEntity(entity, task -> {
            var attr = entity.getAttribute(attribute);
            if (attr == null) return;
            attr.setBaseValue(value);
        });
    }

    public void reset(LivingEntity entity) {
        EntitySize.scheduler().runAtEntity(entity, task -> {
            var attr = entity.getAttribute(attribute);
            if (attr != null) {
                attr.setBaseValue(defaultValue);
            }
        });
    }

    private double calculateValue(double defaultValue, double maxValue, double multiplier) {
        double value = defaultValue * multiplier;
        return Math.clamp(value, 0.01, maxValue);
    }
}
