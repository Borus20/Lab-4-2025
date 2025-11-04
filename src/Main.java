import functions.*;
import functions.basic.*;
import functions.meta.Power; // <-- ВОТ ИСПРАВЛЕНИЕ
import java.io.*;

public class Main {

    public static void main(String[] args) {
        
        // --- Задание 8, п.1: Sin и Cos ---
        System.out.println("--- Задание 8.1: Sin и Cos ---");
        Function sin = new Sin();
        Function cos = new Cos();
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, sin(x)=%.4f, cos(x)=%.4f\n", x, sin.getFunctionValue(x), cos.getFunctionValue(x));
        }

        // --- Задание 8, п.2: Табулирование Sin и Cos ---
        System.out.println("\n--- Задание 8.2: Табулирование Sin и Cos (10 точек) ---");
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, tabSin(x)=%.4f, tabCos(x)=%.4f\n", x, tabSin.getFunctionValue(x), tabCos.getFunctionValue(x));
        }

        // --- Задание 8, п.3: Сумма квадратов ---
        System.out.println("\n--- Задание 8.3: Сумма квадратов (sin^2 + cos^2) ---");
        Function sinSqr = Functions.power(tabSin, 2);
        Function cosSqr = Functions.power(tabCos, 2);
        Function sumSqr = Functions.sum(sinSqr, cosSqr);
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, sin^2+cos^2=%.4f\n", x, sumSqr.getFunctionValue(x));
        }

        // --- Задание 8, п.4: Тест write/read (символьный поток) ---
        System.out.println("\n--- Задание 8.4: Тест write/read (текстовый файл) ---");
        try {
            TabulatedFunction tabExp = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);
            
            // Запись в файл
            Writer out = new FileWriter("tabulated-exp.txt");
            TabulatedFunctions.writeTabulatedFunction(tabExp, out);
            out.close();
            
            // Чтение из файла
            Reader in = new FileReader("tabulated-exp.txt");
            TabulatedFunction readExp = TabulatedFunctions.readTabulatedFunction(in);
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и считанной из файла функции:");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.1f, Orig(x)=%.2f, Read(x)=%.2f\n", x, tabExp.getFunctionValue(x), readExp.getFunctionValue(x));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // --- Задание 8, п.5: Тест output/input (байтовый поток) ---
        System.out.println("\n--- Задание 8.5: Тест output/input (бинарный файл) ---");
        try {
            TabulatedFunction tabLog = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 10);
            
            // Запись в файл
            OutputStream out = new FileOutputStream("tabulated-log.dat");
            TabulatedFunctions.outputTabulatedFunction(tabLog, out);
            out.close();
            
            // Чтение из файла
            InputStream in = new FileInputStream("tabulated-log.dat");
            TabulatedFunction readLog = TabulatedFunctions.inputTabulatedFunction(in);
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и считанной из файла функции:");
            for (double x = 1; x <= 10; x += 1) {
                System.out.printf("x=%.1f, Orig(x)=%.2f, Read(x)=%.2f\n", x, tabLog.getFunctionValue(x), readLog.getFunctionValue(x));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // --- Задание 9: Тест Сериализации (Serializable) ---
        System.out.println("\n--- Задание 9: Тест Сериализации (Serializable, ArrayTabulatedFunction) ---");
        try {
            // log(exp(x)) = x
            Function f = Functions.composition(new Log(Math.E), new Exp());
            // Используем ArrayTabulatedFunction для теста Serializable
            TabulatedFunction tabFunc = TabulatedFunctions.tabulate(f, 0, 10, 11); 
            
            // Сериализация (запись объекта)
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("serialized-array.ser"));
            out.writeObject(tabFunc);
            out.close();

            // Десериализация (чтение объекта)
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("serialized-array.ser"));
            TabulatedFunction readFunc = (TabulatedFunction) in.readObject();
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и десериализованной функции (Array):");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.1f, Orig(x)=%.2f, Read(x)=%.2f\n", x, tabFunc.getFunctionValue(x), readFunc.getFunctionValue(x));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // --- ДОБАВЛЕННЫЙ ТЕСТ: Задание 9 (Externalizable) ---
        System.out.println("\n--- Задание 9: Тест Сериализации (Externalizable, LinkedListTabulatedFunction) ---");
        try {
            // Создаем функцию f(x) = x^2
            Function sqr = new Power(new Function() {
                public double getLeftDomainBorder() { return Double.NEGATIVE_INFINITY; }
                public double getRightDomainBorder() { return Double.POSITIVE_INFINITY; }
                public double getFunctionValue(double x) { return x; }
            }, 2);
            
            // Создаем табулированную функцию на основе LinkedListTabulatedFunction
            double[] values = {0, 1, 4, 9, 16};
            TabulatedFunction tabFuncLinked = new LinkedListTabulatedFunction(0, 4, values);
            
            System.out.println("Исходная функция (LinkedList):");
            for (int i = 0; i < tabFuncLinked.getPointsCount(); i++) {
                 System.out.printf("Point %d: (%.1f, %.1f)\n", i, tabFuncLinked.getPointX(i), tabFuncLinked.getPointY(i));
            }

            // Сериализация (запись объекта)
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("serialized-linkedlist.ser"));
            out.writeObject(tabFuncLinked);
            out.close();

            // Десериализация (чтение объекта)
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("serialized-linkedlist.ser"));
            TabulatedFunction readFuncLinked = (TabulatedFunction) in.readObject();
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и десериализованной функции (LinkedList):");
            for (double x = 0; x <= 4; x += 0.5) {
                System.out.printf("x=%.1f, Orig(x)=%.2f, Read(x)=%.2f\n", x, tabFuncLinked.getFunctionValue(x), readFuncLinked.getFunctionValue(x));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
