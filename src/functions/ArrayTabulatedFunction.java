package functions;

// Класс реализует интерфейс TabulatedFunction, который теперь наследует Function и Serializable
public class ArrayTabulatedFunction implements TabulatedFunction {
    private FunctionPoint[] points;
    private static final long serialVersionUID = 1L; // Для Задания 9

    // Конструктор ЛР 2
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Invalid arguments for function creation: leftX >= rightX or pointsCount < 2");
        }
        this.points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; ++i) {
            points[i] = new FunctionPoint(leftX + i * step, 0);
        }
    }

    // Конструктор ЛР 2
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        int count = values.length;
        if (leftX >= rightX || count < 2) {
            throw new IllegalArgumentException("Invalid arguments for function creation: leftX >= rightX or pointsCount < 2");
        }
        this.points = new FunctionPoint[count];
        double step = (rightX - leftX) / (count - 1);
        for (int i = 0; i < count; ++i) {
            points[i] = new FunctionPoint(leftX + i * step, values[i]);
        }
    }

    // Новый конструктор ЛР 4 (Задание 1)
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Function must have at least 2 points");
        }
        // Проверка на упорядоченность
        for (int i = 0; i < points.length - 1; ++i) {
            // Используем машинный эпсилон для сравнения вещественных чисел (x[i] >= x[i+1])
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Points are not strictly sorted by X");
            }
        }
        // Глубокое копирование для инкапсуляции
        this.points = new FunctionPoint[points.length];
        for (int i = 0; i < points.length; ++i) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[points.length - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        for (int i = 0; i < points.length - 1; ++i) {
            if (points[i].getX() <= x && x <= points[i + 1].getX()) {
                double x1 = points[i].getX();
                double y1 = points[i].getY();
                double x2 = points[i + 1].getX();
                double y2 = points[i + 1].getY();
                
                // Линейная интерполяция
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    public int getPointsCount() {
        return points.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= points.length) {
            throw new FunctionPointIndexOutOfBoundsException("Index " + index + " is out of bounds [0, " + (points.length - 1) + "]");
        }
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]); // Возвращаем копию!
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();
        double leftBound = (index > 0) ? points[index - 1].getX() : Double.NEGATIVE_INFINITY;
        double rightBound = (index < points.length - 1) ? points[index + 1].getX() : Double.POSITIVE_INFINITY;

        if (newX <= leftBound || newX >= rightBound) {
            throw new InappropriateFunctionPointException("New point's X coordinate is out of the allowed interval (" + leftBound + ", " + rightBound + ")");
        }
        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        double leftBound = (index > 0) ? points[index - 1].getX() : Double.NEGATIVE_INFINITY;
        double rightBound = (index < points.length - 1) ? points[index + 1].getX() : Double.POSITIVE_INFINITY;
        
        if (x <= leftBound || x >= rightBound) {
            throw new InappropriateFunctionPointException("New X coordinate is out of the allowed interval (" + leftBound + ", " + rightBound + ")");
        }
        points[index].setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }
    
    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        checkIndex(index);
        if (points.length < 3) {
            throw new IllegalStateException("Cannot delete point: function must have at least 2 points remaining.");
        }
        FunctionPoint[] newPoints = new FunctionPoint[points.length - 1];
        System.arraycopy(points, 0, newPoints, 0, index);
        System.arraycopy(points, index + 1, newPoints, index, points.length - index - 1);
        points = newPoints;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        while (insertIndex < points.length && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }
        
        if (insertIndex < points.length && points[insertIndex].getX() == point.getX()) {
            throw new InappropriateFunctionPointException("Point with X=" + point.getX() + " already exists.");
        }
        
        FunctionPoint[] newPoints = new FunctionPoint[points.length + 1];
        System.arraycopy(points, 0, newPoints, 0, insertIndex);
        newPoints[insertIndex] = new FunctionPoint(point); // Глубокое копирование
        System.arraycopy(points, insertIndex, newPoints, insertIndex + 1, points.length - insertIndex);
        points = newPoints;
    }
}
