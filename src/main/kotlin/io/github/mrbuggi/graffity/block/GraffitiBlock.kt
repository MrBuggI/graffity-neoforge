package io.github.mrbuggi.graffity.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

/**
 * Плоский рисунок на грани соседнего блока.
 *
 * Два свойства: [ATTACH_FACE] - стена, пол или потолок, и [FACING]. Для стены `FACING`
 * указывает на блок-опору, для пола и потолка задаёт поворот рисунка.
 */
class GraffitiBlock(properties: Properties) : Block(properties) {

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ATTACH_FACE, AttachFace.WALL)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, ATTACH_FACE)
    }

    /**
     * Состояние граффити, нанесённого на грань [clickedSide] блока-опоры.
     * Верхняя грань опоры - это пол для граффити, нижняя - потолок.
     */
    fun stateForSide(clickedSide: Direction, playerFacing: Direction): BlockState = when (clickedSide) {
        Direction.UP -> defaultBlockState()
            .setValue(ATTACH_FACE, AttachFace.FLOOR)
            .setValue(FACING, playerFacing.opposite)

        Direction.DOWN -> defaultBlockState()
            .setValue(ATTACH_FACE, AttachFace.CEILING)
            .setValue(FACING, playerFacing.opposite)

        else -> defaultBlockState()
            .setValue(ATTACH_FACE, AttachFace.WALL)
            .setValue(FACING, clickedSide.opposite)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape = when (state.getValue(ATTACH_FACE)) {
        AttachFace.FLOOR -> FLOOR_SHAPE
        AttachFace.CEILING -> CEILING_SHAPE
        else -> when (state.getValue(FACING)) {
            Direction.SOUTH -> WALL_SOUTH_SHAPE
            Direction.EAST -> WALL_EAST_SHAPE
            Direction.WEST -> WALL_WEST_SHAPE
            else -> WALL_NORTH_SHAPE
        }
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape = Shapes.empty()

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val toSupport = supportDirection(state)
        val supportPos = pos.relative(toSupport)
        // Опоре нужна сплошная грань со стороны граффити.
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, toSupport.opposite)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos,
    ): BlockState {
        // Опора исчезла или перестала быть сплошной - граффити пропадает вместе с ней.
        if (direction == supportDirection(state) && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState()
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1.0f

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = true

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING

        /** Ванильное свойство `face` со значениями floor, wall, ceiling - то же, что у кнопок и рычагов. */
        val ATTACH_FACE: EnumProperty<AttachFace> = BlockStateProperties.ATTACH_FACE

        private const val THICKNESS = 1.0 / 16
        private const val FLAT_THICKNESS = 0.048 / 16

        private val WALL_NORTH_SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, THICKNESS)
        private val WALL_SOUTH_SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 1 - THICKNESS, 1.0, 1.0, 1.0)
        private val WALL_EAST_SHAPE: VoxelShape = Shapes.box(1 - THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0)
        private val WALL_WEST_SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, THICKNESS, 1.0, 1.0)

        private val FLOOR_SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, FLAT_THICKNESS, 1.0)
        private val CEILING_SHAPE: VoxelShape = Shapes.box(0.0, 1 - FLAT_THICKNESS, 0.0, 1.0, 1.0, 1.0)

        /** Направление от граффити к блоку, на котором оно держится. */
        private fun supportDirection(state: BlockState): Direction = when (state.getValue(ATTACH_FACE)) {
            AttachFace.FLOOR -> Direction.DOWN
            AttachFace.CEILING -> Direction.UP
            else -> state.getValue(FACING)
        }
    }
}
