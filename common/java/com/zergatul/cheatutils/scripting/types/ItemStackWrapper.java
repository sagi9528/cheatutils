package com.zergatul.cheatutils.scripting.types;

import com.zergatul.scripting.Getter;
import com.zergatul.scripting.Lazy;
import com.zergatul.scripting.type.CustomType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

@SuppressWarnings("unused")
@CustomType(name = "ItemStack")
public class ItemStackWrapper {

    private final ItemStack inner;
    private final Lazy<EnchantmentWrapper[]> enchantments;
    private final Lazy<String[]> tooltip;

    public ItemStackWrapper(ItemStack inner) {
        this.inner = inner;
        this.enchantments = new Lazy<>(this::getEnchantmentsInternal);
        this.tooltip = new Lazy<>(this::getTooltipInternal);
    }

    @Getter(name = "item")
    public ItemWrapper getItem() {
        return new ItemWrapper(inner.getItem());
    }

    @Getter(name = "count")
    public int getCount() {
        return inner.getCount();
    }

    @Getter(name = "enchantments")
    public EnchantmentWrapper[] getEnchantments() {
        return enchantments.value();
    }

    @Getter(name = "isDamageable")
    public boolean getIsDamageable() {
        return inner.isDamageableItem();
    }

    @Getter(name = "durability")
    public int getDurability() {
        return inner.getMaxDamage() - inner.getDamageValue();
    }

    @Getter(name = "maxDurability")
    public int getMaxDurability() {
        return inner.getMaxDamage();
    }

    @Getter(name = "isEmpty")
    public boolean getIsEmpty() {
        return inner.isEmpty();
    }

    @Getter(name = "tooltip")
    public String[] getTooltip() {
        return tooltip.value();
    }

    private EnchantmentWrapper[] getEnchantmentsInternal() {
        ItemEnchantments enchantments;
        if (inner.is(Items.ENCHANTED_BOOK)) {
            enchantments = inner.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        } else {
            enchantments = inner.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }
        return EnchantmentWrapper.of(enchantments);
    }

    private String[] getTooltipInternal() {
        List<Component> components = Screen.getTooltipFromItem(Minecraft.getInstance(), inner);
        String[] result = new String[components.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = components.get(i).getString();
        }
        return result;
    }
}