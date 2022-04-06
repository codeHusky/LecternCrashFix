package lol.hyper.lecterncrashfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lol.hyper.lecterncrashfix.wrapper.WrapperPlayClientWindowClick;
import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;

@Plugin(name = "LecternCrashFix", apiVersion = "1.13", hardDepends = {"ProtocolLib"}, version = "1.0")
public final class LecternCrashFix extends ExtendedJavaPlugin {

    private final Logger logger = this.getLogger();
    public static LecternCrashFix INSTANCE = null;

    @Override
    protected void enable() {

        INSTANCE = this;
        try{
            InventoryType.valueOf("LECTERN");
        }catch (IllegalArgumentException e){
            logger.severe("LecternCrashFix will not load due to InventoryType.LECTERN not being present!");
            return;
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer() == null) {
                    return;
                }

                WrapperPlayClientWindowClick packet = new WrapperPlayClientWindowClick(event.getPacket());
                Player player = event.getPlayer();
                InventoryView inv = player.getOpenInventory();
                if (inv.getType() == InventoryType.LECTERN) {
                    if (packet.getShift() == WrapperPlayClientWindowClick.InventoryClickType.QUICK_MOVE) {
                        event.setCancelled(true);
                        logger.warning(player.getName() + " tried to illegally click a slot in a lectern! Location: " + player.getLocation());
                    }
                }
            }
        });
    }

    @Override
    protected void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }
}
