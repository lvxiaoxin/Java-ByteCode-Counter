class outer {
    int a = 10;
    public void oper() {
        for(int i=0; i<1000; ++i) {
            a++;
        }
    }
}
public class Test {
    public static void main(String[] args) {
        outer o = new outer();
        for(int i=0; i<1; ++i) {
            o.oper();
        }
    }
}

