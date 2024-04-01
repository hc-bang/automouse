package hcbang.automouse;

import java.util.List;
import java.util.ArrayList;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
@ConfigurationProperties(prefix = "schedule")
public class Config {
    private int actionDelaySec;
    private List<Check> checkList;
    private List<ActionTemplate> actionTemplateList;
    private String[] exceptDates;

    public List<String> getActionList(Check check) {
        List<String> actionList = new ArrayList<>();

        for(String templateName : check.getActionTemplates()) {
            for(ActionTemplate template : actionTemplateList) {
                if(template.getName().equals(templateName)) {
                    for(String action : template.getActions()) {
                        actionList.add(action);
                    }
                }
            }
        }
        return actionList;
    }

    @Data
    public static class Check {
        private String name;
        private Condition condition;
        private List<String> actionTemplates;
    }

    @Data
    public static class Condition {
        private int[] dayOfWeeks;
        private String time;
        
        public boolean checkCondition(String currentDate, String currentTime, int currentDayOfWeek) throws Exception {
            boolean result = false;

            if(dayOfWeeks != null) {
                for(int dayOfWeek : dayOfWeeks) {
                    if(currentDayOfWeek == dayOfWeek) {
                        result = true;
                    }
                }
            } else {
                result = true;
            }
            if(!result) return result;

            if(time != null) {
                String timeCheckString[], compare, time1, time2;
                timeCheckString = time.split(" ");
                compare = timeCheckString[0];
                time1 = timeCheckString.length > 1 ? timeCheckString[1] : null;
                time2 = timeCheckString.length > 2 ? timeCheckString[2] : null;
                switch(compare) {
                    case "eq":
                        result = time1 != null && currentTime.equals(time1);
                        break;
                    case "gt":
                        result = time1 != null && currentTime.compareTo(time1) >= 0;
                        break;
                    case "lt":
                        result = time1 != null && currentTime.compareTo(time1) <= 0;
                        break;
                    case "bw":
                        result = time2 != null && currentTime.compareTo(time1) >= 0 && currentTime.compareTo(time2) <= 0;
                        break;
                    default:
                        result = false;
                }
            } else {
                result = true;
            }
            if(!result) return result;

            return result;
        }
    }

    @Data
    public static class ActionTemplate {
        private String name;
        private List<String> actions;
    }
}
