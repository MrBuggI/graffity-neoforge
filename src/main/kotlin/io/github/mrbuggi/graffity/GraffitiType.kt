package io.github.mrbuggi.graffity

import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import org.joml.Vector3f

/**
 * Вид граффити: цвет краски, дополнительные частицы и высота звука распыления.
 *
 * @property accentParticle частицы, характерные для рисунка: дым, пламя, пузыри, сердца
 * @property soundPitch высота звука баллончика
 */
enum class GraffitiType(
    rgb: Int,
    val accentParticle: ParticleOptions,
    val soundPitch: Float,
) {
    SKULL(0x998989, ParticleTypes.SMOKE, 1.2f),
    BOOM(0xF8440E, ParticleTypes.FLAME, 1.2f),
    EYE(0x64C8FF, ParticleTypes.BUBBLE, 1.8f),
    LOVE(0xFF69B4, ParticleTypes.HEART, 1.5f);

    /** Облако краски цвета граффити. */
    val paintParticle: ParticleOptions = DustParticleOptions(
        Vector3f(
            (rgb shr 16 and 0xFF) / 255f,
            (rgb shr 8 and 0xFF) / 255f,
            (rgb and 0xFF) / 255f,
        ),
        1.0f,
    )
}
