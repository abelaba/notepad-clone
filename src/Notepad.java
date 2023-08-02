
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
public class Notepad extends JFrame implements ActionListener, WindowListener{

    JTextArea jta = new JTextArea();
    File fnameContainer;
    private JTextField searchField;
    private JTextField replaceField;
    private JButton searchButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JPanel searchReplacePanel;

    public Notepad(){
        Font font = new Font("Arial",Font.PLAIN,15);
        Container con = getContentPane();
        JMenuBar jmb = new JMenuBar();
        JMenu jmfile = new JMenu("File");
        JMenu jmedit = new JMenu("Edit");
        JMenu jmhelp = new JMenu("Help");

        con.setLayout(new BorderLayout());

        JScrollPane sbrText = new JScrollPane(jta);
        sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sbrText.setVisible(true);

        jta.setFont(font);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);

        con.add(sbrText);
        createMenuItem(jmfile, "New");
        createMenuItem(jmfile, "Open");
        createMenuItem(jmfile, "Save");
        jmfile.addSeparator();
        createMenuItem(jmfile, "Exit");

        createMenuItem(jmedit, "Cut");
        createMenuItem(jmedit, "Copy");
        createMenuItem(jmedit, "Paste");
        createMenuItem(jmedit,"FindAndReplace");


        createMenuItem(jmhelp, "About Notepad");
        createMenuItem(jmfile, "Toggle Dark Mode");


        jmb.add(jmfile);
        jmb.add(jmedit);
        jmb.add(jmhelp);

        setJMenuBar(jmb);

        setIconImage(Toolkit.getDefaultToolkit().getImage("https://images.all-free-download.com/images/graphiclarge/notepad_37173.jpg"));
        addWindowListener(this);
        setSize(500, 500);
        setTitle("Untitled Text");
        setVisible(true);

        // Add spacing between menu items
        addSpacingToMenuItems(jmfile);
        addSpacingToMenuItems(jmedit);
        addSpacingToMenuItems(jmhelp);

        // Create search and replace components
        searchField = new JTextField(20);
        replaceField = new JTextField(20);
        searchButton = new JButton("Find");
        replaceButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");

        // Add action listeners to buttons
        searchButton.addActionListener(this);
        replaceButton.addActionListener(this);
        replaceAllButton.addActionListener(this);

        // Create a panel to hold search and replace components
        searchReplacePanel = new JPanel();
        searchReplacePanel.setLayout(new FlowLayout());
        searchReplacePanel.add(new JLabel("Find: "));
        searchReplacePanel.add(searchField);
        searchReplacePanel.add(searchButton);
        searchReplacePanel.add(new JLabel("Replace: "));
        searchReplacePanel.add(replaceField);
        searchReplacePanel.add(replaceButton);
        searchReplacePanel.add(replaceAllButton);
        searchReplacePanel.setVisible(false);

        // Add the search and replace panel to the frame
        con.add(searchReplacePanel, BorderLayout.NORTH);


    }

    public void createMenuItem(JMenu jm, String text){
        JMenuItem jmi = new JMenuItem(text);
        jmi.addActionListener(this);
        jm.add(jmi);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        if(e.getActionCommand().equals("New")){
            this.setTitle("Untitled.txt");
            jta.setText("");
            fnameContainer = null;
        } else if (e.getActionCommand().equals("Open")) {
            int ret = jfc.showDialog(null, "Open");
            if(ret==JFileChooser.APPROVE_OPTION){
                try{
                    File fyl = jfc.getSelectedFile();
                    openFile(fyl.getAbsolutePath());
                    this.setTitle(fyl.getName());
                    fnameContainer = fyl;
                }catch(IOException exception){

                }
            }

        } else if (e.getActionCommand().equals("Save")) {
            if(fnameContainer!=null){
                jfc.setCurrentDirectory(fnameContainer);
                jfc.setSelectedFile(fnameContainer);
            }else {
                jfc.setSelectedFile(new File("Untitled.txt"));
            }
            int ret = jfc.showSaveDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION){
                try{
                    File fyl = jfc.getSelectedFile();
                    saveFile(fyl.getAbsolutePath());
                    this.setTitle(fyl.getName());
                    fnameContainer = fyl;
                }catch(Exception exception){}
            }
        } else if (e.getActionCommand().equals("Cut")) {
            jta.cut();

        }
        else if(e.getActionCommand().equals("Copy")){
            jta.copy();
        } else if (e.getActionCommand().equals("Paste")) {
            jta.paste();

        }else if (e.getActionCommand().equals("About Notepad")) {
            JOptionPane.showMessageDialog(this, "Created by: Abel", "Notepad", JOptionPane.INFORMATION_MESSAGE);

        } else if (e.getActionCommand().equals("Toggle Dark Mode")) {
            boolean isDarkMode = jta.getBackground().equals(Color.BLACK);
            toggleDarkMode(!isDarkMode);
        } else if (e.getActionCommand().equals("Exit")) {
            exiting();
        }

        if (e.getSource() == searchButton) {
            performSearch();
        } else if (e.getSource() == replaceButton) {
            performReplace();
        } else if (e.getSource() == replaceAllButton) {
            performReplaceAll();
        }else if (e.getActionCommand().equals("FindAndReplace")) {
            boolean isVisible = searchReplacePanel.isVisible();
            searchReplacePanel.setVisible(!isVisible);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText();
        String text = jta.getText();
        int foundIndex = text.indexOf(searchTerm);


        if (foundIndex != -1) {
            jta.setCaretPosition(foundIndex);
            jta.setSelectionStart(foundIndex);
            jta.setSelectionEnd(foundIndex + searchTerm.length());
        }
    }

    private void performReplace() {
        String searchTerm = searchField.getText();
        String replacement = replaceField.getText();
        String text = jta.getText();
        String newText = text.replaceFirst(searchTerm, replacement);
        jta.setText(newText);
    }

    private void performReplaceAll() {
        String searchTerm = searchField.getText();
        String replacement = replaceField.getText();
        String text = jta.getText();
        String newText = text.replaceAll(searchTerm, replacement);

        jta.setText(newText);
    }


    private void openFile(String absolutePath) throws IOException{
        BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath)));
        String l;
        jta.setText("");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        while((l=d.readLine())!=null){
            jta.setText(jta.getText()+l+"\r\n");
        }

        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        d.close();
    }

    private void saveFile(String absolutePath) throws IOException{
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataOutputStream o = new DataOutputStream(new FileOutputStream(absolutePath));
        o.writeBytes(jta.getText());
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    private void exiting() {
        System.exit(0);
    }

    private void toggleDarkMode(boolean darkMode) {
        if (darkMode) {
            // Set dark mode colors
            jta.setBackground(Color.BLACK);
            jta.setForeground(Color.WHITE);
        } else {
            // Set default light mode colors
            jta.setBackground(Color.WHITE);
            jta.setForeground(Color.BLACK);
        }
    }

    private void addSpacingToMenuItems(JMenu menu) {
        for (Component menuComponent : menu.getMenuComponents()) {
            if (menuComponent instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) menuComponent;
                menuItem.setBorder(BorderFactory.createCompoundBorder(
                        new EmptyBorder(5, 10, 5, 10), // Set spacing here (top, left, bottom, right)
                        menuItem.getBorder()));
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        exiting();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
