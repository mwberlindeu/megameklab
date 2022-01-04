/*
 * MegaMekLab - Copyright (C) 2017 - The MegaMek Team
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

<<<<<<< HEAD:src/megameklab/com/ui/aerospace/DropshipMainUI.java
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import megamek.common.Aero;
import megamek.common.Dropship;
import megamek.common.Entity;
import megamek.common.EntityMovementMode;
import megamek.common.EquipmentType;
import megamek.common.ITechManager;
import megamek.common.MechSummaryCache;
import megamek.common.SimpleTechLevel;
import megamek.common.SmallCraft;
import megamek.common.TechConstants;
import megamek.common.logging.LogLevel;
import megameklab.com.MegaMekLab;
import megameklab.com.ui.MegaMekLabMainUI;
import megameklab.com.ui.Aero.tabs.EquipmentTab;
import megameklab.com.ui.Aero.tabs.PreviewTab;
import megameklab.com.util.MenuBarCreator;
=======
import megamek.common.*;
import megameklab.com.ui.MegaMekLabMainUI;
import megameklab.com.ui.generalUnit.AeroEquipmentTab;
import megameklab.com.ui.generalUnit.FluffTab;
import megameklab.com.ui.generalUnit.PreviewTab;
import megameklab.com.ui.generalUnit.TransportTab;
import megameklab.com.ui.util.TabScrollPane;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/DSMainUI.java

/**
 * Main UI for DropShips and Small Craft
 * 
 * @author Neoancient
 */
public class DSMainUI extends MegaMekLabMainUI {

    JTabbedPane configPane = new JTabbedPane(SwingConstants.TOP);
    private DSStructureTab structureTab;
    private AeroEquipmentTab equipmentTab;
    private PreviewTab previewTab;
    private LABuildTab buildTab;
    private TransportTab transportTab;
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/DropshipMainUI.java
    private DropshipStatusBar statusbar;
    JPanel masterPanel = new JPanel();
    JScrollPane scroll = new JScrollPane();
    private MenuBarCreator menubarcreator;
=======
    private DSStatusBar statusbar;
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/DSMainUI.java
    
    public DSMainUI(boolean primitive) {
        super();
        createNewUnit(Entity.ETYPE_DROPSHIP, primitive, false);
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
        if (entitytype == Entity.ETYPE_SMALL_CRAFT) {
            setEntity(new SmallCraft());
            getEntity().setTechLevel(TechConstants.T_IS_TW_NON_BOX);
        } else if (entitytype == Entity.ETYPE_DROPSHIP) {
            setEntity(new Dropship());
            getEntity().setTechLevel(TechConstants.T_IS_TW_NON_BOX);
        } else {
            LogManager.getLogger().error("Received incorrect entityType!");
            return;
        }

        SmallCraft smallCraft = (SmallCraft) getEntity();

        if (isPrimitive) {
            smallCraft.setYear(2470);
            smallCraft.setOriginalBuildYear(2470);
            smallCraft.setArmorType(EquipmentType.T_ARMOR_PRIMITIVE_AERO);
        } else {
            smallCraft.setYear(3145);
            smallCraft.setArmorType(EquipmentType.T_ARMOR_AEROSPACE);
        }
        smallCraft.setWeight(200);
        smallCraft.setOriginalWalkMP(2); // Start at 1G
        smallCraft.setArmorTechLevel(getEntity().getTechLevel());
        smallCraft.set0SI(3);
        smallCraft.setDesignType(SmallCraft.MILITARY);
        
        smallCraft.setHeatType(Aero.HEAT_SINGLE);

        smallCraft.autoSetInternal();
        for (int loc = 0; loc < getEntity().locations(); loc++) {
            smallCraft.initializeArmor(smallCraft.get0SI(), loc);
        }

        if (null == oldUnit) {
            getEntity().setChassis("New");
            if (entitytype == Entity.ETYPE_SMALL_CRAFT) {
                smallCraft.setModel("Small Craft");
            } else {
                smallCraft.setModel("Dropship");
            }
            smallCraft.setSpheroid(false);
            smallCraft.setMovementMode(EntityMovementMode.AERODYNE);
        } else {
            smallCraft.setChassis(oldUnit.getChassis());
            smallCraft.setModel(oldUnit.getModel());
            smallCraft.setYear(Math.max(oldUnit.getYear(),
                    smallCraft.getConstructionTechAdvancement().getIntroductionDate()));
            smallCraft.setSource(oldUnit.getSource());
            smallCraft.setManualBV(oldUnit.getManualBV());
            SimpleTechLevel lvl = SimpleTechLevel.max(smallCraft.getStaticTechLevel(),
                    SimpleTechLevel.convertCompoundToSimple(oldUnit.getTechLevel()));
            smallCraft.setTechLevel(lvl.getCompoundTechLevel(oldUnit.isClan()));
            smallCraft.setMixedTech(oldUnit.isMixedTech());

            smallCraft.setSpheroid(((SmallCraft) oldUnit).isSpheroid());
            smallCraft.setMovementMode(oldUnit.getMovementMode());
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

        structureTab = new DSStructureTab(this);

        previewTab = new PreviewTab(this);

        statusbar = new DSStatusBar(this);
        equipmentTab = new AeroEquipmentTab(this);
        buildTab = new LABuildTab(this, equipmentTab);
        transportTab = new TransportTab(this);
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/DropshipMainUI.java
=======
        FluffTab fluffTab = new FluffTab(this);
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/DSMainUI.java
        structureTab.addRefreshedListener(this);
        equipmentTab.addRefreshedListener(this);
        buildTab.addRefreshedListener(this);
        transportTab.addRefreshedListener(this);
        statusbar.addRefreshedListener(this);

        configPane.addTab("Structure/Armor", new TabScrollPane(structureTab));
        configPane.addTab("Equipment", equipmentTab);
<<<<<<< HEAD:src/megameklab/com/ui/aerospace/DropshipMainUI.java
        configPane.addTab("Assign Criticals", buildTab);
        configPane.addTab("Transport Bays", transportTab);
=======
        configPane.addTab("Assign Criticals", new TabScrollPane(buildTab));
        configPane.addTab("Transport Bays", new TabScrollPane(transportTab));
        configPane.addTab("Fluff", new TabScrollPane(fluffTab));
>>>>>>> origin/master:src/megameklab/com/ui/largeAero/DSMainUI.java
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
