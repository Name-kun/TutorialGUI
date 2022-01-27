package xyz.namekun.tutorialgui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TutorialGUICmd implements CommandExecutor, Listener, TabCompleter {

    TutorialGUI plugin = TutorialGUI.getPlugin(TutorialGUI.class);
    HashMap<Player, Integer> invIndex = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("tutorialgui")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cコンソールからは実行できません。");
                return true;
            }
            if (args.length == 0) {
                Player p = (Player) sender;
                invIndex.put(p, 0);
                p.openInventory(Inv(p));
            } else {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("tutorialgui.reload")) {
                        plugin.createFiles();
                        sender.sendMessage("§aリロードが完了しました。");
                    }
                }
            }
        }
        return true;
    }

    Inventory Inv(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, TutorialGUI.config.getString("name") + " | " + invIndex.get(p));
        //configのlist内にあるセクションの分だけループする
        for (int i = 0; i < TutorialGUI.config.getConfigurationSection("list." + invIndex.get(p)).getKeys(false).size(); i++) {
            ItemStack item = new ItemStack(Material.valueOf(TutorialGUI.config.getString("list." + invIndex.get(p) + "." + i + ".material")));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(TutorialGUI.config.getString("list." + invIndex.get(p) + "." + i + ".name"));
            meta.setLore(TutorialGUI.config.getStringList("list." + invIndex.get(p) + "." + i + ".lore"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        //次のページへ遷移するアレ
        ItemStack next = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("§a次のページ");
        next.setItemMeta(nextMeta);
        //前のページへ遷移するアレ
        ItemStack back = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§b前のページ");
        back.setItemMeta(backMeta);
        //ご飯にする？お風呂にする？それとも...セットする？
        if (invIndex.get(p) > 0) {
            inv.setItem(47, back);
        }
        if (invIndex.get(p) < TutorialGUI.config.getConfigurationSection("list").getKeys(false).size() - 1) {
            inv.setItem(51, next);
        }

        return inv;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        if (e.getView().getTitle().equalsIgnoreCase(TutorialGUI.config.getString("name") + " | " + invIndex.get(p))) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a次のページ")) {
                    invIndex.put(p, invIndex.get(p) + 1);
                    p.openInventory(Inv(p));
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§b前のページ")) {
                    invIndex.put(p, invIndex.get(p) - 1);
                    p.openInventory(Inv(p));
                }
                //クリックしたアイテムがコンフィグ内の項目と適合する場合にコマンドをfor文ぶん回して実行させる
                for (int i = 0; i < TutorialGUI.config.getConfigurationSection("list." + invIndex.get(p)).getKeys(false).size(); i++) {
                    if (e.getCurrentItem().getType().equals(Material.matchMaterial(TutorialGUI.config.getString("list." + invIndex.get(p) + "." + i + ".material"))) &&
                    e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(TutorialGUI.config.getString("list." + invIndex.get(p) + "." + i + ".name"))) {
                        for (String command : TutorialGUI.config.getStringList("list." + invIndex.get(p) + "." + i + ".commands")) {
                            String _command = command.replaceAll("%player%", p.getDisplayName());
                            p.performCommand(_command);
                        }
                        for (String bypassCommand : TutorialGUI.config.getStringList("list." + invIndex.get(p) + "." + i + ".bypassCommands")) {
                            String _bypassCommand = bypassCommand.replaceAll("%player%", p.getDisplayName());
                            Bukkit.dispatchCommand(console, _bypassCommand);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("tutorialgui")) {
            if (args.length == 1) {
                tab.add("reload");
            }
        }
        return tab;
    }
}
