package com.github.xjtushilei.dao;

import com.github.xjtushilei.model.KLine;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by max on 2017/5/10.
 */
public class KLineDao {

    private static KLineDao kLineDao;

    public static KLineDao getInstance() {
        if (kLineDao == null) {
            kLineDao = new KLineDao();
        }
        return kLineDao;
    }

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        if(dataSource == null) {
            BasicDataSource ds = new BasicDataSource();
            Properties prop = new Properties();
            try {
                Class clazz = KLineDao.class;
                prop.load(clazz.getResourceAsStream("/application.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ds.setUsername(prop.getProperty("datasource.username"));
            ds.setPassword(prop.getProperty("datasource.password"));
            ds.setDriverClassName(prop.getProperty("datasource.driver"));
            ds.setUrl(prop.getProperty("datasource.url"));
            dataSource = ds;
        }
        if(jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }

        return jdbcTemplate;
    }

    public void insert(String sql, List<KLine> lineList) {
        List<Object[]> oList = new ArrayList<>();
        for(KLine kLine:lineList) {
            Object[] os = new Object[11];
            os[0] = kLine.getOpen();
            os[1] = kLine.getHigh();
            os[2] = kLine.getLow();
            os[3] = kLine.getClose();
            os[4] = kLine.getVolume();
            os[5] = kLine.getLevel();
            os[6] = Timestamp.valueOf(kLine.getStartTime());
            os[7] = Timestamp.valueOf(kLine.getEndTime());
            os[8] = kLine.getCode();
            os[9] = kLine.getDirection();
            os[10] = kLine.getAvg_price();
            oList.add(os);
        }
        getJdbcTemplate().batchUpdate(sql, oList);
    }
}
