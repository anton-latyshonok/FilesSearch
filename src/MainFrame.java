import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class MainFrame extends JFrame implements ActionListener {
    private JPanel mainFrame;
    private JButton chooseDirectoryButton;
    private JTextField absolutePath;
    private JTextField textField1;
    private JTextField textField2;
    private JComboBox priorityButton;
    private JCheckBox includeSubdirectoriesCheckBox;
    private JButton startNewSearchThreadButton;
    private JButton closeButton;
    private JLabel countOfThreads;


    private boolean pathIsChosen = false;
    private boolean withSubdirectories = false;
    private File file;

    MainFrame() {
        try {
            setBounds(100, 100, 650, 450);
            this.setTitle("Main window");

            countOfThreads.setText(Integer.toString(Thread.activeCount()));

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setResizable(false);
            setContentPane(mainFrame);
        }
        catch (Exception ex) {
            System.err.println("Error in initialization: " + ex);
        }


        //выбор директории
        try {
            this.chooseDirectoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (e.getSource() == chooseDirectoryButton) {
                            JFileChooser fileChooser = new JFileChooser("C:");

                            fileChooser.setAcceptAllFileFilterUsed(false);
                            fileChooser.setMultiSelectionEnabled(false);
                            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


                            int result = fileChooser.showDialog(null, "Choose directory");
                            if (result == JFileChooser.APPROVE_OPTION) {
                                pathIsChosen = true;
                                file = fileChooser.getSelectedFile();
                                absolutePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                            }
                        }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null, "Error in \"actionPerformed\" \"chooseDirectoryButton\":" + exc);
                    }
                }
            });
        }
        catch (Exception ex) {
            System.err.println("Error in this.chooseDirectoryButton: " + ex);
        }

        //изменение галочки
        try {
            this.includeSubdirectoriesCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (e.getSource() == includeSubdirectoriesCheckBox) {
                            withSubdirectories = !withSubdirectories;
                        }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null, "Error in \"actionPerformed\" \"includeSubdirectoriesCheckBox\":" + exc);
                    }
                }
            });
        }
        catch (Exception ex) {
            System.err.println("Error in this.includeSubdirectoriesCheclBox: " + ex);
        }

        //старт нового потока
        try {
            this.startNewSearchThreadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (e.getSource() == startNewSearchThreadButton) {
                            if (pathIsChosen) {
                                if (textField1.getText().length() != 0) {
                                    //System.out.println("Путь файла: " + file.getAbsolutePath() + ", маска: " + textField1.getText() + ", подстрока: " + textField2.getText() + ", поиск в поддиректориях: " + withSubdirectories);
                                    SearchingThread st = new SearchingThread(file, textField1.getText(), textField2.getText(), withSubdirectories, priorityButton.getSelectedIndex() + 1);
                                    countOfThreads.setText(Integer.toString(Thread.activeCount()));
                                    while (st.isActive()) {
                                        Thread.sleep(1000);
                                    }
                                    countOfThreads.setText(Integer.toString(Thread.activeCount()));
                                } else {
                                    JOptionPane.showMessageDialog(null, "Mask isn't entered!!!");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Unchosen directory!!!");
                            }
                        }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null, "Error in \"actionPerformed\" \"startNewSearchThreadButton\":" + exc);
                    }
                }
            });
        }
        catch (Exception ex) {
            System.err.println("Error in this.startNewSearchThreadButton: " + ex);
        }

        //закрытие окна
        try {
            this.closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (e.getSource() == closeButton) {
                            System.exit(0);
                        }
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null, "Error in \"actionPerformed\" \"closeButton\":" + exc);
                    }
                }
            });
        }
        catch (Exception ex) {
            System.err.println("Error in this.closeButton: " + ex);
        }




        setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
    }

}
