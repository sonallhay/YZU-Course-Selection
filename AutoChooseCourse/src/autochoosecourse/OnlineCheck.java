package autochoosecourse;

import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OnlineCheck {

    public static WebDriver driver = null;
    public static String diskDriverName = null;
    private boolean isAccount = false;
    private String Account = null;
    private String Password = null;
    private String semesYear = null;
    private JFrame mainFrame = null;
    private UpdateWorker updateWorker;
    private StringBuilder nameStringBuilder = new StringBuilder("s1084832");
    private static final JSONObject data = new JSONObject();
    List<String> classTime = Arrays.asList("101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113",
            "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213",
            "301", "302", "303", "304", "305", "306", "307", "308", "309", "310", "311", "312", "313",
            "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413",
            "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "511", "512", "513",
            "601", "602", "603", "604", "605", "606", "607", "608", "609", "610", "611", "612", "613",
            "701", "702", "703", "704", "705", "706", "707", "708", "709", "710", "711", "712", "713");

    public OnlineCheck(String s, String acc, String pwd, JFrame frame) throws JSONException {
        this.semesYear = s;
        this.Account = acc;
        this.Password = pwd;
        this.mainFrame = frame;
    }

    public Boolean checksemesYearAlived(String driverStr) {
        mainFrame.setEnabled(false);
        boolean isAlived = false;

        try {
            File dir = new File(new File(Login.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath());
            //JOptionPane.showMessageDialog(null, new TextFeelCustom(new File(FrontFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath()));
            //System.out.println(new File(FrontFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath());

            File[] exeFile = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return FilenameUtils.isExtension(file.getName(), "exe");
                } 
            });

            if (driverStr.equals("chrome")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                options.merge(capabilities);
                try {
                    for (File file : exeFile) {
                        //System.out.println(file.getName().matches("(.*)chromedriver(.*)"));
                        if (file.getName().matches("(.*)chromedriver(.*)")) {
                            //System.out.println("inside chrom");
                            System.setProperty("webdriver.chrome.driver", dir + "/" + file.getName());
                            if (driver != null) {
                                try {
                                    if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("firefox")) {
                                        driver.quit();
                                        driver = new ChromeDriver(options);
                                    }
                                } catch (Exception e) {
                                    driver.quit();
                                    driver = new ChromeDriver(options);
                                }
                            } else {
                                driver = new ChromeDriver(options);
                            }
                            break;
                        }
                    }
                } catch (SessionNotCreatedException e) {
                    JOptionPane.showMessageDialog(null, new SetLabelFont("<html>目前您的<span style='color: red'>「chromedriver(...).exe」</span>版本已經過舊，請至 README.TXT 詳閱解決辦法。</html>"));
                    System.exit(0);
                } catch (Exception e) {
                    System.err.println(e.getClass().getSimpleName());
                }
                if (driver != null && ((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("firefox")) {
                    try {
                        driver.quit();
                        driver = null;
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            } else if (driverStr.equals("firefox")) {

                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setHeadless(true);
                try {
                    for (File file : exeFile) {
                        //System.out.println("in firefox " + file.getName());
                        //System.out.println(file.getName().matches("(.*)geckodriver(.*)"));
                        if (file.getName().matches("(.*)geckodriver(.*)")) {
                            //System.out.println("inside");
                            System.setProperty("webdriver.gecko.driver", dir + "/" + file.getName());
                            if (driver != null) {
                                try {
                                    if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                                        driver.quit();
                                        driver = new FirefoxDriver(firefoxOptions);
                                    }
                                } catch (Exception e) {
                                    driver.quit();
                                    driver = new FirefoxDriver(firefoxOptions);
                                }
                            } else {
                                driver = new FirefoxDriver(firefoxOptions);
                            }
                            break;
                        }
                    }
                } catch (SessionNotCreatedException e) {
                    JOptionPane.showMessageDialog(null, new SetLabelFont("<html>目前您的<span style='color: red'>「geckodriver(...).exe」</span>版本已經過舊，請至 README.TXT 詳閱解決辦法。</html>"));
                    System.exit(0);
                } catch (NullPointerException e) {
                    System.err.println(e);
                }
                if (driver != null && ((RemoteWebDriver) driver).getCapabilities().getBrowserName().toLowerCase().equals("chrome")) {
                    try {
                        driver.quit();
                        driver = null;
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            }

            if (driver == null) {
                if (driverStr.equals("firefox")) {
                    JOptionPane.showMessageDialog(null, new SetLabelFont("<html>請確認您的<span style='color: red'>「geckodriver(...).exe」</span>是否有與程式主執行檔(.exe)在同一層位置。</html>"));
                } else {
                    JOptionPane.showMessageDialog(null, new SetLabelFont("<html>請確認您的<span style='color: red'>「chromedriver(...).exe」</span>是否有與程式主執行檔(.exe)在同一層位置。</html>"));
                }
                System.exit(0);
            }

            // 檢查是否是元智學生
            driver.get("http://lib.yzu.edu.tw/ajaxYZlib/PersonLogin/PersonLogin.aspx?PassURL=../UserAccount/Personal.aspx");
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            driver.findElement(By.id("txtUserID")).sendKeys(Account);
            driver.findElement(By.id("txtUserPWD")).sendKeys(Password);
            driver.findElement(By.id("btnConfirm")).click();

            //嘗試看看是否有問券或填錯帳密
            try {
                driver.switchTo().alert().accept();
            } catch (NoAlertPresentException Ex) {
            }
            
            //登入暫停2秒
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(OnlineCheck.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // 是授權者但帳密填錯
            if (!driver.getCurrentUrl().equals("http://lib.yzu.edu.tw/ajaxYZlib/UserAccount/Personal.aspx")) {
                throw new UnhandledAlertException("");
            }

            // 檢查是否有此學年度
            driver.get("https://portalfun.yzu.edu.tw/cosSelect/index.aspx?D=G");
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            driver.findElement(By.id("RadioButton4")).click();
            WebElement select = driver.findElement(By.id("DDL_YM4"));
            Select dropDown = new Select(select);
            List<WebElement> Options = dropDown.getOptions();
            for (WebElement option : Options) {
                String str;
                if (option.getText().contains(" ")) {
                    str = option.getText().substring(0, option.getText().indexOf(" "));
                } else {
                    str = option.getText();
                }
                if (str.equals(semesYear)) {
                    isAlived = true;
                }
            }
        } catch (NoSuchSessionException ex) {
        } catch (URISyntaxException ex) {
            Logger.getLogger(OnlineCheck.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            mainFrame.setEnabled(true);
        }
        return isAlived;
    }

    public boolean getIsAccount() {
        return this.isAccount;
    }

    public void accessInternet() {
        setUpBar(this.mainFrame);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    getCourseData(); // scrape courses from website
                    setCourseData(); // save the json file of courses data into the disk
                    //driver.quit();
                } catch (JSONException ex) {
                    Logger.getLogger(OnlineCheck.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    private void getCourseData() throws JSONException {
        driver.get("https://portalfun.yzu.edu.tw/cosSelect/index.aspx?D=G");
        driver.findElement(By.id("RadioButton4")).click();
        WebElement select = driver.findElement(By.id("DDL_YM4"));
        Select dropDown = new Select(select);
        List<WebElement> Options = dropDown.getOptions();
        for (WebElement option : Options) {
            String str;
            if (option.getText().contains(" ")) {
                str = option.getText().substring(0, option.getText().indexOf(" "));
            } else {
                str = option.getText();
            }
            if (str.equals(semesYear)) {
                option.click(); //get 學年度 (1072)
            }
        }
        for (String time : classTime) {
            driver.findElement(By.xpath("//input[@value=" + time + "]")).click();

            List<WebElement> courseList = driver.findElements(By.xpath("//table[@id='Table1']/tbody/tr[contains(@class, 'record2') or contains(@class, 'hi_line')]"));//div[contains(@class, 'value') and contains(@class, 'test')]
            for (int i = 0; i < courseList.size(); i++) {

                List<WebElement> courseID = courseList.get(i).findElements(By.xpath("*[contains(@title, '查詢課程大綱') or contains(@title, '查詢學程')][1]"));
                List<WebElement> courseName = courseList.get(i).findElements(By.xpath("*[contains(@title, '查詢課程大綱') or contains(@title, '查詢學程')][2]//a[1]"));
                List<WebElement> courseTeacher = courseList.get(i).findElements(By.xpath("*[last()]"));

                for (int j = 0; j < courseID.size(); j++) {
                    String id = courseID.get(j).getText().replace(" ", ",");
                    String name = courseName.get(j).getAttribute("innerHTML");
                    String teacher = courseTeacher.get(j).getAttribute("innerHTML");
                    if (!data.has(id)) {
                        JSONArray array = new JSONArray();
                        JSONObject item = new JSONObject();
                        item.put("Teacher", teacher);
                        item.put("CourseName", name);
                        item.put("Time", new JSONArray().put(time));
                        array.put(item);
                        data.put(id, array);
                    } else {
                        data.getJSONArray(id).getJSONObject(0).append("Time", time);
                    }
                }
            }
            updateWorker.setMyProgress();
        }
    }

    private void setCourseData() {
        OutputStreamWriter write = null;
        BufferedWriter writer = null;
        try {
            write = new OutputStreamWriter(new FileOutputStream("resourses/" + semesYear + "CoursesData.json"), "UTF-8");
            writer = new BufferedWriter(write);
            writer.write(data.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != writer) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != write) {
                    write.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpBar(JFrame frame) {
        JFrame f = new JFrame("Loading Data");
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setResizable(false);
        Container content = f.getContentPane();
        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Reading...(執行中無法關閉)");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        java.awt.Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        f.setSize(300, 100);
        f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height / 2 - f.getSize().height / 2);
        f.setVisible(true);
        updateWorker = new UpdateWorker(f, progressBar, frame, semesYear, Account, Password);
        updateWorker.execute();

    }

}

class UpdateWorker extends SwingWorker {

    String account = null;
    String password = null;
    String semesY = null;
    int progress = 0;
    JProgressBar bar = null;
    JFrame main = null;
    JFrame f = null;
    Boolean closeDownload = false;

    public UpdateWorker(JFrame f, JProgressBar bar, JFrame frame, String s, String acc, String pwd) {
        this.bar = bar;
        this.f = f;
        this.main = frame;
        this.semesY = s;
        this.account = acc;
        this.password = pwd;
        main.setEnabled(false);
    }

    @Override
    protected String doInBackground() throws Exception {

        Random rdm = new Random();

        while (progress < 91) {
            try {
                Thread.sleep(rdm.nextInt(500) + 500);
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(progress);
            bar.setValue(progress);
            bar.paintImmediately(0, 0, 300, 100);

        }
        while (progress != 100) {
            try {
                Thread.sleep(rdm.nextInt(300) + 300);
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdateWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            progress = progress + 1;
            bar.setValue(progress);
            bar.paintImmediately(0, 0, 300, 100);
        }
        return null;
    }

    @Override
    protected void done() {
        if (progress == 100) {
            Toolkit.getDefaultToolkit().beep();
            f.setVisible(false);
            f.dispose();
            JOptionPane.showMessageDialog(null, new SetLabelFont("課程資料已獲取。"));
            main.setEnabled(true);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MainPanel(semesY, account, password, main).setVisible(true);
                }
            });
        } else {
            main.setEnabled(true);
        }

    }

    public void setMyProgress() {
        progress = progress + 1;
    }
}
