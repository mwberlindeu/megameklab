/*
 * MegaMekLab - Copyright (C) 2008
 *
 * Original author - jtighe (torren@users.sourceforge.net)
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
package megameklab.com.ui.fighterAero;

import megamek.common.*;
import megameklab.com.ui.MegaMekLabMainUI;
import megameklab.com.ui.generalUnit.AeroEquipmentTab;
import megameklab.com.ui.generalUnit.FluffTab;
import megameklab.com.ui.generalUnit.PreviewTab;
import megameklab.com.ui.util.TabScrollPane;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;

public class ASMainUI extends MegaMekLabMainUI {

    JTabbedPane configPane = new JTabbedPane(SwingConstants.TOP);

    private ASStructureTab structureTab;
    private AeroEquipmentTab equipmentTab;
    private PreviewTab previewTab;
    private ASBuildTab buildTab;
    private FluffTab fluffTab;
    private ASStatusBar statusbar;

    public ASMainUI(boolean primitive) {

        super();
        createNewUnit(Entity.ETYPE_AERO, primitive);
        setTitle(getEntity().getChassis() + " " + getEntity().getModel() + ".blk");
        finishSetup();
    }

    @Override
    public void reloadTabs() {
        configPane.removeAll();
        getContentPane().removeAll();

        structureTab = new ASStructureTab(this);
        previewTab = new PreviewTab(this);
        statusbar = new ASStatusBar(this);
        equipmentTab = new AeroEquipmentTab(this);
        buildTab = new ASBuildTab(this, equipmentTab);
        fluffTab = new FluffTab(this);
        structureTab.addRefreshedListener(this);
        equipmentTab.addRefreshedListener(this);
        buildTab.addRefreshedListener(this);
        fluffTab.setRefreshedListener(this);
        statusbar.addRefreshedListener(this);

        configPane.addTab("Structure/Armor", new TabScrollPane(structureTab));
        configPane.addTab("Equipment", equipmentTab);
        configPane.addTab("Assign Criticals", new TabScrollPane(buildTab));
        configPane.addTab("Fluff", new TabScrollPane(fluffTab));
        configPane.addTab("Preview", previewTab);

        add(configPane, BorderLayout.CENTER);
        add(statusbar, BorderLayout.SOUTH);

        refreshHeader();
        validate();
    }

    @Override
    public void createNewUnit(long entityType, boolean isPrimitive, boolean isIndustrial, Entity oldEntity) {

        if (entityType == Entity.ETYPE_AERO) {
            setEntity(new Aero());
            getEntity().setTechLevel(TechConstants.T_IS_TW_NON_BOX);
        } else if (entityType == Entity.ETYPE_CONV_FIGHTER) {
            setEntity(new ConvFighter());
            getEntity().setTechLevel(TechConstants.T_IS_TW_NON_BOX);
        } else {
            LogManager.getLogger().error("Received incorrect entityType!");
            return;
        }

        Aero aero = (Aero) getEntity();

        aero.setYear(3145);
        aero.setWeight(25);
        aero.setEngine(new Engine(25, Engine.NORMAL_ENGINE, 0));
        if (isPrimitive) {
            aero.setCockpitType(Aero.COCKPIT_PRIMITIVE);
            aero.setArmorType(EquipmentType.T_ARMOR_PRIMITIVE_FIGHTER);
        } else {
            aero.setArmorType(EquipmentType.T_ARMOR_STANDARD);
        }
        aero.setArmorTechLevel(getEntity().getTechLevel());
        aero.setStructureType(EquipmentType.T_STRUCTURE_STANDARD);

        aero.setHeatSinks(10);
        aero.setHeatType(Aero.HEAT_SINGLE);

        aero.autoSetInternal();
        for (int loc = 0; loc < getEntity().locations(); loc++) {
            aero.initializeArmor(0, loc);
        }

        if (null == oldEntity) {
            getEntity().setChassis("New");
            getEntity().setModel("Aero");
        } else {
            aero.setChassis(oldEntity.getChassis());
            aero.setModel(oldEntity.getModel());
            aero.setYear(Math.max(oldEntity.getYear(),
                    aero.getConstructionTechAdvancement().getIntroductionDate()));
            aero.setSource(oldEntity.getSource());
            aero.setManualBV(oldEntity.getManualBV());
            SimpleTechLevel lvl = SimpleTechLevel.max(aero.getStaticTechLevel(),
                    SimpleTechLevel.convertCompoundToSimple(oldEntity.getTechLevel()));
            aero.setTechLevel(lvl.getCompoundTechLevel(oldEntity.isClan()));
            aero.setMixedTech(oldEntity.isMixedTech());
        }
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
    public void refreshTransport() {
        // not used for fighters
    }

    @Override
    public void refreshPreview() {
        previewTab.refresh();

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
    public void refreshSummary() {
        structureTab.refreshSummary();
    }
    
    @Override
    public void refreshEquipmentTable() {
        equipmentTab.refreshTable();
    }

    @Override
    public ITechManager getTechManager() {
        return structureTab.getTechManager();
    }

}