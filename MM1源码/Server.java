public class Server implements Comparable<Server> {
    public static double lambda;
    private static int cntId = 0;
    private int id;
    private boolean busy = false;
    private Customer curCustomer;
    private double freeTime = 0.0;

    private double generateTime() {
        //return -Math.log(random.nextDouble()) * mu; // - ln(rand()) / lambda
        double u = Math.random();
        return (- Math.log(u) / lambda);
    }

    //开始服务
    public void serve(Customer customer, double curTime) {
        customer.setServeBeginTime(curTime);//设置顾客的服务开始时间
        Statisticer.sumWaitTime += curTime - customer.getArriveTime();
        busy = true;//设置服务器为忙
        curCustomer = customer;//设置当前顾客
        freeTime = curTime + generateTime();//设置当前顾客服务结束时间
        customer.setFinishTime(freeTime);
        System.out.printf("[%.2f]", curTime);//输出
        System.out.println(": 窗口 " + id + " 开始了 对顾客 " + curCustomer.getId() + " 的服务");
    }

    //更新服务器的状态，判断是否服务完上一个顾客
    public double update(double curTime) {
        double ret = 0;
        //若为真，则说明此时已经服务完上一个
        if (busy && freeTime < curTime) {
            busy = false;//将服务器状态设置为空闲
            double dtime = freeTime - curCustomer.getServeBeginTime();// 服务上一个顾客用时
            Statisticer.sumExcuTime += freeTime - curCustomer.getArriveTime();//顾客的总执行时间累加
            Statisticer.sumSerS += dtime;//服务器的总服务时间累加
            ret = dtime;
            Statisticer.cntCustomer++;//服务完的顾客+1
            System.out.printf("[%.2f]", freeTime);
            System.out.println(": 窗口 " + id + " 结束了 对顾客 " + curCustomer.getId() + " 的服务");
            curCustomer = null;
        }
        //若当前没有服务完上一个顾客，则返回0；若已经服务完了，则返回该顾客的服务时间
        return ret;
    }

    public Server() {
        cntId++;
        id = cntId;
    }

    @Override
    public int compareTo(Server o) {
        if (!this.busy && !o.busy) {
            return Integer.compare(this.id, o.id);
        } else if (!this.busy && o.busy) {
            return -1;
        } else if (this.busy && !o.busy) {
            return 1;
        } else {
            int k = Double.compare(this.freeTime, o.freeTime);
            return k != 0 ? k : Integer.compare(this.id, o.id);
        }
    }

    public boolean isBusy() {
        return busy;
    }

    public double getFreeTime() {
        return freeTime;
    }

}
