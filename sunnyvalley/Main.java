package sunnyvalley;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static String version = "alpha";

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        ASCIIOutput.printTitle();
        SunnyValley.onEnable(this);
    }
}
