package com.github.godshang.devtool.util;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrontabUtils {

    public enum CrontabType {
        Linux, Spring, Quartz;
    }

    public static List<String> getNextExecution(String expression, CrontabType crontabType, int num) {
        Cron cron = parse(expression, crontabType);
        ZonedDateTime dateTime = ZonedDateTime.now();
        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Optional<ZonedDateTime> optional = executionTime.nextExecution(dateTime);
            if (optional.isPresent()) {
                dateTime = optional.get();
                list.add(DateTimeUtils.format(dateTime));
            }
        }
        return list;
    }

    public static Cron parse(String expression, CrontabType crontabType) {
        CronDefinition cronDefinition = getCronDefinition(crontabType);
        CronParser parser = new CronParser(cronDefinition);
        return parser.parse(expression);
    }

    private static CronDefinition getCronDefinition(CrontabType crontabType) {
        switch (crontabType) {
            case Linux -> {
                return CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
            }
            case Spring -> {
                return CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
            }
            case Quartz -> {
                return CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
            }
        }
        return null;
    }
}
