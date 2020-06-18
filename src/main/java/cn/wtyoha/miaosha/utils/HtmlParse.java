package cn.wtyoha.miaosha.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 为静态html页面指定tag的地址属性添加@{}(即项目的ContentPath)
 */
public class HtmlParse {

    static Map<String, String> tagToAttr = new HashMap<>();
    static String[] targetTag;
    static String[] headSub;

    static {
        tagToAttr.put("img", "src");
        tagToAttr.put("link", "href");
        tagToAttr.put("script", "src");
        tagToAttr.put("a", "href");
        targetTag = new String[]{"img", "link", "script", "a"};
        headSub = new String[]{"link", "meta", "title"};
    }

    public static Document parseHtml(String s) {
        return Jsoup.parse(s);
    }

    public static void addContentPath(Element tag) {
        if (undoTag(tag)) {
            return;
        }
        String src, className = attrAimClassName(tag);
        assert className != null;
        src = tag.attr(className);
//        tag.removeAttr(className);
        String newAttr = "th:" + className, newSrc = "@{/" + src + "}";
        tag.attr(newAttr, newSrc);
    }

    private static boolean undoTag(Element tag) {
        String className = attrAimClassName(tag);
        if (className == null) {
            return true;
        }
        String attrValue = tag.attr(className);
        try {
            if ('#' == attrValue.charAt(0) ||
                    "@{/".equals(attrValue.substring(0, 3)) ||
                    "http://".equals(attrValue.substring(0, 7)) ||
                    "https://".equals(attrValue.substring(0, 8))
            ) {
                return true;
            }
        } catch (IndexOutOfBoundsException ignore) {
        }
        return false;
    }

    public static String attrAimClassName(Element tag) {
        if (!tagToAttr.containsKey(tag.tagName())) {
            return null;
        }
        return tagToAttr.get(tag.tagName());
    }

    public static String getHtmlByPath(String path) {
        FileChannel htmlSource = null;
        String html = null;
        try {
            htmlSource = new FileInputStream(path).getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int) htmlSource.size());
            htmlSource.read(buffer);
            html = new String(buffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert htmlSource != null;
                htmlSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;
    }

    public static void modifySpecifyTag(Elements elements) {
        for (Element element : elements) {
            addContentPath(element);
        }
    }

    public static String analysAndModify(Document document, String[] tagNames) {
        for (String tagName : tagNames) {
            Elements tags = document.getElementsByTag(tagName);
            modifySpecifyTag(tags);
        }
        Elements head = document.getElementsByTag("head");
        for (String s : headSub) {
            Elements elementsByTag = document.getElementsByTag(s);
            for (Element element : elementsByTag) {
                element.remove();
                head.append(element.toString());
            }
        }
        return document.toString();
    }

    public static void processSinglePage(String rePath, String outPath) throws IOException {
        String html = getHtmlByPath(rePath);
        Document document = parseHtml(html);
        // 添加thymeleaf依赖
        Elements htmlTag = document.getElementsByTag("html");
        htmlTag.attr("xmlns:th", "http://www.thymeleaf.org");

        // 修改所有静态路径
        String modify = analysAndModify(document, targetTag);

        // 后处理，加上<!DOCTYPE html> 并去除多余空行
        int indexBody = modify.indexOf("<body>");
        int first = indexBody + 6, second = modify.indexOf("<header");
        modify = modify.substring(0, first) + "\n" + modify.substring(second);
        modify = "<!DOCTYPE html>\n" + modify;

        // 保存结果
        File outFile = new File(outPath);
        FileOutputStream outputStream = new FileOutputStream(outFile);
        outputStream.write(modify.getBytes());
        System.out.println(rePath + " modify html done");
    }

    public static void processingBatchPage(File sourceDir, File outputDir) throws IOException {
        if (!sourceDir.exists()) {
            throw new RuntimeException("源文件夹不存在");
        }
        if (!outputDir.exists()) {
            boolean success = outputDir.mkdirs();
            if (!success) {
                throw new RuntimeException("目标文件夹创建失败");
            }
        }

        File[] files = sourceDir.listFiles();
        assert files != null;
        for (File file : files) {
            boolean isDir = file.isDirectory();
            // 文件是一个目录
            if (isDir) {
                processingBatchPage(file, outputDir);
            } else {
                String fileName = file.getName();
                if ("html".equals(fileName.substring(fileName.length() - 4))) {
                    String outFileName = outputDir.getAbsolutePath() + "/" + fileName;
                    processSinglePage(file.getAbsolutePath(), outFileName);
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("确定执行程序吗？");
        String input = scanner.nextLine();
        if (!('y' == input.charAt(0) || 'Y' == input.charAt(0))) {
            System.out.println("程序终止");
            return;
        }

//        String rePath = "D:\\D\\DeskTop\\Java\\Bootstrap模板\\index.html", outPath = "D:\\D\\JavaWorks\\MiaoShaSystem\\src\\main\\resources\\templates\\shopr.html";
//        processSinglePage(rePath, outPath);
        processingBatchPage(new File("D:\\D\\DeskTop\\Java\\Bootstrap模板"), new File("D:\\D\\DeskTop\\Java\\MiaoShaSystem动态模板"));

        System.out.println("程序结束");
    }
}
