import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    public static int total;
    public static int maxQue;// 顾客能够容忍的队列最大长度，超过了就会离开
    public static double maxTime = 480000;
    private static ArrayList<Customer> customers = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入[平均到达时间]： ");
        Customer.lambda = (double) 1 / scanner.nextDouble();
        System.out.println("请输入[平均服务时间]： ");
        Server.lambda = (double) 1 / scanner.nextDouble();
        System.out.println("请输入[顾客数目]： ");
        total = scanner.nextInt();
        System.out.println("请输入[队列最大长度]： ");
        maxQue = scanner.nextInt();
        System.out.println("\n\n---------------------------------------事件流数据---------------------------------------");
        double curTime = 0.0;
        Queue<Customer> queue = new LinkedList<>();
        int lost = 0;
        Server server = new Server();
        for (int i = 1; i <= total; i++) {
            Customer curCustomer = new Customer(curTime);
            customers.add(curCustomer);
            System.out.printf("[%.2f]", curCustomer.getArriveTime());
            System.out.println(": 顾客 " + curCustomer.getId() + " 到达了");
            curTime = curCustomer.getArriveTime();
            if (curTime > maxTime) {
                break;
            }
            //可能这个顾客来的比较晚，在其来之前已经服务了多名队列中的顾客，因此loop
            while (true) {
                double dt = server.update(curTime);//服务完了则为服务时间，否则为0
                Statisticer.sumQueS += dt * queue.size();//若服务完了，则累加到队列面积上
                if (server.isBusy() || queue.isEmpty()) {
                    break;
                }
                double nextStartTime = server.getFreeTime();
                server.serve(queue.poll(), nextStartTime);
            }
            //说明服务器忙，需要将新来的顾客加入队列
            if (server.isBusy()) {
                //说明队列长度过大，顾客无法忍受，溜了
                if (queue.size() >= maxQue) {
                    System.out.printf("[%.2f]", curTime);
                    System.out.println(": 队列人数已满, 顾客 " + curCustomer.getId() + " 离开了");
                    lost++;
                }
                //队列长度在顾客的容忍范围之内，顾客进入队列
                else {
                    queue.add(curCustomer);
                    System.out.printf("[%.2f]", curTime);
                    System.out.println(": 顾客 " + curCustomer.getId() + " 进入了队列");
                }
            }
            //说明队列为空，可以直接服务这个新来的顾客
            else {
                server.serve(curCustomer, curTime);
            }
        }
        //此时不会再有新顾客进来了，因此进入循环将队列中将现有的顾客全部服务完
        while (!queue.isEmpty() || server.isBusy()) {
            double v = server.update(Double.POSITIVE_INFINITY);//无限等当前顾客服务完
            Statisticer.sumQueS += v * queue.size();
            curTime = server.getFreeTime();
            if (!queue.isEmpty()) {
                server.serve(queue.poll(), server.getFreeTime());
            }
        }

        System.out.println("\n\n---------------------------------------顾客数据---------------------------------------");
        for (Customer cus : customers) {
            cus.printInfo();
        }

        System.out.println("\n\n---------------------------------------统计数据---------------------------------------");
        System.out.print("平均逗留时间： ");
        System.out.printf("%.2f\n", Statisticer.sumExcuTime / Statisticer.cntCustomer);

        System.out.print("平均等待时间： ");
        System.out.printf("%.2f\n", Statisticer.sumWaitTime / Statisticer.cntCustomer);

        System.out.print("队列中平均等待客户数： ");
        System.out.printf("%.2f\n", Statisticer.sumQueS / curTime);//输出队列的平均长度

        System.out.print("服务器利用率： ");
        System.out.printf("%.2f", Statisticer.sumSerS / curTime * 100.0);//输出服务器的利用率
        System.out.println("%");

        System.out.print("顾客流失率： ");
        System.out.printf("%.2f", (double)lost / (double)total * 100.0);//输出服务器的利用率
        System.out.println("%");
    }
}
