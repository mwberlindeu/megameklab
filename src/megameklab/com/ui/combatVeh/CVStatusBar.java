/*
 * MegaMekLab - Copyright (C) 2009
 *
 * Original author - jtighe (torren@users.sourceforge.net)
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

package megameklab.com.ui.combatVeh;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import megamek.common.Tank;
import megamek.common.verifier.EntityVerifier;
import megamek.common.verifier.TestTank;
import megameklab.com.ui.MegaMekLabMainUI;
import megameklab.com.ui.util.ITab;
import megameklab.com.util.ImageHelper;
import megameklab.com.ui.util.RefreshListener;
import megameklab.com.util.UnitUtil;

public class CVStatusBar extends ITab {

    private static final long serialVersionUID = -6754327753693500675L;

    private final JPanel slotsPanel = new JPanel();
    private final JLabel move = new JLabel();
    private final JLabel bvLabel = new JLabel();
    private final JLabel tons = new JLabel();
    private final JLabel slots = new JLabel();
    private final JLabel cost = new JLabel();
    private final JLabel invalid = new JLabel();
    private final EntityVerifier entityVerifier = EntityVerifier.getInstance(new File(
            "data/mechfiles/UnitVerifierOptions.xml"));
    private TestTank testEntity;
    private final DecimalFormat formatter;
    private final JFrame parentFrame;

    private RefreshListener refresh;

    public CVStatusBar(MegaMekLabMainUI parent) {
        super(parent);
        parentFrame = parent;

        formatter = new DecimalFormat();
        testEntity = new TestTank((Tank) parent.getEntity(), entityVerifier.tankOption,
                null);
        JButton btnValidate = new JButton("Validate Unit");
        btnValidate.addActionListener(evt -> UnitUtil.showValidation(getTank(), getParentFrame()));
        JButton btnFluffImage = new JButton("Set Fluff Image");
        btnFluffImage.addActionListener(evt -> getFluffImage());
        invalid.setText("Invalid");
        invalid.setForeground(Color.RED);
        invalid.setVisible(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,2,2,20);
        gbc.anchor = GridBagConstraints.WEST;
        this.add(btnValidate, gbc);
        gbc.gridx = 1;
        this.add(btnFluffImage, gbc);
        gbc.gridx = 2;
        this.add(tons, gbc);
        gbc.gridx = 3;
        this.add(movementLabel(), gbc);
        gbc.gridx = 4;
        this.add(bvLabel(), gbc);
        gbc.gridx = 5;
        this.add(bvLabel, gbc);
        gbc.gridx = 6;
        this.add(tonnageLabel(), gbc);
        gbc.gridx = 7;
        this.add(slotsPanel(), gbc);
        gbc.gridx = 8;
        this.add(invalid, gbc);
        gbc.gridx = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        this.add(cost, gbc);
        refresh();
    }

    public void setRefreshListener(RefreshListener refresh) {
        this.refresh = refresh;
    }

    public JLabel movementLabel() {
        int walk = getTank().getOriginalWalkMP();
        int run = getTank().getRunMP(false, true, false);
        int jump = getTank().getOriginalJumpMP();

        move.setText("Movement: " + walk + "/" + run + "/" + jump);
        return move;
    }

    public JLabel bvLabel() {
        int bv = getTank().calculateBattleValue();
        bvLabel.setText("BV: " + bv);

        return bvLabel;
    }

    public JLabel tonnageLabel() {
        double tonnage = getTank().getWeight();
        double currentTonnage;

        currentTonnage = testEntity.calculateWeight();
        currentTonnage += UnitUtil.getUnallocatedAmmoTonnage(getTank());

        tons.setText("Tonnage: " + currentTonnage + "/" + tonnage);
        return tons;
    }

    public JPanel slotsPanel() {
        Tank tank = getTank();
        int currentSlots = tank.getTotalSlots() - tank.getFreeSlots();
        slots.setText("Slots: "+currentSlots+"/"+tank.getTotalSlots());
        slotsPanel.add(slots);
        return slotsPanel;
    }

    public void refresh() {
        int walk = getTank().getOriginalWalkMP();
        int run = getTank().getRunMP(true, true, false);
        int jump = getTank().getOriginalJumpMP();
        double tonnage = getTank().getWeight();
        double currentTonnage;
        int bv = getTank().calculateBattleValue();

        testEntity = new TestTank(getTank(), entityVerifier.tankOption,
                null);

        currentTonnage = testEntity.calculateWeight();

        currentTonnage += UnitUtil.getUnallocatedAmmoTonnage(getTank());
        long currentCost = Math.round(getTank().getCost(false));

        tons.setText(String.format("Tonnage: %.1f/%.1f (%.1f Remaining)", currentTonnage, tonnage, tonnage - currentTonnage));
        tons.setToolTipText("Current Tonnage/Max Tonnage");
        if (currentTonnage > tonnage) {
            tons.setForeground(Color.red);
        } else {
            tons.setForeground(UIManager.getColor("Label.foreground"));
        }
        Tank tank = getTank();
        int currentSlots = tank.getTotalSlots() - tank.getFreeSlots();
        slots.setText("Slots: "+currentSlots+"/"+tank.getTotalSlots());
        if (currentSlots > tank.getTotalSlots()) {
            slots.setForeground(Color.red);
        } else {
            slots.setForeground(UIManager.getColor("Label.foreground"));
        }

        bvLabel.setText("BV: " + bv);
        bvLabel.setToolTipText("BV 2.0");

        cost.setText("Cost: " + formatter.format(currentCost) + " C-bills");

        move.setText("Movement: " + walk + "/" + run + "/" + jump);
        move.setToolTipText("Walk/Run/Jump MP");
        StringBuffer sb = new StringBuffer();
        invalid.setVisible(!testEntity.correctEntity(sb));
        invalid.setToolTipText("<html>" + sb.toString().replaceAll("\n", "<br/>") + "</html>");
    }

    private void getFluffImage() {
        //copied from structureTab
        FileDialog fDialog = new FileDialog(getParentFrame(), "Image Path", FileDialog.LOAD);
        fDialog.setDirectory(new File(ImageHelper.fluffPath).getAbsolutePath() + File.separatorChar + ImageHelper.imageMech + File.separatorChar);
        /*
         //This does not seem to be working
        if (getMech().getFluff().getMMLImagePath().trim().length() > 0) {
            String fullPath = new File(getMech().getFluff().getMMLImagePath()).getAbsolutePath();
            String imageName = fullPath.substring(fullPath.lastIndexOf(File.separatorChar) + 1);
            fullPath = fullPath.substring(0, fullPath.lastIndexOf(File.separatorChar) + 1);
            fDialog.setDirectory(fullPath);
            fDialog.setFile(imageName);
        } else {
            fDialog.setDirectory(new File(ImageHelper.fluffPath).getAbsolutePath() + File.separatorChar + ImageHelper.imageMech + File.separatorChar);
            fDialog.setFile(getMech().getChassis() + " " + getMech().getModel() + ".png");
        }
        */
        fDialog.setLocationRelativeTo(this);

        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            String relativeFilePath = new File(fDialog.getDirectory() + fDialog.getFile()).getAbsolutePath();
            relativeFilePath = "." + File.separatorChar + relativeFilePath
                    .substring(new File(System.getProperty("user.dir")).getAbsolutePath().length() + 1);
            getTank().getFluff().setMMLImagePath(relativeFilePath);
        }
        if (refresh != null) {
            refresh.refreshPreview();
        }
    }

    private JFrame getParentFrame() {
        return parentFrame;
    }
}