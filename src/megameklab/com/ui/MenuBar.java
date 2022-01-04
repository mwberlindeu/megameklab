<<<<<<< HEAD:src/megameklab/com/util/MenuBarCreator.java
/*
 * MegaMekLab - Copyright (C) 2011
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

package megameklab.com.util;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;

import megamek.MegaMek;
import megamek.client.ui.swing.UnitLoadingDialog;
import megamek.client.ui.swing.UnitSelectorDialog;
import megamek.common.Aero;
import megamek.common.BattleArmor;
import megamek.common.Entity;
import megamek.common.FixedWingSupport;
import megamek.common.GunEmplacement;
import megamek.common.Infantry;
import megamek.common.Largewetnavy;
import megamek.common.Jumpship;
import megamek.common.Mech;
import megamek.common.MechFileParser;
import megamek.common.MechView;
import megamek.common.SmallCraft;
import megamek.common.Tank;
import megamek.common.loaders.BLKFile;
import megameklab.com.MegaMekLab;
import megameklab.com.ui.MegaMekLabMainUI;

public class MenuBarCreator extends JMenuBar implements ClipboardOwner {

    /**
     *
     */
    private static final long serialVersionUID = -3998342610654551481L;
    private JMenu file = new JMenu("File");
    private JMenu help = new JMenu("Help");
    private JMenu validate = new JMenu("Validate");
    private JMenu themeMenu = new JMenu("Themes");
    private MegaMekLabMainUI parentFrame = null;

    public MenuBarCreator(MegaMekLabMainUI parent) {

        parentFrame = parent;

        loadFileMenuOptions();

        JMenuItem item = new JMenuItem();
        item.setText("About");
        item.setMnemonic(KeyEvent.VK_A);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuHelpAbout_actionPerformed();
            }
        });
        help.add(item);

        item = new JMenuItem();
        item.setText("Record Sheet Images");
        item.setMnemonic(KeyEvent.VK_R);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuHelpFluff_actionPerformed();
            }
        });
        help.add(item);

        item = new JMenuItem();
        item.setText("Insert Image To File");
        item.setMnemonic(KeyEvent.VK_I);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuInsertImageFile_actionPerformed();
            }
        });
        help.add(item);

        validate.add(loadBVMenuOptions());

        validate.add(loadValidateMenuOptions());

        validate.add(loadSpecsMenuOptions());

        validate.add(loadUnitCostBreakdownMenuOptions());
        
        validate.add(loadUnitWeightBreakdownMenuOptions());

        this.add(file);
        this.add(validate);
        this.add(help);

    }

    private JMenu loadBVMenuOptions() {
        JMenu bv = new JMenu("BV Calculations");
        JMenuItem item = new JMenuItem();
        item.setText("Current Units BV Calculations");
        item.setMnemonic(KeyEvent.VK_B);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuBVCalculations_actionPerformed();
            }
        });
        bv.add(item);

        item = new JMenuItem();
        item.setText("BV Calculations From File");
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitBVFromFile_actionPerformed();
            }
        });
        bv.add(item);

        item = new JMenuItem();
        item.setText("BV Calculations From Cache");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitBVFromCache_actionPerformed();
            }
        });
        bv.add(item);
        return bv;
    }

    private JMenu loadValidateMenuOptions() {
        JMenu entityValidation = new JMenu("Unit Validation");
        JMenuItem item = new JMenuItem();
        item.setText("Validate Current Unit");
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuValidateUnit_actionPerformed();
            }
        });
        entityValidation.add(item);

        item = new JMenuItem();
        item.setText("Validate Unit From File");
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitValidationFromFile_actionPerformed();
            }
        });
        entityValidation.add(item);

        item = new JMenuItem();
        item.setText("Validate Unit From Cache");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitValidationFromCache_actionPerformed();
            }
        });
        entityValidation.add(item);
        return entityValidation;
    }

    private JMenu loadUnitCostBreakdownMenuOptions() {
        JMenu entityBreakdown = new JMenu("Unit Cost Breakdown");
        JMenuItem item = new JMenuItem();
        item.setText("Breakdown Current Unit");
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuUnitCostBreakdown_actionPerformed();
            }
        });
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText("Unit Breakdown From File");
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitBreakdownFromFile_actionPerformed();
            }
        });
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText("Unit Breakdown From Cache");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitBreakdownFromCache_actionPerformed();
            }
        });
        entityBreakdown.add(item);
        return entityBreakdown;
    }
    
    private JMenu loadUnitWeightBreakdownMenuOptions() {
        JMenu entityBreakdown = new JMenu("Unit Weight Breakdown");
        JMenuItem item = new JMenuItem();
        item.setText("Breakdown Current Unit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuUnitWeightBreakdown_actionPerformed();
            }
        });
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText("Unit Breakdown From File");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitWeightBreakdownFromFile_actionPerformed();
            }
        });
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText("Unit Breakdown From Cache");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitWeightBreakdownFromCache_actionPerformed();
            }
        });
        entityBreakdown.add(item);
        return entityBreakdown;
    }

    private JMenu loadSpecsMenuOptions() {
        JMenu unitSpecs = new JMenu("Unit Specs");
        JMenuItem item = new JMenuItem();
        item.setText("Current Unit Specs");
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuUnitSpecs_actionPerformed();
            }
        });
        unitSpecs.add(item);

        item = new JMenuItem();
        item.setText("Unit Specs From File");
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitSpecsFromFile_actionPerformed();
            }
        });
        unitSpecs.add(item);

        item = new JMenuItem();
        item.setText("Unit Specs From Cache");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuGetUnitSpecsFromCache_actionPerformed();
            }
        });
        unitSpecs.add(item);
        return unitSpecs;
    }

    private void loadFileMenuOptions() {

        file.removeAll();

        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem item = new JMenuItem();

        item = new JMenuItem("Reset Current Unit");
        item.setMnemonic(KeyEvent.VK_R);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuResetEntity_actionPerformed(e);
            }
        });
        file.add(item);

        JMenu unitMenu = new JMenu("Switch Unit Type");
        unitMenu.setMnemonic(KeyEvent.VK_S);
        Entity en = parentFrame.getEntity();

        if (!(en instanceof Mech)
                || ((Mech)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Mech");
            item.setMnemonic(KeyEvent.VK_M);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadMech());
            unitMenu.add(item);
        }

        if (!(en.isFighter()
                || (en.isFighter() && ((Aero)en).isPrimitive()))) {
            item = new JMenuItem();
            item.setText("Aero/Conv Fighter");
            item.setMnemonic(KeyEvent.VK_A);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadAero();
                }

            });
            unitMenu.add(item);
        }

        if (!(en instanceof SmallCraft)
                || ((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Dropship/Small Craft");
            item.setMnemonic(KeyEvent.VK_D);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadDropship());
            unitMenu.add(item);
        }

        if (!(en instanceof Jumpship)
                || ((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Jumpship/Warship/Space Station");
            item.setMnemonic(KeyEvent.VK_J);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadAdvAero());
            unitMenu.add(item);
        }

        if (!(parentFrame.getEntity() instanceof Tank)) {
            item = new JMenuItem();
            item.setText("Combat Vehicle");
            item.setMnemonic(KeyEvent.VK_T);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadVehicle();
                }

            });
            unitMenu.add(item);
        }

        if (!(parentFrame.getEntity() instanceof BattleArmor)) {
            item = new JMenuItem();
            item.setText("BattleArmor");
            item.setMnemonic(KeyEvent.VK_B);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadBattleArmor();
                }

            });
            unitMenu.add(item);
        }

        if (!(parentFrame.getEntity() instanceof Infantry) || (parentFrame.getEntity() instanceof BattleArmor)) {
            item = new JMenuItem();
            item.setText("Infantry");
            item.setMnemonic(KeyEvent.VK_I);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadInfantry();
                }

            });
            unitMenu.add(item);
        }

        //mwberlin: Adding menu selection for Large Naval Vessel Support Vehicles
        
        if (!(parentFrame.getEntity() instanceof Largewetnavy) || (parentFrame.getEntity() instanceof BattleArmor)) {
            item = new JMenuItem();
            item.setText("Large Naval Vessel Support Vehicle");
            item.setMnemonic(KeyEvent.VK_LWN);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LWN,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadLargewetnavy();
                }

            });
            unitMenu.add(item);
        }
        
        
        JMenu pMenu = new JMenu("Primitive/Retro");
        if (!(en instanceof Mech)
                || !((Mech)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Mech");
            item.addActionListener(e ->jMenuLoadPrimitiveMech());
            pMenu.add(item);
        }
        
        if (!(en.isFighter())
                || !((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Aero");
            item.addActionListener(e ->jMenuLoadPrimitiveAero());
            pMenu.add(item);
        }
        
        if (!(en.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT))
                || !((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Dropship/Small Craft");
            item.addActionListener(e ->jMenuLoadPrimitiveDropship());
            pMenu.add(item);
        }
        
        if (!(en.hasETypeFlag(Entity.ETYPE_JUMPSHIP))
                || !((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText("Jumpship");
            item.addActionListener(e ->jMenuLoadPrimitiveJumpship());
            pMenu.add(item);
        }
        
        unitMenu.add(pMenu);

        file.add(unitMenu);

        JMenu loadMenu = new JMenu("Load");
        loadMenu.setMnemonic(KeyEvent.VK_L);
        item = new JMenuItem();

        item.setText("From Cache");
        item.setMnemonic(KeyEvent.VK_C);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuLoadEntity_actionPerformed(e);
            }
        });
        loadMenu.add(item);

        item = new JMenuItem();
        item.setText("From File");
        item.setMnemonic(KeyEvent.VK_F);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuLoadEntityFromFile_actionPerformed(e);
            }
        });
        loadMenu.add(item);

        file.add(loadMenu);

        item = new JMenuItem(String.format("Current Unit"));
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuPrintCurrentUnit();
            }
        });

        file.add(UnitPrintManager.printMenu(parentFrame, item));

        item = new JMenuItem();
        item.setText("Save");
        item.setMnemonic(KeyEvent.VK_S);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuSaveEntity_actionPerformed(e);
            }
        });
        file.add(item);

        item = new JMenuItem();
        item.setText("Save As");
        item.setMnemonic(KeyEvent.VK_A);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuSaveAsEntity_actionPerformed(e);
            }
        });
        file.add(item);

        JMenu exportMenu = new JMenu("Export");

        item = new JMenuItem("to HTML");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuExportEntityHTML_actionPerformed(e);
            }
        });
        exportMenu.add(item);

        item = new JMenuItem("to Text");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuExportEntityText_actionPerformed(e);
            }
        });
        exportMenu.add(item);

        item = new JMenuItem("to Clipboard (text)");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuExportEntityClipboard_actionPerformed(e);
            }
        });
        exportMenu.add(item);

        file.add(exportMenu);
        
        JMenu themeMenu = createThemeMenu();
        file.add(themeMenu);

        item = new JMenuItem("Configuration");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuConfiguration_actionPerformed(e);
            }
        });
        file.add(item);

        int fileNumber = 1;
        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1).length() > 1) {
            file.addSeparator();
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadEntityFromFile_actionPerformed(1);
                }
            });

            file.add(item);
            fileNumber++;
        }

        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_2).length() > 1) {
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_2);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadEntityFromFile_actionPerformed(2);
                }
            });

            file.add(item);
            fileNumber++;
        }

        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_3).length() > 1) {
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_3);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadEntityFromFile_actionPerformed(3);
                }
            });

            file.add(item);
            fileNumber++;
        }

        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_4).length() > 1) {
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_4);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLoadEntityFromFile_actionPerformed(4);
                }
            });

            file.add(item);
            fileNumber++;
        }

        file.addSeparator();

        item = new JMenuItem();
        item.setText("Exit");
        item.setMnemonic(KeyEvent.VK_X);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(ev -> parentFrame.exit());
        file.add(item);

    }
    
    /**
     * Creates a menu that includes all installed look and feel options
     * 
     * @return The new menu
     */
    private JMenu createThemeMenu() {
        themeMenu = new JMenu("Themes");
        JCheckBoxMenuItem item;
        for (LookAndFeelInfo plaf : UIManager.getInstalledLookAndFeels()) {
            item = new JCheckBoxMenuItem(plaf.getName());
            if (plaf.getName().equalsIgnoreCase(
                    UIManager.getLookAndFeel().getName())) {
                item.setSelected(true);
            }
            themeMenu.add(item);
            item.addActionListener(ev -> {
                parentFrame.changeTheme(plaf);
                refreshThemeMenu(plaf.getName());
            });
        }
        return themeMenu;
    }
    
    /**
     * Updates the checkbox items on the theme menu to show which is currently selected.
     * 
     * @param currentThemeName The name returned by {@link LookAndFeelInfo#getName()}
     */
    private void refreshThemeMenu(String currentThemeName) {
        for (int i = 0; i < themeMenu.getItemCount(); i++) {
            final JMenuItem item = themeMenu.getItem(i);
            if (item instanceof JCheckBoxMenuItem) {
                ((JCheckBoxMenuItem) item).setSelected(item.getText().equals(currentThemeName));
            }
        }
    }

    private void jMenuGetUnitBVFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        UnitSelectorDialog viewer = new UnitSelectorDialog(parentFrame, unitLoadingDialog, true);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        tempEntity.calculateBattleValue(true, true);
        UnitUtil.showBVCalculations(tempEntity.getBVText(), parentFrame);

    }

    private void jMenuGetUnitValidationFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        UnitSelectorDialog viewer = new UnitSelectorDialog(parentFrame, unitLoadingDialog, true);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        UnitUtil.showValidation(tempEntity, parentFrame);

    }

    private void jMenuGetUnitSpecsFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        UnitSelectorDialog viewer = new UnitSelectorDialog(parentFrame, unitLoadingDialog, true);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        UnitUtil.showUnitSpecs(tempEntity, parentFrame);

    }

    private void jMenuGetUnitBreakdownFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        UnitSelectorDialog viewer = new UnitSelectorDialog(parentFrame, unitLoadingDialog, true);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        UnitUtil.showUnitCostBreakDown(tempEntity, parentFrame);

    }
    
    private void jMenuGetUnitWeightBreakdownFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        UnitSelectorDialog viewer = new UnitSelectorDialog(parentFrame, unitLoadingDialog, true);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        UnitUtil.showUnitWeightBreakDown(tempEntity, parentFrame);

    }

    private void jMenuGetUnitBVFromFile_actionPerformed() {

        Entity tempEntity = null;
        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";
        File unitFile = new File(filePathName);
        JFileChooser f = new JFileChooser(filePathName);
        f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
        f.setDialogTitle("Choose Unit");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

        // Add a filter for mul files
        f.setFileFilter(filter);

        int returnVal = f.showOpenDialog(parentFrame);
        if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
            // I want a file, y'know!
            return;
        }
        unitFile = f.getSelectedFile();

        try {
            tempEntity = new MechFileParser(unitFile).getEntity();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format("Warning:Invalid unit, it might load incorrectly!\n%1$s", ex.getMessage()));
        } finally {
            tempEntity.calculateBattleValue(true, true);
            UnitUtil.showBVCalculations(tempEntity.getBVText(), parentFrame);
        }

    }

    private void jMenuGetUnitValidationFromFile_actionPerformed() {

        Entity tempEntity = null;
        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";
        File unitFile = new File(filePathName);
        JFileChooser f = new JFileChooser(filePathName);
        f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
        f.setDialogTitle("Choose Unit");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

        // Add a filter for mul files
        f.setFileFilter(filter);

        int returnVal = f.showOpenDialog(parentFrame);
        if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
            // I want a file, y'know!
            return;
        }
        unitFile = f.getSelectedFile();

        try {
            tempEntity = new MechFileParser(unitFile).getEntity();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format("Warning:Invalid unit, it might load incorrectly!\n%1$s", ex.getMessage()));
        } finally {
            UnitUtil.showValidation(tempEntity, parentFrame);
        }
    }

    private void jMenuGetUnitBreakdownFromFile_actionPerformed() {

        Entity tempEntity = null;
        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";
        File unitFile = new File(filePathName);
        JFileChooser f = new JFileChooser(filePathName);
        f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
        f.setDialogTitle("Choose Unit");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

        // Add a filter for mul files
        f.setFileFilter(filter);

        int returnVal = f.showOpenDialog(parentFrame);
        if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
            // I want a file, y'know!
            return;
        }
        unitFile = f.getSelectedFile();

        try {
            tempEntity = new MechFileParser(unitFile).getEntity();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format("Warning:Invalid unit, it might load incorrectly!\n%1$s", ex.getMessage()));
        } finally {
            UnitUtil.showUnitCostBreakDown(tempEntity, parentFrame);
        }
    }
    
    private void jMenuGetUnitWeightBreakdownFromFile_actionPerformed() {

        Entity tempEntity = null;
        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";
        File unitFile = new File(filePathName);
        JFileChooser f = new JFileChooser(filePathName);
        f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
        f.setDialogTitle("Choose Unit");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

        // Add a filter for mul files
        f.setFileFilter(filter);

        int returnVal = f.showOpenDialog(parentFrame);
        if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
            // I want a file, y'know!
            return;
        }
        unitFile = f.getSelectedFile();

        try {
            tempEntity = new MechFileParser(unitFile).getEntity();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format("Warning:Invalid unit, it might load incorrectly!\n%1$s", ex.getMessage()));
        } finally {
            UnitUtil.showUnitWeightBreakDown(tempEntity, parentFrame);
        }
    }    

    private void jMenuGetUnitSpecsFromFile_actionPerformed() {

        Entity tempEntity = null;
        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";
        File unitFile = new File(filePathName);
        JFileChooser f = new JFileChooser(filePathName);
        f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
        f.setDialogTitle("Choose Unit");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

        // Add a filter for mul files
        f.setFileFilter(filter);

        int returnVal = f.showOpenDialog(parentFrame);
        if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
            // I want a file, y'know!
            return;
        }
        unitFile = f.getSelectedFile();

        try {
            tempEntity = new MechFileParser(unitFile).getEntity();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format("Warning:Invalid unit, it might load incorrectly!\n%1$s", ex.getMessage()));
        } finally {
            UnitUtil.showUnitSpecs(tempEntity, parentFrame);
        }
    }

    private void jMenuInsertImageFile_actionPerformed() {

        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";

        File unitFile = new File(filePathName);
        JFileChooser f = new JFileChooser(filePathName);
        f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
        f.setDialogTitle("Load Mech");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

        // Add a filter for mul files
        f.setFileFilter(filter);

        int returnVal = f.showOpenDialog(parentFrame);
        if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
            // I want a file, y'know!
            return;
        }
        unitFile = f.getSelectedFile();

        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();

            if (UnitUtil.validateUnit(parentFrame.getEntity()).trim().length() > 0) {
                JOptionPane.showMessageDialog(parentFrame, "Warning:Invalid unit, it might load incorrectly!");
            }

            FileDialog fDialog = new FileDialog(parentFrame, "Image Path", FileDialog.LOAD);

            if (parentFrame.getEntity().getFluff().getMMLImagePath().trim().length() > 0) {
                String fullPath = new File(parentFrame.getEntity().getFluff().getMMLImagePath()).getAbsolutePath();
                String imageName = fullPath.substring(fullPath.lastIndexOf(File.separatorChar) + 1);
                fullPath = fullPath.substring(0, fullPath.lastIndexOf(File.separatorChar) + 1);
                fDialog.setDirectory(fullPath);
                fDialog.setFile(imageName);
            } else {
                fDialog.setDirectory(new File(ImageHelper.fluffPath).getAbsolutePath() + File.separatorChar + "mech" + File.separatorChar);
                fDialog.setFile(parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel() + ".png");
            }

            fDialog.setLocationRelativeTo(parentFrame);

            fDialog.setVisible(true);

            if (fDialog.getFile() != null) {
                String relativeFilePath = new File(fDialog.getDirectory() + fDialog.getFile()).getAbsolutePath();
                relativeFilePath = "." + File.separatorChar + relativeFilePath.substring(new File(System.getProperty("user.dir").toString()).getAbsolutePath().length() + 1);
                parentFrame.getEntity().getFluff().setMMLImagePath(relativeFilePath);
                BLKFile.encode(unitFile.getAbsolutePath(), tempEntity);
            }
        } catch (Exception ex) {

        }
        return;
    }

    // Show BV Calculations

    public void jMenuBVCalculations_actionPerformed() {
        parentFrame.getEntity().calculateBattleValue(true, true);
        UnitUtil.showBVCalculations(parentFrame.getEntity().getBVText(), parentFrame);
    }

    public void jMenuUnitCostBreakdown_actionPerformed() {
        UnitUtil.showUnitCostBreakDown(parentFrame.getEntity(), parentFrame);
    }
    
    public void jMenuUnitWeightBreakdown_actionPerformed() {
        UnitUtil.showUnitWeightBreakDown(parentFrame.getEntity(), parentFrame);
    }

    public void jMenuUnitSpecs_actionPerformed() {
        UnitUtil.showUnitSpecs(parentFrame.getEntity(), parentFrame);
    }

    // Show Validation data.
    public void jMenuValidateUnit_actionPerformed() {
        UnitUtil.showValidation(parentFrame.getEntity(), parentFrame);
    }

    // Show data about MegaMekLab
    public void jMenuHelpAbout_actionPerformed() {

        // make the dialog
        JDialog dlg = new JDialog(parentFrame, "MegaMekLab Info");

        // set up the contents
        JPanel child = new JPanel();
        child.setLayout(new BoxLayout(child, BoxLayout.Y_AXIS));

        // set the text up.
        JLabel mekwars = new JLabel("MegaMekLab Version: " + MegaMekLab.VERSION);
        JLabel version = new JLabel("MegaMek Version: " + MegaMek.VERSION);
        JLabel license1 = new JLabel("MegaMekLab software is under GPL. See");
        JLabel license2 = new JLabel("license.txt in ./Docs/licenses for details.");
        JLabel license3 = new JLabel("Project Info:");
        JLabel license4 = new JLabel("       https://github.com/MegaMek/megameklab       ");

        // center everything
        mekwars.setAlignmentX(Component.CENTER_ALIGNMENT);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        license1.setAlignmentX(Component.CENTER_ALIGNMENT);
        license2.setAlignmentX(Component.CENTER_ALIGNMENT);
        license3.setAlignmentX(Component.CENTER_ALIGNMENT);
        license4.setAlignmentX(Component.CENTER_ALIGNMENT);

        // add to child panel
        child.add(new JLabel("\n"));
        child.add(mekwars);
        child.add(version);
        child.add(new JLabel("\n"));
        child.add(license1);
        child.add(license2);
        child.add(new JLabel("\n"));
        child.add(license3);
        child.add(license4);
        child.add(new JLabel("\n"));

        // then add child panel to the content pane.
        dlg.getContentPane().add(child);
        dlg.setLocationRelativeTo(parentFrame);
        dlg.setModal(true);
        dlg.setResizable(false);
        dlg.pack();
        dlg.setVisible(true);
    }

    // Show how to create fluff images for Record Sheets
    public void jMenuHelpFluff_actionPerformed() {

        // make the dialog
        JDialog dlg = new JDialog(parentFrame, "Image Help");

        // set up the contents
        JPanel child = new JPanel();
        child.setLayout(new BoxLayout(child, BoxLayout.Y_AXIS));

        // set the text up.
        JTextArea recordSheetImageHelp = new JTextArea();

        recordSheetImageHelp.setEditable(false);

        recordSheetImageHelp.setText("To add a fluff image to a record sheet the following steps need to be taken\nPlease Note that currently only \'Mechs use fluff Images\nPlace the image you want to use in the data/images/fluff folder\nMegaMekLab will attempt to match the name of the \'Mech your are printing\nwith the images in the fluff folder.\nThe following is an example of how MegaMekLab look for the image\nExample\nYour \'Mech is called Archer ARC-7Q\nMegaMekLab would look for the following\n\nArcher ARC-7Q.jpg/png/gif\nARC-7Q.jpg/png/gif\nArcher.jpg/png/gif\nhud.png\n");
        // center everything
        recordSheetImageHelp.setAlignmentX(Component.CENTER_ALIGNMENT);

        // add to child panel
        child.add(recordSheetImageHelp);

        // then add child panel to the content pane.
        dlg.getContentPane().add(child);

        // set the location of the dialog
        dlg.setLocationRelativeTo(parentFrame);
        dlg.setModal(true);
        dlg.setResizable(false);
        dlg.pack();
        dlg.setVisible(true);
    }

    public void jMenuConfiguration_actionPerformed(ActionEvent event) {
        new ConfigurationDialog(parentFrame).setVisible(true);
        parentFrame.refreshAll();
    }

    private void jMenuLoadVehicle() {
        new megameklab.com.ui.Vehicle.MainUI();
        parentFrame.dispose();
    }

    private void jMenuLoadBattleArmor() {
        new megameklab.com.ui.BattleArmor.MainUI();
        parentFrame.dispose();
    }

    private void jMenuLoadMech() {
        new megameklab.com.ui.Mek.MainUI(false, false);
        parentFrame.dispose();
    }
    
    private void jMenuLoadPrimitiveMech() {
        new megameklab.com.ui.Mek.MainUI(true, false);
        parentFrame.dispose();
    }

    private void jMenuLoadAero() {
        new megameklab.com.ui.Aero.MainUI(false);
        parentFrame.dispose();
    }

    private void jMenuLoadPrimitiveAero() {
        new megameklab.com.ui.Aero.MainUI(true);
        parentFrame.dispose();
    }
    
    private void jMenuLoadDropship() {
        new megameklab.com.ui.aerospace.DropshipMainUI(false);
        parentFrame.dispose();
    }

    private void jMenuLoadPrimitiveDropship() {
        new megameklab.com.ui.aerospace.DropshipMainUI(true);
        parentFrame.dispose();
    }
    
    private void jMenuLoadAdvAero() {
        new megameklab.com.ui.aerospace.AdvancedAeroUI(false);
        parentFrame.dispose();
    }

    private void jMenuLoadPrimitiveJumpship() {
        new megameklab.com.ui.aerospace.AdvancedAeroUI(true);
        parentFrame.dispose();
    }

    private void jMenuLoadInfantry() {
    	new megameklab.com.ui.Infantry.MainUI();
        parentFrame.dispose();
    }

    private void jMenuPrintCurrentUnit() {
        UnitPrintManager.printEntity(parentFrame.getEntity());
    }

    public void jMenuLoadEntity_actionPerformed(ActionEvent event) {
        loadUnit();
    }

    public void jMenuLoadEntityFromFile_actionPerformed(ActionEvent event) {
        loadUnitFromFile();
    }

    public void jMenuLoadEntityFromFile_actionPerformed(int fileNumber) {
        loadUnitFromFile(fileNumber);
    }

    public void jMenuResetEntity_actionPerformed(ActionEvent event) {
        CConfig.updateSaveFiles("Reset Unit");
        CConfig.setParam(CConfig.CONFIG_SAVE_FILE_1, "");
        Entity en = parentFrame.getEntity();
        if (en instanceof Tank) {
            parentFrame.createNewUnit(Entity.ETYPE_TANK);
        } else if (en instanceof Mech) {
            parentFrame.createNewUnit(Entity.ETYPE_BIPED_MECH, ((Mech)en).isPrimitive(), ((Mech)en).isIndustrial());
        } else if (en.hasETypeFlag(Entity.ETYPE_DROPSHIP)) {
            parentFrame.createNewUnit(Entity.ETYPE_DROPSHIP);
        } else if (en.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
            parentFrame.createNewUnit(Entity.ETYPE_SMALL_CRAFT, ((Aero)en).isPrimitive());
        } else if (en.hasETypeFlag(Entity.ETYPE_SPACE_STATION)) {
            parentFrame.createNewUnit(Entity.ETYPE_SPACE_STATION);
        } else if (en.hasETypeFlag(Entity.ETYPE_WARSHIP)) {
            parentFrame.createNewUnit(Entity.ETYPE_WARSHIP, ((Aero)en).isPrimitive());
        } else if (en.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
            parentFrame.createNewUnit(Entity.ETYPE_JUMPSHIP);
        } else if (parentFrame.getEntity() instanceof Aero) {
            parentFrame.createNewUnit(Entity.ETYPE_AERO, ((Aero)en).isPrimitive());
        } else if (parentFrame.getEntity() instanceof BattleArmor) {
            parentFrame.createNewUnit(Entity.ETYPE_BATTLEARMOR);
        } else if (parentFrame.getEntity() instanceof Infantry) {
            parentFrame.createNewUnit(Entity.ETYPE_INFANTRY);
        } else {
            System.out.println("util.MenuBarCreatoer: " +
                        "Received unknown entityType!");
        }
        setVisible(true);
        reload();
        refresh();
        parentFrame.setVisible(true);
        parentFrame.repaint();
    }

    public void jMenuSaveEntity_actionPerformed(ActionEvent event) {

        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame, "Warning: Saving an invalid unit, it might load incorrectly!");
        }

        String unitName = parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel();
        UnitUtil.compactCriticals(parentFrame.getEntity());

        String filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1);

        if ((filePathName.trim().length() < 1) || !filePathName.contains(unitName)) {
            FileDialog fDialog = new FileDialog(parentFrame, "Save As", FileDialog.SAVE);

            filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_LOC);

            fDialog.setDirectory(filePathName);
            fDialog.setFile(unitName + (parentFrame.getEntity() instanceof Mech?".mtf":".blk"));
            fDialog.setLocationRelativeTo(parentFrame);

            fDialog.setVisible(true);

            if (fDialog.getFile() != null) {
                filePathName = fDialog.getDirectory() + fDialog.getFile();
                CConfig.setParam(CConfig.CONFIG_SAVE_LOC, fDialog.getDirectory());
            } else {
                return;
            }
        }
        try {
            if (parentFrame.getEntity() instanceof Mech) {
                FileOutputStream out = new FileOutputStream(filePathName);
                PrintStream p = new PrintStream(out);

                p.println(((Mech) parentFrame.getEntity()).getMtf());
                p.close();
                out.close();
            } else {
                BLKFile.encode(filePathName, parentFrame.getEntity());
            }
            CConfig.updateSaveFiles(filePathName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(parentFrame, parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel() + " saved to " + filePathName);

    }

    public void jMenuSaveAsEntity_actionPerformed(ActionEvent event) {

        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame, "Warning: Saving an invalid unit, it might load incorrectly!");
        }

        UnitUtil.compactCriticals(parentFrame.getEntity());

        FileDialog fDialog = new FileDialog(parentFrame, "Save As", FileDialog.SAVE);

        String filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_LOC);

        fDialog.setDirectory(filePathName);
        fDialog.setFile(parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel() + (parentFrame.getEntity() instanceof Mech?".mtf":".blk"));
        fDialog.setLocationRelativeTo(parentFrame);

        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            filePathName = fDialog.getDirectory() + fDialog.getFile();
            CConfig.setParam(CConfig.CONFIG_SAVE_LOC, fDialog.getDirectory());
        } else {
            return;
        }

        try {
            if (parentFrame.getEntity() instanceof Mech) {
                FileOutputStream out = new FileOutputStream(filePathName);
                PrintStream p = new PrintStream(out);

                p.println(((Mech) parentFrame.getEntity()).getMtf());
                p.close();
                out.close();
            } else {
                BLKFile.encode(filePathName, parentFrame.getEntity());
            }
            CConfig.updateSaveFiles(filePathName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(parentFrame, parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel() + " saved to " + filePathName);

    }

    public void jMenuExportEntityHTML_actionPerformed(ActionEvent event) {

        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame, "Warning: exporting an invalid unit!");
        }

        String unitName = parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel();
        MechView mview = new MechView(parentFrame.getEntity(), false);

        FileDialog fDialog = new FileDialog(parentFrame, "Save As", FileDialog.SAVE);

        String filePathName = new File(System.getProperty("user.dir").toString()).getAbsolutePath();

        fDialog.setDirectory(filePathName);
        fDialog.setFile(unitName + ".html");
        fDialog.setLocationRelativeTo(parentFrame);

        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            filePathName = fDialog.getDirectory() + fDialog.getFile();
        } else {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(filePathName);
            PrintStream p = new PrintStream(out);
            p.println(mview.getMechReadout());
            p.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void jMenuExportEntityText_actionPerformed(ActionEvent event) {

        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame, "Warning: exporting an invalid unit!");
        }

        String unitName = parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel();
        MechView mview = new MechView(parentFrame.getEntity(), true, true, false);

        FileDialog fDialog = new FileDialog(parentFrame, "Save As", FileDialog.SAVE);

        String filePathName = new File(System.getProperty("user.dir").toString()).getAbsolutePath();

        fDialog.setDirectory(filePathName);
        fDialog.setFile(unitName + ".txt");
        fDialog.setLocationRelativeTo(parentFrame);

        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            filePathName = fDialog.getDirectory() + fDialog.getFile();
        } else {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(filePathName);
            PrintStream p = new PrintStream(out);
            p.println(mview.getMechReadout());
            p.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void jMenuExportEntityClipboard_actionPerformed(ActionEvent event) {
        MechView mview = new MechView(parentFrame.getEntity(), true, true, false);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(mview.getMechReadout());
        clipboard.setContents(stringSelection, this);
    }

    private void loadUnit() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        UnitSelectorDialog viewer = new UnitSelectorDialog(parentFrame, unitLoadingDialog, true);

        Entity newUnit = viewer.getChosenEntity();
        viewer.setVisible(false);
        viewer.dispose();

        if (null == newUnit) {
            return;
        }

        if (UnitUtil.validateUnit(newUnit).trim().length() > 0) {
            JOptionPane.showMessageDialog(parentFrame, String.format(
                    "Warning:Invalid unit, it might load incorrectly!\n%1$s",
                            UnitUtil.validateUnit(newUnit)));
        }

        if (newUnit.getEntityType() != parentFrame.getEntity().getEntityType()) {
            MegaMekLabMainUI newUI = null;
            if (newUnit.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
                newUI = new megameklab.com.ui.aerospace.DropshipMainUI(((Aero)newUnit).isPrimitive());
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
                newUI = new megameklab.com.ui.aerospace.AdvancedAeroUI(((Aero)newUnit).isPrimitive());
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_AERO)
                    && !newUnit.hasETypeFlag(Entity.ETYPE_FIXED_WING_SUPPORT)) {
                newUI = new megameklab.com.ui.Aero.MainUI(((Aero)newUnit).isPrimitive());
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_BATTLEARMOR)) {
                newUI = new megameklab.com.ui.BattleArmor.MainUI();
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_INFANTRY)) {
                newUI = new megameklab.com.ui.Infantry.MainUI();
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_MECH)) {
                newUI = new megameklab.com.ui.Mek.MainUI();
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_TANK)
                    && !newUnit.hasETypeFlag(Entity.ETYPE_GUN_EMPLACEMENT)) {
                newUI = new megameklab.com.ui.Vehicle.MainUI();
            }
            if (null == newUI) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Warning: Could not create new UI, aborting unit load!"
                        +System.lineSeparator()
                        +"Probable cause: Unsupported unit type.");
                return;
            }
            parentFrame.dispose();
            UnitUtil.updateLoadedUnit(newUnit);
            newUI.setEntity(newUnit);
            newUI.reloadTabs();
            newUI.repaint();
            newUI.refreshAll();
            return;
        }

        CConfig.updateSaveFiles("");
        UnitUtil.updateLoadedUnit(newUnit);

        if (viewer.getChosenMechSummary().getSourceFile().getName().endsWith(".zip")) {
            String fileName = viewer.getChosenMechSummary().getSourceFile().getAbsolutePath();
            fileName = fileName.substring(0, fileName.lastIndexOf(File.separatorChar) + 1);
            fileName = fileName + viewer.getChosenMechSummary().getName() + ".mtf";
            CConfig.updateSaveFiles(fileName);
        } else {
            CConfig.updateSaveFiles(viewer.getChosenMechSummary().getSourceFile().getAbsolutePath());
        }
        parentFrame.setEntity(newUnit);
        reload();
        refresh();
        parentFrame.setVisible(true);
    }

    private void loadUnitFromFile() {
        loadUnitFromFile(-1);
    }

    private void loadUnitFromFile(int fileNumber) {

        String filePathName = System.getProperty("user.dir").toString() + "/data/mechfiles/";

        if (fileNumber > 0) {
            switch (fileNumber) {
                case 1:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1);
                    break;
                case 2:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_2);
                    break;
                case 3:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_3);
                    break;
                case 4:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_4);
                    break;
            }
        }

        File unitFile = new File(filePathName);
        if (!(unitFile.isFile())) {
            JFileChooser f = new JFileChooser(filePathName);
            f.setLocation(parentFrame.getLocation().x + 150, parentFrame.getLocation().y + 100);
            f.setDialogTitle("Load Mech");
            f.setDialogType(JFileChooser.OPEN_DIALOG);
            f.setMultiSelectionEnabled(false);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Unit Files", "blk", "mtf", "hmp");

            // Add a filter for mul files
            f.setFileFilter(filter);

            int returnVal = f.showOpenDialog(parentFrame);
            if ((returnVal != JFileChooser.APPROVE_OPTION) || (f.getSelectedFile() == null)) {
                // I want a file, y'know!
                return;
            }
            unitFile = f.getSelectedFile();
        }

        loadUnitFromFile(unitFile);
    }

    private void loadUnitFromFile(File unitFile) {
        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();


            if (null == tempEntity) {
                return;
            }

            if (UnitUtil.validateUnit(tempEntity).trim().length() > 0) {
                JOptionPane.showMessageDialog(parentFrame, String.format(
                        "Warning:Invalid unit, it might load incorrectly!\n%1$s",
                                UnitUtil.validateUnit(tempEntity)));
            }

            if (tempEntity.getEntityType() != parentFrame.getEntity().getEntityType()) {
                MegaMekLabMainUI newUI = null;
                if (tempEntity.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
                    newUI = new megameklab.com.ui.aerospace.DropshipMainUI(((Aero)tempEntity).isPrimitive());
                } else if (tempEntity.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
                    newUI = new megameklab.com.ui.aerospace.AdvancedAeroUI(((Aero)tempEntity).isPrimitive());
                } else if ((tempEntity instanceof Aero)
                        && !(tempEntity instanceof FixedWingSupport)) {
                    newUI = new megameklab.com.ui.Aero.MainUI(((Aero)tempEntity).isPrimitive());
                } else if (tempEntity instanceof BattleArmor) {
                    newUI = new megameklab.com.ui.BattleArmor.MainUI();
                } else if (tempEntity instanceof Infantry) {
                    newUI = new megameklab.com.ui.Infantry.MainUI();
                } else if (tempEntity instanceof Mech) {
                    newUI = new megameklab.com.ui.Mek.MainUI();
                } else if ((tempEntity instanceof Tank)
                        && !(tempEntity instanceof GunEmplacement)) {
                    newUI = new megameklab.com.ui.Vehicle.MainUI();
                }
                if (null == newUI) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Warning: Could not create new UI, aborting unit load!");
                    return;
                }
                parentFrame.dispose();
                UnitUtil.updateLoadedUnit(tempEntity);
                newUI.setEntity(tempEntity);
                newUI.reloadTabs();
                newUI.repaint();
                newUI.refreshAll();
                return;
            }
            parentFrame.setEntity(tempEntity);
            UnitUtil.updateLoadedUnit(parentFrame.getEntity());

            CConfig.updateSaveFiles(unitFile.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format(
                    "Warning:Invalid unit, it might load incorrectly!\n%1$s",
                    ex.getMessage()));
        }
        reload();
        refresh();
        parentFrame.setVisible(true);
    }

    private void refresh() {
        parentFrame.refreshAll();
    }

    private void reload() {
        parentFrame.reloadTabs();
    }

    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1) {

    }

}
=======
/*
 * MegaMekLab - Copyright (C) 2011-2019 The MegaMek Team
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
package megameklab.com.ui;

import megamek.client.ui.swing.UnitLoadingDialog;
import megamek.common.*;
import megamek.common.annotations.Nullable;
import megamek.common.loaders.BLKFile;
import megamek.common.templates.TROView;
import megamek.common.util.EncodeControl;
import megameklab.com.MMLConstants;
import megameklab.com.ui.dialog.ConfigurationDialog;
import megameklab.com.ui.dialog.LoadingDialog;
import megameklab.com.ui.dialog.MegaMekLabUnitSelectorDialog;
import megameklab.com.util.CConfig;
import megameklab.com.util.ImageHelper;
import megameklab.com.util.UnitPrintManager;
import megameklab.com.util.UnitUtil;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ResourceBundle;

public class MenuBar extends JMenuBar implements ClipboardOwner {
    private static final long serialVersionUID = -3998342610654551481L;
    private final JMenu themeMenu;
    private final MegaMekLabMainUI parentFrame;

    private final ResourceBundle resourceMap = ResourceBundle.getBundle("megameklab.resources.Menu", new EncodeControl());

    public MenuBar(MegaMekLabMainUI parent) {

        parentFrame = parent;

        themeMenu = createThemeMenu();
        JMenu fileMenu = createFileMenu();

        JMenuItem item = new JMenuItem();
        item.setText(resourceMap.getString("menu.help.about"));
        item.setMnemonic(KeyEvent.VK_A);
        item.addActionListener(e -> jMenuHelpAbout_actionPerformed());
        JMenu help = new JMenu(resourceMap.getString("menu.help"));
        help.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.help.recordSheetImages"));
        item.setMnemonic(KeyEvent.VK_R);
        item.addActionListener(e -> jMenuHelpFluff_actionPerformed());
        help.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.help.insertImage"));
        item.setMnemonic(KeyEvent.VK_I);
        item.addActionListener(e -> jMenuInsertImageFile_actionPerformed());
        help.add(item);

        JMenu validate = new JMenu(resourceMap.getString("menu.validate"));
        validate.add(loadBVMenuOptions());

        validate.add(loadValidateMenuOptions());

        validate.add(loadSpecsMenuOptions());

        validate.add(loadUnitCostBreakdownMenuOptions());
        
        validate.add(loadUnitWeightBreakdownMenuOptions());

        this.add(fileMenu);
        this.add(validate);
        this.add(help);

    }

    private JMenu loadBVMenuOptions() {
        JMenu bv = new JMenu(resourceMap.getString("menu.validate.bv"));
        JMenuItem item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.bv.currentUnit"));
        item.setMnemonic(KeyEvent.VK_B);
        item.addActionListener(e -> jMenuBVCalculations_actionPerformed());
        bv.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.bv.fromFile"));
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(e -> jMenuGetUnitBVFromFile_actionPerformed());
        bv.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.bv.fromCache"));
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(e -> jMenuGetUnitBVFromCache_actionPerformed());
        bv.add(item);
        return bv;
    }

    private JMenu loadValidateMenuOptions() {
        JMenu entityValidation = new JMenu(resourceMap.getString("menu.validate"));
        JMenuItem item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.currentUnit"));
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(e -> jMenuValidateUnit_actionPerformed());
        entityValidation.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.fromFile"));
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(e -> jMenuGetUnitValidationFromFile_actionPerformed());
        entityValidation.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.fromCache"));
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(e -> jMenuGetUnitValidationFromCache_actionPerformed());
        entityValidation.add(item);
        return entityValidation;
    }

    private JMenu loadUnitCostBreakdownMenuOptions() {
        JMenu entityBreakdown = new JMenu(resourceMap.getString("menu.validate.cost"));
        JMenuItem item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.cost.currentUnit"));
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(e -> jMenuUnitCostBreakdown_actionPerformed());
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.cost.fromFile"));
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(e -> jMenuGetUnitBreakdownFromFile_actionPerformed());
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.cost.fromCache"));
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(e -> jMenuGetUnitBreakdownFromCache_actionPerformed());
        entityBreakdown.add(item);
        return entityBreakdown;
    }
    
    private JMenu loadUnitWeightBreakdownMenuOptions() {
        JMenu entityBreakdown = new JMenu(resourceMap.getString("menu.validate.weight"));
        JMenuItem item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.weight.currentUnit"));
        item.addActionListener(e -> jMenuUnitWeightBreakdown_actionPerformed());
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.weight.fromFile"));
        item.addActionListener(e -> jMenuGetUnitWeightBreakdownFromFile_actionPerformed());
        entityBreakdown.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.weight.fromCache"));
        item.addActionListener(e -> jMenuGetUnitWeightBreakdownFromCache_actionPerformed());
        entityBreakdown.add(item);
        return entityBreakdown;
    }

    private JMenu loadSpecsMenuOptions() {
        JMenu unitSpecs = new JMenu(resourceMap.getString("menu.validate.specs"));
        JMenuItem item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.specs.currentUnit"));
        item.setMnemonic(KeyEvent.VK_V);
        item.addActionListener(e -> jMenuUnitSpecs_actionPerformed());
        unitSpecs.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.specs.fromFile"));
        item.setMnemonic(KeyEvent.VK_F);
        item.addActionListener(e -> jMenuGetUnitSpecsFromFile_actionPerformed());
        unitSpecs.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.validate.specs.fromCache"));
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(e -> jMenuGetUnitSpecsFromCache_actionPerformed());
        unitSpecs.add(item);
        return unitSpecs;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu(resourceMap.getString("menu.file"));

        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem item = new JMenuItem(resourceMap.getString("menu.file.resetCurrentUnit"));
        item.setMnemonic(KeyEvent.VK_R);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(this::jMenuResetEntity_actionPerformed);
        fileMenu.add(item);

        JMenu unitMenu = new JMenu(resourceMap.getString("menu.file.switchUnitType"));
        unitMenu.setMnemonic(KeyEvent.VK_S);
        Entity en = parentFrame.getEntity();

        if (!(en instanceof Mech)
                || ((Mech)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.mech"));
            item.setMnemonic(KeyEvent.VK_M);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadMech());
            unitMenu.add(item);
        }

        if (!en.isFighter()
                || ((en instanceof Aero) && ((Aero)en).isPrimitive())) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.fighter"));
            item.setMnemonic(KeyEvent.VK_A);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadAero());
            unitMenu.add(item);
        }

        if (!(en instanceof SmallCraft)
                || ((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.dropshipSmallCraft"));
            item.setMnemonic(KeyEvent.VK_D);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadDropship());
            unitMenu.add(item);
        }

        if (!(en instanceof Jumpship)
                || ((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.advancedAero"));
            item.setMnemonic(KeyEvent.VK_J);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadAdvAero());
            unitMenu.add(item);
        }

        if (!(parentFrame.getEntity() instanceof Tank)
                || parentFrame.getEntity().isSupportVehicle()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.combatVehicle"));
            item.setMnemonic(KeyEvent.VK_T);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadVehicle());
            unitMenu.add(item);
        }

        if (!parentFrame.getEntity().isSupportVehicle()) {
            item = new JMenuItem();
            item.setText("Support Vehicle");
            item.addActionListener(e -> jMenuLoadSupportVehicle());
            unitMenu.add(item);
        }

        if (!(parentFrame.getEntity() instanceof BattleArmor)) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.battleArmor"));
            item.setMnemonic(KeyEvent.VK_B);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadBattleArmor());
            unitMenu.add(item);
        }

        if (!parentFrame.getEntity().isConventionalInfantry()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.infantry"));
            item.setMnemonic(KeyEvent.VK_I);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            item.addActionListener(e -> jMenuLoadInfantry());
            unitMenu.add(item);
        }
        
        if (!parentFrame.getEntity().hasETypeFlag(Entity.ETYPE_PROTOMECH)) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.protomech"));
            item.setMnemonic(KeyEvent.VK_P);
            item.addActionListener(ev -> jMenuLoadProtomech());
            unitMenu.add(item);
        }

        JMenu pMenu = new JMenu(resourceMap.getString("menu.file.unitType.primitive"));
        if (!(en instanceof Mech)
                || !((Mech)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.mech"));
            item.addActionListener(e ->jMenuLoadPrimitiveMech());
            pMenu.add(item);
        }
        
        if (!(en.isFighter())
                || (en instanceof Aero && !((Aero)en).isPrimitive())) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.aero"));
            item.addActionListener(e ->jMenuLoadPrimitiveAero());
            pMenu.add(item);
        }
        
        if (!(en instanceof SmallCraft)
                || !((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.dropshipSmallCraft"));
            item.addActionListener(e ->jMenuLoadPrimitiveDropship());
            pMenu.add(item);
        }
        
        if (!(en instanceof Jumpship)
                || !((Aero)en).isPrimitive()) {
            item = new JMenuItem();
            item.setText(resourceMap.getString("menu.file.unitType.jumpship"));
            item.addActionListener(e ->jMenuLoadPrimitiveJumpship());
            pMenu.add(item);
        }
        
        unitMenu.add(pMenu);

        fileMenu.add(unitMenu);

        JMenu loadMenu = new JMenu(resourceMap.getString("menu.file.load"));
        loadMenu.setMnemonic(KeyEvent.VK_L);
        item = new JMenuItem();

        item.setText(resourceMap.getString("menu.file.load.fromCache"));
        item.setMnemonic(KeyEvent.VK_C);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(this::jMenuLoadEntity_actionPerformed);
        loadMenu.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.file.load.fromFile"));
        item.setMnemonic(KeyEvent.VK_F);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(this::jMenuLoadEntityFromFile_actionPerformed);
        loadMenu.add(item);

        fileMenu.add(loadMenu);

        fileMenu.add(UnitPrintManager.printMenu(parentFrame));
        fileMenu.add(UnitPrintManager.exportMenu(parentFrame));

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.file.save"));
        item.setMnemonic(KeyEvent.VK_S);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(this::jMenuSaveEntity_actionPerformed);
        fileMenu.add(item);

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.file.saveAs"));
        item.setMnemonic(KeyEvent.VK_A);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(this::jMenuSaveAsEntity_actionPerformed);
        fileMenu.add(item);

        JMenu exportMenu = new JMenu(resourceMap.getString("menu.file.export"));
        
        item = new JMenuItem(resourceMap.getString("menu.file.export.toHTML"));
        item.addActionListener(e -> exportSummary(true));
        exportMenu.add(item);

        item = new JMenuItem(resourceMap.getString("menu.file.export.toText"));
        item.addActionListener(e -> exportSummary(false));
        exportMenu.add(item);

        item = new JMenuItem(resourceMap.getString("menu.file.export.toClipboard"));
        item.addActionListener(e -> exportSummaryClipboard());
        exportMenu.add(item);

        fileMenu.add(exportMenu);
        
        fileMenu.add(themeMenu);

        item = new JMenuItem(resourceMap.getString("menu.file.configuration"));
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(this::jMenuConfiguration_actionPerformed);
        fileMenu.add(item);

        int fileNumber = 1;
        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1).length() > 1) {
            fileMenu.addSeparator();
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(e -> jMenuLoadEntityFromFile_actionPerformed(1));

            fileMenu.add(item);
            fileNumber++;
        }

        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_2).length() > 1) {
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_2);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(e -> jMenuLoadEntityFromFile_actionPerformed(2));

            fileMenu.add(item);
            fileNumber++;
        }

        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_3).length() > 1) {
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_3);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(e -> jMenuLoadEntityFromFile_actionPerformed(3));

            fileMenu.add(item);
            fileNumber++;
        }

        if (CConfig.getParam(CConfig.CONFIG_SAVE_FILE_4).length() > 1) {
            item = new JMenuItem();
            String newFile = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_4);
            if (newFile.length() > 35) {
                item.setText(fileNumber + ". .." + newFile.substring(newFile.length() - 36));
            } else {
                item.setText(fileNumber + ". " + newFile);
            }
            item.setMnemonic(fileNumber);
            item.addActionListener(e -> jMenuLoadEntityFromFile_actionPerformed(4));

            fileMenu.add(item);
        }

        fileMenu.addSeparator();

        item = new JMenuItem();
        item.setText(resourceMap.getString("menu.file.exit"));
        item.setMnemonic(KeyEvent.VK_X);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.addActionListener(ev -> parentFrame.exit());
        fileMenu.add(item);

        return fileMenu;
    }
    
    /**
     * Creates a menu that includes all installed look and feel options
     * 
     * @return The new menu
     */
    private JMenu createThemeMenu() {
        JMenu menu = new JMenu(resourceMap.getString("menu.file.themes"));
        JCheckBoxMenuItem item;
        for (LookAndFeelInfo plaf : UIManager.getInstalledLookAndFeels()) {
            item = new JCheckBoxMenuItem(plaf.getName());
            if (plaf.getName().equalsIgnoreCase(
                    UIManager.getLookAndFeel().getName())) {
                item.setSelected(true);
            }
            menu.add(item);
            item.addActionListener(ev -> {
                parentFrame.changeTheme(plaf);
                refreshThemeMenu(plaf.getName());
            });
        }
        return menu;
    }
    
    /**
     * Updates the checkbox items on the theme menu to show which is currently selected.
     * 
     * @param currentThemeName The name returned by {@link LookAndFeelInfo#getName()}
     */
    private void refreshThemeMenu(String currentThemeName) {
        for (int i = 0; i < themeMenu.getItemCount(); i++) {
            final JMenuItem item = themeMenu.getItem(i);
            if (item instanceof JCheckBoxMenuItem) {
                item.setSelected(item.getText().equals(currentThemeName));
            }
        }
    }

    private void jMenuGetUnitBVFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        MegaMekLabUnitSelectorDialog viewer = new MegaMekLabUnitSelectorDialog(parentFrame, unitLoadingDialog);
        UnitUtil.showBVCalculations(parentFrame, viewer.getChosenEntity());
    }

    private void jMenuGetUnitValidationFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        MegaMekLabUnitSelectorDialog viewer = new MegaMekLabUnitSelectorDialog(parentFrame, unitLoadingDialog);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        UnitUtil.showValidation(tempEntity, parentFrame);

    }

    private void jMenuGetUnitSpecsFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        MegaMekLabUnitSelectorDialog viewer = new MegaMekLabUnitSelectorDialog(parentFrame, unitLoadingDialog);

        Entity tempEntity = viewer.getChosenEntity();
        if(null == tempEntity) {
            return;
        }
        UnitUtil.showUnitSpecs(tempEntity, parentFrame);

    }

    private void jMenuGetUnitBreakdownFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        MegaMekLabUnitSelectorDialog viewer = new MegaMekLabUnitSelectorDialog(parentFrame, unitLoadingDialog);
        UnitUtil.showUnitCostBreakDown(parentFrame, viewer.getChosenEntity());
    }
    
    private void jMenuGetUnitWeightBreakdownFromCache_actionPerformed() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        MegaMekLabUnitSelectorDialog viewer = new MegaMekLabUnitSelectorDialog(parentFrame, unitLoadingDialog);

        Entity tempEntity = viewer.getChosenEntity();
        if (null == tempEntity) {
            return;
        }
        UnitUtil.showUnitWeightBreakDown(tempEntity, parentFrame);
    }

    private void jMenuGetUnitBVFromFile_actionPerformed() {
        File unitFile = loadUnitFile();
        if (unitFile == null) {
            return;
        }

        try {
            UnitUtil.showBVCalculations(parentFrame, new MechFileParser(unitFile).getEntity());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    String.format(resourceMap.getString("message.invalidUnit.format"),
                            ex.getMessage()));
        }

    }

    private void jMenuGetUnitValidationFromFile_actionPerformed() {
        File unitFile = loadUnitFile();
        if (unitFile == null) return;

        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();
            UnitUtil.showValidation(tempEntity, parentFrame);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    String.format(resourceMap.getString("message.invalidUnit.format"),
                    ex.getMessage()));
        }
    }

    private @Nullable
    File loadUnitFile() {
        String filePathName = System.getProperty("user.dir") + "/data/mechfiles/"; // TODO : remove inline file path
        FileDialog fDialog = new FileDialog(parentFrame,
                resourceMap.getString("dialog.chooseUnit.title"),
                FileDialog.LOAD);
        fDialog.setLocationRelativeTo(parentFrame);
        fDialog.setMultipleMode(false);
        fDialog.setDirectory(filePathName);
        fDialog.setFilenameFilter(unitFilesFilter);

        fDialog.setVisible(true);
        if (fDialog.getFile() == null) {
            return null;
        }
        return new File(fDialog.getDirectory(), fDialog.getFile());
    }

    private void jMenuGetUnitBreakdownFromFile_actionPerformed() {
        File unitFile = loadUnitFile();
        if (unitFile == null) {
            return;
        }

        try {
            UnitUtil.showUnitCostBreakDown(parentFrame, new MechFileParser(unitFile).getEntity());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    String.format(resourceMap.getString("message.invalidUnit.format"),
                            ex.getMessage()));
        }
    }
    
    private void jMenuGetUnitWeightBreakdownFromFile_actionPerformed() {
        File unitFile = loadUnitFile();
        if (unitFile == null) {
            return;
        }

        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();
            UnitUtil.showUnitWeightBreakDown(tempEntity, parentFrame);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    String.format(resourceMap.getString("message.invalidUnit.format"),
                            ex.getMessage()));
        }
    }    

    private void jMenuGetUnitSpecsFromFile_actionPerformed() {
        File unitFile = loadUnitFile();
        if (unitFile == null) {
            return;
        }

        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();
            UnitUtil.showUnitSpecs(tempEntity, parentFrame);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    String.format(resourceMap.getString("message.invalidUnit.format"),
                            ex.getMessage()));
        }
    }

    private void jMenuInsertImageFile_actionPerformed() {
        File unitFile = loadUnitFile();
        if (unitFile == null) {
            return;
        }

        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();

            if (UnitUtil.validateUnit(parentFrame.getEntity()).trim().length() > 0) {
                JOptionPane.showMessageDialog(parentFrame,
                        resourceMap.getString("message.invalidUnit.text"));
            }

            FileDialog fDialog = new FileDialog(parentFrame,
                    resourceMap.getString("dialog.imagePath.title"), FileDialog.LOAD);

            if (parentFrame.getEntity().getFluff().getMMLImagePath().trim().length() > 0) {
                String fullPath = new File(parentFrame.getEntity().getFluff().getMMLImagePath()).getAbsolutePath();
                String imageName = fullPath.substring(fullPath.lastIndexOf(File.separatorChar) + 1);
                fullPath = fullPath.substring(0, fullPath.lastIndexOf(File.separatorChar) + 1);
                fDialog.setDirectory(fullPath);
                fDialog.setFile(imageName);
            } else {
                fDialog.setDirectory(new File(ImageHelper.fluffPath).getAbsolutePath() + File.separatorChar + "mech" + File.separatorChar);
                fDialog.setFile(parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel() + ".png");
            }

            fDialog.setLocationRelativeTo(parentFrame);

            fDialog.setVisible(true);

            if (fDialog.getFile() != null) {
                String relativeFilePath = new File(fDialog.getDirectory() + fDialog.getFile()).getAbsolutePath();
                relativeFilePath = "." + File.separatorChar + relativeFilePath.substring(new File(System.getProperty("user.dir")).getAbsolutePath().length() + 1);
                parentFrame.getEntity().getFluff().setMMLImagePath(relativeFilePath);
                BLKFile.encode(unitFile.getAbsolutePath(), tempEntity);
            }
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
        }
    }

    private void jMenuBVCalculations_actionPerformed() {
        UnitUtil.showBVCalculations(parentFrame, parentFrame.getEntity());
    }

    private void jMenuUnitCostBreakdown_actionPerformed() {
        UnitUtil.showUnitCostBreakDown(parentFrame, parentFrame.getEntity());
    }
    
    private void jMenuUnitWeightBreakdown_actionPerformed() {
        UnitUtil.showUnitWeightBreakDown(parentFrame.getEntity(), parentFrame);
    }

    private void jMenuUnitSpecs_actionPerformed() {
        UnitUtil.showUnitSpecs(parentFrame.getEntity(), parentFrame);
    }

    // Show Validation data.
    private void jMenuValidateUnit_actionPerformed() {
        UnitUtil.showValidation(parentFrame.getEntity(), parentFrame);
    }

    // Show data about MegaMekLab
    private void jMenuHelpAbout_actionPerformed() {

        // make the dialog
        JDialog dlg = new JDialog(parentFrame, resourceMap.getString("menu.help.about.title"));

        // set up the contents
        JPanel child = new JPanel();
        child.setLayout(new BoxLayout(child, BoxLayout.Y_AXIS));

        // set the text up.
        JLabel version = new JLabel(String.format(resourceMap.getString("menu.help.about.version.format"),
                MMLConstants.VERSION));
        JLabel license1 = new JLabel(resourceMap.getString("menu.help.about.license.1"));
        JLabel license2 = new JLabel(resourceMap.getString("menu.help.about.license.2"));
        JLabel license3 = new JLabel(resourceMap.getString("menu.help.about.info.1"));
        JLabel license4 = new JLabel(resourceMap.getString("menu.help.about.info.2"));

        // center everything
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        license1.setAlignmentX(Component.CENTER_ALIGNMENT);
        license2.setAlignmentX(Component.CENTER_ALIGNMENT);
        license3.setAlignmentX(Component.CENTER_ALIGNMENT);
        license4.setAlignmentX(Component.CENTER_ALIGNMENT);

        // add to child panel
        child.add(new JLabel("\n"));
        child.add(version);
        child.add(new JLabel("\n"));
        child.add(license1);
        child.add(license2);
        child.add(new JLabel("\n"));
        child.add(license3);
        child.add(license4);
        child.add(new JLabel("\n"));

        // then add child panel to the content pane.
        dlg.getContentPane().add(child);
        dlg.setLocationRelativeTo(parentFrame);
        dlg.setModal(true);
        dlg.setResizable(false);
        dlg.pack();
        dlg.setVisible(true);
    }

    // Show how to create fluff images for Record Sheets
    private void jMenuHelpFluff_actionPerformed() {

        // make the dialog
        JDialog dlg = new JDialog(parentFrame, resourceMap.getString("menu.help.imageHelp.title"));

        // set up the contents
        JPanel child = new JPanel();
        child.setLayout(new BoxLayout(child, BoxLayout.Y_AXIS));

        // set the text up.
        JTextArea recordSheetImageHelp = new JTextArea();

        recordSheetImageHelp.setEditable(false);

        recordSheetImageHelp.setText(resourceMap.getString("menu.help.imageHelp.text"));
        // center everything
        recordSheetImageHelp.setAlignmentX(Component.CENTER_ALIGNMENT);

        // add to child panel
        child.add(recordSheetImageHelp);

        // then add child panel to the content pane.
        dlg.getContentPane().add(child);

        // set the location of the dialog
        dlg.setLocationRelativeTo(parentFrame);
        dlg.setModal(true);
        dlg.setResizable(false);
        dlg.pack();
        dlg.setVisible(true);
    }

    private void jMenuConfiguration_actionPerformed(ActionEvent event) {
        new ConfigurationDialog(parentFrame).setVisible(true);
        parentFrame.refreshAll();
    }

    private void jMenuLoadVehicle() {
        newUnit(Entity.ETYPE_TANK, false, false, null);
    }

    private void jMenuLoadSupportVehicle() {
        newUnit(Entity.ETYPE_SUPPORT_TANK, false, false, null);
    }

    private void jMenuLoadBattleArmor() {
        newUnit(Entity.ETYPE_BATTLEARMOR, false, false, null);
    }

    private void jMenuLoadMech() {
        newUnit(Entity.ETYPE_MECH, false, false, null);
    }
    
    private void jMenuLoadPrimitiveMech() {
        newUnit(Entity.ETYPE_MECH, true, false, null);
    }

    private void jMenuLoadAero() {
        newUnit(Entity.ETYPE_AERO, false, false, null);
    }

    private void jMenuLoadPrimitiveAero() {
        newUnit(Entity.ETYPE_AERO, true, false, null);

    }
    
    private void jMenuLoadDropship() {
        newUnit(Entity.ETYPE_DROPSHIP, false, false, null);
    }

    private void jMenuLoadPrimitiveDropship() {
        newUnit(Entity.ETYPE_DROPSHIP, true, false, null);
    }
    
    private void jMenuLoadAdvAero() {
        newUnit(Entity.ETYPE_JUMPSHIP, false, false, null);
    }

    private void jMenuLoadPrimitiveJumpship() {
        newUnit(Entity.ETYPE_JUMPSHIP, true, false, null);
    }

    private void jMenuLoadInfantry() {
        newUnit(Entity.ETYPE_INFANTRY, false, false, null);
    }

    private void jMenuLoadProtomech() {
        newUnit(Entity.ETYPE_PROTOMECH, false, false, null);

    }

    private void jMenuLoadEntity_actionPerformed(ActionEvent event) {
        loadUnit();
    }

    private void jMenuLoadEntityFromFile_actionPerformed(ActionEvent event) {
        loadUnitFromFile();
    }

    private void jMenuLoadEntityFromFile_actionPerformed(int fileNumber) {
        loadUnitFromFile(fileNumber);
    }

    private void jMenuResetEntity_actionPerformed(ActionEvent event) {
        CConfig.updateSaveFiles("Reset Unit");
        CConfig.setParam(CConfig.CONFIG_SAVE_FILE_1, "");
        Entity en = parentFrame.getEntity();
        if (en instanceof Tank) {
            parentFrame.createNewUnit(Entity.ETYPE_TANK);
        } else if (en instanceof Mech) {
            parentFrame.createNewUnit(Entity.ETYPE_BIPED_MECH, en.isPrimitive(), ((Mech)en).isIndustrial());
        } else if (en.hasETypeFlag(Entity.ETYPE_DROPSHIP)) {
            parentFrame.createNewUnit(Entity.ETYPE_DROPSHIP, en.isPrimitive());
        } else if (en.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
            parentFrame.createNewUnit(Entity.ETYPE_SMALL_CRAFT, en.isPrimitive());
        } else if (en.hasETypeFlag(Entity.ETYPE_SPACE_STATION)) {
            parentFrame.createNewUnit(Entity.ETYPE_SPACE_STATION);
        } else if (en.hasETypeFlag(Entity.ETYPE_WARSHIP)) {
            parentFrame.createNewUnit(Entity.ETYPE_WARSHIP, en.isPrimitive());
        } else if (en.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
            parentFrame.createNewUnit(Entity.ETYPE_JUMPSHIP);
        } else if (parentFrame.getEntity() instanceof Aero) {
            parentFrame.createNewUnit(Entity.ETYPE_AERO, en.isPrimitive());
        } else if (parentFrame.getEntity() instanceof BattleArmor) {
            parentFrame.createNewUnit(Entity.ETYPE_BATTLEARMOR);
        } else if (parentFrame.getEntity() instanceof Infantry) {
            parentFrame.createNewUnit(Entity.ETYPE_INFANTRY);
        } else if (parentFrame.getEntity() instanceof Protomech) {
            parentFrame.createNewUnit(Entity.ETYPE_PROTOMECH);
        } else {
            LogManager.getLogger().warn("Received unknown entityType!");
        }
        setVisible(true);
        reload();
        refresh();
        parentFrame.setVisible(true);
        parentFrame.repaint();
    }

    /**
     * Constructs a file name for the current Entity using the chassis and model name and the
     * correct extension for the unit type. Any character that is not legal for a Windows filename
     * is replace by an underscore.
     *
     * @param entity The Entity
     * @return       A default filename for the Entity
     */
    private String createUnitFilename(Entity entity) {
        String fileName = (entity.getChassis() + " " + entity.getModel()).trim();
        fileName = fileName.replaceAll("[/\\\\<>:\"|?*]", "_");
        if (entity instanceof Mech) {
            return fileName + ".mtf";
        } else {
            return fileName + ".blk";
        }
    }

    private void jMenuSaveEntity_actionPerformed(ActionEvent event) {

        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame,
                    resourceMap.getString("message.invalidUnit.text"));
        }

        String fileName = createUnitFilename(parentFrame.getEntity());
        UnitUtil.compactCriticals(parentFrame.getEntity());

        String filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1);

        if ((filePathName.trim().length() < 1) || !filePathName.contains(fileName)) {
            FileDialog fDialog = new FileDialog(parentFrame,
                    resourceMap.getString("dialog.saveAs.title"),
                    FileDialog.SAVE);

            filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_LOC);

            fDialog.setDirectory(filePathName);
            fDialog.setFile(fileName);
            fDialog.setLocationRelativeTo(parentFrame);

            fDialog.setVisible(true);

            if (fDialog.getFile() != null) {
                filePathName = fDialog.getDirectory() + fDialog.getFile();
                CConfig.setParam(CConfig.CONFIG_SAVE_LOC, fDialog.getDirectory());
            } else {
                return;
            }
        }
        try {
            if (parentFrame.getEntity() instanceof Mech) {
                FileOutputStream out = new FileOutputStream(filePathName);
                PrintStream p = new PrintStream(out);

                p.println(((Mech) parentFrame.getEntity()).getMtf());
                p.close();
                out.close();
            } else {
                BLKFile.encode(filePathName, parentFrame.getEntity());
            }
            CConfig.updateSaveFiles(filePathName);
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
        }

        JOptionPane.showMessageDialog(parentFrame,
                String.format(resourceMap.getString("dialog.saveAs.message.format"),
                        parentFrame.getEntity().getChassis(),
                        parentFrame.getEntity().getModel(), filePathName));

    }

    private void jMenuSaveAsEntity_actionPerformed(ActionEvent event) {
        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame,
                    resourceMap.getString("message.savingInvalidUnit.text"));
        }

        UnitUtil.compactCriticals(parentFrame.getEntity());

        FileDialog fDialog = new FileDialog(parentFrame,
                resourceMap.getString("dialog.saveAs.title"), FileDialog.SAVE);

        String filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_LOC);

        fDialog.setDirectory(filePathName);
        fDialog.setFile(createUnitFilename(parentFrame.getEntity()));
        fDialog.setLocationRelativeTo(parentFrame);

        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            filePathName = fDialog.getDirectory() + fDialog.getFile();
            CConfig.setParam(CConfig.CONFIG_SAVE_LOC, fDialog.getDirectory());
        } else {
            return;
        }

        try {
            if (parentFrame.getEntity() instanceof Mech) {
                FileOutputStream out = new FileOutputStream(filePathName);
                PrintStream p = new PrintStream(out);

                p.println(((Mech) parentFrame.getEntity()).getMtf());
                p.close();
                out.close();
            } else {
                BLKFile.encode(filePathName, parentFrame.getEntity());
            }
            CConfig.updateSaveFiles(filePathName);
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
        }

        JOptionPane.showMessageDialog(parentFrame,
                String.format(resourceMap.getString("dialog.saveAs.message.format"),
                        parentFrame.getEntity().getChassis(),
                        parentFrame.getEntity().getModel(), filePathName));
    }
    
    private String entitySummaryText(boolean html) {
        if (CConfig.getBooleanParam(CConfig.SUMMARY_FORMAT_TRO)) {
            TROView view = TROView.createView(parentFrame.getEntity(), html);
            return view.processTemplate();
        } else {
            MechView view = new MechView(parentFrame.getEntity(), !html, false, html);
            return view.getMechReadout();
        }
    }

    private void exportSummary(boolean html) {
        if (UnitUtil.validateUnit(parentFrame.getEntity()).length() > 0) {
            JOptionPane.showMessageDialog(parentFrame,
                    resourceMap.getString("message.exportingInvalidUnit.text"));
        }

        String unitName = parentFrame.getEntity().getChassis() + " " + parentFrame.getEntity().getModel();

        FileDialog fDialog = new FileDialog(parentFrame,
                resourceMap.getString("dialog.saveAs.title"), FileDialog.SAVE);
        String filePathName = new File(System.getProperty("user.dir")).getAbsolutePath();
        fDialog.setDirectory(filePathName);
        fDialog.setFile(unitName + (html?".html" : ".txt"));
        fDialog.setLocationRelativeTo(parentFrame);
        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            filePathName = fDialog.getDirectory() + fDialog.getFile();
        } else {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(filePathName);
            PrintStream p = new PrintStream(out);
            p.println(entitySummaryText(html));
            p.close();
            out.close();
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
        }
    }

    private void exportSummaryClipboard() {
        final String summaryText = entitySummaryText(false);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(summaryText);
        clipboard.setContents(stringSelection, this);
    }

    private void loadUnit() {
        UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(parentFrame);
        unitLoadingDialog.setVisible(true);
        MegaMekLabUnitSelectorDialog viewer = new MegaMekLabUnitSelectorDialog(parentFrame, unitLoadingDialog);

        Entity newUnit = viewer.getChosenEntity();
        viewer.setVisible(false);
        viewer.dispose();

        if (null == newUnit) {
            return;
        }

        if (UnitUtil.validateUnit(newUnit).trim().length() > 0) {
            JOptionPane.showMessageDialog(parentFrame, String.format(
                    resourceMap.getString("message.invalidUnit.format"),
                            UnitUtil.validateUnit(newUnit)));
        }

        if (newUnit.getEntityType() != parentFrame.getEntity().getEntityType()) {
            if (newUnit.isSupportVehicle()) {
                newUnit(Entity.ETYPE_SUPPORT_TANK, false, false, newUnit);
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
                newUnit(Entity.ETYPE_DROPSHIP, ((Aero)newUnit).isPrimitive(), false, newUnit);
            } else if (newUnit.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
                newUnit(Entity.ETYPE_JUMPSHIP, ((Aero)newUnit).isPrimitive(), false, newUnit);
            } else if ((newUnit instanceof Aero)
                    && !(newUnit instanceof FixedWingSupport)) {
                newUnit(Entity.ETYPE_AERO, ((Aero)newUnit).isPrimitive(), false, newUnit);
            } else if (newUnit instanceof BattleArmor) {
                newUnit(Entity.ETYPE_BATTLEARMOR, false, false, newUnit);
            } else if (newUnit instanceof Infantry) {
                newUnit(Entity.ETYPE_INFANTRY, false, false, newUnit);
            } else if (newUnit instanceof Mech) {
                newUnit(Entity.ETYPE_MECH, false, false, newUnit);
            } else if (newUnit instanceof Protomech) {
                newUnit(Entity.ETYPE_PROTOMECH, false, false, newUnit);
            } else if ((newUnit instanceof Tank)
                    && !(newUnit instanceof GunEmplacement)) {
                newUnit(Entity.ETYPE_TANK, false, false, newUnit);
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        resourceMap.getString("message.abortUnitLoad.text"));
            }
            return;
        }

        UnitUtil.updateLoadedUnit(newUnit);

        if (viewer.getChosenMechSummary().getSourceFile().getName().endsWith(".zip")) {
            String fileName = viewer.getChosenMechSummary().getSourceFile().getAbsolutePath();
            fileName = fileName.substring(0, fileName.lastIndexOf(File.separatorChar) + 1);
            fileName = fileName + createUnitFilename(newUnit);
            CConfig.updateSaveFiles(fileName);
        } else {
            CConfig.updateSaveFiles(viewer.getChosenMechSummary().getSourceFile().getAbsolutePath());
        }
        parentFrame.setEntity(newUnit);
        reload();
        refresh();
        parentFrame.setVisible(true);
    }

    private void loadUnitFromFile() {
        loadUnitFromFile(-1);
    }

    private void loadUnitFromFile(int fileNumber) {
        String filePathName = System.getProperty("user.dir") + "/data/mechfiles/";

        if (fileNumber > 0) {
            switch (fileNumber) {
                case 1:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_1);
                    break;
                case 2:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_2);
                    break;
                case 3:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_3);
                    break;
                case 4:
                    filePathName = CConfig.getParam(CConfig.CONFIG_SAVE_FILE_4);
                    break;
            }
        }

        File unitFile = new File(filePathName);
        if (!(unitFile.isFile())) {
            unitFile = loadUnitFile();
            if (unitFile == null) {
                // I want a file, y'know!
                return;
            }
        }

        loadUnitFromFile(unitFile);
    }

    private void loadUnitFromFile(File unitFile) {
        try {
            Entity tempEntity = new MechFileParser(unitFile).getEntity();


            if (null == tempEntity) {
                return;
            }

            if (UnitUtil.validateUnit(tempEntity).trim().length() > 0) {
                JOptionPane.showMessageDialog(parentFrame, String.format(
                        resourceMap.getString("message.invalidUnit.format"),
                                UnitUtil.validateUnit(tempEntity)));
            }

            if (tempEntity.getEntityType() != parentFrame.getEntity().getEntityType()) {
                if (tempEntity.isSupportVehicle()) {
                    newUnit(Entity.ETYPE_SUPPORT_TANK, false, false, tempEntity);
                } else if (tempEntity.hasETypeFlag(Entity.ETYPE_SMALL_CRAFT)) {
                    newUnit(Entity.ETYPE_DROPSHIP, ((Aero)tempEntity).isPrimitive(), false, tempEntity);
                } else if (tempEntity.hasETypeFlag(Entity.ETYPE_JUMPSHIP)) {
                    newUnit(Entity.ETYPE_JUMPSHIP, ((Aero)tempEntity).isPrimitive(), false, tempEntity);
                } else if ((tempEntity instanceof Aero)
                        && !(tempEntity instanceof FixedWingSupport)) {
                    newUnit(Entity.ETYPE_AERO, ((Aero)tempEntity).isPrimitive(), false, tempEntity);
                } else if (tempEntity instanceof BattleArmor) {
                    newUnit(Entity.ETYPE_BATTLEARMOR, false, false, tempEntity);
                } else if (tempEntity instanceof Infantry) {
                    newUnit(Entity.ETYPE_INFANTRY, false, false, tempEntity);
                } else if (tempEntity instanceof Mech) {
                    newUnit(Entity.ETYPE_MECH, false, false, tempEntity);
                } else if (tempEntity instanceof Protomech) {
                    newUnit(Entity.ETYPE_PROTOMECH, false, false, tempEntity);
                } else if ((tempEntity instanceof Tank)
                        && !(tempEntity instanceof GunEmplacement)) {
                    newUnit(Entity.ETYPE_TANK, false, false, tempEntity);
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            resourceMap.getString("message.abortUnitLoad.text"));
                }
                return;
            }
            parentFrame.setEntity(tempEntity);
            UnitUtil.updateLoadedUnit(parentFrame.getEntity());

            CConfig.updateSaveFiles(unitFile.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, String.format(
                    resourceMap.getString("message.invalidUnit.format"),
                    ex.getMessage()));
        }
        reload();
        refresh();
        parentFrame.setVisible(true);
    }

    private void refresh() {
        parentFrame.refreshAll();
    }

    private void reload() {
        parentFrame.reloadTabs();
    }
    
    /**
     * This function will create a new mainUI frame (via the loading dialog) for the 
     * given unit type and get rid of the existing frame
     * @param type an <code>int</code> corresponding to the unit type to construct
     */
    private void newUnit(long type, boolean primitive, boolean industrial, Entity en) {
        parentFrame.setVisible(false);
        LoadingDialog ld = new LoadingDialog(parentFrame, type, primitive, industrial, en);
        ld.setVisible(true);
    }

    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1) {

    }

    private final FilenameFilter unitFilesFilter =
            (dir, filename) -> {
                String fn = filename.toLowerCase();
                return fn.endsWith(".mtf")
                        || fn.endsWith(".blk")
                        || fn.endsWith(".hmp");
            };
}
>>>>>>> origin/master:src/megameklab/com/ui/MenuBar.java
