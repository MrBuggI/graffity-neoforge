package io.github.mrbuggi.graffity.registry

import io.github.mrbuggi.graffity.Graffity
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModCreativeTabs {
    /** Ключ перевода названия вкладки; сам перевод задаёт датаген. */
    const val GENERAL_TITLE = "itemGroup.graffity.general"

    val TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Graffity.MOD_ID)

    val GENERAL: DeferredHolder<CreativeModeTab, CreativeModeTab> = TABS.register("general", Supplier {
        CreativeModeTab.builder()
            .title(Component.translatable(GENERAL_TITLE))
            .icon { ItemStack(ModItems.SPRAY_SKULL.get()) }
            .displayItems { _, output ->
                output.accept(ModItems.ERASER)
                output.accept(ModItems.SPRAY_SKULL)
                output.accept(ModItems.SPRAY_BOOM)
                output.accept(ModItems.SPRAY_LOVE)
                output.accept(ModItems.SPRAY_EYE)
            }
            .build()
    })
}
