package autochoosecourse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GetClassInfo {

    private String resourceName = null;
    private JSONTokener tokener = null;
    private JSONObject object = null;
    private final String strCourse = "CourseName";
    private final String strTeacher = "Teacher";
    private final String strTime = "Time";

    public GetClassInfo(String fileName) {
        this.resourceName = fileName + ".json";
        File f = new File("resourses/" + this.resourceName);
        if (f.isFile() && f.canRead()) {
            try {
                InputStreamReader read = new InputStreamReader (new FileInputStream(f), "UTF-8"); 
                BufferedReader reader=new BufferedReader(read); 
                tokener = new JSONTokener(reader);
                object = new JSONObject(tokener);
            } catch (Exception ex) {
                Logger.getLogger(GetClassInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else
            throw new NullPointerException("Cannot find resource file " + resourceName);
    }

    public String getCourseName(String str) throws JSONException {
        JOptionPane.showMessageDialog(null,  new SetLabelFont("<html>獲取「<span style='color:red;'>"+object.getJSONArray(str).getJSONObject(0).getString(strCourse)+"</span>」課程資料。</html>"));
        return object.getJSONArray(str).getJSONObject(0).getString(strCourse);
    }

    public String getCourseTime(String str) throws JSONException {
        String concat = " ";
        for (int i = 0; i < object.getJSONArray(str).getJSONObject(0).getJSONArray(strTime).length(); i++) {
            concat = concat + object.getJSONArray(str).getJSONObject(0).getJSONArray(strTime).getString(i) + " ";
        }
        return concat;
    }

    public String getCourseTeacher(String str) throws JSONException {
        return object.getJSONArray(str).getJSONObject(0).getString(strTeacher);
    }
}
