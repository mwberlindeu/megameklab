/*
 * MegaMekLab - Copyright (C) 2017 - The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megameklab.com.ui.listeners;

import megamek.common.Engine;

/**
 * Listener for views used by aerospace units.
 * 
 * @author Neoancient
 *
 */
public interface AeroBuildListener extends BuildListener {

    void tonnageChanged(double tonnage);
    void omniChanged(boolean omni);
    void vstolChanged(boolean vstol);
    void fighterTypeChanged(int type);
    void engineChanged(Engine engine);
    void cockpitChanged(int cockpitType);
    void resetChassis();

}
