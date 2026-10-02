package io.github.mrbuggi.graffity.datagen

import io.github.mrbuggi.graffity.Graffity
import io.github.mrbuggi.graffity.block.GraffitiBlock
import io.github.mrbuggi.graffity.registry.ModBlocks
import io.github.mrbuggi.graffity.registry.ModCreativeTabs
import io.github.mrbuggi.graffity.registry.ModItems
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.state.properties.AttachFace
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

/**
 * Датаген: `./gradlew runData` пишет блокстейты и переводы в `src/generated/resources`.
 * Модели нарисованы в Blockbench и лежат в `src/main/resources`, датаген на них только ссылается.
 */
object ModDataGenerators {

    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput

        generator.addProvider(event.includeClient(), GraffitiBlockStates(output, event.existingFileHelper))
        generator.addProvider(event.includeClient(), EnglishLanguage(output))
        generator.addProvider(event.includeClient(), RussianLanguage(output))
    }
}

/** Двенадцать вариантов на блок: три вида крепления на четыре стороны. */
private class GraffitiBlockStates(output: PackOutput, fileHelper: ExistingFileHelper) :
    BlockStateProvider(output, Graffity.MOD_ID, fileHelper) {

    override fun registerStatesAndModels() {
        for (block in ModBlocks.ALL) {
            val name = block.id.path
            val wall = models().getExistingFile(modLoc("block/$name"))
            val floor = models().getExistingFile(modLoc("block/${name}_floor"))
            val ceiling = models().getExistingFile(modLoc("block/${name}_ceiling"))

            getVariantBuilder(block.get()).forAllStates { state ->
                val model = when (state.getValue(GraffitiBlock.ATTACH_FACE)) {
                    AttachFace.FLOOR -> floor
                    AttachFace.CEILING -> ceiling
                    else -> wall
                }
                ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(rotationY(state.getValue(GraffitiBlock.FACING)))
                    .build()
            }
        }
    }

    // Модели смотрят на север; остальные стороны получаются поворотом вокруг вертикали.
    private fun rotationY(facing: Direction): Int = when (facing) {
        Direction.EAST -> 90
        Direction.SOUTH -> 180
        Direction.WEST -> 270
        else -> 0
    }
}

private class EnglishLanguage(output: PackOutput) : LanguageProvider(output, Graffity.MOD_ID, "en_us") {
    override fun addTranslations() {
        addItem(ModItems.ERASER, "Eraser")
        addItem(ModItems.SPRAY_SKULL, "Skull Spray")
        addItem(ModItems.SPRAY_BOOM, "Boom Spray")
        addItem(ModItems.SPRAY_EYE, "Eye Spray")
        addItem(ModItems.SPRAY_LOVE, "Love Spray")

        addBlock(ModBlocks.GRAFFITY_SKULL, "Skull Graffiti")
        addBlock(ModBlocks.GRAFFITY_BOOM, "Boom Graffiti")
        addBlock(ModBlocks.GRAFFITY_EYE, "Eye Graffiti")
        addBlock(ModBlocks.GRAFFITY_LOVE, "Love Graffiti")

        add(ModCreativeTabs.GENERAL_TITLE, "Graffity+")
    }
}

private class RussianLanguage(output: PackOutput) : LanguageProvider(output, Graffity.MOD_ID, "ru_ru") {
    override fun addTranslations() {
        addItem(ModItems.ERASER, "Ластик")
        addItem(ModItems.SPRAY_SKULL, "Баллончик «Череп»")
        addItem(ModItems.SPRAY_BOOM, "Баллончик «Взрыв»")
        addItem(ModItems.SPRAY_EYE, "Баллончик «Глаза»")
        addItem(ModItems.SPRAY_LOVE, "Баллончик «Сердце»")

        addBlock(ModBlocks.GRAFFITY_SKULL, "Граффити «Череп»")
        addBlock(ModBlocks.GRAFFITY_BOOM, "Граффити «Взрыв»")
        addBlock(ModBlocks.GRAFFITY_EYE, "Граффити «Глаза»")
        addBlock(ModBlocks.GRAFFITY_LOVE, "Граффити «Сердце»")

        add(ModCreativeTabs.GENERAL_TITLE, "Graffity+")
    }
}
