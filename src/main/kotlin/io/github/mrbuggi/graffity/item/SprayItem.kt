package io.github.mrbuggi.graffity.item

import io.github.mrbuggi.graffity.GraffitiType
import io.github.mrbuggi.graffity.block.GraffitiBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.Vec3
import java.util.function.Supplier

/**
 * Баллончик: наносит граффити своего вида на грань блока и тратит один заряд.
 *
 * Блок передаётся поставщиком, потому что предметы создаются раньше,
 * чем заканчивается регистрация блоков.
 */
class SprayItem(
    properties: Properties,
    val graffitiType: GraffitiType,
    private val graffitiBlock: Supplier<GraffitiBlock>,
) : Item(properties.durability(MAX_USES)) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val side = context.clickedFace
        val graffitiPos = context.clickedPos.relative(side)

        // Рисовать можно в пустом месте или поверх старого граффити.
        val existing = level.getBlockState(graffitiPos)
        if (!existing.isAir && existing.block !is GraffitiBlock) {
            return InteractionResult.FAIL
        }

        // Тип крепления и опору считает сам блок.
        val graffitiState = graffitiBlock.get().stateForSide(side, context.horizontalDirection)
        if (!graffitiState.canSurvive(level, graffitiPos)) {
            return InteractionResult.FAIL
        }

        if (level is ServerLevel) {
            spawnSprayParticles(level, graffitiPos, side)
            level.playSound(
                null, graffitiPos, SoundEvents.FIREWORK_ROCKET_LAUNCH,
                SoundSource.PLAYERS, 1.0f, graffitiType.soundPitch,
            )
            level.setBlockAndUpdate(graffitiPos, graffitiState)

            // В креативе hurtAndBreak сам ничего не тратит.
            context.player?.let { player ->
                context.itemInHand.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.hand))
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun getBarColor(stack: ItemStack): Int {
        val left = 1.0f - stack.damageValue.toFloat() / stack.maxDamage
        return when {
            left > 0.6f -> BAR_COLOR_FULL
            left > 0.2f -> BAR_COLOR_HALF
            else -> BAR_COLOR_LOW
        }
    }

    override fun isValidRepairItem(stack: ItemStack, repairCandidate: ItemStack): Boolean = false

    private fun spawnSprayParticles(level: ServerLevel, pos: BlockPos, side: Direction) {
        // Облако смещено от центра блока в сторону игрока, то есть от опоры.
        val center = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(side.normal).scale(0.3))
        val random = level.random

        repeat(PARTICLE_COUNT) {
            val particle = if (random.nextBoolean()) graffitiType.paintParticle else graffitiType.accentParticle
            level.sendParticles(
                particle,
                center.x + (random.nextDouble() - 0.5) * 0.8,
                center.y + (random.nextDouble() - 0.5) * 0.8,
                center.z + (random.nextDouble() - 0.5) * 0.8,
                1,
                random.nextGaussian() * 0.1,
                random.nextGaussian() * 0.1,
                random.nextGaussian() * 0.1,
                0.2,
            )
        }
    }

    private companion object {
        const val MAX_USES = 16
        const val PARTICLE_COUNT = 30

        const val BAR_COLOR_FULL = 0x00FF00
        const val BAR_COLOR_HALF = 0xFFFF00
        const val BAR_COLOR_LOW = 0xFF0000
    }
}
