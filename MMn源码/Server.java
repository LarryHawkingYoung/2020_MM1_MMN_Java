import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Server implements Comparable<Server> {
    public static double lambda;
    private static int cntId = 0;
    private int id;
    private boolean busy = false;
    private Customer curCustomer;
    private double freeTime = 0.0;
    private double totalServeTime = 0.0;
    private Queue<Customer> queue = new LinkedList<>();
    private ArrayList<String> outputList = new ArrayList<>();
    private ArrayList<Integer> customRecord = new ArrayList<>();
    private double queueArea = 0.0;

    private double generateTime() {
        //return -Math.log(random.nextDouble()) * mu; // - ln(rand()) / lambda
        double u = Math.random();
        return (- Math.log(u) / lambda);
    }

    //开始服务
    public void serve(Customer customer, double curTime) {
        customRecord.add(customer.getId());
        customer.setServeBeginTime(curTime);//设置顾客的服务开始时间
        Statisticer.sumWaitTime += curTime - customer.getArriveTime();
        busy = true;//设置服务器为忙
        curCustomer = customer;//设置当前顾客
        freeTime = curTime + generateTime();//设置当前顾客服务结束时间
        customer.setFinishTime(freeTime);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%.2f]", curTime));
        sb.append(": 窗口 " + id + " 开始了 对顾客 " + curCustomer.getId() + " 的服务");
        outputList.add(sb.toString());
    }

    //更新服务器的状态，判断是否服务完上一个顾客
    public double update(double curTime) {
        double ret = 0;
        //若为真，则说明此时已经服务完上一个
        if (busy && freeTime < curTime) {
            busy = false;//将服务器状态设置为空闲
            double dtime = freeTime - curCustomer.getServeBeginTime();// 服务上一个顾客用时
            Statisticer.sumExcuTime += freeTime - curCustomer.getArriveTime();//顾客的总执行时间累加
            Statisticer.sumSerS += dtime;//所有服务器的总服务时间累加
            totalServeTime += dtime;//本服务器的总服务时间累加
            ret = dtime;
            Statisticer.cntCustomer++;//服务完的顾客+1
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%.2f]", freeTime));
            sb.append(": 窗口 " + id + " 结束了 对顾客 " + curCustomer.getId() + " 的服务");
            outputList.add(sb.toString());
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
        if (queue.size() != o.getQueueLen())
            return Integer.compare(queue.size(), o.getQueueLen());
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

    public int getId() {
        return id;
    }

    public boolean isBusy() {
        return busy;
    }

    public double getFreeTime() {
        return freeTime;
    }

    public int getQueueLen() {
        return this.queue.size();
    }

    public void serveFirst(double nextStartTime) {
        this.serve(queue.poll(), nextStartTime);
    }

    public void insertQueue(Customer curCustomer) {
        queue.add(curCustomer);
    }

    public void addOutput(String output) {
        outputList.add(output);
    }

    public void printOutput() {
        for (String output: outputList) {
            System.out.println(output);
        }
    }

    public void printCustomRecord(double totalTime) {
        System.out.println("[服务器" + id + "] " + "利用率: " + String.format("%.2f", (double)(totalServeTime / totalTime * 100.0)) + "%, 队列平均长度: " + String.format("%.2f", getAvgLen(totalTime)) + ", 共服务了 " + customRecord.size() + " 个顾客，分别是：");
        for (int id : customRecord) {
            System.out.print(id + " ");
        }
        System.out.println();
        System.out.println();
    }

    public double getTotalServeTime() {
        return totalServeTime;
    }

    public void addQueueArea(double dt) {
        queueArea += queue.size() * dt;
    }

    public double getAvgLen(double totalTime) {
        return queueArea / totalTime;
    }
}
