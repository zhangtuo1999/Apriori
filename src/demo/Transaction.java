package demo;

import java.util.Arrays;

public class Transaction {
    private int tid;
    private String[] items;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "tid=" + tid +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
