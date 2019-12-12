import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class SearchingThread extends JFrame implements Runnable {

    private Thread myThread;

    //изначальные параметры
    private File file;
    private String subString;
    private boolean withDirectories;

    //вспомогательные параметры
    private Pattern p = null;
    private Matcher m = null;
    private DefaultListModel listModel;

    private JPanel panel;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton closeButton;
    private JList listOfFiles;
    private JScrollPane scroolPane;

    private boolean isPause = false;
    private boolean needToStop = false;

    SearchingThread(File file, String mask, String subString, boolean withDirectories, int priority) {
        myThread = new Thread(this, "itself");
        myThread.setPriority(priority);
        //начальные установки для окна
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(200, 200, 690, 500);

        panel = new JPanel();
        panel.setLayout(null);

        pauseButton = new JButton("Pause thread");
        pauseButton.setLocation(50, 400);
        pauseButton.setSize(150,35);
        panel.add(pauseButton);

        stopButton = new JButton("Stop thread");
        stopButton.setLocation(270, 400);
        stopButton.setSize(150, 35);
        panel.add(stopButton);

        closeButton = new JButton("Close app");
        closeButton.setLocation(490, 400);
        closeButton.setSize(150, 35);
        panel.add(closeButton);

        listOfFiles = new JList();

        scroolPane = new JScrollPane(listOfFiles);
        scroolPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroolPane.setLocation(40, 20);
        scroolPane.setSize(610, 360);
        scroolPane.setVisible(true);
        panel.add(scroolPane);

        setContentPane(panel);
        setTitle("Information about search thread");
        setResizable(false);

        this.file = file;
        this.subString = subString;
        this.withDirectories = withDirectories;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            switch (mask.charAt(i)) {
                case ('?'): {
                    sb.append('.');
                    break;
                }
                case ('*'): {
                    sb.append(".*");
                    break;
                }
                case ('.'): {
                    sb.append("\\.");
                    break;
                }
                default: {
                    sb.append(mask.charAt(i));
                }
            }
        }
        p = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
        System.out.println(p);

        listModel = new DefaultListModel();

        listOfFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOfFiles.setModel(listModel);


        this.pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!isPause) {
                        isPause = true;
                        pauseButton.setText("Resume thread");
                        myThread.suspend();
                    }
                    else {
                        isPause = false;
                        pauseButton.setText("Pause thread");
                        myThread.resume();
                    }
                }
                catch (Exception exc) {
                    JOptionPane.showMessageDialog(null, exc);
                }
            }
        });
        this.stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                needToStop = true;
                if (listModel.size() == 0) {
                    listModel.addElement("Search had been stopped");
                }
            }
        });
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    needToStop = true;
                    dispose();
                }
                catch (Exception exc) {
                    JOptionPane.showMessageDialog(null, exc);
                }
            }
        });

        setVisible(true);
        myThread.start();
    }

    //проверка имени файла
    private boolean accept(String name) {
        m = p.matcher(name);
        return(m.matches());
    }

    //проверка содержимого
    private boolean withSubstring(File f) {
        try {
            if (subString.equals("")) {
                return (true);
            }
            Scanner sc = new Scanner(new FileReader(f.getAbsoluteFile()));
            String s;
            while (sc.hasNextLine()) {
                if ((s = sc.nextLine()) != null) {
                    if (s.contains(subString)) {
                        sc.close();
                        return (true);
                    }
                }
            }
            sc.close();
            return (false);
        }
        catch (Exception exc) {
            System.err.println("Error in \"withSubstring\": " + exc);
        }
        return(false);
    }

    //начальные приготовления для поиска
    private void preparation() {
        search(file);
    }

    //сам поиск
    private void search (File topDirectory) {
        try {
            File[] list = topDirectory.listFiles();
            for (File f: list) {
                /*while (isPause) {
                    try {
                        synchronized (this) {
                            if (needToStop) {
                                isPause = false;
                            }
                            myThread.wait();
                        }
                    }
                    catch (Exception exc) {
                        System.err.println("Error in \"wait()\": " + exc);
                    }
                }*/
                if (needToStop) {
                    return;
                }
                //если это директория и просматривать поддиректории
                if (f.isDirectory() && withDirectories) {
                    search(f);
                    continue;
                }

                //если это файл, подходит маска и содержится подстрока
                if (f.isFile()) {
                    if (accept(f.getName())) {
                        if (f.getName().endsWith(".txt")) {
                            if (withSubstring(f)) {
                                TimeUnit.MILLISECONDS.sleep(10);
                                System.out.println(f.getAbsolutePath());
                                listModel.addElement(f.getAbsolutePath());
                                continue;
                            }
                        }
                        TimeUnit.MILLISECONDS.sleep(10);
                        System.out.println(f.getAbsolutePath());
                        listModel.addElement(f.getAbsolutePath());
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error in \"search()\": " + e);
        }
    }

    public void run() {
        try {
            preparation();
            if (listModel.getSize() == 0) {
                listModel.addElement("There are any files");
            }
        }
        catch (Exception e) {
            System.err.println("Error in \"run\": " + e);
        }
    }
}
