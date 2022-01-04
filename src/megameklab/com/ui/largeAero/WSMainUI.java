/*
 * MegaMekLab - Copyright (C) 2018 - The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package megameklab.com.ui.largeAero;

<<<<<<< HEAD:src/megameklab/com/ui/aerospace/AdvancedAeroUI.java
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import megamek.common.Aero;
import megamek.common.Entity;
import megamek.common.EquipmentType;
import megamek.common.ITechManager;
import megamek.common.Jumpship;
import megamek.common.MechSummaryCache;
import megamek.common.SimpleTechLevel;
import megamek.common.SpaceStation;
import megamek.common.TechConstants;
import megamek.common.Warship;
import megamek.common.logging.LogLevel;
=======
import megamek.common.*;
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/WSMainUI.java
import megamek.common.verifier.TestAdvancedAerospace;
import megameklab.com.ui.MegaMekLabMainUI;
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/AdvancedAeroUI.java
import megameklab.com.ui.Aero.tabs.EquipmentTab;
import megameklab.com.ui.Aero.tabs.PreviewTab;
import megameklab.com.util.MenuBarCreator;
=======
import megameklab.com.ui.generalUnit.AeroEquipmentTab;
import megameklab.com.ui.generalUnit.FluffTab;
import megameklab.com.ui.generalUnit.PreviewTab;
import megameklab.com.ui.generalUnit.TransportTab;
import megameklab.com.ui.util.TabScrollPane;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/WSMainUI.java

/**
 * MainUI for JumpShips, WarShips, and Space Stations
 * 
 * @author Neoancient
 */
public class WSMainUI extends MegaMekLabMainUI {
    private static final long serialVersionUID = -91028543221939757L;
    
    JTabbedPane configPane = new JTabbedPane(SwingConstants.TOP);
    private WSStructureTab structureTab;
    private AeroEquipmentTab equipmentTab;
    private PreviewTab previewTab;
    private LABuildTab buildTab;
    private TransportTab transportTab;
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/AdvancedAeroUI.java
    private AdvancedAeroStatusBar statusbar;
    JPanel masterPanel = new JPanel();
    JScrollPane scroll = new JScrollPane();
    private MenuBarCreator menubarcreator;
=======
    private FluffTab fluffTab;
    private WSStatusBar statusbar;
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/WSMainUI.java
    
    public WSMainUI(boolean primitive) {
        super();
        if (!primitive) {
            createNewUnit(Entity.ETYPE_JUMPSHIP, false, false);
        } else {
            createNewUnit(Entity.ETYPE_WARSHIP, true, false);
        }
        setTitle(getEntity().getChassis() + " " + getEntity().getModel() + ".blk");
        finishSetup();
        MechSummaryCache.getInstance();
    }

    @Override
    public void refreshSummary() {
        structureTab.refreshSummary();
    }

    @Override
    public void refreshEquipmentTable() {
        equipmentTab.refreshTable();
    }
    
    @Override
    public void refreshTransport() {
        transportTab.refresh();
    }

