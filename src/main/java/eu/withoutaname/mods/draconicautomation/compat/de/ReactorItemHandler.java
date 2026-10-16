package eu.withoutaname.mods.draconicautomation.compat.de;

import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore.ReactorState;
import com.brandon3055.draconicevolution.init.DEContent;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class ReactorItemHandler implements IItemHandler {
  private static final int MAX_FUEL = 10368 + 15;
  private static final int[] VALUES = {1296, 144, 16};

  private final Supplier<TileReactorCore> core;

  public ReactorItemHandler(Supplier<TileReactorCore> core) {
    this.core = core;
  }

  private TileReactorCore accessibeCore() {
    TileReactorCore c = core.get();
    return c != null && c.reactorState.get() == ReactorState.COLD ? c : null;
  }

  // slot 0-2: draconic block/ingot/nugget, slot 3-5: large/medium/small chaos shard
  @Override
  public int getSlots() {
    return accessibeCore() != null ? 6 : 0;
  }

  private static Item fuelItem(int idx) {
    return switch (idx) {
      case 0 -> DEContent.ITEM_AWAKENED_DRACONIUM_BLOCK.get();
      case 1 -> DEContent.INGOT_DRACONIUM_AWAKENED.get();
      case 2 -> DEContent.NUGGET_DRACONIUM_AWAKENED.get();
      default -> null;
    };
  }

  private static Item chaosItem(int idx) {
    return switch (idx) {
      case 0 -> DEContent.CHAOS_FRAG_LARGE.get();
      case 1 -> DEContent.CHAOS_FRAG_MEDIUM.get();
      case 2 -> DEContent.CHAOS_FRAG_SMALL.get();
      default -> null;
    };
  }

  private static int getAmount(double raw, int idx) {
    int value = (int) Math.floor(raw);
    return value / VALUES[idx];
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    TileReactorCore c = accessibeCore();
    if (c == null) return ItemStack.EMPTY;

    boolean isFuel = slot < 3;
    int idx = slot % 3;

    double raw = isFuel ? c.reactableFuel.get() : c.convertedFuel.get();
    int count = getAmount(raw, idx);
    if (count <= 0) return ItemStack.EMPTY;

    Item item = isFuel ? fuelItem(idx) : chaosItem(idx);
    return new ItemStack(item, count);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    TileReactorCore c = accessibeCore();
    if (c == null || stack.isEmpty()) return stack;

    boolean isFuel = slot < 3;
    int idx = slot % 3;
    int value = VALUES[idx];

    Item item = isFuel ? fuelItem(idx) : chaosItem(idx);
    if (!stack.is(item)) return stack;

    double free = MAX_FUEL - (c.reactableFuel.get() + c.convertedFuel.get());
    int maxInsertion = getAmount(free, idx);
    int inserted = Math.min(maxInsertion, stack.getCount());
    if (inserted <= 0) return stack;

    if (!simulate) {
      if (isFuel) {
        c.reactableFuel.add(inserted * value);
      } else {
        c.convertedFuel.add(inserted * value);
      }
      c.setChanged();
    }

    return stack.copyWithCount(stack.getCount() - inserted);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    TileReactorCore c = accessibeCore();
    if (c == null) return ItemStack.EMPTY;

    ItemStack available = getStackInSlot(slot);

    boolean isFuel = slot < 3;
    int idx = slot % 3;
    int value = VALUES[idx];

    int extracted = Math.min(amount, available.getCount());
    if (extracted <= 0) return ItemStack.EMPTY;

    if (!simulate) {
      if (isFuel) {
        c.reactableFuel.subtract(extracted * value);
      } else {
        c.convertedFuel.subtract(extracted * value);
      }
      c.setChanged();
    }

    return available.copyWithCount(extracted);
  }

  @Override
  public int getSlotLimit(int slot) {
    int idx = slot % 3;
    return getAmount(MAX_FUEL, idx);
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    boolean isFuel = slot < 3;
    int idx = slot % 3;

    Item item = isFuel ? fuelItem(idx) : chaosItem(idx);
    return stack.is(item);
  }
}
