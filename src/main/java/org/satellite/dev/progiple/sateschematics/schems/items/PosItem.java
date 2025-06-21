package org.satellite.dev.progiple.sateschematics.schems.items;

import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.satellite.dev.progiple.sateschematics.Config;

public class PosItem extends NonMenuItem {
    public PosItem() {
        super(Config.getSection("pos_item"));
        this.setAmount(1);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NBTManager.setBool(stack, "sateschems-item", true);
        return stack;
    }
}
