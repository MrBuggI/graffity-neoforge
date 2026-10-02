# Graffity+ для NeoForge

[![Build](https://github.com/MrBuggI/graffity-neoforge/actions/workflows/build.yml/badge.svg)](https://github.com/MrBuggI/graffity-neoforge/actions/workflows/build.yml)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![Loader](https://img.shields.io/badge/loader-NeoForge-F08A2A)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

Система граффити: баллончики для стен, пола и потолка и ластик. Порт мода
[Graffity+](https://github.com/MrBuggI/graffity) с Fabric 1.20.1 на NeoForge 1.21.1,
переписанный с Java на Kotlin.

> **English:** a NeoForge 1.21.1 port of Graffity+ written in Kotlin. Spray cans (skull, boom, love, eye) paint graffiti on walls, floors and ceilings; an eraser removes it. Graffiti hangs on its support block and disappears with it. No mixins, no custom packets; blockstates and translations are generated.

## Возможности

- **Четыре спрея**: череп, взрыв, сердце, глаз. Наведи на поверхность, и появится граффити с частицами и звуком распыления.
- Граффити крепится на **стены, пол и потолок** и ориентируется по направлению взгляда игрока.
- Граффити держится на блоке, на который нанесено: если сломать опору, рисунок исчезает.
- Поверх старого граффити можно нанести новое.
- **Ластик** убирает граффити и не расходуется.
- У каждого спрея **16 использований** и цветной индикатор заряда.
- Отдельная вкладка креатива **Graffity+**, переводы на английский и русский.

Поведение то же, что в версии для Fabric: это перенос, а не новая версия мода.

## Установка

1. Установите [NeoForge](https://neoforged.net/) 21.1.x для Minecraft 1.21.1.
2. Положите в папку `mods/` этот мод и [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge) 5.12.0 или новее.

## Что изменилось при переносе

| Что | Fabric 1.20.1, Java | NeoForge 1.21.1, Kotlin |
|---|---|---|
| Имена классов игры | Yarn (`ItemUsageContext`, `BlockView`) | официальные имена Mojang (`UseOnContext`, `BlockGetter`) |
| Точка входа | класс с `ModInitializer` | `object` с `@Mod`, загрузчик языка Kotlin for Forge |
| Регистрация | `Registry.register` в статических полях | `DeferredRegister` на шине мода |
| Свойство крепления | свой enum `AttachFace` | ванильные `AttachFace` и `BlockStateProperties.ATTACH_FACE` |
| Заряды баллончика | `maxDamage` и `stack.damage(...)` | `durability` и `hurtAndBreak` со слотом руки; заряд хранится в компоненте `DAMAGE` |
| Прозрачность текстур | `BlockRenderLayerMap` в клиентском коде | `render_type` в файле модели, клиентского кода нет |
| Блокстейты и переводы | JSON руками | датаген |
| Предметы-блоки граффити | были зарегистрированы без моделей | убраны: рисунок появляется только из баллончика |

## Архитектура

Пакет `io.github.mrbuggi.graffity`. Миксинов нет, своих сетевых пакетов нет, клиентского кода нет.

| Файл | Роль |
|---|---|
| `Graffity` | точка входа: регистры на шине мода, подписка на датаген |
| `GraffitiType` | цвет краски, частицы и высота звука для каждого рисунка |
| `block/GraffitiBlock` | плоский блок: крепление, форма, проверка опоры |
| `item/SprayItem` | баллончик: проверяет место, ставит блок, тратит заряд |
| `item/EraserItem` | ластик |
| `registry/*` | `DeferredRegister` блоков, предметов и вкладки креатива |
| `datagen/ModDataGenerators` | блокстейты (12 вариантов на блок) и переводы |

### Как граффити понимает, на чём оно держится

Всё, что связано с креплением, живёт в `GraffitiBlock`:

- `stateForSide` переводит грань, по которой кликнул игрок, в состояние блока. Верхняя грань опоры - это пол для граффити, нижняя - потолок, боковая - стена.
- `supportDirection` возвращает направление к блоку-опоре: вниз для пола, вверх для потолка, `FACING` для стены.
- `canSurvive` требует, чтобы у опоры была сплошная грань (`isFaceSturdy`), а `updateShape` убирает граффити, когда опора исчезла.

Блок граффити наследует свойства воздуха: не мешает движению и свету и замещается при установке других блоков. Прочность `-1`, поэтому в выживании рисунок убирается только ластиком.

### Ресурсы

Модели баллончиков и граффити нарисованы в Blockbench и лежат в `src/main/resources`. Блокстейты и переводы генерируются:

```bash
./gradlew runData
```

Результат лежит в `src/generated/resources` и хранится в репозитории, для обычной сборки запускать датаген не нужно.

## Сборка из исходников

Нужен JDK 21; если его нет, Gradle скачает сам.

```bash
./gradlew build       # готовый .jar появится в build/libs/
./gradlew runClient   # запустить клиент Minecraft с модом
```

## Лицензия

[MIT](LICENSE).
