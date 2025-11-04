package functions.meta;
import functions.Function;

public class Sum implements Function {
    private Function f1, f2;
    private double left, right;
    public Sum(Function f1, Function f2) {
        this.f1 = f1; this.f2 = f2;
        this.left = Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
        this.right = Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }
    public double getLeftDomainBorder() { return left; }
    public double getRightDomainBorder() { return right; }
    public double getFunctionValue(double x) { return f1.getFunctionValue(x) + f2.getFunctionValue(x); }
}
