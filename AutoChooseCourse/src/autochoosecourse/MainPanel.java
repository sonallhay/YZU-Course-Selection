/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autochoosecourse;

import static autochoosecourse.OnlineCheck.driver;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.json.JSONException;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import org.apache.commons.lang3.StringUtils;

class MyDocumentFilter extends DocumentFilter {

    private int maxLength = 0;

    public MyDocumentFilter(int len) {
        this.maxLength = len;
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
        if (fb.getDocument().getLength() > this.maxLength) {
            super.remove(fb, i, i);
        }
        for (int n = string.length(); n > 0; n--) {//an inserted string may be more than a single character i.e a copy and paste of 'aaa123d', also we iterate from the back as super.XX implementation will put last insterted string first and so on thus 'aa123d' would be 'daa', but because we iterate from the back its 'aad' like we want
            char c = string.charAt(n - 1);//get a single character of the string
            if ((c >= 33 && c <= 126)) {//if its an alphabetic character or white space
                super.replace(fb, i, i1, String.valueOf(c), as);//allow update to take place for the given character
            }
        }
    }

    @Override
    public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
            throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);
        if (sb.toString().length() == 0) {
            super.replace(fb, offset, length, "", null);
        } else {
            super.remove(fb, offset, length);
        }
    }
}

class EnDocumentFilter extends DocumentFilter {

    private int maxLength = 0;

    public EnDocumentFilter(int len) {
        this.maxLength = len;
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
        if (fb.getDocument().getLength() > this.maxLength) {
            super.remove(fb, i, i);
        }
        for (int n = string.length(); n > 0; n--) {//an inserted string may be more than a single character i.e a copy and paste of 'aaa123d', also we iterate from the back as super.XX implementation will put last insterted string first and so on thus 'aa123d' would be 'daa', but because we iterate from the back its 'aad' like we want
            char c = string.charAt(n - 1);//get a single character of the string
            if ((c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) {//if its an alphabetic character or white space
                super.replace(fb, i, i1, String.valueOf(c), as);//allow update to take place for the given character
            }
        }
    }

    @Override
    public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
            throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);
        if (sb.toString().length() == 0) {
            super.replace(fb, offset, length, "", null);
        } else {
            super.remove(fb, offset, length);
        }
    }
}

class PostTip extends JPanel {

    public PostTip(FocusEvent evt) {
        JComponent component = (JComponent) evt.getSource();
        MouseEvent phantom = new MouseEvent(component, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 10, 10, 0, false);
        ToolTipManager.sharedInstance().mouseMoved(phantom);
    }

}

public class MainPanel extends javax.swing.JFrame {

    JFrame frontFrame = null;
    String semesterYear = null;
    String portalAccount = null;
    String portalPassword = null;
    GetClassInfo parseData = null;
    RoutineChoosen testSearchPage = null;
    DefaultTableModel model;
    MusicInfo genVol = null;
    Map<String, String> AccountInfo = new HashMap<String, String>() {
        {
            put("mail", null);
            put("lineEvent", null);
            put("lineKey", null);
        }
    };

