package net.natte.bankstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.natte.bankstorage.blockentity.BankDockBlockEntity;
import net.natte.bankstorage.container.BankItemStorage;
import net.natte.bankstorage.util.Util;

public class BankDockBlock extends Block implements BlockEntityProvider {

    public BankDockBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BankDockBlockEntity(blockPos, blockState);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        BlockEntity blockEntity = world.getBlockEntity(pos);
        // BankStorage.BANK_DOCK_BLOCK;
        if (blockEntity instanceof BankDockBlockEntity bankDockBlockEntity) {
            ItemStack stackInHand = player.getStackInHand(hand);
            if (bankDockBlockEntity.hasBank()) {

                // pick up bank from dock
                if (stackInHand.isEmpty() && player.isSneaking()) {
                    // player.getInventory().insertStack(bankDockBlockEntity.pickUpBank());
                    ItemStack bankInDock = bankDockBlockEntity.pickUpBank();
                    bankInDock.setBobbingAnimationTime(5);
                    player.setStackInHand(hand, bankInDock);

                    world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4f + 2.0f, false);
                    return ActionResult.SUCCESS;
                }

                // swap hand and dock
                if (Util.isBank(stackInHand)) {
                    player.setStackInHand(hand, ItemStack.EMPTY);
                    ItemStack bankInDock = bankDockBlockEntity.pickUpBank();
                    bankInDock.setBobbingAnimationTime(5);
                    player.setStackInHand(hand, bankInDock);

                    world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4f + 2.0f, false);

                    bankDockBlockEntity.putBank(stackInHand);
                    return ActionResult.SUCCESS;
                }

                // open bank screen
                if (!world.isClient) {
                    BankItemStorage bankItemStorage = Util.getBankItemStorage(bankDockBlockEntity.getBank(), world);
                    player.openHandledScreen(bankItemStorage);
                }

                return ActionResult.SUCCESS;
            } else {
                // place bank in dock
                if (Util.isBank(stackInHand)) {
                    bankDockBlockEntity.putBank(player.getStackInHand(hand));
                    player.setStackInHand(hand, ItemStack.EMPTY);
                    world.playSound(pos.getX() + 0.5f, pos.getY() +  0.5f, pos.getZ() + 0.5f, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, 0.0f, false);


                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BankDockBlockEntity bankDockBlockEntity) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), bankDockBlockEntity.getBank());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
