import java.text.DecimalFormat;
import java.util.Stack;
import java.util.regex.Pattern;


/**
 * 学堂在线 软件工程(2016春) 第一章作业
 *
 * Created with WangCheng
 * E-mail:  blue20080@gmail.com
 * Date: 2016-03-09
 *Description:
 * 调用方法:
 * 1、编译java文件：javac  Main
 * 2、运行：java Main  1 + 2 - 3 + 4
 */


public class main {

    public static void main(String[] args) {

        //处理入参
        StringBuffer str = new StringBuffer();
        for(String s : args)
        {
            str = str.append(s);
        }

        String checkResult = check(str.toString());

        if(!checkResult.contains("ERROR")){
            System.out.println(stringToArithmetic(str.toString()));
        }else{
            System.out.println(checkResult);
        }
    }


    //方法：给出一个算术表达式（中缀表达式），得到计算结果。 例如 (5+8+10)*1，返回23
    public static String stringToArithmetic(String string) {
        Double f = 0D;
        try {
             f = suffixToArithmetic(infixToSuffix(string));
        }catch (Exception e){
           return  "FORMAT ERROR";
        }
        return formatDoubleToString(f,10,true);
    }

    /**
     * 中缀表达式转后缀表达式 只处理了+,-,*,/和括号，没有处理负号及其它运算符，也没对前缀表达式验证。
     * 如要处理负号，可对表达式进行预转义处理，当下面条件成立时，将负号换成单目运算符"!" infix.charAt[i]=='-'&&(
     * i==0||infix.charAt[i-1]=='(')
     * 3*6/4+3
     * 3+6-4           3 6 + 4 -
     * 3+(6-4/2)*5    3 6 4 2 / - 5 * +
     */
    //方法：中缀表达式转成后缀表达式
    public static String infixToSuffix(String infix) {
        Stack<Character> stack = new Stack<Character>();
        String suffix = "";
        int length = infix.length();
        for (int i = 0; i < length; i++) {
            Character temp;
            char c = infix.charAt(i);
            switch (c) {
                // 忽略空格
                case ' ':
                    break;
                // 碰到'('，push到栈
                case '(':
                    stack.push(c);
                    break;
                // 碰到'+''-'，将栈中所有运算符弹出，送到输出队列中
                case '+':
                case '-':
                    while (stack.size() != 0) {
                        temp = stack.pop();
                        if (temp == '(') {
                            stack.push('(');
                            break;
                        }
                        suffix += " " + temp;
                    }
                    stack.push(c);
                    suffix += " ";
                    break;
                // 碰到'*''/'，将栈中所有乘除运算符弹出，送到输出队列中
                case '*':
                case '^':
                case '/':
                    while (stack.size() != 0) {
                        temp = stack.pop();
                        if (temp == '(' || temp == '+' || temp == '-') {
                            stack.push(temp);
                            break;
                        } else {
                            suffix += " " + temp;
                        }
                    }
                    stack.push(c);
                    suffix += " ";
                    break;
                // 碰到右括号，将靠近栈顶的第一个左括号上面的运算符全部依次弹出，送至输出队列后，再丢弃左括号
                case '%':
                    while (stack.size() != 0) {
                        temp = stack.pop();
                        if (temp == '(' || temp == '+' || temp == '-') {
                            stack.push(temp);
                            break;
                        } else {
                            suffix += " " + temp;
                        }
                    }
                    stack.push(c);
                    suffix += " ";
                    break;
                // 碰到右括号，将靠近栈顶的第一个左括号上面的运算符全部依次弹出，送至输出队列后，再丢弃左括号
                case ')':
                    while (stack.size() != 0) {
                        temp = stack.pop();
                        if (temp == '(')
                            break;
                        else
                            suffix += " " + temp;
                    }
                    // suffix += " ";
                    break;
                //如果是数字，直接送至输出序列
                default:
                    suffix += c;
            }
        }

        //如果栈不为空，把剩余的运算符依次弹出，送至输出序列。
        while (stack.size() != 0) {
            suffix += " " + stack.pop();
        }
        return suffix;
    }


