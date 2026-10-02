package io.github.mrbuggi.graffity.registry

import io.github.mrbuggi.graffity.Graffity
import io.github.mrbuggi.graffity.block.GraffitiBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(Graffity.MOD_ID)

    val GRAFFITY_SKULL: DeferredBlock<GraffitiBlock> = registerGraffiti("graffity_skull")
    val GRAFFITY_BOOM: DeferredBlock<GraffitiBlock> = registerGraffiti("graffity_boom")
    val GRAFFITY_EYE: DeferredBlock<GraffitiBlock> = registerGraffiti("graffity_eye")
    val GRAFFITY_LOVE: DeferredBlock<GraffitiBlock> = registerGraffiti("graffity_love")

    /** Все блоки граффити - для датагена. */
    val ALL: List<DeferredBlock<GraffitiBlock>> = listOf(GRAFFITY_SKULL, GRAFFITY_BOOM, GRAFFITY_EYE, GRAFFITY_LOVE)

    // Предмета-блока у граффити нет: рисунок появляется только из баллончика.
    private fun registerGraffiti(name: String): DeferredBlock<GraffitiBlock> = BLOCKS.registerBlock(
        name,
        ::GraffitiBlock,
        // Свойства воздуха: блок не мешает движению и свету и замещается при установке других блоков.
        // Прочность -1: в выживании граффити убирается только ластиком.
        BlockBehaviour.Properties.ofFullCopy(Blocks.AIR)
            .noCollission()
            .strength(-1.0f, 3600000.0f)
            .noLootTable()
            .noOcclusion()
            .lightLevel { 5 },
    )
}
