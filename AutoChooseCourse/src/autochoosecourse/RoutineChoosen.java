package autochoosecourse;

import static autochoosecourse.OnlineCheck.driver;
import static autochoosecourse.MainPanel.jLabel20;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RoutineChoosen extends SwingWorker<Void, Void> {

    private String account = null;
    private String password = null;
    private Map<String, String> AccountInfo = null;
    private String semesterYear = null;
    private JTable courses = null;
    private JComboBox<String> method = null;
    private JComboBox<String> keepTime = null;
    private JComboBox<String> wait = null;
    private JComboBox<String> delay = null;
    private JToggleButton monitor = null;
    private JCheckBox checkAlert = null;
    private JCheckBox checkMail = null;
    private JCheckBox checkLine = null;
    private JCheckBox loopNotice = null;
    private MusicInfo music = null;
    private String MainPath = null;
    private Map<String, Boolean> isNotice = new HashMap<String, Boolean>();
    private final ArrayList<String> overflow = new ArrayList<String>();
    private TensorModel dwarf = null;
    private boolean loop;

    public RoutineChoosen(String acc, String pwd, Map accountInfo, String semesterYear, JTable table, JComboBox<String> method, JComboBox<String> keepTime, JComboBox<String> wait, JComboBox<String> delay, JToggleButton btn_monitor, JCheckBox alert, JCheckBox mail, JCheckBox line, JCheckBox noticeBox, boolean first) throws IOException, InterruptedException, Exception {
        this.account = acc;
        this.password = pwd;
        this.AccountInfo = accountInfo;
        this.semesterYear = semesterYear;
        this.courses = table;
        this.method = method;
        this.keepTime = keepTime;
        this.wait = wait;
        this.delay = delay;
        this.monitor = btn_monitor;
        this.checkAlert = alert;
        this.checkMail = mail;
        this.checkLine = line;
        this.loopNotice = noticeBox;
        this.loop = first;
        for (int i = 0; i < courses.getRowCount(); i++) {
            this.isNotice.put(courses.getModel().getValueAt(i, 1).toString() + "," + courses.getModel().getValueAt(i, 2).toString(), Boolean.FALSE);
            //System.err.println(this.isNotice.get(courses.getModel().getValueAt(i, 1).toString() + "," + courses.getModel().getValueAt(i, 2).toString()));
        }

        try {
            MainPath = new File(Login.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        } catch (URISyntaxException ex) {
        }
        this.dwarf = new TensorModel(MainPath);
        ChromeOptions chromeoptions = new ChromeOptions();
        chromeoptions.addArguments("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(true);

        try {
            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                if (Login.visBrowser.isSelected()) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    DesiredCapabilities capabilities = new DesiredCapabilities();
                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                    chromeoptions.merge(capabilities);
                    driver = new ChromeDriver();
                   
                } else {
                }
            } else {
                if (Login.visBrowser.isSelected()) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    driver = new FirefoxDriver();
                } else {
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected Void doInBackground() {
        String checkLogin = null;
        long end = System.currentTimeMillis() + Integer.parseInt(keepTime.getSelectedItem().toString().replaceAll("[^0-9]+", "")) * 60 * 1000; // 60 seconds * 1000 ms/sec
        //System.out.println(end);
        //System.out.println(Integer.parseInt(wait.getSelectedItem().toString().replaceAll("[^0-9]+", "")));
        do {
            try {
                if (System.currentTimeMillis() >= end) {
                    driver.get("https://www.google.com.tw/");
                    new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                    Thread.sleep(1000 * 60 * Integer.parseInt(wait.getSelectedItem().toString().replaceAll("[^0-9]+", "")));
                    end = System.currentTimeMillis() + Integer.parseInt(keepTime.getSelectedItem().toString().replaceAll("[^0-9]+", "")) * 60 * 1000;
                }

                if (MainPanel.retrieve.isSelected()) {// 重新連接瀏覽器有打勾
                    while (true) {
                        driver.get("https://isdna1.yzu.edu.tw/Cnstdsel/Index.aspx");
                        Thread.sleep(1000);
                        // 處理選課系統未開啟前
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        do {
                            if (this.isAlertPresent()) {
                                Thread.sleep(3000);
                                driver.switchTo().alert().accept();
                                Thread.sleep(3000);
                                driver.get("https://isdna1.yzu.edu.tw/Cnstdsel/Index.aspx");
                                Thread.sleep(3000);
                            } else {
                                new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                                break;
                            }
                        } while (true);
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='100%'");
                        if (driver.findElements(By.xpath("//select[@id='DPL_SelCosType']/option[contains(text(), '" + this.semesterYear + "')]")).isEmpty()) {
                            jLabel20.setForeground(Color.red);
                            jLabel20.setText("<html><span style='color: red'>程式無法在登入頁面中找到您填入的學年度 : 「" + semesterYear + "」。請確認網路是否<br>穩定連線、或是因為輸入錯誤的學年度，請回到程式初始頁面輸入正確的學年度。</span></html>");
                            Thread.sleep(3000);
                        } else {
                            //System.out.println(driver.findElement(By.xpath("//select[@id='DPL_SelCosType']/option[contains(text(), '" + this.semesterYear + "')]")).getText());
                            driver.findElement(By.xpath("//select[@id='DPL_SelCosType']/option[contains(text(), '" + this.semesterYear + "')]")).click();
                            break;
                        }
                    }
                } else { // 重新連接瀏覽器沒有打勾
                    driver.get("https://isdna1.yzu.edu.tw/Cnstdsel/Index.aspx");
                    Thread.sleep(1000);
                    // 處理選課系統未開啟前
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    do {
                        if (this.isAlertPresent()) {
                            Thread.sleep(3000);
                            driver.switchTo().alert().accept();
                            Thread.sleep(3000);
                            driver.get("https://isdna1.yzu.edu.tw/Cnstdsel/Index.aspx");
                            Thread.sleep(3000);
                        } else {
                            new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                            break;
                        }
                    } while (true);
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='100%'");
                    if (driver.findElements(By.xpath("//select[@id='DPL_SelCosType']/option[contains(text(), '" + this.semesterYear + "')]")).isEmpty()) {
                        jLabel20.setForeground(Color.red);
                        jLabel20.setText("<html><span style='color: red'>程式無法在登入頁面中找到您填入的學年度 : 「" + semesterYear + "」。請確認網路是否<br>穩定連線、或是因為輸入錯誤的學年度，請回到程式初始頁面輸入正確的學年度。</span></html>");
                        return null;
                    } else {
                        //System.out.println(driver.findElement(By.xpath("//select[@id='DPL_SelCosType']/option[contains(text(), '" + this.semesterYear + "')]")).getText());
                        driver.findElement(By.xpath("//select[@id='DPL_SelCosType']/option[contains(text(), '" + this.semesterYear + "')]")).click();
                    }
                }

                driver.findElement(By.name("Txt_User")).sendKeys(account);
                driver.findElement(By.name("Txt_Password")).sendKeys(password);
                WebElement logo = driver.findElement(By.cssSelector("img[src='SelRandomImage.aspx']"));
                // 擷取驗證碼圖片
                Point p = logo.getLocation();
                int width = logo.getSize().getWidth();
                int height = logo.getSize().getHeight();
                TakesScreenshot screenshoot = (TakesScreenshot) driver;
                File src = screenshoot.getScreenshotAs(OutputType.FILE);
                BufferedImage img = ImageIO.read(src);
                BufferedImage dest = img.getSubimage(p.getX(), p.getY(), width, height);
                ImageIO.write(dest, "png", src);
                FileUtils.copyFile(src, new File(MainPath + "/image.png"));

                String line = null;
                try {
                    line = dwarf.getVerificationCode();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, new SetLabelFont("<html>如果不知怎麼解決此 error 請先截圖此錯誤訊息，再詢問作者~<br>" + e.toString() + "</html>"));
                }

                //////////////////////////////////////////////////////////////
                if (line == null) {
                    jLabel20.setForeground(Color.red);
                    jLabel20.setText("解析驗證碼失敗，請解決此問題後再重啟");
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            //////////////////////////////////////////////////////////////
                            JOptionPane.showMessageDialog(null, new SetLabelFont("<html>主程式無法解析驗證碼，請確認以下幾點 : <br>1. code_model.pb 是否與主程式(.exe檔)在同一路徑。<br>2. 是否開啟過多應用程式，使得記憶體或是cpu過高。 <br>3. 與主程式(.exe檔)同一路徑是否有生成\"image_java_generated.png\"檔案。</html>"));
                        }
                    });
                    return null;
                }
                driver.findElement(By.id("Txt_CheckCode")).sendKeys(line);
                WebElement btnOk = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.id("btnOK")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnOk);
                //driver.findElement(By.id("btnOK")).click();
                new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.alertIsPresent());
                checkLogin = driver.switchTo().alert().getText();
                driver.switchTo().alert().accept();
                ArrayList<Integer> selectedCourse = new ArrayList<Integer>();
                int countWanted = 0;
                if (checkLogin.equals("已經逾時,請重新執行!")) {
                    driver.get("https://www.google.com.tw/");
                    new WebDriverWait(driver, Duration.ofSeconds(15)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                    Thread.sleep(1000);
                    continue;
                } else if (checkLogin.equals("您非在學的學生") || checkLogin.equals("資料庫發生異常,請洽系統管理員!")) {
                    jLabel20.setForeground(Color.red);
                    jLabel20.setText("您非在學的學生，已經無法登入學校選課系統!");
                    return null;
                } else if (!checkLogin.equals("驗證碼錯誤，請輸入正確的驗證碼。")) {//進入到學校選課系統裡面
                    try {
                        new WebDriverWait(driver, Duration.ofSeconds(20)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                        driver.switchTo().defaultContent();
                        driver.switchTo().frame("frameright");
                        // 檢查所有加入的課程是否重疊「線上的」選課時間 ， 如果是 ， 則取消 START
                        for (int row = 0; row < courses.getRowCount(); row++) {
                            String str[] = courses.getModel().getValueAt(row, 3).toString().substring(1).split(" ");
                            Boolean IsEmpty = true; //確認所選的課的所有時間是否在選課系統裡都是空的
                            for (int k = 0; k < str.length; k++) {
                                // 檢查某時段是否已經有選課
                                //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + str[k].substring(0, 1) + "  " + Integer.parseInt(str[k].substring(1)) + "</span></html>"));
                                //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + driver.findElements(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[@id='" + str[k].substring(0, 1) + "" + Integer.parseInt(str[k].substring(1)) + "']/table")).size() + "</span></html>"));
                                if (driver.findElements(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[@id='" + str[k].substring(0, 1) + "" + Integer.parseInt(str[k].substring(1)) + "']/table")).size() != 0) {
                                    // 檢查已有選課的內容是否在我們 Table 裡面
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + driver.findElement(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[@id='" + str[k].substring(0, 1) + "" + Integer.parseInt(str[k].substring(1)) + "']/table/tbody/tr/td[2]/a")).getText() + "</span></html>"));
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2) + "</span></html>"));
                                    if (driver.findElement(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[@id='" + str[k].substring(0, 1) + "" + Integer.parseInt(str[k].substring(1)) + "']/table/tbody/tr/td[2]/a")).getText().equals(courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2))) {
                                        //System.out.println(driver.findElement(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[contains(@id, " + Integer.parseInt(string.substring(0, 1)) + Integer.parseInt(string.substring(1)) + ")]/table/tbody/tr/td[2]/a")).getText());
                                        selectedCourse.add(countWanted, -1); // -1 代表課程已選
                                        countWanted++;
                                        break;
                                    } else {
                                        //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>str[k] = " + str[k] + "</span></html>"));
                                        overflow.add(str[k]);
                                        IsEmpty = false;
                                    }
                                } else {
                                    if (k == str.length - 1 && IsEmpty) { 
                                        selectedCourse.add(countWanted, 0); // 0 代表尚未選課
                                        countWanted++;
                                    }
                                }
                            }
                        }

                        /*
                         for (Integer num : selectedCourse) {
                         JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + num + "</span></html>"));
                         }
                         */
                        
                        if (!overflow.isEmpty()) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    for (String string : overflow) {
                                        //System.out.println(string);
                                        JOptionPane.showMessageDialog(null, new SetLabelFont("<html><span style='color: blue'>檢查到星期 <span style='color: red'>" + Integer.parseInt(string.substring(0, 1))
                                                + "</span> 第 <span style='color: red'>" + Integer.parseInt(string.substring(1)) + " </span><span>節課您已經有修其他課程!<br>請至前頁移除重疊時間的課程，或是自行登入選課系統移除同時段的課程。<span></html>"));
                                        //System.out.println("檢查到星期 " + Integer.parseInt(string.substring(0, 1)) + " 第 " + Integer.parseInt(string.substring(1)) + " 節課您已經有修其他課程!<br>請至前頁移除重複時間的課程。");
                                    }
                                }
                            });
                            return null;
                        }
                        /*
                         for (int row = 0; row < selectedCourse.size(); row++) {
                         System.out.print( selectedCourse.get(row) + " ");
                         }
                         System.out.println("");
                         System.out.println(selectedCourse.size());
                         */
                        // 開始檢查課程或是自動選課
                        driver.switchTo().defaultContent();
                    } catch (Exception e) {
                        if (monitor.isSelected()) {
                            throw new UnknownError();
                        }
                    }
                    for (int row = 0; row < courses.getRowCount(); row++) {
                        //System.err.println(this.isNotice.get(courses.getModel().getValueAt(row, 1).toString() + "," + courses.getModel().getValueAt(row, 2).toString()));
                        //Thread.sleep(1000);
                        try {
                            Boolean finished = true;
                            for (int i = 0; i < selectedCourse.size(); i++) {
                                if (selectedCourse.get(i) == 0) {
                                    finished = false;
                                    break;
                                }

                                if (i == selectedCourse.size() - 1 && finished == true) {
                                    jLabel20.setText("所有課程都搶課完畢，並已關閉網頁");
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            JOptionPane.showMessageDialog(null, new SetLabelFont("<html><span style='color: red'>所有課程都搶課完畢。</span></html>"));
                                        }
                                    });
                                    return null;
                                }
                            }
                            if (selectedCourse.get(row) == -1) {
                                continue;
                            }

                            String str = courses.getModel().getValueAt(row, 3).toString().substring(1).split(" ")[0];
                            driver.switchTo().defaultContent();
                            driver.switchTo().frame(2);
                            WebElement getLeftCourses = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[contains(@id, '" + Integer.parseInt(str.substring(0, 1)) + Integer.parseInt(str.substring(1)) + "')]")));
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", getLeftCourses);
                            //driver.findElement(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[contains(@id, '" + Integer.parseInt(str.substring(0, 1)) + Integer.parseInt(str.substring(1)) + "')]")).click(); //填入第幾格
                            // click 動作之後應該要檢查到了沒 (左側課程列)
                            long startLeft = System.currentTimeMillis();
                            long waitLeft = startLeft + 30000;
                            while (true) {
                                driver.switchTo().defaultContent();
                                driver.switchTo().frame(1);
                                try {
                                    if (System.currentTimeMillis() > waitLeft) {
                                        throw new UnknownError();
                                    }
                                    String day_num = driver.findElement(By.xpath("//span[@id='LabT_Week']")).getText();
                                    String class_num = driver.findElement(By.xpath("//span[@id='LabT_Item']")).getText();
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>「" + (day_num + class_num) + "」「" + (str.substring(0, 1) + str.substring(1)) + "」</span></html>"));
                                    if ((day_num + class_num).equals(str.substring(0, 1) + str.substring(1))) {
                                        break;
                                    }
                                } catch (Exception e) {
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + e + "</span></html>"));////////////////////////////////////////
                                }
                            }
                            //new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[@id='TC_Cos3']")));
                            WebElement getSingleCourse = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@id='CosListTable']/tbody/tr/td/a[starts-with(text(),'" + courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2) + "')]")));
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", getSingleCourse);
                            //WebElement courseElement = driver.findElement(By.xpath("//table[@id='CosListTable']/tbody/tr/td/a[starts-with(text(),'" + courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2) + "')]"));
                            //courseElement.click(); 
                            // click 動作之後應該要檢查到了沒 (下側單課程資料)
                            long starBtn = System.currentTimeMillis();
                            long waitBtn = starBtn + 30000;
                            while (true) {
                                driver.switchTo().defaultContent();
                                driver.switchTo().frame(3);
                                try {
                                    if (System.currentTimeMillis() > waitBtn) {
                                        throw new UnknownError();
                                    }
                                    WebElement openTime = driver.findElement(By.xpath("//div[@id='Pan_CosInfo']/table[@class='table_1']/tbody/tr[1]"));
                                    List<WebElement> listOfRows = openTime.findElements(By.tagName("td"));
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + listOfRows.size() + "</span></html>"));
                                    int td;
                                    for (td = 0; td < listOfRows.size(); td++) {
                                        //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + listOfRows.get(td).getText() + "</span></html>"));
                                        if (listOfRows.get(td).getText().equals("上課時間")) {
                                            break;
                                        }
                                    }
                                    WebElement downElement = driver.findElement(By.xpath("//div[@id='Pan_CosInfo']/table[@class='table_1']/tbody/tr[2]/td[@class='cls_info_main'][" + (td + 1) + "]")); // 取得上課時間
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>「" + downElement.getText() + "」「"+ courses.getModel().getValueAt(row, 3).toString().substring(1) +"」</span></html>"));
                                    if (downElement.getText().replace(" ", "").equals(courses.getModel().getValueAt(row, 3).toString().substring(1).replace(" ", ""))) {
                                        break;
                                    }
                                } catch (Exception e) {
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + e + "</span></html>"));////////////////////////////////////////
                                }
                            }
                            // 課程內容 
                            String current = driver.findElement(By.xpath("//div[@id='Pan_CosInfo']/table[@class='table_1']/tbody/tr[2]/td[11]")).getText();
                            String total = driver.findElement(By.xpath("//div[@id='Pan_CosInfo']/table[@class='table_1']/tbody/tr[2]/td[10]")).getText();
                            if (!(Integer.parseInt(total) <= Integer.parseInt(current))) {  // 10 ,11 為人數上限與已選人數
                                //System.out.println(driver.findElement(By.xpath("//div[@id='Pan_CosInfo']/table[@class='table_1']/tbody/tr/td[contains(@class, 'cls_info_main')][1]")).getText() + " 人數已滿");
                                if (method.getSelectedIndex() != 0) { // 自動選課
                                    driver.switchTo().defaultContent();
                                    driver.switchTo().frame(1);
                                    WebElement button = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@id='CosListTable']/tbody/tr/td/a[starts-with(text(),'" + courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2) + "')]/../*[position()=1]")));//左邊課程查詢
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                                    //driver.findElement(By.xpath("//table[@id='CosListTable']/tbody/tr/td/a[starts-with(text(),'" + courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2) + "')]/../input")).click();
                                    // 確認是否選課
                                    new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.alertIsPresent());
                                    driver.switchTo().alert().accept();
                                    // 系統返回選課成功與否的 alert
                                    new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.alertIsPresent());
                                    Alert confirm = driver.switchTo().alert();
                                    confirm.accept();
                                    new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                                    //加選後檢查網頁中是否有課程
                                    driver.switchTo().defaultContent();
                                    driver.switchTo().frame(2);
                                    WebElement courseAdded = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@id='CosHtmlTable']/tbody/tr/td[contains(@id, '" + Integer.parseInt(str.substring(0, 1)) + Integer.parseInt(str.substring(1)) + "')]")));
                                    Integer checkCourseSelected = courseAdded.findElements(By.xpath("//table/tbody/tr/td/a[contains(text(),'" + courses.getModel().getValueAt(row, 1) + "," + courses.getModel().getValueAt(row, 2) + "')]")).size();
                                    //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + checkCourseSelected + "</span></html>"));
                                    driver.switchTo().defaultContent();

                                    if (checkAlert.isSelected()) {
                                        if (checkCourseSelected != 0) {
                                            if (music == null) {
                                                music = new MusicInfo();
                                                music.setRunning();
                                                music.start();
                                            }
                                        } else {
                                            //  沒加選成功 (課程不開放加選等等)
                                        }
                                    }
                                    if (checkMail.isSelected()) {
                                        if (checkCourseSelected != 0) {
                                            try {
                                                new SendEmail(AccountInfo.get("mail"), courses.getModel().getValueAt(row, 0).toString(), "<html>已幫您搶到 課程 ： 「"
                                                        + courses.getModel().getValueAt(row, 0).toString() + "」 !!! 😊<br>課程時間為 ： "
                                                        + courses.getModel().getValueAt(row, 3).toString().substring(1) + "<br>課號班別為 ： "
                                                        + courses.getModel().getValueAt(row, 1).toString() + " " + courses.getModel().getValueAt(row, 2).toString() + "</html>");
                                            } catch (Exception ex) {
                                                throw new UnknownError();
                                            }
                                        } else {
                                            //JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + this.loop + "</span></html>"));
                                            try {
                                                if (this.loop) {
                                                    new SendEmail(false, AccountInfo.get("mail"), courses.getModel().getValueAt(row, 0).toString(), "<html>目前程式 (Monitor) 監控到 課程 ： 「"
                                                            + courses.getModel().getValueAt(row, 0).toString() + "」現階段無法加選 !!! ☹<br>課程時間為 ： "
                                                            + courses.getModel().getValueAt(row, 3).toString().substring(1) + "<br>課號班別為 ： "
                                                            + courses.getModel().getValueAt(row, 1).toString() + " " + courses.getModel().getValueAt(row, 2).toString()
                                                            + "<br>造成此問題可能是此階段您無法加選(要到最後階段)、非本科系無法加選、已修過此課程、或是年級、課程限制等問題(反正就是你現階段這堂課你無法加選)。<br>請再次上學校官網確認課程相關加選問題，並把此課程從程式 (Monitor) 移除(不然程式會一直嘗試選它)。</html>");
                                                }
                                            } catch (Exception ex) {
                                                throw new UnknownError();
                                            }
                                            //  沒加選成功 (課程不開放加選等等)
                                        }
                                    }
                                    if (checkLine.isSelected()) {
                                        if (checkCourseSelected != 0) {
                                            SendMessage maker = new SendMessage(AccountInfo.get("lineEvent"), AccountInfo.get("lineKey"));
                                            try {
                                                maker.trigger("<html>已幫您搶到 課程 ： 「" + courses.getModel().getValueAt(row, 0).toString()
                                                        + "」 !!! 😊<br>課程時間為 ： " + courses.getModel().getValueAt(row, 3).toString().substring(1) + "<br>課號班別為 ： "
                                                        + courses.getModel().getValueAt(row, 1).toString() + " " + courses.getModel().getValueAt(row, 2).toString() + "</html>");
                                            } catch (Exception ex) {
                                                throw new UnknownError();
                                            }
                                        } else {
                                            // JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>" + this.loop + "</span></html>"));
                                            if (this.loop) {
                                                SendMessage maker = new SendMessage(AccountInfo.get("lineEvent"), AccountInfo.get("lineKey"));
                                                try {
                                                    maker.trigger("<html>目前程式 (Monitor) 監控到 課程 ： 「"
                                                            + courses.getModel().getValueAt(row, 0).toString() + "」現階段無法加選 !!! ☹<br>課程時間為 ： "
                                                            + courses.getModel().getValueAt(row, 3).toString().substring(1) + "<br>課號班別為 ： "
                                                            + courses.getModel().getValueAt(row, 1).toString() + " " + courses.getModel().getValueAt(row, 2).toString()
                                                            + "<br>造成此問題可能是此階段您無法加選(要到最後階段)、非本科系無法加選、已修過此課程、或是年級、課程限制等問題(反正就是你現階段這堂課你無法加選)。<br>請再次上學校官網確認課程相關加選問題，並把此課程從程式 (Monitor) 移除(不然程式會一直嘗試選它)。</html>");
                                                } catch (Exception ex) {
                                                    throw new UnknownError();
                                                }
                                            }
                                            //  沒加選成功 (課程不開放加選等等)
                                        }
                                    }
                                } else { // 監控但是不自動選課
                                    try {
                                        if (checkAlert.isSelected()) {
                                            if (music == null) {
                                                music = new MusicInfo();
                                                music.start();
                                            }
                                        }
                                        if (checkMail.isSelected() && !this.isNotice.get(courses.getModel().getValueAt(row, 1).toString() + "," + courses.getModel().getValueAt(row, 2).toString())) {
                                            try {
                                                new SendEmail(AccountInfo.get("mail"), courses.getModel().getValueAt(row, 0).toString(), "<html>目前 課程 ： 「"
                                                        + courses.getModel().getValueAt(row, 0).toString() + "」 尚有空缺餘額!!! 😊<br>請盡快至學校選課系統選課!!! <br>(請先 STOP 程式 (Monitor)，不然你跟程式會搶登入 session (類似權限)，會讓你選到一半被退出，因為程式又從另一個瀏覽器進去選課系統)"
                                                        + "<br>課程時間為 ： " + courses.getModel().getValueAt(row, 3).toString().substring(1)
                                                        + "<br>課號班別為 ： " + courses.getModel().getValueAt(row, 1).toString() + " " + courses.getModel().getValueAt(row, 2).toString()
                                                        + "<br>此課程 人數上限為 ： " + total
                                                        + "<br>此課程 目前人數為 ： " + current + "</html>", "");
                                            } catch (Exception ex) {
                                                throw new UnknownError();
                                            }
                                        }
                                        if (checkLine.isSelected() && !this.isNotice.get(courses.getModel().getValueAt(row, 1).toString() + "," + courses.getModel().getValueAt(row, 2).toString())) {
                                            SendMessage maker = new SendMessage(AccountInfo.get("lineEvent"), AccountInfo.get("lineKey"));
                                            try {
                                                maker.trigger("<html>目前 課程 ： 「"
                                                        + courses.getModel().getValueAt(row, 0).toString() + "」 尚有空缺餘額!!! 😊<br>請盡快至學校選課系統選課!!! <br>(請先 STOP 程式 (Monitor)，不然你跟程式會搶登入 session (類似權限)，會讓你選到一半被退出，因為程式又從另一個瀏覽器進去選課系統)"
                                                        + "<br>課程時間為 ： " + courses.getModel().getValueAt(row, 3).toString().substring(1)
                                                        + "<br>課號班別為 ： " + courses.getModel().getValueAt(row, 1).toString() + " " + courses.getModel().getValueAt(row, 2).toString()
                                                        + "<br>此課程 人數上限為 ： " + total
                                                        + "<br>此課程 目前人數為 ： " + current + "</html>");
                                            } catch (Exception ex) {
                                                throw new UnknownError();
                                            }
                                        }
                                    } finally {
                                        this.isNotice.put(courses.getModel().getValueAt(row, 1).toString() + "," + courses.getModel().getValueAt(row, 2).toString(), !loopNotice.isSelected());
                                    }
                                }
                                //Thread.sleep(3000);
                            }
                            driver.switchTo().defaultContent();
                            //Thread.sleep(1000);
                        } catch (Exception e) {
                            /*
                             SwingUtilities.invokeLater(new Runnable() {
                             public void run() {
                             JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>WebDriverException : 選課過程中網路斷線或是不穩定</span></html>"));////////////////////////////////////////
                             JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>WebDriverException : " + e + "</span></html>"));////////////////////////////////////////
                             }
                             });
                             break;
                             */
                            if (monitor.isSelected()) {
                                throw new UnknownError();
                            }
                        }
                    }

                    // 結束選課
                    driver.switchTo().frame("frameright");
                    WebElement end_sel = new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@id='CosDataListTB']/tbody/tr/td/input[@id='HBtn_EndSelCos']")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", end_sel);
                    //driver.findElement(By.xpath("//table[@id='CosDataListTB']/tbody/tr/td/input[@id='HBtn_EndSelCos']")).click();
                    new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.alertIsPresent());
                    driver.switchTo().alert().accept();
                    // 登入選課系統之延遲時間
                    if (delay.getSelectedIndex() <= 5) {
                        //System.out.println(Integer.parseInt(delay.getSelectedItem().toString().replaceAll("[^0-9]+", "")));
                        Thread.sleep(Integer.parseInt(delay.getSelectedItem().toString().replaceAll("[^0-9]+", "")) * 1000);
                    } else if (delay.getSelectedIndex() > 5) {
                        List timeList = Arrays.asList(delay.getSelectedItem().toString().replaceAll("[^0-9]+", " ").trim().split(" "));
                        //System.out.println(Integer.parseInt(timeList.get(0).toString()));
                        //System.out.println(Integer.parseInt(timeList.get(1).toString()));
                        //System.out.println(new Random().nextInt(Integer.parseInt(timeList.get(1).toString()) * 1000 - Integer.parseInt(timeList.get(0).toString()) * 1000 + 1) + Integer.parseInt(timeList.get(0).toString()) * 1000);
                        Thread.sleep(new Random().nextInt(Integer.parseInt(timeList.get(1).toString()) * 1000 - Integer.parseInt(timeList.get(0).toString()) * 1000 + 1) + Integer.parseInt(timeList.get(0).toString()) * 1000);
                    }
                    this.loop = false;
                } else {
                }
            } catch (InterruptedException e) {
                return null;
            } catch (IOException e) {
                if (monitor.isSelected()) {
                    if (MainPanel.retrieve.isSelected()) {
                        ChromeOptions chromeoptions = new ChromeOptions();
                        chromeoptions.addArguments("--headless");
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.setHeadless(true);
                        try {
                            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    DesiredCapabilities capabilities = new DesiredCapabilities();
                                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                                    chromeoptions.merge(capabilities);
                                    driver = new ChromeDriver();
                                } else {
                                }
                            } else {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    driver = new FirefoxDriver();
                                } else {
                                }
                            }
                        } catch (Exception ex) {
                        }
                    } else {
                        return null;
                    }
                }
            } catch (UnknownError e) {
                jLabel20.setForeground(Color.red);
                jLabel20.setText("偵測到嚴重的網路連線問題，導致連不上學校網站");
                if (monitor.isSelected()) {
                    if (MainPanel.retrieve.isSelected()) {
                        ChromeOptions chromeoptions = new ChromeOptions();
                        chromeoptions.addArguments("--headless");
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.setHeadless(true);
                        try {
                            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    DesiredCapabilities capabilities = new DesiredCapabilities();
                                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                                    chromeoptions.merge(capabilities);
                                    driver = new ChromeDriver();
                                } else {
                                }
                            } else {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    driver = new FirefoxDriver();
                                } else {
                                }
                            }
                        } catch (Exception ex) {
                        }
                    } else {
                        return null;
                    }
                }
            } catch (UnhandledAlertException e) {
                if (monitor.isSelected()) {
                    jLabel20.setForeground(Color.red);
                    jLabel20.setText("未預期之視窗問題 (程式執行中請勿對瀏覽器內做動作)");
                    if (MainPanel.retrieve.isSelected()) {
                        ChromeOptions chromeoptions = new ChromeOptions();
                        chromeoptions.addArguments("--headless");
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.setHeadless(true);
                        try {
                            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    DesiredCapabilities capabilities = new DesiredCapabilities();
                                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                                    chromeoptions.merge(capabilities);
                                    driver = new ChromeDriver();
                                } else {
                                }
                            } else {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    driver = new FirefoxDriver();
                                } else {
                                }
                            }
                        } catch (Exception ex) {
                        }
                    } else {
                        return null;
                    }
                }
            } catch (NoSuchElementException e) {
                if (monitor.isSelected()) {
                    jLabel20.setForeground(Color.red);
                    jLabel20.setText("未預期的網頁，找不到網頁標籤 (程式執行中請勿對瀏覽器內做動作)");
                    if (MainPanel.retrieve.isSelected()) {
                        ChromeOptions chromeoptions = new ChromeOptions();
                        chromeoptions.addArguments("--headless");
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.setHeadless(true);
                        try {
                            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    DesiredCapabilities capabilities = new DesiredCapabilities();
                                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                                    chromeoptions.merge(capabilities);
                                    driver = new ChromeDriver();
                                } else {
                                }
                            } else {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    driver = new FirefoxDriver();
                                } else {
                                }
                            }
                        } catch (Exception ex) {
                        }
                    } else {
                        return null;
                    }
                }
                //return null;
            } catch (WebDriverException e) {
                if (monitor.isSelected()) {
                    /*
                     SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                     if (monitor.isSelected()) {
                     JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>WebDriverException :" + e + "</span></html>"));////////////////////////////////////////
                     }
                     }
                     });*/
                    jLabel20.setForeground(Color.red);
                    jLabel20.setText("瀏覽器可能有連線問題(請確認是否網路斷線)、或是使用者亂動網頁(網頁只能觀賞用)");
                    if (MainPanel.retrieve.isSelected()) {
                        ChromeOptions chromeoptions = new ChromeOptions();
                        chromeoptions.addArguments("--headless");
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.setHeadless(true);
                        try {
                            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    DesiredCapabilities capabilities = new DesiredCapabilities();
                                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                                    chromeoptions.merge(capabilities);
                                    driver = new ChromeDriver();
                                } else {
                                }
                            } else {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    driver = new FirefoxDriver();
                                } else {
                                }
                            }
                        } catch (Exception ex) {
                        }
                    } else {
                        return null;
                    }
                }
                //return null;
            } catch (Exception e) {
                if (monitor.isSelected()) {
                    /*
                     SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                     JOptionPane.showMessageDialog(null, new DontEvenTry("<html><span style='color: red'>Exception : " + e + "</span></html>"));////////////////////////////////////////

                     }
                     });*/
                    jLabel20.setForeground(Color.red);
                    jLabel20.setText("其他問題導致連線中斷");
                    if (MainPanel.retrieve.isSelected()) {
                        ChromeOptions chromeoptions = new ChromeOptions();
                        chromeoptions.addArguments("--headless");
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        firefoxOptions.setHeadless(true);
                        try {
                            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    DesiredCapabilities capabilities = new DesiredCapabilities();
                                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeoptions);
                                    chromeoptions.merge(capabilities);
                                    driver = new ChromeDriver();
                                } else {
                                }
                            } else {
                                if (Login.visBrowser.isSelected()) {
                                    try {
                                        driver.quit();
                                    } catch (Exception ex) {
                                        System.out.println(ex);
                                    }
                                    driver = new FirefoxDriver();
                                } else {
                                }
                            }
                        } catch (Exception ex) {
                        }
                    } else {
                        return null;
                    }
                }
                //return null;
            } finally {

                try {
                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                }

            }
        } while (!isCancelled());
        return null;
    }

    @Override
    protected void done() {
        try {
            if (music != null) {
                music.terminate();
            }
            if (Login.visBrowser.isSelected()) {
                if (isAlertPresent()) {
                    driver.switchTo().alert().accept();
                }
                try {
                    driver.close();
                } catch (Exception e) {
                    System.out.println("exxxxx" + e);
                }
            } else {
                Thread.sleep(200);
                driver.get("https://www.google.com.tw/");
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            }

        } catch (Exception e) {
        }
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        }
    }
}
