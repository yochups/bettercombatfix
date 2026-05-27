package com.example.bettercombatfix.mixin;

import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = PlayerAttackHelper.class, remap = false)
public class PlayerAttackHelperMixin {
    @Invoker("evaluateConditions")
    private static boolean callEvaluateConditions(
            WeaponAttributes.Condition[] conditions,
            Player player,
            boolean isOffHandAttack
    ) {
        throw new AssertionError();
    }

    /**
     * @author yochu
     * @reason Skip invalid combo attacks instead of pausing combo
     */
    @Overwrite
    public static WeaponAttributes.Attack getCurrentAttack(
            Player player,
            AttackHand hand,
            List<WeaponAttributes.Attack> attacks,
            int comboCount
    ) {
        if (attacks == null || attacks.isEmpty()) {
            return null;
        }

        int size = attacks.size();

        for (int offset = 0; offset < size; offset++) {
            int index = (comboCount + offset) % size;

            WeaponAttributes.Attack attack = attacks.get(index);

            if (attack == null) {
                continue;
            }

            if (callEvaluateConditions(
                attack.conditions(),
                player,
                hand.isOffHand()
            )) {
                return attack;
            }
        }

        return null;
    }
}