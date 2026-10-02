package io.github.mrbuggi.graffity.registry

import io.github.mrbuggi.graffity.Graffity
import io.github.mrbuggi.graffity.GraffitiType
import io.github.mrbuggi.graffity.block.GraffitiBlock
import io.github.mrbuggi.graffity.item.EraserItem
import io.github.mrbuggi.graffity.item.SprayItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(Graffity.MOD_ID)

    val SPRAY_SKULL: DeferredItem<SprayItem> =
        registerSpray("spray_skull", GraffitiType.SKULL, ModBlocks.GRAFFITY_SKULL, Rarity.UNCOMMON)
    val SPRAY_BOOM: DeferredItem<SprayItem> =
        registerSpray("spray_boom", GraffitiType.BOOM, ModBlocks.GRAFFITY_BOOM, Rarity.EPIC)
    val SPRAY_LOVE: DeferredItem<SprayItem> =
        registerSpray("spray_love", GraffitiType.LOVE, ModBlocks.GRAFFITY_LOVE, Rarity.UNCOMMON)
    val SPRAY_EYE: DeferredItem<SprayItem> =
        registerSpray("spray_eye", GraffitiType.EYE, ModBlocks.GRAFFITY_EYE, Rarity.UNCOMMON)

    val ERASER: DeferredItem<EraserItem> =
        ITEMS.registerItem("eraser", ::EraserItem, Item.Properties().stacksTo(1))

    private fun registerSpray(
        name: String,
        type: GraffitiType,
        block: DeferredBlock<GraffitiBlock>,
        rarity: Rarity,
    ): DeferredItem<SprayItem> = ITEMS.registerItem(
        name,
        { properties -> SprayItem(properties, type, block) },
        Item.Properties().rarity(rarity),
    )
}
