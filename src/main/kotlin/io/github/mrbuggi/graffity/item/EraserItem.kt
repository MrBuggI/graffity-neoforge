package io.github.mrbuggi.graffity.item

import io.github.mrbuggi.graffity.block.GraffitiBlock
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext

/** Ластик: стирает граффити, на которое смотрит игрок. Не расходуется. */
class EraserItem(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        if (level.getBlockState(pos).block !is GraffitiBlock) {
            return InteractionResult.PASS
        }

        if (level is ServerLevel) {
            level.sendParticles(
                ParticleTypes.CLOUD,
                pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                15, 0.3, 0.3, 0.3, 0.05,
            )
            level.playSound(null, pos, SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 1.0f, 0.5f)
            level.removeBlock(pos, false)
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }
}
