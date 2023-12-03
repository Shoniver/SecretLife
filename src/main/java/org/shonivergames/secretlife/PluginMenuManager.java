package org.shonivergames.secretlife;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.shonivergames.secretlife.admincommands._CommandBase;
import org.shonivergames.secretlife.config_readers.SettingReader;
import org.shonivergames.secretlife.config_readers.SpecialItemReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginMenuManager {
    private static final String baseConfigPath = "plugin_menu_manager";
    private static final int menuSize = 5 * 9;

    public static void showMenu(Player player){
        Inventory inv = createBasicInv(player, "main");
        for (int i = 13; i < menuSize - 9; i+=9)
            inv.setItem(i, SpecialItemReader.get(baseConfigPath, "separator"));
        inv.setItem(2, changeItemLore(SpecialItemReader.get(baseConfigPath, "info"), "§bServer Options"));
        inv.setItem(6, changeItemLore(SpecialItemReader.get(baseConfigPath, "info"), "§bPlayer Options"));

        List<ItemStack> serverOptions = SpecialItemReader.getAll(baseConfigPath, "commands.server");
        int i = 10;
        for (ItemStack option : serverOptions) {
            inv.setItem(i, option);
            i++;
            if(i % 9 == 4) // got to the border
                i = (i/9 + 1) * 9 + 1; // go to the start of the next line
        }
        List<ItemStack> playerOptions = SpecialItemReader.getAll(baseConfigPath, "commands.player");
        i = 14;
        for (ItemStack option : playerOptions) {
            inv.setItem(i, option);
            i++;
            if((i + 1) % 9 == 0) // got to the end of the line
                i += 6; // Jumps to the next line, and then skip 5 slots, putting us after the border
        }
    }

    public static void showOnlinePlayersMenu(Player player, _CommandBase command){
        Inventory inv = createBasicInv(player, "player_select");
        inv.setItem(4, changeItemLore(SpecialItemReader.get(baseConfigPath, "info"),
                SettingReader.getString(baseConfigPath, "current_command_prefix"), command.name));

        fillOnlinePlayersPage(inv, 1);
    }

    public static void showWarningMenu(Player player, _CommandBase command){
        Inventory inv = createBasicInv(player, "warning");
        inv.setItem(4, changeItemLore(SpecialItemReader.get(baseConfigPath, "info"),
                SettingReader.getString(baseConfigPath, "current_command_prefix"), command.name));

        int middleRowStart = menuSize / 2 - ((menuSize / 2) % 9);
        inv.setItem(middleRowStart + 2, changeItemLore(SpecialItemReader.get(baseConfigPath, "proceed"), command.name));
        inv.setItem(middleRowStart + 6, SpecialItemReader.get(baseConfigPath, "go_back"));
    }

    public static void handleInventoryInteractionEvent(InventoryClickEvent event){
        InventoryView invView = event.getView();
        String mainMenuName = SettingReader.getString(baseConfigPath, "menu_names.main");
        String playerSelectMenuName = SettingReader.getString(baseConfigPath, "menu_names.player_select");
        String warningMenuName = SettingReader.getString(baseConfigPath, "menu_names.warning");
        // We should only do something if it is our menu
        if(!invView.getTitle().equals(mainMenuName) && !invView.getTitle().equals(playerSelectMenuName)
                && !invView.getTitle().equals(warningMenuName))
            return;

        // Cancel the event either way, even if the clicked inventory wasn't the top inventory -
        // that way, the player can't move items in the inventory while in this menu.
        event.setCancelled(true);

        Player player = (Player)event.getWhoClicked();
        Inventory inv = invView.getTopInventory();
        ItemStack item = event.getCurrentItem();
        if(event.getClickedInventory() == inv) {
            int slot = event.getSlot();
            if (slot == 0) {
                if(invView.getTitle().equals(mainMenuName))
                    invView.close();
                else
                    showMenu(player);
            }
            else if(item != null){
                Material itemType = item.getType();

                if (invView.getTitle().equals(playerSelectMenuName)) {
                    if(itemType == Material.getMaterial(SettingReader.getString(baseConfigPath, "change_page_buttons_material"))){
                        if (slot == menuSize - 9)
                            showPrevOnlinePlayersPage(inv);
                        else if (slot == menuSize - 1)
                            showNextOnlinePlayersPage(inv);
                    }
                    else if(itemType == Material.PLAYER_HEAD) {
                        _CommandBase command = Main.adminCommandsManager.getCommand(extractCommandName(inv.getItem(4)));
                        Main.adminCommandsManager.executeCommand(command, player, ((SkullMeta) item.getItemMeta()).getOwningPlayer().getName(), false);
                        // Refresh the menu
                        refreshOnlinePlayersPage(inv);
                    }
                }
                else{
                    _CommandBase command = Main.adminCommandsManager.getCommand(extractCommandName(item));
                    if(command != null) {
                        if(command.isPerPlayer)
                            showOnlinePlayersMenu(player, command);
                        else
                            Main.adminCommandsManager.executeCommand(command, player, null,
                                    invView.getTitle().equals(warningMenuName));
                    }
                }
            }
        }
    }

    private static void showNextOnlinePlayersPage(Inventory inv){
        fillOnlinePlayersPage(inv, extractPageNumber(inv) + 1);
    }

    private static void showPrevOnlinePlayersPage(Inventory inv){
        fillOnlinePlayersPage(inv, extractPageNumber(inv) - 1);
    }

    private static void refreshOnlinePlayersPage(Inventory inv){
        fillOnlinePlayersPage(inv, extractPageNumber(inv));
    }

    private static void fillOnlinePlayersPage(Inventory inv, int pageIndex){
        List<String> currentCommandLore = inv.getItem(4).getItemMeta().getLore();
        inv.clear();
        fillInventoryWithBasicItems(inv);
        // Set up the inventory's buttons
        inv.setItem(menuSize - 5, changeItemLore(SpecialItemReader.get(baseConfigPath, "info"),
                SettingReader.getString(baseConfigPath, "current_page_prefix"), String.valueOf(pageIndex)));
        if(pageIndex > 1)
            inv.setItem(menuSize - 9, SpecialItemReader.get(baseConfigPath, "prev_page"));
        inv.setItem(menuSize - 1, SpecialItemReader.get(baseConfigPath, "next_page"));

        // Fill all available slots with player heads
        List<Player> players = (List<Player>) Main.server.getOnlinePlayers();
        int firstPlayerOnPageIndex = (pageIndex - 1) * (menuSize - (2 * 9));
        for (int i = 10, playerIndex = firstPlayerOnPageIndex; playerIndex < players.size() && i < menuSize - 9; playerIndex++) {
            inv.setItem(i, getPlayerHeadItem(players.get(playerIndex)));
            i++;
            if((i + 1) % 9 == 0) // if we're at the last col, which should be frames
                i += 2;
        }
        inv.setItem(4, changeItemLore(SpecialItemReader.get(baseConfigPath, "info"), currentCommandLore));
    }

    private static Inventory createBasicInv(Player player, String nameConfigVar){
        String invName = SettingReader.getString(baseConfigPath, "menu_names." + nameConfigVar);
        Inventory inv = Bukkit.createInventory(player, menuSize, invName);
        fillInventoryWithBasicItems(inv);
        player.openInventory(inv);
        return inv;
    }

    private static void fillInventoryWithBasicItems(Inventory inv){
        inv.setItem(0, SpecialItemReader.get(baseConfigPath, "cancel"));
        for (int i = 1; i < menuSize; i++) {
            if(i % 9 == 0 || i < 9 || (i+1) % 9 == 0 || i >= menuSize - 9)
                inv.setItem(i, SpecialItemReader.get(baseConfigPath, "frame"));
        }
    }

    private static ItemStack getPlayerHeadItem(Player player){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setDisplayName(LivesManager.getColoredPlayerName(player));
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack changeItemLore(ItemStack item, String... lore){
        List<String> loreList = new ArrayList<>(Arrays.asList(lore));
        return changeItemLore(item, loreList);
    }
    private static ItemStack changeItemLore(ItemStack item, List<String> lore){
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Assumes the command is in the last line of the lore
    private static String extractCommandName(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return null;
        List<String> lore = item.getItemMeta().getLore();
        if(lore == null || lore.isEmpty())
            return null;
        return lore.get(lore.size() - 1);
    }

    private static int extractPageNumber(Inventory inv) {
        // currently, the page number is stored the same way as commands do, so I can just reuse the same function
        return Integer.parseInt(extractCommandName(inv.getItem(menuSize - 5)));
    }
}
