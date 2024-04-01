package hcbang.automouse;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hcbang.automouse.Config.Check;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class ScheduledTask {
    @Autowired
    private Config config;

    private Robot robot;

    @PostConstruct
    private void printInfo() {
        log.info(config.toString());
        System.setProperty("java.awt.headless", "false");
    }

    @Scheduled(fixedRate = 60000)
    private void scheduledTask() {
        try {
            if(robot == null) robot = new Robot();

            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
            log.debug("{} {} {}", currentDate, currentTime, currentDayOfWeek);

            boolean isSkip = false;
            for(String exceptDate : config.getExceptDates()) {
                if(currentDate.equals(exceptDate)) {
                    isSkip = true;
                    log.debug("skip: {}", exceptDate);
                    break;
                }
            }

            if(!isSkip) {
                for(Check check : config.getCheckList()) {
                    if(check.getCondition().checkCondition(currentDate, currentTime, currentDayOfWeek)) {
                        log.info("doAction: {}", check.getName());
                        doAction(config.getActionList(check));
                        break;
                    }
                }
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doAction(List<String> actions) throws Exception {
        if(actions == null) {
            return;
        }

        String datas[], description, commands[];
        for(String action : actions) {
            datas = action.split("::");
            description = datas[0];
            commands = datas[1].split(" ");
            sleepSec(config.getActionDelaySec());
            log.info("action: {}", description);

            switch(commands[0]) {
                case "awake": awake(); break;
                case "point": point(commands); break;
                case "click": click(commands[1], commands[2]); break;
                case "sleep": sleepSec(commands[1]); break;
                case "keyin": {
                        StringBuilder sb = new StringBuilder();
                        for(int i = 1; i < commands.length; i++) {
                            if(i > 1) sb.append(" ");
                            sb.append(commands[i]);
                        }
                        keyin(sb.toString());
                    }
                    break;
                case "enter": doType(KeyEvent.VK_ENTER); break;
                case "exit": exit(); break;
            }
        }
    }

    private void awake() throws Exception {
        Point point = getPoint();
        if(point != null) {
            ++point.x;
            move(point);
            sleep(1);

            --point.x;
            move(point);
        } else {
            doType(KeyEvent.VK_ESCAPE);
        }
        sleepSec(3);
    }

    private void point(String[] commands) throws Exception {
        int count;
        try {
            count = Integer.parseInt(commands[1]);
        } catch(Exception e) {
            count = 1;
        }
        if(count > 5) count = 5;

        Point point = null;
        for(int i = 0; i < count; i++) {
            point = getPoint();
            if(point != null) {
                log.info("point: {} {}", point.x, point.y);
            }
            sleepSec(5);
        }
    }

    private void click(String xString, String yString) throws Exception {
        move(new Point(Integer.parseInt(xString), Integer.parseInt(yString)));
        sleep(100);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void keyin(String string) throws Exception {
        for(char c : string.toCharArray()) {
            switch(c) {
                case 'a': doType(KeyEvent.VK_A); break;
                case 'b': doType(KeyEvent.VK_B); break;
                case 'c': doType(KeyEvent.VK_C); break;
                case 'd': doType(KeyEvent.VK_D); break;
                case 'e': doType(KeyEvent.VK_E); break;
                case 'f': doType(KeyEvent.VK_F); break;
                case 'g': doType(KeyEvent.VK_G); break;
                case 'h': doType(KeyEvent.VK_H); break;
                case 'i': doType(KeyEvent.VK_I); break;
                case 'j': doType(KeyEvent.VK_J); break;
                case 'k': doType(KeyEvent.VK_K); break;
                case 'l': doType(KeyEvent.VK_L); break;
                case 'm': doType(KeyEvent.VK_M); break;
                case 'n': doType(KeyEvent.VK_N); break;
                case 'o': doType(KeyEvent.VK_O); break;
                case 'p': doType(KeyEvent.VK_P); break;
                case 'q': doType(KeyEvent.VK_Q); break;
                case 'r': doType(KeyEvent.VK_R); break;
                case 's': doType(KeyEvent.VK_S); break;
                case 't': doType(KeyEvent.VK_T); break;
                case 'u': doType(KeyEvent.VK_U); break;
                case 'v': doType(KeyEvent.VK_V); break;
                case 'w': doType(KeyEvent.VK_W); break;
                case 'x': doType(KeyEvent.VK_X); break;
                case 'y': doType(KeyEvent.VK_Y); break;
                case 'z': doType(KeyEvent.VK_Z); break;
                case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
                case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
                case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
                case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
                case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
                case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
                case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
                case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
                case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
                case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
                case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
                case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
                case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
                case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
                case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
                case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
                case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
                case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
                case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
                case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
                case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
                case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
                case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
                case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
                case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
                case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
                case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
                case '0': doType(KeyEvent.VK_0); break;
                case '1': doType(KeyEvent.VK_1); break;
                case '2': doType(KeyEvent.VK_2); break;
                case '3': doType(KeyEvent.VK_3); break;
                case '4': doType(KeyEvent.VK_4); break;
                case '5': doType(KeyEvent.VK_5); break;
                case '6': doType(KeyEvent.VK_6); break;
                case '7': doType(KeyEvent.VK_7); break;
                case '8': doType(KeyEvent.VK_8); break;
                case '9': doType(KeyEvent.VK_9); break;
                case '-': doType(KeyEvent.VK_MINUS); break;
                case '=': doType(KeyEvent.VK_EQUALS); break;
                case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
                case '!': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_1); break; //doType(KeyEvent.VK_EXCLAMATION_MARK);
                case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break; // doType(KeyEvent.VK_AT);
                case '#': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_3); break; // doType(KeyEvent.VK_NUMBER_SIGN);
                case '$': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_4); break; // doType(KeyEvent.VK_DOLLAR);
                case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
                case '^': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_6); break; // doType(KeyEvent.VK_CIRCUMFLEX);
                case '&': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_7); break; // doType(KeyEvent.VK_AMPERSAND);
                case '*': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_8); break; // doType(KeyEvent.VK_ASTERISK);
                case '(': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_9); break; // doType(KeyEvent.VK_LEFT_PARENTHESIS);
                case ')': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_0); break; // doType(KeyEvent.VK_RIGHT_PARENTHESIS);
                case '_': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break; // doType(KeyEvent.VK_UNDERSCORE);
                case '+': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS); break; // doType(KeyEvent.VK_PLUS);
                case '\t': doType(KeyEvent.VK_TAB); break;
                case '\n': doType(KeyEvent.VK_ENTER); break;
                case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
                case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
                case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
                case '{': doType(KeyEvent.VK_SHIFT, KeyEvent. VK_OPEN_BRACKET); break;
                case '}': doType(KeyEvent.VK_SHIFT, KeyEvent. VK_CLOSE_BRACKET); break;
                case '|': doType(KeyEvent.VK_SHIFT, KeyEvent. VK_BACK_SLASH); break;
                case ';': doType(KeyEvent.VK_SEMICOLON); break;
                case ':': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break; // doType(KeyEvent.VK_COLON);
                case '\'': doType(KeyEvent.VK_QUOTE); break;
                case '"': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE); break; // doType(KeyEvent.VK_QUOTEDBL);
                case ',': doType(KeyEvent.VK_COMMA); break;
                case '<': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA); break; // doType(KeyEvent.VK_LESS);
                case '.': doType(KeyEvent.VK_PERIOD); break;
                case '>': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD); break; // doType(KeyEvent.VK_GREATER);
                case '/': doType(KeyEvent.VK_SLASH); break;
                case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
                case ' ': doType(KeyEvent.VK_SPACE); break;
                default: throw new IllegalArgumentException("Cannot type character: " + c);
            }
            sleep(100);
        }
    }
 
    private void doType(int... keyCodes) {
        try {
            for(int i = 0, j = keyCodes.length; i < j; i++) {
                robot.keyPress(keyCodes[i]);
            }
            for(int i = keyCodes.length - 1; i >= 0; i--) {
                robot.keyRelease(keyCodes[i]);
            }
        } catch(Exception e) {
            log.error("keyCodes: {}", keyCodes, e);
        }
    }

    private void sleepSec(String secondString) throws Exception {
        sleepSec(Integer.parseInt(secondString));
    }
    private void sleepSec(int second) throws Exception {
        sleep(second*1000);
    }
    private void sleep(long millis) throws Exception {
        Thread.sleep(millis);
    }

    private void exit() throws Exception {
        System.exit(0);
    }

    private Point getPoint() throws Exception {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        return pointerInfo != null ? pointerInfo.getLocation() : null;
    }

    private void move(Point point) throws Exception {
        robot.mouseMove(point.x, point.y);
    }
}