    @Override
    public void createNewUnit(long entitytype, boolean isPrimitive, boolean isIndustrial, Entity oldUnit) {
        if (entitytype == Entity.ETYPE_JUMPSHIP) {
            setEntity(new Jumpship());
        } else if (entitytype == Entity.ETYPE_WARSHIP) {
            setEntity(new Warship());
            if ((null != oldUnit)
                    && (((Jumpship) oldUnit).getDriveCoreType() == Jumpship.DRIVE_CORE_SUBCOMPACT)) {
                ((Jumpship) getEntity()).setDriveCoreType(Jumpship.DRIVE_CORE_SUBCOMPACT);
            }
        } else if (entitytype == Entity.ETYPE_SPACE_STATION) {
            setEntity(new SpaceStation());
        } else {
            LogManager.getLogger().error("Received incorrect entityType!");
            return;
        }
        getEntity().setTechLevel(TechConstants.T_IS_ADVANCED);

        Jumpship ship = (Jumpship) getEntity();

        if (isPrimitive) {
            ship.setYear(2470);
            ship.setOriginalBuildYear(2470);
            ship.setDriveCoreType(Jumpship.DRIVE_CORE_PRIMITIVE);
            ship.setJumpRange(30);
        } else {
            ship.setYear(3145);
        }
        if (isPrimitive) {
            ship.setArmorType(EquipmentType.T_ARMOR_PRIMITIVE_AERO);
        } else {
            ship.setArmorType(EquipmentType.T_ARMOR_AEROSPACE);
        }
        ship.setWeight(TestAdvancedAerospace.getMinTonnage(ship));
        if (entitytype == Entity.ETYPE_WARSHIP) {
            ship.setOriginalWalkMP(2); // Start at 1G
            ship.set0SI(3);
        } else {
            ship.setOriginalWalkMP(0);
            ship.set0SI(1);
        }
        ship.setArmorTechLevel(getEntity().getTechLevel());
        
        ship.setHeatType(Aero.HEAT_SINGLE);

        ship.autoSetInternal();
        ship.initializeKFIntegrity();
        ship.initializeSailIntegrity();
        for (int loc = 0; loc < getEntity().locations(); loc++) {
            ship.initializeArmor((int) Math.round(ship.get0SI() / 10.0), loc);
        }

        if (null == oldUnit) {
            getEntity().setChassis("New");
            if ((entitytype == Entity.ETYPE_WARSHIP) && !isPrimitive) {
                ship.setModel("Warship");
            } else if (entitytype == Entity.ETYPE_SPACE_STATION) {
                ship.setModel("Station");
            } else {
                ship.setModel("Jumpship");
            }
        } else {
            ship.setChassis(oldUnit.getChassis());
            ship.setModel(oldUnit.getModel());
            ship.setYear(Math.max(oldUnit.getYear(),
                    ship.getConstructionTechAdvancement().getIntroductionDate()));
            ship.setSource(oldUnit.getSource());
            ship.setManualBV(oldUnit.getManualBV());
            SimpleTechLevel lvl = SimpleTechLevel.max(ship.getStaticTechLevel(),
                    SimpleTechLevel.convertCompoundToSimple(oldUnit.getTechLevel()));
            ship.setTechLevel(lvl.getCompoundTechLevel(oldUnit.isClan()));
            ship.setMixedTech(oldUnit.isMixedTech());
        }
    }

    @Override
    public ITechManager getTechManager() {
        return structureTab.getTechManager();
    }

    @Override
    public void reloadTabs() {
        configPane.removeAll();
        getContentPane().removeAll();

        structureTab = new WSStructureTab(this);
        previewTab = new PreviewTab(this);
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/AdvancedAeroUI.java

        statusbar = new AdvancedAeroStatusBar(this);
        equipmentTab = new EquipmentTab(this);
        buildTab = new DropshipBuildTab(this, equipmentTab);
=======
        statusbar = new WSStatusBar(this);
        equipmentTab = new AeroEquipmentTab(this);
        buildTab = new LABuildTab(this, equipmentTab);
        fluffTab = new FluffTab(this);
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/WSMainUI.java
        transportTab = new TransportTab(this);
        structureTab.addRefreshedListener(this);
        equipmentTab.addRefreshedListener(this);
        buildTab.addRefreshedListener(this);
        transportTab.addRefreshedListener(this);
        statusbar.addRefreshedListener(this);

        configPane.addTab("Structure/Armor", new TabScrollPane(structureTab));
        configPane.addTab("Equipment", equipmentTab);
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/AdvancedAeroUI.java
        configPane.addTab("Assign Criticals", buildTab);
        configPane.addTab("Transport Bays", transportTab);
=======
        configPane.addTab("Assign Criticals", new TabScrollPane(buildTab));
        configPane.addTab("Transport Bays", new TabScrollPane(transportTab));
        configPane.addTab("Fluff", new TabScrollPane(fluffTab));
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/WSMainUI.java
        configPane.addTab("Preview", previewTab);

        add(configPane, BorderLayout.CENTER);
        add(statusbar, BorderLayout.SOUTH);

        refreshHeader();
        validate();
    }

    @Override
    public void refreshAll() {
        statusbar.refresh();
        structureTab.refresh();
        equipmentTab.refresh();
        buildTab.refresh();
        previewTab.refresh();
    }

    @Override
    public void refreshArmor() {
    }

    @Override
    public void refreshBuild() {
        buildTab.refresh();
    }

    @Override
    public void refreshEquipment() {
        equipmentTab.refresh();
    }

    @Override
    public void refreshHeader() {
        String title = getEntity().getChassis() + " " + getEntity().getModel() + ".blk";
        setTitle(title);
    }

    @Override
    public void refreshStatus() {
        statusbar.refresh();
    }

    @Override
    public void refreshStructure() {
        structureTab.refresh();
    }

    @Override
    public void refreshWeapons() {
    }

    @Override
    public void refreshPreview() {
        previewTab.refresh();
    }

}
