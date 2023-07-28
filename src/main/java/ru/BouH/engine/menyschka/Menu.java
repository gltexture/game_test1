package ru.BouH.engine.menyschka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Menu{

    // Функции игрового движка
    private boolean function1Enabled;
    private boolean function2Enabled;
    private boolean function3Enabled;

    public Menu() {
        function1Enabled = true;
        function2Enabled = true;
        function3Enabled = true;
        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame("Game Engine Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //  карта для хранения флажков и связанных с ними функций
        Map<JCheckBox, Runnable> checkboxFunctionMap = new HashMap<>();

        // добавили пункты меню с флажками и связываем их с функциями игрового движка
        addMenuItem(panel, checkboxFunctionMap, "Function 1", () -> function1Enabled = !function1Enabled);
        addMenuItem(panel, checkboxFunctionMap, "Function 2", () -> function2Enabled = !function2Enabled);
        addMenuItem(panel, checkboxFunctionMap, "Function 3", () -> function3Enabled = !function3Enabled);
        JButton applyButton = new JButton("Применить");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Применяем изменения в функциях
                for (JCheckBox checkBox : checkboxFunctionMap.keySet()) {
                    checkboxFunctionMap.get(checkBox).run();
                }
            }
        });

        panel.add(applyButton);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void addMenuItem(JPanel panel, Map<JCheckBox, Runnable> checkboxFunctionMap, String itemName, Runnable function) {
        JCheckBox checkBox = new JCheckBox(itemName);
        checkBox.setSelected(true);
        checkboxFunctionMap.put(checkBox, function);
        panel.add(checkBox);
    }

    // сами игровые функции, которые будут выполняться в игровом цикле
    private void gameLoop() {
        if (function1Enabled) {
            // Выполнение функции 1
            System.out.println("Function 1 is enabled");
        }

        if (function2Enabled) {
            // Выполнение функции 2
            System.out.println("Function 2 is enabled");
        }

        if (function3Enabled) {
            // Выполнение функции 3
            System.out.println("Function 3 is enabled");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Menu().gameLoop();
            }
        });
    }
}
