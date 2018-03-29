package com.github.xjtushilei.example.百科名医;

import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.model.Page;
import com.github.xjtushilei.utils.JsonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author scriptshi
 * 2018/3/29
 */
public class 结果保存器 implements Saver {

    public static String path = "D:\\名医百科文件存储位置\\";

    @Override
    public void save(Page page) {
        File fileRoot = new File(path);
        if (!fileRoot.exists() && fileRoot.isDirectory()) {
            System.out.println("目标文件夹不存在，开始创建");
            if (fileRoot.mkdirs()) {
                System.out.println("\"" + path + "\"创建成功！");
            } else {
                System.out.println("目标文件夹不存在，创建失败");
                System.exit(-1);
            }
        }

        String name = (String) page.getItems().get("name");
        if (name == null || name.equals("null")) {
            return;
        }
        String json = JsonUtils.toJsonBeautiful(page.getItems());
        try {
//            把json存到文件里
            FileUtils.write(new File(path + name + ".json"), json, "utf-8");
            System.out.println("保存成功：" + path + name + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
