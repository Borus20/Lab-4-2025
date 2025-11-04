package functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

// 1. Реализуем TabulatedFunction и Externalizable
public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {

    // ВНУТРЕННИЙ КЛАСС ДЛЯ УЗЛА СПИСКА
    private class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;
    }

    private FunctionNode head; // Голова списка, не хранит данные
    private int count; // Храним количество точек

    // 2. serialVersionUID для Externalizable
    private static final long serialVersionUID = 1L;

    // 3. ПУСТОЙ КОНСТРУКТОР (обязателен для Externalizable)
    public LinkedListTabulatedFunction() {
        this.count = 0;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;
    }

    // --- Конструктор из ЛР 2/3 ---
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Invalid arguments for function creation");
        }
        this.count = pointsCount;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            // Cразу добавляем в хвост, count увеличится в addNodeToTail
            addNodeToTail().point = new FunctionPoint(leftX + i * step, 0);
        }
        // Корректируем count, так как addNodeToTail его не меняет
        this.count = pointsCount; 
    }

    // --- Конструктор из ЛР 2/3 ---
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX || values.length < 2) {
            throw new IllegalArgumentException("Invalid arguments for function creation");
        }
        this.count = values.length;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addNodeToTail().point = new FunctionPoint(leftX + i * step, values[i]);
        }
    }

    // Новый конструктор (Задание 1 из ЛР 4)
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Function must have at least 2 points");
        }
        // Инициализация головы списка
        this.count = 0;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;

        for (int i = 0; i < points.length; ++i) {
            // Проверка на упорядоченность при добавлении
            if (i > 0 && points[i-1].getX() >= points[i].getX()) {
                 throw new IllegalArgumentException("Points are not sorted by X");
            }
            // Глубокое копирование и добавление в хвост
            addNodeToTail().point = new FunctionPoint(points[i]);
            this.count++;
        }
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ СО СПИСКОМ (внутренняя логика)
    private FunctionNode getNodeByIndex(int index) {
        // Оптимизация: если index > count/2, идем с хвоста
        if (index > count / 2) {
            FunctionNode current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        } else {
            FunctionNode current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        }
    }

    // Добавляет узел в конец списка (перед головой)
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();
        newNode.prev = head.prev;
        newNode.next = head;
        head.prev.next = newNode;
        head.prev = newNode;
        return newNode;
    }

    // Добавляет узел ПЕРЕД узлом с заданным индексом
    private FunctionNode addNodeByIndex(int index) {
        // Если index == count, вставляем в хвост (перед head)
        FunctionNode nextNode = (index == count) ? head : getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode();
        newNode.prev = nextNode.prev;
        newNode.next = nextNode;
        nextNode.prev.next = newNode;
        nextNode.prev = newNode;
        count++;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        FunctionNode toDelete = getNodeByIndex(index);
        toDelete.prev.next = toDelete.next;
        toDelete.next.prev = toDelete.prev;
        toDelete.prev = null;
        toDelete.next = null;
        count--;
        return toDelete;
    }

    // --- РЕАЛИЗАЦИЯ МЕТОДОВ ИНТЕРФЕЙСА TabulatedFunction ---
    
    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        FunctionNode current = head.next;
        // Идем до 'head', а не 'count', т.к. список кольцевой
        while(current != head) {
            // Точное попадание в узел
            if (current.point.getX() == x) {
                return current.point.getY();
            }
            // Попадание между узлами (проверяем, что current.next - не голова)
            if (current.next != head && current.point.getX() < x && x < current.next.point.getX()) {
                double x1 = current.point.getX();
                double y1 = current.point.getY();
                double x2 = current.next.point.getX();
                double y2 = current.next.point.getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.next;
        }
        // Проверка на точное попадание в последнюю точку
        if (x == getRightDomainBorder()) {
            return head.prev.point.getY();
        }
        return Double.NaN;
    }
    
    public int getPointsCount() {
        return count;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException("Index " + index + " is out of bounds for count " + count);
        }
    }
    
    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(getNodeByIndex(index).point);
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();
        FunctionNode node = getNodeByIndex(index);
        double leftBound = (node.prev == head) ? Double.NEGATIVE_INFINITY : node.prev.point.getX();
        double rightBound = (node.next == head) ? Double.POSITIVE_INFINITY : node.next.point.getX();
        if (newX <= leftBound || newX >= rightBound) {
            throw new InappropriateFunctionPointException("X coordinate is out of the allowed interval");
        }
        node.point = new FunctionPoint(point);
    }
    
    public double getPointX(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getX();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        FunctionNode node = getNodeByIndex(index);
        double leftBound = (node.prev == head) ? Double.NEGATIVE_INFINITY : node.prev.point.getX();
        double rightBound = (node.next == head) ? Double.POSITIVE_INFINITY : node.next.point.getX();
        if (x <= leftBound || x >= rightBound) {
            throw new InappropriateFunctionPointException("X coordinate is out of the allowed interval");
        }
        node.point.setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getY();
    }
    
    public void setPointY(int index, double y) {
        checkIndex(index);
        getNodeByIndex(index).point.setY(y);
    }

    public void deletePoint(int index) {
        checkIndex(index);
        if (count < 3) {
            throw new IllegalStateException("Cannot delete point, function must have at least 2 points");
        }
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.next;
        int index = 0;
        // Ищем место для вставки
        while (current != head && current.point.getX() < point.getX()) {
            current = current.next;
            index++;
        }
        // Проверка на дубликат
        if (current != head && current.point.getX() == point.getX()) {
            throw new InappropriateFunctionPointException("Point with such X already exists");
        }
        // Вставляем узел перед 'current' (т.е. по 'index')
        addNodeByIndex(index).point = new FunctionPoint(point);
    }

    // --- МЕТОДЫ ИНТЕРФЕЙСА Externalizable (Задание 9) ---
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(count);
        FunctionNode current = head.next;
        // Записываем каждую точку
        while(current != head) {
            out.writeObject(current.point);
            current = current.next;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int readCount = in.readInt();
        // Убедимся, что список пуст перед чтением
        this.head.prev = head;
        this.head.next = head;
        this.count = 0;
        
        for (int i = 0; i < readCount; i++) {
            FunctionPoint point = (FunctionPoint) in.readObject();
            addNodeToTail().point = point; // Просто добавляем в хвост
            this.count++; // Увеличиваем count здесь
        }
    }
}
