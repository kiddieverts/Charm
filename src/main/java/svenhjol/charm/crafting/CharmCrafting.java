package svenhjol.charm.crafting;

import svenhjol.charm.automation.feature.RedstoneSand;
import svenhjol.charm.crafting.feature.*;
import svenhjol.meson.Module;

public class CharmCrafting extends Module
{
    public CharmCrafting()
    {
        features.add(new Barrel());
        features.add(new BookshelfChest());
        features.add(new Composter());
        features.add(new Crate());
        features.add(new EnderPearlBlock());
        features.add(new Lantern());
        features.add(new RottenFleshBlock());
        features.add(new SuspiciousSoup());
    }
}