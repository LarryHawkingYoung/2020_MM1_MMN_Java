public class Customer {
    public static double lambda;
    private static int cntId = 0;
    private int id;
    private double arriveTime;
    private double serveBeginTime;
    private double finishTime;

    public Customer(double lastTime) {
        cntId++;
        id = cntId;
        arriveTime = lastTime + generateTime();
    }

    public void setServeBeginTime(double serveBeginTime) {
        this.serveBeginTime = serveBeginTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    private double generateTime() {
        double u = Math.random();
        return (- Math.log(u) / lambda);
    }

    public int getId() {
        return id;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public double getServeBeginTime() {
        return serveBeginTime;
    }

    public void printInfo() {
        System.out.print("ID: " + id + "\t");
        System.out.printf("到达时间: %.2f\t", arriveTime);
        if (serveBeginTime < arriveTime) {
            System.out.println("嫌队伍太长无法忍受，故跑路了~");
            return;
        }
        System.out.printf("开始服务时间: %.2f\t", serveBeginTime);
        System.out.printf("结束服务时间: %.2f\t", finishTime);
        System.out.printf("排队等待时间: %.2f\t", serveBeginTime - arriveTime);
        System.out.printf("逗留时间: %.2f\t", finishTime - arriveTime);
        System.out.printf("净服务时间: %.2f\n", finishTime - serveBeginTime);
    }
}
