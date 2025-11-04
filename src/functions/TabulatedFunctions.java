package functions;
import java.io.*;

public class TabulatedFunctions {
    // Приватный конструктор
    private TabulatedFunctions() {}

    // --- Метод для Задания 6 ---
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Tabulation bounds are out of function's domain");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Points count must be 2 or more");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, function.getFunctionValue(x));
        }
        // Возвращаем объект одного из классов, реализующих интерфейс
        return new ArrayTabulatedFunction(points); 
        // Можно вернуть и LinkedListTabulatedFunction, по заданию это не важно
    }

    // --- Задание 7 (Байтовые потоки) ---
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(function.getPointsCount());
        for (int i = 0; i < function.getPointsCount(); i++) {
            dos.writeDouble(function.getPointX(i));
            dos.writeDouble(function.getPointY(i));
        }
        dos.flush(); // Принудительно записываем буфер
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int count = dis.readInt();
        FunctionPoint[] points = new FunctionPoint[count];
        for (int i = 0; i < count; i++) {
            points[i] = new FunctionPoint(dis.readDouble(), dis.readDouble());
        }
        return new ArrayTabulatedFunction(points);
    }

    // --- Задание 7 (Символьные потоки) ---
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter pw = new PrintWriter(out);
        pw.println(function.getPointsCount());
        for (int i = 0; i < function.getPointsCount(); i++) {
            pw.println(function.getPointX(i) + " " + function.getPointY(i));
        }
        pw.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.nextToken();
        int count = (int) tokenizer.nval;

        FunctionPoint[] points = new FunctionPoint[count];
        for (int i = 0; i < count; i++) {
            tokenizer.nextToken();
            double x = tokenizer.nval;
            tokenizer.nextToken();
            double y = tokenizer.nval;
            points[i] = new FunctionPoint(x, y);
        }
        return new ArrayTabulatedFunction(points);
    }
}