    /**
     * postfix
     *
     * @return double
     */
    //方法：通过后缀表达式求出算术结果
    public static double suffixToArithmetic(String postfix) {
        Pattern pattern = Pattern.compile("\\d+||(\\d+\\.\\d+)"); //使用正则表达式 匹配数字
        String strings[] = postfix.split(" ");  //将字符串转化为字符串数组

        for (int i = 0; i < strings.length; i++)
            strings[i].trim();  //去掉字符串首尾的空格
        Stack<Double> stack = new Stack<Double>();

        for (int i = 0; i < strings.length; i++) {

            if (strings[i].equals(""))
                continue;

            //如果是数字，则进栈
            if ((pattern.matcher(strings[i])).matches()) {

                stack.push(Double.parseDouble(strings[i]));
            } else {
                //如果是运算符，弹出运算数，计算结果。
                double y = stack.pop();
                double x = stack.pop();
                stack.push(caculate(x, y, strings[i])); //将运算结果重新压入栈。
            }
        }
        return stack.pop(); //弹出栈顶元素就是运算最终结果。

    }

    /*
    输入校验
     */
    public static String check(String str){

        String msg = "";
        str = str.replace(" ","");

        //判断含有字母等其他非法输入
        if(str.matches(".*[a-zA-Z]+.*") || str.contains("%")){
            msg = "INPUT ERROR";
        }

        //判断分母为0
        if(str.contains("/0") || str.contains("^0") || str.contains("%0")){
            msg = "VALUE ERROR";
        }

        //判断连续输入多个运算符的情况
        if(str.contains("++") || str.contains("--") || str.contains("**")  || str.contains("//") | str.contains("^^")  ){
            msg = " FORMAT ERROR";
        }
        //判断括号不匹配情况
        if(findStr(str,"(") != findStr(str,")")){
            msg = " FORMAT ERROR";
        }

        return  msg;
    }

    /*
    判断字符出现次数
     */
    public static int findStr(String srcText, String keyword) {
        int count = 0;
        int leng = srcText.length();
        int j = 0;
        for (int i = 0; i < leng; i++) {
            if (srcText.charAt(i) == keyword.charAt(j)) {
                j++;
                if (j == keyword.length()) {
                    count++;
                    j = 0;
                }
            } else {
                i = i - j;// should rollback when not match
                j = 0;
            }
        }
        return count;
    }

    /**
     *
     *<b>Summary: double类型保留指定位数小数，返回字符串,五舍六入</b>
     * formatDoubleToMoney()
     * @param value 传入的参数
     * @param digits 指定位数, 如果为空或者小于0返回原值
     * @param remove 是否去除0，true 去除，false 不去除
     * @return
     */
    public static String formatDoubleToString(Double value,Integer digits,boolean remove){
        if(value == null){
            return "";
        }
        if(digits == null || digits < 0){
            return String.valueOf(value);
        } else if(digits == 0){
            DecimalFormat df=new DecimalFormat("0");
            return df.format(value);
        } else {
            String temp = "0";
            if(remove){
                temp ="#";
            }
            StringBuffer buffer = new StringBuffer("0.");
            for(int i=0;i<digits;i++){
                buffer.append(temp);
            }
            DecimalFormat df=new DecimalFormat(buffer.toString());
            return df.format(value);
        }
    }

    private static double caculate(double x, double y, String simble) {
        if (simble.trim().equals("+"))
            return x + y;
        if (simble.trim().equals("-"))
            return x - y;
        if (simble.trim().equals("*"))
            return x * y;
        if (simble.trim().equals("/"))
            return x / y;
        if (simble.trim().equals("%"))
            return x % y;
        if (simble.trim().equals("^"))
            return Math.pow(x,y);
        return 0;
    }
}