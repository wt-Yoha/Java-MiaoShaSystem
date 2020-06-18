package cn.wtyoha.miaosha.utils;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 向数据库中插入商品图片
 */
public class DBdataUtil {

    public static void insert(String[] imgs, PreparedStatement preparedStatement) throws ClassNotFoundException, SQLException {
        preparedStatement.setString(1, imgs[0]);
        preparedStatement.setString(2, imgs[1]);
        preparedStatement.setString(3, imgs[2]);
        preparedStatement.execute();
    }

    static boolean isMatch(Pattern pattern, String s) {
        return pattern.matcher(s).matches();
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://www.wtyoha.cn:3306/miaosha_system?serverTimezone=UTC", "root", "996895");
        String sql = "INSERT INTO goods(small_img, middle_img, large_img) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        HashMap<String, String[]> imgMap = new HashMap<>();

        File dir = new File("D:\\D\\JavaWorks\\Java-MiaoShaSystem-master\\src\\main\\resources\\static\\assets\\images");
        Pattern pattern1 = Pattern.compile(".*90x90\\.jpg");
        Pattern pattern2 = Pattern.compile(".*270x350\\.jpg");
        Pattern pattern3 = Pattern.compile(".*600x778\\.jpg");
        String[] strings;
        for (String s : dir.list()) {
            String imgName = s.split("-")[0];
            System.out.println(imgName);
            if (isMatch(pattern1, s)) {
                if (!imgMap.containsKey(imgName)) {
                    strings = new String[3];
                    imgMap.put(imgName, strings);
                } else {
                    strings = imgMap.get(imgName);
                }
                strings[0] = s;
            }
            if (isMatch(pattern2, s)) {
                if (!imgMap.containsKey(imgName)) {
                    strings = new String[3];
                    imgMap.put(imgName, strings);
                } else {
                    strings = imgMap.get(imgName);
                }
                strings[1] = s;
            }
            if (isMatch(pattern3, s)) {
                if (!imgMap.containsKey(imgName)) {
                    strings = new String[3];
                    imgMap.put(imgName, strings);
                } else {
                    strings = imgMap.get(imgName);
                }
                strings[2] = s;
            }

        }

        connection.setAutoCommit(false);
        for (String key : imgMap.keySet()) {
            String[] imgs = imgMap.get(key);
            insert(imgs, preparedStatement);
        }
        connection.commit();

        System.out.println("Yes");
    }

}
