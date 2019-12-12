import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SearchingThread1 extends JFrame implements Runnable {

    private Thread myThread;

    private Vector<String> array;
    private File file;
    private String subString;
    private String mask;
    private boolean withDirectories;

    private Pattern p = null;
    private DefaultListModel listModel;

    private JPanel panel1;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton closeButton;
    private JList listOfFiles;

    SearchingThread1(File file, String mask, String subString, boolean withDirectories) {
        setBounds(200, 200, 700, 500);
        setTitle("Information about search thread");
        setResizable(false);
        myThread = new Thread(this, "itself");
        myThread.start();

        this.array = new Vector<>();
        this.file = file;
        this.mask = mask;
        this.subString = subString;
        this.withDirectories = withDirectories;
        listModel = new DefaultListModel();

        listOfFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOfFiles.setModel(listModel);


        this.pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!myThread.isInterrupted()) {
                        myThread.interrupt();
                        pauseButton.setText("Resume");
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
                Thread.interrupted();
            }
        });

        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    myThread.interrupted();
                }
                catch (Exception exc) {
                    JOptionPane.showMessageDialog(null, exc);
                }
            }
        });


        setVisible(true);
    }

    //проверка имени файла
    private boolean accept(String name) {
        Matcher m = p.matcher(name);
        //System.out.println(p + " " + name + " " + m.matches());
        return(name.contains(mask));
        //return (m.matches());
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
        p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
        search(file);
    }

    //сам поиск
    private void search (File topDirectory) {
         try {
             File[] list = topDirectory.listFiles();
             for (File f: list) {
                 //если это директория и просматривать поддиректории
                 if (f.isDirectory() && withDirectories) {
                     search(f);
                     continue;
                 }

                 //если это файл, подходит маска и содержится подстрока
                 if (f.isFile()) {
                     if (accept(f.getName())) {
                         if (withSubstring(f)) {
                             //System.out.println(f.getAbsolutePath());
                             listModel.addElement(f.getAbsolutePath());
                         }
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
            System.out.println("Count of files: " +array.size());
            for (String s: array) {
                System.out.println(s);
            }
        }
        catch (Exception e) {
            System.err.println("Error in \"run\": " + e);
        }
    }
}
