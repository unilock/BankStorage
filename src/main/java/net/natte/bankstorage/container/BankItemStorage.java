package net.natte.bankstorage.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.natte.bankstorage.BankStorage;
import net.natte.bankstorage.BankType;
import net.natte.bankstorage.screen.BankScreenHandler;

public class BankItemStorage extends SimpleInventory implements NamedScreenHandlerFactory {

    public DefaultedList<ItemStack> inventory;

    public int rows;
    public int cols;

    private BankType type;

    private Text displayName;

    public BankItemStorage(BankType type) {
        this.type = type;
        this.rows = this.type.rows;
        this.cols = this.type.cols;
        this.inventory = DefaultedList.ofSize(this.rows * this.cols, ItemStack.EMPTY);

    }

    public BankItemStorage withDisplayName(Text displayName){
        this.displayName = displayName;
        return this;
    }

    @Override
    public BankScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        // return GenericContainerScreenHandler.createGeneric9x1(syncId,
        // playerInventory, (Inventory) this);
        return new BankScreenHandler(syncId, playerInventory, this, this.type);
    }

    @Override
    public Text getDisplayName() {
        return displayName;
    }

    @Override
    public void clear() {
        this.inventory.clear();
        this.inventory = DefaultedList.ofSize(this.rows * this.cols, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return this.type.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty())
                continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.inventory.get(slot).split(amount);

    }

    @Override
    public int getMaxCountPerStack() {
        return super.getMaxCountPerStack();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return super.isValid(slot, stack);
    }

    @Override
    public ItemStack removeStack(int slot) {

        ItemStack itemStack = this.inventory.get(slot);
        this.inventory.set(slot, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack itemStack) {
        this.inventory.set(slot, itemStack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity playerEntity) {
        return true;
    }

    public NbtCompound saveToNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("type", this.type.getName());
        Inventories.writeNbt(nbtCompound, this.inventory);
        return nbtCompound;
    }

    public static BankItemStorage createFromNbt(NbtCompound nbtCompound) {
        System.out.println("fromNBT");
        BankItemStorage bankItemStorage = new BankItemStorage( BankStorage.getBankTypeFromName(nbtCompound.getString("type")));
        Inventories.readNbt(nbtCompound, bankItemStorage.inventory);
        System.out.println("fromNBT done");
        return bankItemStorage;
    }


    @Override
    public boolean canInsert(ItemStack stack) {
        return super.canInsert(stack);
    }
    @Override
    public ItemStack addStack(ItemStack stack) {
        return super.addStack(stack);
    }
}
