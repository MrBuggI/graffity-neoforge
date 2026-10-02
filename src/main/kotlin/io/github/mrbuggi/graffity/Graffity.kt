package io.github.mrbuggi.graffity

import io.github.mrbuggi.graffity.registry.ModBlocks
import io.github.mrbuggi.graffity.registry.ModCreativeTabs
import io.github.mrbuggi.graffity.registry.ModItems
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

/**
 * Точка входа. Kotlin for Forge создаёт `object` один раз при загрузке мода,
 * шина мода доступна как [MOD_BUS].
 */
@Mod(Graffity.MOD_ID)
object Graffity {
    const val MOD_ID = "graffity"

    init {
        ModBlocks.BLOCKS.register(MOD_BUS)
        ModItems.ITEMS.register(MOD_BUS)
        ModCreativeTabs.TABS.register(MOD_BUS)

    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
