package functions.meta;
import functions.Function;

public class Power implements Function {
    private Function f;
    private double power;

    public Power(Function f, double power) {
        this.f = f;
        this.power = power;
    }

    public double getLeftDomainBorder() { return f.getLeftDomainBorder(); }
    public double getRightDomainBorder() { return f.getRightDomainBorder(); }
    public double getFunctionValue(double x) {
        return Math.pow(f.getFunctionValue(x), power);
    }
}
