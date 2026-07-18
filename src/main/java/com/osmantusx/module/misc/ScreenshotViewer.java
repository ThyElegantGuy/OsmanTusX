package com.osmantusx.module.misc;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.util.Util;

import java.io.File;

/** Opens the screenshots folder in the system file browser when enabled. */
public final class ScreenshotViewer extends Module {

    public ScreenshotViewer() {
        super("Screenshot Viewer", "Opens the screenshots folder", Category.MISC);
    }

    @Override
    protected void onEnable() {
        File folder = new File(mc.runDirectory, "screenshots");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Util.getOperatingSystem().open(folder);
        OsmanTusX.NOTIFICATIONS.info("Opened screenshots folder");
    }
}
