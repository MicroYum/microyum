package com.microyum.common.cache;

import com.google.common.collect.Lists;
import com.microyum.dao.jpa.MyDayOffDao;
import com.microyum.model.common.MyDayOff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author syaka.hong
 */
@Component
public class DayOffCache {

    @Autowired
    private MyDayOffDao dayOffDao;

    private List<MyDayOff> dayOffListCache = Lists.newArrayList();

    private Duration duration;
    private LocalDateTime lastSyncDateTime;
    private boolean syncStatus = false;

    public synchronized void init() {

        if (null != lastSyncDateTime) {
            duration = Duration.between(lastSyncDateTime, LocalDateTime.now());
        } else {
            duration = Duration.ZERO;
        }

        if (!syncStatus || duration.toMinutes() > 60) {
            dayOffListCache = dayOffDao.findAll();

            //update sync status
            syncStatus = true;
            lastSyncDateTime = LocalDateTime.now();
        }
    }

    public List<MyDayOff> getMyDayOffList() {
        init();
        return dayOffListCache;
    }
}