    public MainPanel(String semesY, String account, String pwd, JFrame frame) {
        this.semesterYear = semesY;
        this.portalAccount = account;
        this.portalPassword = pwd;
        this.frontFrame = frame;
        this.parseData = new GetClassInfo(this.semesterYear + "CoursesData");
        frontFrame.setVisible(false);
        initComponents();
        model = (DefaultTableModel) courses.getModel();
        getData();
        getAccountinfo();
        setIcon();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        tablePane = new javax.swing.JPanel();
        courseID = new javax.swing.JTextField();
        courseClass = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_add = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        courses = new javax.swing.JTable();
        btn_del = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        check_line = new javax.swing.JCheckBox();
        check_mail = new javax.swing.JCheckBox();
        check_alert = new javax.swing.JCheckBox();
        hint = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        test_mail = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        test_line = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        semesYear = new javax.swing.JTextField(this.semesterYear);
        warningPanel = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("border.png"));
                    g.drawImage(img, 0, 0, this);
                }catch(Exception e){}

            }
        };
        jLabel7 = new javax.swing.JLabel();
        forward = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        myAccount = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        myPassword = new javax.swing.JTextField();
        edit_key_btn = new javax.swing.JButton();
        event_text = new javax.swing.JLabel();
        line_key_label = new javax.swing.JLabel();
        edit_mail_btn = new javax.swing.JButton();
        line_event_label = new javax.swing.JLabel();
        key_text = new javax.swing.JLabel();
        mail_label = new javax.swing.JLabel();
        edit_event_btn = new javax.swing.JButton();
        mail_text = new javax.swing.JLabel();
        back0 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        test_alert = new javax.swing.JToggleButton();
        configPane = new javax.swing.JPanel();
        method = new javax.swing.JComboBox<>();
        keepTime = new javax.swing.JComboBox<>();
        wait = new javax.swing.JComboBox<>();
        delay = new javax.swing.JComboBox<>();
        back1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btn_monitor = new javax.swing.JToggleButton();
        jLabel14 = new javax.swing.JLabel();
        defaultConfig = new javax.swing.JButton();
        loopNotice = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        retrieve = new javax.swing.JCheckBox();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Monitor");
        setMinimumSize(new java.awt.Dimension(840, 660));
        setSize(new java.awt.Dimension(840, 610));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tablePane.setVisible(true);

        ((PlainDocument)courseID.getDocument()).setDocumentFilter(new EnDocumentFilter(8));
        courseID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                courseIDActionPerformed(evt);
            }
        });
        courseID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                courseIDKeyPressed(evt);
            }
        });

        ((PlainDocument)courseClass.getDocument()).setDocumentFilter(new EnDocumentFilter(5));
        courseClass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                courseClassKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("課號 :");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("班別 : ");

        btn_add.setBackground(new java.awt.Color(153, 204, 255));
        btn_add.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        btn_add.setText("新增");
        btn_add.registerKeyboardAction(btn_add.getActionForKeyStroke(
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
        JComponent.WHEN_FOCUSED);
    btn_add.registerKeyboardAction(btn_add.getActionForKeyStroke(
        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
    JComponent.WHEN_FOCUSED);
    btn_add.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn_add.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btn_addActionPerformed(evt);
        }
    });

    jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jScrollPane1MouseClicked(evt);
        }
    });

    courses.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 14)); // NOI18N
    courses.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {
            "課名", "課號", "班別", "時間", "教師"
        }
    ) {
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }

    });
    courses.setGridColor(new java.awt.Color(51, 102, 255));
    courses.setSelectionBackground(new java.awt.Color(204, 204, 204));
    courses.setSelectionForeground(new java.awt.Color(102, 153, 255));
    courses.getColumnModel().getColumn(0).setPreferredWidth(50);
    courses.getColumnModel().getColumn(1).setPreferredWidth(10);
    courses.getColumnModel().getColumn(2).setPreferredWidth(5);
    courses.getColumnModel().getColumn(3).setPreferredWidth(50);
    courses.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    courses.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JComponent jc = (JComponent)c;
            jc.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
            c.setBackground(row % 2 != 0 ? Color.LIGHT_GRAY : Color.WHITE);
            return c;
        }
    });
    courses.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            coursesKeyPressed(evt);
        }
    });
    jScrollPane1.setViewportView(courses);

    btn_del.setBackground(new java.awt.Color(153, 204, 255));
    btn_del.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
    btn_del.setText("移除");
    btn_del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn_del.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btn_delActionPerformed(evt);
        }
    });

    jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/Cat.gif"))); // NOI18N

    check_line.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    check_line.setText("LINE");
    check_line.setToolTipText("以傳送Line訊息的方式，告知您課程有空位。");
    check_line.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            check_lineFocusGained(evt);
        }
    });
    check_line.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            check_lineActionPerformed(evt);
        }
    });

    check_mail.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    check_mail.setText("MAIL");
    check_mail.setToolTipText("以傳送郵件信箱的方式，告知您課程有空位。");
    check_mail.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            check_mailFocusGained(evt);
        }
    });
    check_mail.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            check_mailActionPerformed(evt);
        }
    });

    check_alert.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
    check_alert.setText("SOUND ALERT");
    check_alert.setToolTipText("以電腦播放音樂的方式，告知您課程有空位。");
    check_alert.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            check_alertFocusGained(evt);
        }
    });
    check_alert.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            check_alertActionPerformed(evt);
        }
    });

    hint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    hint.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
    hint.setText("使用說明");
    hint.setPreferredSize(new java.awt.Dimension(48, 25));
    hint.setForeground(Color.BLUE);
    hint.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            hintMouseClicked(evt);
        }
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            hintMouseEntered(evt);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            hintMouseExited(evt);
        }
    });

    jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/arror.png"))); // NOI18N

    test_mail.setText("test");
    test_mail.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            test_mailActionPerformed(evt);
        }
    });

    jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/arror.png"))); // NOI18N

    test_line.setText("test");
    test_line.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            test_lineActionPerformed(evt);
        }
    });

    jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    jLabel6.setText("學年度 :");

    semesYear.setFont(new Font("Arial", Font.BOLD, 16));
    semesYear.setEditable(false);
    semesYear.setEnabled(false);
    semesYear.setDisabledTextColor(new Color(150, 150, 150));

    jLabel7.setText("<html>『"+semesterYear+"』此學期在按開始紐後，必須要出現在學校的選課系統上，如果沒有，系統會要求您回到上一頁輸入正確的學期和年份喔!<br> PS : 沒事不要最大化，雖然可以(`・ω・´) </html>");
    jLabel7.setFont(new Font("微軟正黑體", Font.BOLD, 12));
    jLabel7.setForeground(Color.RED);

    javax.swing.GroupLayout warningPanelLayout = new javax.swing.GroupLayout(warningPanel);
    warningPanel.setLayout(warningPanelLayout);
    warningPanelLayout.setHorizontalGroup(
        warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, warningPanelLayout.createSequentialGroup()
            .addContainerGap(20, Short.MAX_VALUE)
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(21, 21, 21))
    );
    warningPanelLayout.setVerticalGroup(
        warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(warningPanelLayout.createSequentialGroup()
            .addGap(22, 22, 22)
            .addComponent(jLabel7)
            .addContainerGap(18, Short.MAX_VALUE))
    );

    forward.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/forward.png")));
    forward.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            forwardMouseEntered(evt);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            forwardMouseExited(evt);
        }
    });
    forward.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            forwardActionPerformed(evt);
        }
    });

    jLabel8.setText("Portal 帳號 :");

    myAccount.setText(this.portalAccount);
    myAccount.setFont(new Font("Arial", Font.BOLD, 16));
    myAccount.setEditable(false);
    myAccount.setEnabled(false);
    myAccount.setDisabledTextColor(new Color(150, 150, 150));

    jLabel9.setText("Portal 密碼 : ");

    myPassword.setText(StringUtils.repeat("*", this.portalPassword.length()));
    myPassword.setFont(new Font("微軟正黑體", Font.BOLD, 16));
    myPassword.setEditable(false);
    myPassword.setEnabled(false);
    myPassword.setDisabledTextColor(new Color(150, 150, 150));

    edit_key_btn.setVisible(false);
    edit_key_btn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            edit_key_btnActionPerformed(evt);
        }
    });

    event_text.setText("jLabel4");
    event_text.setVisible(false);

    line_key_label.setText("Key : ");
    line_key_label.setForeground(Color.red);
    line_key_label.setVisible(false);

    edit_mail_btn.setVisible(false);
    edit_mail_btn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            edit_mail_btnActionPerformed(evt);
        }
    });

    line_event_label.setText("WebhooksEventName : ");
    line_event_label.setForeground(Color.red);
    line_event_label.setVisible(false);

    key_text.setText("sadsadsa");
    key_text.setVisible(false);

    mail_label.setEnabled(true);
    mail_label.setText("Mail : ");
    mail_label.setForeground(Color.red);
    mail_label.setVisible(false);

    edit_event_btn.setVisible(false);
    edit_event_btn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            edit_event_btnActionPerformed(evt);
        }
    });

    mail_text.setText("sadasd");
    mail_text.setVisible(false);

    back0.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    back0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/back.png")));
    back0.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            back0MouseEntered(evt);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            back0MouseExited(evt);
        }
    });
    back0.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            back0ActionPerformed(evt);
        }
    });

    jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/arror.png"))); // NOI18N
    jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/arror.png"))); // NOI18N

    test_alert.setText("test");
    test_alert.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            test_alertActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout tablePaneLayout = new javax.swing.GroupLayout(tablePane);
    tablePane.setLayout(tablePaneLayout);
    tablePaneLayout.setHorizontalGroup(
        tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(tablePaneLayout.createSequentialGroup()
            .addComponent(edit_mail_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2)
            .addComponent(mail_label)
            .addGap(0, 0, 0)
            .addComponent(mail_text)
            .addGap(15, 15, 15)
            .addComponent(edit_event_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(1, 1, 1)
            .addComponent(line_event_label)
            .addGap(0, 0, 0)
            .addComponent(event_text)
            .addGap(15, 15, 15)
            .addComponent(edit_key_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(line_key_label)
            .addGap(0, 0, 0)
            .addComponent(key_text)
            .addGap(0, 0, Short.MAX_VALUE))
        .addGroup(tablePaneLayout.createSequentialGroup()
            .addGap(65, 65, 65)
            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tablePaneLayout.createSequentialGroup()
                    .addComponent(back0, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(jLabel6)
                    .addGap(18, 18, 18)
                    .addComponent(semesYear, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(forward, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(61, Short.MAX_VALUE))
                .addGroup(tablePaneLayout.createSequentialGroup()
                    .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(tablePaneLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addComponent(jLabel3)
                            .addGap(25, 25, 25)
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel8))
                            .addGap(12, 12, 12)
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(courseID, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(myAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(20, 20, 20)
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel9))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(tablePaneLayout.createSequentialGroup()
                                    .addComponent(courseClass, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(29, 29, 29)
                                    .addComponent(btn_add, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(28, 28, 28)
                                    .addComponent(btn_del, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(myPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(tablePaneLayout.createSequentialGroup()
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(check_alert)
                                .addGroup(tablePaneLayout.createSequentialGroup()
                                    .addComponent(jLabel18)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(test_alert, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(44, 44, 44)
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tablePaneLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(71, 71, 71))
                                    .addGroup(tablePaneLayout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(test_mail)))
                                .addComponent(check_mail))
                            .addGap(10, 10, 10)
                            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(tablePaneLayout.createSequentialGroup()
                                    .addGap(31, 31, 31)
                                    .addComponent(test_line))
                                .addGroup(tablePaneLayout.createSequentialGroup()
                                    .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(check_line))
                                    .addGap(36, 36, 36)
                                    .addComponent(hint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGap(0, 0, Short.MAX_VALUE))))
    );
    tablePaneLayout.setVerticalGroup(
        tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(tablePaneLayout.createSequentialGroup()
            .addGap(30, 30, 30)
            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(back0, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tablePaneLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(semesYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addComponent(forward, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(2, 2, 2)
            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tablePaneLayout.createSequentialGroup()
                    .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9)
                        .addComponent(myAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(myPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(courseClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(btn_del, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_add, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(courseID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(5, 5, 5)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(14, 14, 14)
            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(check_line)
                .addComponent(check_mail)
                .addComponent(check_alert)
                .addComponent(hint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(test_mail))
                .addComponent(test_line)
                .addComponent(jLabel18)
                .addComponent(jLabel5)
                .addComponent(test_alert, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(20, 20, 20)
            .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mail_label)
                .addComponent(mail_text)
                .addComponent(edit_event_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(line_event_label)
                    .addComponent(event_text))
                .addGroup(tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(line_key_label)
                    .addComponent(edit_key_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(key_text))
                .addComponent(edit_mail_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(45, Short.MAX_VALUE))
    );

    configPane.setVisible(false);

    ((JLabel)method.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    method.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
    method.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "只監控，不選課", "* 自動選課" }));
    method.setSelectedIndex(1);
    method.setAlignmentX(0.0F);
    method.setAlignmentY(0.0F);
    method.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            methodActionPerformed(evt);
        }
    });

    ((JLabel)keepTime.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    keepTime.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
    keepTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "每 10 分鐘一輪", "每 15 分鐘一輪", "每 30 分鐘一輪", "每 60 分鐘一輪", "* 每 120 分鐘一輪", "每 180 分鐘一輪", "每 240 分鐘一輪" }));
    keepTime.setSelectedIndex(4);

    ((JLabel)wait.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    wait.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
    wait.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "休息 1 分鐘", "休息 2 分鐘", "休息 3 分鐘", "* 休息 5 分鐘", "休息 10 分鐘", "休息 20 分鐘", "休息 30 分鐘" }));
    wait.setSelectedIndex(3);

    ((JLabel)delay.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    delay.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
    delay.setMaximumRowCount(30);
    delay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5s", "10s", "20s", "30s", "60s", "120s", "隨機 5s ~ 10s", "隨機 5s ~ 20s", "隨機 5s ~ 30s", "隨機 5s ~ 60s", "隨機 5s ~ 120s", "隨機 10s ~ 20s", "隨機 10s ~ 30s", "* 隨機 10s ~ 60s", "隨機 10s ~ 120s", "隨機 20s ~ 30s", "隨機 20s ~ 60s", "隨機 20s ~ 120s", "隨機 30s ~ 60s", "隨機 30s ~ 120s", "隨機 60s ~ 120s" }));
    delay.setSelectedIndex(13);

    back1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    back1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/back.png")));
    back1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            back1MouseEntered(evt);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            back1MouseExited(evt);
        }
    });
    back1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            back1ActionPerformed(evt);
        }
    });

    jLabel10.setFont(new java.awt.Font("微軟正黑體", 0, 18)); // NOI18N
    jLabel10.setText("選擇方式 : ");

    jLabel11.setFont(new java.awt.Font("微軟正黑體", 0, 18)); // NOI18N
    jLabel11.setText("程式持續執行持間 : ");

    jLabel12.setFont(new java.awt.Font("微軟正黑體", 0, 18)); // NOI18N
    jLabel12.setText("程式暫停間隔時間 : ");

    jLabel13.setFont(new java.awt.Font("微軟正黑體", 0, 18)); // NOI18N
    jLabel13.setText("每次登入選課系統之延遲時間 : ");

    btn_monitor.setBackground(new java.awt.Color(225, 53, 45));
    btn_monitor.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
    btn_monitor.setText("START");
    btn_monitor.setName(""); // NOI18N
    btn_monitor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn_monitor.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btn_monitorActionPerformed(evt);
        }
    });

    jLabel14.setFont(new java.awt.Font("微軟正黑體", 1, 12)); // NOI18N
    jLabel14.setText("( 越長越安全 )");

    defaultConfig.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
    defaultConfig.setText("恢復預設");
    defaultConfig.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            defaultConfigActionPerformed(evt);
        }
    });

    loopNotice.setFont(new java.awt.Font("微軟正黑體", 1, 12)); // NOI18N
    loopNotice.setForeground(new java.awt.Color(51, 112, 224));
    loopNotice.setText("( 重複循環通知 )");
    loopNotice.setVisible(false);

    jLabel15.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
    jLabel15.setForeground(new java.awt.Color(153, 102, 255));
    jLabel15.setText("意見與回報 :  chenpuddingcorgi@gmail.com");

    jLabel16.setFont(new java.awt.Font("微軟正黑體", 1, 12)); // NOI18N
    jLabel16.setText("( 越短越安全 )");

    jLabel17.setFont(new java.awt.Font("微軟正黑體", 1, 12)); // NOI18N
    jLabel17.setText("( 越長越安全 )");

    jLabel20.setForeground(new java.awt.Color(255, 51, 51));

    retrieve.setForeground(new java.awt.Color(255, 51, 0));
    retrieve.setSelected(true);
    retrieve.setText("自動重連瀏覽器(推薦要打勾，滑鼠久放有詳說明)");
    retrieve.setToolTipText("<html>如果打勾，程式開啟的瀏覽器就會「重連」，且在無網路下按 START 也會不斷嘗試連接(需要重連是因為當網路不穩、學校太多人登等問題，常常接收到的網站不完全，程式看到只能重開或關閉)<br>因此當自己網路不穩、斷線或學校網站崩潰等，造成程式無法接收到正常網頁時，打勾表示會重新開啟瀏覽器連接選課系統，反之則遇到損壞的網站程式就會停了(停了自然就不動，只能STOP再START)</html>");
    retrieve.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            retrieveFocusGained(evt);
        }
    });

    javax.swing.GroupLayout configPaneLayout = new javax.swing.GroupLayout(configPane);
    configPane.setLayout(configPaneLayout);
    configPaneLayout.setHorizontalGroup(
        configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(configPaneLayout.createSequentialGroup()
            .addContainerGap(80, Short.MAX_VALUE)
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(configPaneLayout.createSequentialGroup()
                    .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(configPaneLayout.createSequentialGroup()
                            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel11)
                                .addComponent(jLabel10))
                            .addGap(116, 116, 116)
                            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(delay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(wait, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(keepTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(method, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(configPaneLayout.createSequentialGroup()
                            .addComponent(defaultConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20)))
                    .addGap(25, 25, 25)
                    .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(loopNotice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_monitor, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(configPaneLayout.createSequentialGroup()
                    .addComponent(back1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel15)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(retrieve)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap(68, Short.MAX_VALUE))
    );
    configPaneLayout.setVerticalGroup(
        configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(configPaneLayout.createSequentialGroup()
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(configPaneLayout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addComponent(back1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(configPaneLayout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(retrieve))))
            .addGap(48, 48, 48)
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(method, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel10)
                .addComponent(loopNotice))
            .addGap(70, 70, 70)
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(keepTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel11)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(68, 68, 68)
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(wait, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel12)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(65, 65, 65)
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(delay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel13)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(66, 66, 66)
            .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(configPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_monitor, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jLabel20))
            .addContainerGap(57, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tablePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(configPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(configPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void getData() {

        File f = new File("resourses/" + this.semesterYear + "PersonalFav");
        if (f.isFile() && f.canRead()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream("resourses/" + this.semesterYear + "PersonalFav"));
                Vector data = (Vector) ois.readObject();
                for (int i = 0; i < data.size(); i++) {
                    model.addRow((Vector) data.get(i));
                    System.out.println(data.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private void getAccountinfo() {

        Properties properties = new Properties();
        File f = new File("resourses/AccountInfo.properties");
        if (f.isFile() && f.canRead()) {
            try {
                properties.load(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (String key : properties.stringPropertyNames()) {
            AccountInfo.put(key, properties.get(key).toString());
        }

    }

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        courses.getSelectionModel().clearSelection();
    }//GEN-LAST:event_formMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int ret = JOptionPane.showConfirmDialog(this, "Save courses and configurations before exitting?", "Save All the Courses Into File?", JOptionPane.YES_NO_CANCEL_OPTION);

        if (ret == JOptionPane.CANCEL_OPTION) {
            return;
        }

        if (btn_monitor.isSelected()) {
            btn_monitor.doClick();
        }

        if (ret == JOptionPane.NO_OPTION)
            ;

        if (ret == JOptionPane.YES_OPTION) {
            storeTableModel(model);
            storeAccountInfo(AccountInfo);
        }
        try {
            driver.quit();
        } catch (Exception e) {
            System.err.println("此時沒有driver");
        }

        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void btn_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addActionPerformed
        String name = null, time = null, teacher = null, text_id = null, text_class = null;
        try {
            text_id = courseID.getText().toUpperCase();
            text_class = courseClass.getText().toUpperCase();
            if (text_id.equals("") || text_class.equals("")) {
                throw new NullPointerException();
            }
            name = parseData.getCourseName(text_id + "," + text_class);
            //JOptionPane.showMessageDialog(null, courses.getModel().getRowCount(), "Yay, java", JOptionPane.PLAIN_MESSAGE);
            time = parseData.getCourseTime(text_id + "," + text_class);
            teacher = parseData.getCourseTeacher(text_id + "," + text_class);

            for (int i = 0; i < courses.getModel().getRowCount(); i++) {
                if (courses.getModel().getValueAt(i, 1).equals(text_id) && courses.getModel().getValueAt(i, 2).equals(text_class)) {
                    throw new Exception();
                }
            }
            model.insertRow(courses.getRowCount(), new Object[]{name, text_id, text_class, time, teacher});
            resizeColumnWidth(courses);

        } catch (JSONException e) {
            JOptionPane.showMessageDialog(this, new SetLabelFont("輸入的資料不正確。"), "error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, new SetLabelFont("課號以及班別都需輸入。"), "error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, new SetLabelFont("已加入過此資料。"), "error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                courseID.getDocument().remove(0, courseID.getText().length());
                courseClass.getDocument().remove(0, courseClass.getText().length());
            } catch (BadLocationException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            courseID.requestFocus();
        }

    }//GEN-LAST:event_btn_addActionPerformed

    private void check_mailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_mailActionPerformed
        // TODO add your handling code here:
        /*try {    
         SendEmail sendEmail = new SendEmail();
         } catch (MessagingException ex) {
         Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
         }*/

        JTextField Field;
        if (check_mail.isSelected()) {
            /*
             for (Map.Entry<String, String> entry : AccountInfo.entrySet()) {
             System.out.println(entry.getKey() + "/" + entry.getValue());
             }*/

            if (AccountInfo.get("mail") == null) {
                do {
                    Field = new JTextField(25);
                    ((AbstractDocument) Field.getDocument()).setDocumentFilter(new MyDocumentFilter(50));
                    JPanel myPanel = new JPanel(new GridLayout(2, 1));
                    myPanel.add(new JLabel("<html>請輸入 「一個」 要接收通知訊息的信箱帳號。(gmail.com, yahoo.com.tw, mail.yzu.edu.tw, etc...)<br>(請關閉所有相關防毒程式不然收不到喔!)</html>"));
                    myPanel.add(Field);

                    int result = JOptionPane.showConfirmDialog(null, myPanel, "輸入", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        if (!(Field.getText().equals(""))) {
                            mail_text.setText(Field.getText());
                            AccountInfo.put("mail", Field.getText());
                            break;
                        }
                    } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                        check_mail.setSelected(false);
                        break;
                    }
                } while (Field.getText() != null);

            }
            if (AccountInfo.get("mail") != null) {
                mail_label.setVisible(true);
                mail_text.setText(AccountInfo.get("mail"));
                mail_text.setVisible(true);
                edit_mail_btn.setVisible(true);
                //jLabel4.setVisible(true);
                //jButton1.setVisible(true);
            }
        } else {
            mail_label.setVisible(false);
            mail_text.setVisible(false);
            edit_mail_btn.setVisible(false);
            // jLabel4.setVisible(false);
            //jButton1.setVisible(false);
        }


    }//GEN-LAST:event_check_mailActionPerformed

    private void check_lineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_lineActionPerformed
        String eventstr = null;
        String keystr = null;
        int result;
        if (check_line.isSelected()) {
            if (AccountInfo.get("lineEvent") == null || AccountInfo.get("lineKey") == null) {
                while (true) {
                    Map<String, String> currentMap = createInputs(eventstr, keystr);
                    eventstr = currentMap.get("event");
                    keystr = currentMap.get("key");
                    result = Integer.parseInt(currentMap.get("result"));

                    if (result == JOptionPane.OK_OPTION) {
                        if (!(eventstr.equals("") || keystr.equals(""))) {
                            AccountInfo.put("lineEvent", eventstr);
                            AccountInfo.put("lineKey", keystr);
                            event_text.setText(eventstr);
                            key_text.setText(keystr);
                            break;
                        }
                    } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                        check_line.setSelected(false);
                        break;
                    }
                }
            }
            if (AccountInfo.get("lineEvent") != null && AccountInfo.get("lineKey") != null) {
                event_text.setText(AccountInfo.get("lineEvent"));
                key_text.setText(AccountInfo.get("lineKey"));
                line_event_label.setVisible(true);
                line_key_label.setVisible(true);
                edit_event_btn.setVisible(true);
                edit_key_btn.setVisible(true);
                event_text.setVisible(true);
                key_text.setVisible(true);
            }
        } else {
            line_event_label.setVisible(false);
            line_key_label.setVisible(false);
            edit_event_btn.setVisible(false);
            edit_key_btn.setVisible(false);
            event_text.setVisible(false);
            key_text.setVisible(false);
        }
    }//GEN-LAST:event_check_lineActionPerformed

    private Map createInputs(String tempEvent, String tempKey) {

        Map<String, String> objMap = new HashMap<String, String>();
        JTextField xField = new JTextField(tempEvent, 25);
        ((AbstractDocument) xField.getDocument()).setDocumentFilter(new MyDocumentFilter(45));
        JTextField yField = new JTextField(tempKey, 25);
        ((AbstractDocument) yField.getDocument()).setDocumentFilter(new MyDocumentFilter(45));
        JPanel myPanel = new JPanel(new GridLayout(5, 1));
        myPanel.add(new JLabel("請輸入「Webhooks event 名稱」 以及 所取得的網址 「key」值"));
        myPanel.add(new JLabel("EventName : "));
        myPanel.add(xField);

        myPanel.add(new JLabel("Key : "));
        myPanel.add(yField);
        int result = JOptionPane.showConfirmDialog(null, myPanel, "輸入", JOptionPane.OK_CANCEL_OPTION);
        objMap.put("event", xField.getText());
        objMap.put("key", yField.getText());
        objMap.put("result", Integer.toString(result));

        return objMap;
    }

    private void check_mailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_check_mailFocusGained
        PostTip postTip = new PostTip(evt);
    }//GEN-LAST:event_check_mailFocusGained

    private void check_alertFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_check_alertFocusGained
        PostTip postTip = new PostTip(evt);
    }//GEN-LAST:event_check_alertFocusGained

    private void check_lineFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_check_lineFocusGained
        PostTip postTip = new PostTip(evt);
    }//GEN-LAST:event_check_lineFocusGained

    private void btn_monitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_monitorActionPerformed

        Timer timer = new Timer(3500, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_monitor.setEnabled(true);
            }
        });
        timer.setRepeats(false);
        //printUsage();
        if (btn_monitor.isSelected()) {
            jLabel20.setForeground(Color.blue);
            jLabel20.setText("請盡量在程式執行時提供穩定快速的網路，按開始前確保詳閱 README.TXT");
            if (myAccount.getText().equals("") || myPassword.getText().equals("")) {
                JOptionPane.showMessageDialog(null, new SetLabelFont("Portal 帳號以及密碼皆需要輸入。"));
                btn_monitor.setSelected(false);
            } else if ((courses.getModel().getRowCount() == 0)) {
                JOptionPane.showMessageDialog(null, new SetLabelFont("請先於選課表格內加入要監控的課程，再按開始按鈕。"));
                btn_monitor.setSelected(false);

            } else if (!(check_alert.isSelected() || check_mail.isSelected() || check_line.isSelected())) {
                JOptionPane.showMessageDialog(null, new SetLabelFont("請至少選擇一種提醒方式(MAIL, LINE, ALERT)，再按開始監控。"));
                btn_monitor.setSelected(false);
            } else {
                try {
                    // 檢查所有加入的課程是否「相互」重疊
                    ArrayList<String> courseTime = new ArrayList<String>();
                    for (int row = 0; row < courses.getRowCount(); row++) {
                        String str[] = courses.getModel().getValueAt(row, 3).toString().substring(1).split(" ");
                        for (String string : str) {
                            if (courseTime.contains(string)) {
                                throw new Exception();
                            } else {
                                courseTime.add(string);
                            }
                        }
                    }
                    btn_monitor.setEnabled(false);
                    retrieve.setEnabled(false);
                    check_line.setEnabled(false);
                    check_mail.setEnabled(false);
                    check_alert.setEnabled(false);
                    edit_event_btn.setEnabled(false);
                    edit_key_btn.setEnabled(false);
                    edit_mail_btn.setEnabled(false);
                    test_line.setEnabled(false);
                    test_mail.setEnabled(false);
                    btn_add.setEnabled(false);
                    btn_del.setEnabled(false);
                    method.setEnabled(false);
                    keepTime.setEnabled(false);
                    wait.setEnabled(false);
                    delay.setEnabled(false);
                    back1.setEnabled(false);
                    defaultConfig.setEnabled(false);
                    loopNotice.setEnabled(false);
                    btn_add.setForeground(new Color(102, 102, 102));
                    btn_del.setForeground(new Color(102, 102, 102));
                    btn_monitor.setLabel("STOP");
                    btn_monitor.setBackground(new Color(154, 35, 10));
                    // 檢查是否還在撥放音樂 如果是 --> 關閉
                    if (genVol != null) {
                        genVol.terminate();
                        test_alert.setText("test");
                        test_alert.setForeground(Color.black);
                        test_alert.setSelected(false);
                    }
                    if (check_alert.isSelected() && method.getSelectedIndex() == 1) {
                        JOptionPane.showMessageDialog(null, new SetLabelFont("<html>因為您勾選了 <span style='color: red'>「SOUND ALERT」 & 「自動選課」<br></span>當選到任何一堂課時，程式會<span style='color: red'>重複循環播放音樂</span>。(直到你 STOP 或是關閉程式)</html>"));
                    } else if (check_alert.isSelected() && method.getSelectedIndex() == 0) {
                        JOptionPane.showMessageDialog(null, new SetLabelFont("<html>因為您勾選了 <span style='color: red'>「SOUND ALERT」 & 「只監控，不選課」<br></span>當檢查到任一堂課有空位時，程式<span style='color: red'>只會播放一次音樂</span>。(重新 START 後重算)<br>(播完再檢查到別堂空位時，不會再次播放，此方法推薦一堂課自己選完STOP後再START)<br>(勾選循環通知只影響信箱及 LINE )<br>有勾選 --> 每檢查到有空位傳一次；無勾選 --> 只傳第一次檢查到空位。</html>"));
                    }
                    timer.start();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                testSearchPage = new RoutineChoosen(portalAccount, portalPassword, AccountInfo, semesterYear, courses, method, keepTime, wait, delay, btn_monitor, check_alert, check_mail, check_line, loopNotice, true);
                            } catch (IOException ex) {
                                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception e) {
                                /////////////////////////////////////////////////////////////////////////////////////
                                OnlineCheck.driver.quit();
                                JOptionPane.showMessageDialog(null, new SetLabelFont("<html>無法建立可視化之瀏覽器，請嘗試關閉開啟中的瀏覽器，並重新啟動程式。</html>"), "確認", JOptionPane.OK_OPTION);
                                //JOptionPane.showMessageDialog(null, new TextFeelCustom(e.toString()), "確認", JOptionPane.OK_OPTION);
                                System.exit(0);
                            }
                            testSearchPage.execute();
                        }
                    });
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, new SetLabelFont("列表內有課程重複於同一時間/時段，請確認後留下一個並移除其他。"));
                    btn_monitor.setSelected(false);
                }
            }

        } else {
            btn_monitor.setEnabled(false);
            retrieve.setEnabled(true);
            testSearchPage.cancel(true);
            check_line.setEnabled(true);
            check_mail.setEnabled(true);
            check_alert.setEnabled(true);
            edit_event_btn.setEnabled(true);
            edit_key_btn.setEnabled(true);
            edit_mail_btn.setEnabled(true);
            test_line.setEnabled(true);
            test_mail.setEnabled(true);
            btn_add.setEnabled(true);
            btn_del.setEnabled(true);
            method.setEnabled(true);
            keepTime.setEnabled(true);
            wait.setEnabled(true);
            delay.setEnabled(true);
            back1.setEnabled(true);
            defaultConfig.setEnabled(true);
            loopNotice.setEnabled(true);
            btn_add.setForeground(new Color(0, 0, 0));
            btn_del.setForeground(new Color(0, 0, 0));
            btn_monitor.setLabel("START");
            btn_monitor.setBackground(new Color(225, 53, 45));
            timer.restart();
        }

    }//GEN-LAST:event_btn_monitorActionPerformed

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
        courses.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void hintMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hintMouseExited
        hint.setForeground(Color.blue);
    }//GEN-LAST:event_hintMouseExited

    private void hintMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hintMouseEntered
        hint.setForeground(Color.red);
    }//GEN-LAST:event_hintMouseEntered

    private void hintMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hintMouseClicked
        String info = "<html><head><meta charset=\"utf-8\" /></head><body style='color: #834627; background-image: url(" + this.getClass().getResource("/autochoosecourse/fall.png") + ");'>"
                + "<span>&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4; <span stype='color: red;'>可複選<span> &#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;&#x26E4;</span><br> <br>"
                + "<span style='color: #080613'>&#9728; SOUND ALERT</span><br>"
                + "&emsp;&emsp; 勾選後，當系統檢查到任一課程有空位時，將自動播放音樂。 (請開啟電腦音量)<br>"
                + "&emsp;&emsp; 如果要更改音樂請放入新的 .mp3 檔案來取代「See_You_Again.MP3」檔案。<br>"
                + "&emsp;&emsp; 選擇「自動選課」，選到任何一堂課時，音樂會重複循環播放。<br>"
                + "&emsp;&emsp; 選擇「只監控，不選課」，檢查到任一課程有空位時<span style='color: red'>，音樂只播放一次!播完再檢查到也不會再播(重新 START 後重算)</span>。(與循環通知無關)<br>"
                + "<span style='color: #080613'>&#9728; MAIL</span><br>"
                + "&emsp;&emsp; 勾選後，系統會要求您輸入常使用的信箱帳號，如學校信箱、google信箱 (ex : s1017788@mail.yzu.edu.tw)，並沒有限制信箱種類。<br>"
                + "&emsp;&emsp; 當系統檢查到課程有空位時，會自動以帳號 : chenpuddingcorgi@gmail.com (系統帳號) 發送提醒信件。<br>"
                + "&emsp;&emsp; 如果填學校信箱，信只會跑到「學校信箱」，不管你有沒有轉移信到google信箱!<br>"
                + "&emsp;&emsp; 請關閉所有相關防毒程式，才不會收不到信喔!(EX:Avast、McAfee、卡巴斯基、小雨傘等)<br>"
                + "<span style='color: #080613'>&#9728; LINE</span><br>"
                + "&emsp;&emsp; 由於Line權限問題，以至於系統無法直接傳送提示訊息，因此請先藉由下列網址前往網頁進行設定。<br>"
                + "&emsp;&emsp; 連結網址 : <a href='web'>https://www.oxxostudio.tw/articles/201803/ifttt-line.html</a><br>"
                + "&emsp;&emsp; 按照網頁上的步驟設定Line Notify、Webhooks以及其trigger，並設定傳送的內容，如下圖。(填上圖片上相同的內容即可)<br>"
                + "&emsp;&emsp;<img src=\"" + this.getClass().getResource("/autochoosecourse/line_value.png") + "\"><br>"
                + "&emsp;&emsp; 最後，根據文章取得您的<span style='color: red'>「Webhooks event 名稱」</span> 和 <span style='color: red'>「你的金鑰」</span>，填入此系統的詢問框(勾選後跳出)便完成設定。<br><br>"
                + "</body></html>";
        JEditorPane jep = new JEditorPane("text/html;utf-8", info);
        jep.setEditable(false);
        jep.setFont(new Font("微軟正黑體", Font.BOLD, 18));

        jep.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    if (hle.getDescription().equalsIgnoreCase("web")) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://www.oxxostudio.tw/articles/201803/ifttt-line.html"));
                        } catch (URISyntaxException | IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(jep);
        scrollPane.setPreferredSize(new Dimension(1150, scrollPane.getPreferredSize().height));
        JOptionPane.showConfirmDialog(this.getContentPane(), scrollPane, "Information", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_hintMouseClicked

    private void edit_event_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_event_btnActionPerformed
        String eventstr;
        String keystr;
        String result;
        do {
            Map<String, String> map = createInputs(event_text.getText(), key_text.getText());
            eventstr = map.get("event");
            keystr = map.get("key");
            result = map.get("result");
        } while (eventstr.equals("") || keystr.equals(""));
        if (Integer.parseInt(result) == JOptionPane.OK_OPTION) {
            AccountInfo.put("lineEvent", eventstr);
            AccountInfo.put("lineKey", keystr);
            event_text.setText(eventstr);
            key_text.setText(keystr);
        }
    }//GEN-LAST:event_edit_event_btnActionPerformed

    private void edit_mail_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_mail_btnActionPerformed
        JTextField Field;
        do {
            Field = new JTextField(mail_text.getText(), 25);
            ((AbstractDocument) Field.getDocument()).setDocumentFilter(new MyDocumentFilter(45));
            JPanel myPanel = new JPanel(new GridLayout(2, 1));
            myPanel.add(new JLabel("<html>請輸入 「一個」 要接收通知訊息的信箱帳號。(gmail.com, yahoo.com.tw, mail.yzu.edu.tw, etc...)<br>(請關閉所有相關防毒程式，才會收的到信喔!)</html>"));
            myPanel.add(Field);
            int result = JOptionPane.showConfirmDialog(null, myPanel, "輸入", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                if (!(Field.getText().equals(""))) {
                    AccountInfo.put("mail", Field.getText());
                    mail_text.setText(AccountInfo.get("mail"));
                    break;
                }
            } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                if (!Field.getText().equals("")) {
                    break;
                }
            }
        } while (Field.getText() != null);


    }//GEN-LAST:event_edit_mail_btnActionPerformed

    private void edit_key_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_key_btnActionPerformed
        String eventstr;
        String keystr;
        String result;
        do {
            Map<String, String> map = createInputs(event_text.getText(), key_text.getText());
            eventstr = map.get("event");
            keystr = map.get("key");
            result = map.get("result");
        } while (eventstr.equals("") || keystr.equals(""));
        if (Integer.parseInt(result) == JOptionPane.OK_OPTION) {
            AccountInfo.put("lineEvent", eventstr);
            AccountInfo.put("lineKey", keystr);
            event_text.setText(eventstr);
            key_text.setText(keystr);
        }
    }//GEN-LAST:event_edit_key_btnActionPerformed

    private void test_lineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_test_lineActionPerformed
        if (check_line.isSelected()) {
            SendMessage maker = new SendMessage(AccountInfo.get("lineEvent"), AccountInfo.get("lineKey"));
            try {
                maker.trigger("This is a Test Message Generated by Monitor Program. (Line Test)");
            } catch (IOException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, new SetLabelFont("<html>已傳送測試之通知訊息至您的 LINE 帳號 (由 LINE Notify 發送)。<br>如果沒有接收到 LINE 訊息，表示您有東西填錯囉!<br>(或是沒按照網頁做設定，請再次確認!)</html>"));
        } else {
            JOptionPane.showMessageDialog(null, new SetLabelFont("請先勾選 LINE 選項，加入後再點擊測試。"));
        }
    }//GEN-LAST:event_test_lineActionPerformed

    private void test_mailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_test_mailActionPerformed
        if (check_mail.isSelected()) {
            try {
                new SendEmail(AccountInfo.get("mail"));
                JOptionPane.showMessageDialog(null, new SetLabelFont("<html>已傳送測試郵件到您的信箱。(如果沒有收到郵件，表示您的信箱填錯囉!如果填寫學校信箱要到「學校Outlook」才有喔!)</html>"));
            } catch (MessagingException ex) {
                JOptionPane.showMessageDialog(null, new SetLabelFont("<html>目前無法傳送信件，可能問題為:<br>1. 偵測到您可能有開啟防毒程式(Avast、McAfee、卡巴斯基等等都算)，造成程式權限不夠，無法傳送信件!<br>2. 你可能信箱填錯囉(檢查空格之類的)!<br>3. 此程式因IP位置異動被禁止發信!<br>請修正上述問題或選擇別的方式提醒。(硬是勾選頂多選課時提醒失效)</html>"));
                System.err.println(ex.toString());
            } catch (Exception ex) {
                System.err.println(ex);
            }

        } else {
            JOptionPane.showMessageDialog(null, new SetLabelFont("請先勾選 MAIL 選項，加入後再點擊測試。"));
        }
    }//GEN-LAST:event_test_mailActionPerformed

    private void courseIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_courseIDKeyPressed
        if (evt.getKeyCode() == 10) {
            courseClass.requestFocus();
        }
    }//GEN-LAST:event_courseIDKeyPressed

    private void courseClassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_courseClassKeyPressed
        if (evt.getKeyCode() == 10) {
            btn_add.requestFocus();
        }
    }//GEN-LAST:event_courseClassKeyPressed

    private void btn_delActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delActionPerformed
        try {
            DefaultTableModel model = (DefaultTableModel) courses.getModel();
            int[] rows = courses.getSelectedRows();
            if (rows.length == 0) {
                throw new IndexOutOfBoundsException();
            }
            for (int i = 0; i < rows.length; i++) {
                model.removeRow(rows[i] - i);
            }
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, new SetLabelFont("請選擇要移除的課程(橫向欄位，可多選)。"), "error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_delActionPerformed

    private void forwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardActionPerformed
        tablePane.setVisible(false);
        configPane.setVisible(true);
    }//GEN-LAST:event_forwardActionPerformed

    private void forwardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_forwardMouseEntered
        forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/forwardon.png")));
    }//GEN-LAST:event_forwardMouseEntered

    private void forwardMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_forwardMouseExited
        forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/forward.png")));
    }//GEN-LAST:event_forwardMouseExited

    private void back1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back1MouseEntered
        back1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/backon.png")));
    }//GEN-LAST:event_back1MouseEntered

    private void back1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back1MouseExited
        back1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/back.png")));
    }//GEN-LAST:event_back1MouseExited

    private void back1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back1ActionPerformed
        configPane.setVisible(false);
        tablePane.setVisible(true);
    }//GEN-LAST:event_back1ActionPerformed

    private void back0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back0MouseEntered
        back0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/backon.png")));
    }//GEN-LAST:event_back0MouseEntered

    private void back0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back0MouseExited
        back0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/autochoosecourse/back.png")));
    }//GEN-LAST:event_back0MouseExited

    private void back0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back0ActionPerformed
        if (genVol != null) {
            genVol.terminate();

        }
        if (Login.visBrowser.isSelected()) {
            try {
                driver.quit();
            } catch (Exception e) {
            }
            driver = null;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dispose();
            }
        });
        Login.countGOPressed--;
        frontFrame.setEnabled(true);
        frontFrame.setVisible(true);
    }//GEN-LAST:event_back0ActionPerformed

    private void defaultConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultConfigActionPerformed
        method.setSelectedIndex(1);
        keepTime.setSelectedIndex(4);
        wait.setSelectedIndex(3);
        delay.setSelectedIndex(13);
        retrieve.setSelected(true);
        loopNotice.setSelected(false);
    }//GEN-LAST:event_defaultConfigActionPerformed

    private void methodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodActionPerformed
        if (method.getSelectedIndex() == 0) {
            loopNotice.setVisible(true);
        }
        if (method.getSelectedIndex() == 1) {
            loopNotice.setVisible(false);
        }
    }//GEN-LAST:event_methodActionPerformed

    private void test_alertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_test_alertActionPerformed
        if (check_alert.isSelected()) {
            if (test_alert.isSelected()) {
                test_alert.setText("stop");
                test_alert.setForeground(Color.red);
                genVol = new MusicInfo();
                genVol.setRunning();
                genVol.start();
            } else {
                genVol.terminate();
                genVol = null;
                test_alert.setText("test");
                test_alert.setForeground(Color.black);
            }
        } else {
            test_alert.setSelected(false);
            JOptionPane.showMessageDialog(null, new SetLabelFont("請先勾選 SOUND ALERT 選項，加入後再點擊測試。"));
        }
    }//GEN-LAST:event_test_alertActionPerformed

    private void check_alertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_alertActionPerformed
        if (!check_alert.isSelected()) {
            if (genVol != null) {
                genVol.terminate();
                test_alert.setText("test");
                test_alert.setForeground(Color.black);
                test_alert.setSelected(false);
            }
        } else {
            if (!MusicInfo.mp3Existed()) {
                JOptionPane.showMessageDialog(null, new SetLabelFont("<html>與主程式(.exe檔)同一層路徑找不到相關音效檔案(<span style = 'color: red;'>「.mp3」or「.MP3」</span>)，請放入後再試。</html>"));
                check_alert.setSelected(false);
            }
        }
    }//GEN-LAST:event_check_alertActionPerformed

    private void coursesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_coursesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            btn_del.doClick();
        }
    }//GEN-LAST:event_coursesKeyPressed

    private void courseIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_courseIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_courseIDActionPerformed

    private void retrieveFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_retrieveFocusGained
        PostTip postTip = new PostTip(evt);
    }//GEN-LAST:event_retrieveFocusGained

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
       
     try {
     for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
     if ("Nimbus".equals(info.getName())) {
     javax.swing.UIManager.setLookAndFeel(info.getClassName());
     break;

     }
     }
     } catch (ClassNotFoundException ex) {
     java.util.logging.Logger.getLogger(MainFrame.class
     .getName()).log(java.util.logging.Level.SEVERE, null, ex);

     } catch (InstantiationException ex) {
     java.util.logging.Logger.getLogger(MainFrame.class
     .getName()).log(java.util.logging.Level.SEVERE, null, ex);

     } catch (IllegalAccessException ex) {
     java.util.logging.Logger.getLogger(MainFrame.class
     .getName()).log(java.util.logging.Level.SEVERE, null, ex);

     } catch (javax.swing.UnsupportedLookAndFeelException ex) {
     java.util.logging.Logger.getLogger(MainFrame.class
     .getName()).log(java.util.logging.Level.SEVERE, null, ex);
     }

     java.awt.EventQueue.invokeLater(new Runnable() {
     public void run() {
     new MainFrame(null, null, null, null).setVisible(true);

     }
     });
     }*/
    public void storeTableModel(DefaultTableModel model) {

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("resourses/" + this.semesterYear + "PersonalFav"));
            oos.writeObject(model.getDataVector());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }

    }

    public void storeAccountInfo(Object mymap) {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : AccountInfo.entrySet()) {
            if (entry.getValue() != null) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        try {
            properties.store(new FileOutputStream("resourses/AccountInfo.properties"), null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300) {
                width = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private void setIcon() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("mainIcon.png")));
    }

    private static void printUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                } // try
                System.out.println(method.getName() + " = " + value);
            } // if
        } // for
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton back0;
    private javax.swing.JButton back1;
    private javax.swing.JButton btn_add;
    private javax.swing.JButton btn_del;
    private javax.swing.JToggleButton btn_monitor;
    private javax.swing.JCheckBox check_alert;
    private javax.swing.JCheckBox check_line;
    private javax.swing.JCheckBox check_mail;
    private javax.swing.JPanel configPane;
    private javax.swing.JTextField courseClass;
    private javax.swing.JTextField courseID;
    private javax.swing.JTable courses;
    private javax.swing.JButton defaultConfig;
    private javax.swing.JComboBox<String> delay;
    private javax.swing.JButton edit_event_btn;
    private javax.swing.JButton edit_key_btn;
    private javax.swing.JButton edit_mail_btn;
    private javax.swing.JLabel event_text;
    private javax.swing.JButton forward;
    private javax.swing.JLabel hint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    public static javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> keepTime;
    private javax.swing.JLabel key_text;
    private javax.swing.JLabel line_event_label;
    private javax.swing.JLabel line_key_label;
    private javax.swing.JCheckBox loopNotice;
    private javax.swing.JLabel mail_label;
    private javax.swing.JLabel mail_text;
    private javax.swing.JComboBox<String> method;
    private javax.swing.JTextField myAccount;
    private javax.swing.JTextField myPassword;
    public static javax.swing.JCheckBox retrieve;
    private javax.swing.JTextField semesYear;
    private javax.swing.JPanel tablePane;
    private javax.swing.JToggleButton test_alert;
    private javax.swing.JButton test_line;
    private javax.swing.JButton test_mail;
    private javax.swing.JComboBox<String> wait;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}
