package cn.zm1001.util.web.kaptcha.captcha;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * @Desc 数学计算验证码生成器
 * @Author Dongd_Zhou
 */
public class MathTextCreator extends DefaultTextCreator {
    private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9".split(",");

    @Override
    public String getText() {
        Random random = new Random();
        int x = random.nextInt(NUMBERS.length);
        int y = random.nextInt(NUMBERS.length);

        int randomOperands = (int) Math.round(Math.random() * 2);

        StringBuilder sb = new StringBuilder();
        int result;
        if (randomOperands == 0) {
            sb.append(NUMBERS[x]);
            sb.append("*");
            sb.append(NUMBERS[y]);
            result = x * y;
        } else if (randomOperands == 1) {
            if (x != 0 && y % x == 0) {
                sb.append(NUMBERS[y]);
                sb.append("/");
                sb.append(NUMBERS[x]);
                result = y / x;
            } else {
                sb.append(NUMBERS[x]);
                sb.append("+");
                sb.append(NUMBERS[y]);
                result = x + y;
            }
        } else if (randomOperands == 2) {
            if (x >= y) {
                sb.append(NUMBERS[x]);
                sb.append("-");
                sb.append(NUMBERS[y]);
                result = x - y;
            } else {
                sb.append(NUMBERS[y]);
                sb.append("-");
                sb.append(NUMBERS[x]);
                result = y - x;
            }
        } else {
            sb.append(NUMBERS[x]);
            sb.append("+");
            sb.append(NUMBERS[y]);
            result = x + y;
        }
        sb.append("=?@").append(result);
        return sb.toString();
    }
}
