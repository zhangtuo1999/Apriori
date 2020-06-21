package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Apriori {

    //储存事务的数据库 List<Transaction>形式
    private static List<Transaction> dataList = new ArrayList<>();

    //储存事务的数据库  List<HashSet<String>>形式
    private static List<HashSet<String>> datas = new ArrayList<>();

    //正在计算n-项集
    private static int index_n = 2;

    //频繁项集 生成强规则时用，不生成强规则不使用
    private static HashMap<List<String>, Integer> map1;
    private static HashMap<List<String>, Integer> map2;
    private static HashMap<List<String>, Integer> map3;
    private static HashMap<List<String>, Integer> map4;
    private static HashMap<List<String>, Integer> map5;

    //支持度
    private static double sup_percent;  //百分比，小数
    private static int sup; //百分比乘以事务数量

    //置信度
    private static double con;


    public static void main(String[] args) throws Exception {
        System.out.println("开始执行");
        System.out.println();
        long startTime = System.currentTimeMillis();    //获取开始时间

        dataManage();   //数据处理

        /*
            支持度             运行时间
            0.005             163 s
            0.010             14 s
            0.015             4 s
            0.020             2 s
         */

        sup_percent = 0.01;    //设置阈值
        con = 0.5;

        sup = (int) Math.ceil(sup_percent * dataList.size());

//        start();      //带强规则
        begin();        //不带强规则

        long endTime = System.currentTimeMillis();  //获取结束时间
        System.out.println("执行完毕");
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }

    /**
     * 循环执行，但是不能储存每一步的hashmap 不能生成强规则
     */
    private static void begin() {
        //获取频繁一项集
        System.out.println("正在获取频繁1-项集");
        HashMap map = getOneItemset();  //获取频繁 1-项集
        System.out.println("候选的频繁1-项集剪枝完毕，得到频繁1-项集 " + map.size() + " 个：");
        System.out.println(map + "\n");

        //获取频繁n项集
        HashMap temp = null;
        while (map.size() != 0) {   //获取频繁 n-项集
            temp = map;     //缓存map 避免最后map为空找不到最大频繁项集
            System.out.println("正在获取频繁" + index_n + "-项集");
            map = getItemset(map);
            System.out.println("候选的频繁" + index_n + "-项集剪枝完毕，得到频繁" + index_n + "-项集 " + map.size() + " 个：");
            System.out.println(map + "\n");
            index_n++;
        }
        System.out.println("综上所述，最大频繁项集为" + (index_n - 2) + "-项集 " + temp.size() + " 个： \n" + temp);
    }


    /**
     * 根据数据 固定执行4步 只支持最大4-项集 可以生成固定最大4-项集的强规则
     */
    private static void start() {
        System.out.println("正在获取频繁1-项集");
        map1= getOneItemset();
        System.out.println("候选的频繁1-项集剪枝完毕，得到频繁1-项集 " + map1.size() + " 个：");
        System.out.println(map1 + "\n");

        System.out.println("正在获取频繁2-项集");
        index_n=2;
        map2= getItemset(map1);
        System.out.println("候选的频繁2-项集剪枝完毕，得到频繁1-项集 " + map2.size() + " 个：");
        System.out.println(map2 + "\n");

        System.out.println("正在获取频繁3-项集");
        index_n=3;
        map3= getItemset(map2);
        System.out.println("候选的频繁3-项集剪枝完毕，得到频繁3-项集 " + map3.size() + " 个：");
        System.out.println(map3 + "\n");

        System.out.println("正在获取频繁4-项集");
        index_n=4;
        map4= getItemset(map3);
        System.out.println("候选的频繁4-项集剪枝完毕，得到频繁4-项集 " + map4.size() + " 个：");
        System.out.println(map4 + "\n");

        System.out.println("正在获取强规则");
        getStrongRule(map4);
    }


    /**
     * 生成强规则
     *
     * @param map4
     */
    private static void getStrongRule(HashMap<List<String>, Integer> map4) {
        List<String> l = null;      //新的子集l
        double confidence = 0.0;    //计算l置信度
        int sup=0;                  //计算l的支持度

        for (List<String> list : map4.keySet()) {   //每一行最大频繁项集
            int sup_list = map4.get(list);          //最大频繁项集的支持度

            //1
            for (int i = 0; i < list.size(); i++) {
                l = new ArrayList<>();
                l.add(list.get(i));                 //只有 1 个元素的子集

                sup=map1.get(l);                    //拿到这个子集的支持度
                confidence = 1.0 * sup_list / sup;  //计算置信度
                if (confidence >= con) {            //满足阈值 ——> 强规则
                    System.out.println(l + "-->" + list);
                }
            }

            //2
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    l = new ArrayList<>();
                    l.add(list.get(i));
                    l.add(list.get(j));             //只有 2 个元素的子集

                    sup=map2.get(l);
                    confidence = 1.0 * sup_list / sup;
                    if (confidence >= con) {
                        System.out.println(l + "-->" + list);
                    }
                }
            }

            //n-1
            for (int i = 0; i < list.size(); i++) {
                l=new ArrayList<>();
                for(String s:list){
                    l.add(s);
                }

                l.remove(i);
                sup=map3.get(l);

                confidence = 1.0 * sup_list / sup;
                if (confidence >= con) {
                    System.out.println(l + "-->" + list);
                }
            }

            //n-2
//            for (int i = 0; i < list.size()-1; i++) {
//                for (int j=0;j< list.size();j++){
//                    l=new ArrayList<>();
//                    for(String s:list){
//                        l.add(s);
//                    }
//
//                    l.remove(i);
//                    sup=map3.get(l);
//
//                    confidence = 1.0 * sup_list / sup;
//                    if (confidence >= con) {
//                        System.out.println(l + "-->" + list);
//                    }
//                }
//            }

        }
    }


    /**
     * 从n-1项集获取频繁n项集 加上带支持度后的hashmap
     * 处理结果以HashMap<List<String>, Integer>返回
     * 处理方法：自连接、剪枝
     *
     * @param map
     * @return n项集
     */
    private static HashMap<List<String>, Integer> getItemset(HashMap<List<String>, Integer> map) {

        HashMap<List<String>, Integer> resMap = new HashMap<>();    //待返回的结果

        List<List<String>> arr = new ArrayList<>(); //二维数组
        int n = 0;  //项集的长度

        //把hashmap中的key取出 转化成二维数组  行数为arr.size  列数为n
        for (List<String> list : map.keySet()) {
            arr.add(list);
            n = list.size(); //n-项集
        }

        //自连接 生成返回的hashmap中的itemset
        List<String> resList = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            for (int j = i + 1; j < arr.size(); j++) {
                resList = getResList(arr, i, j); //只有两个n-1项集有且只有一个不同元素，才会生成n项集
                if (resList != null) {  //只有符合上述条件才生成支持度
                    int support = getResSupport(resList);   //拿到支持度，并插入到待返回的结果
                    resMap.put(resList, support);
                }
            }
        }
        System.out.println("候选的频繁" + index_n + "-项集有 " + resMap.size() + " 个");

        //剪枝后返回n-项集
        return prune(resMap);
    }


    /**
     * 获取频繁项集的支持度
     *
     * @param resList
     * @return
     */
    private static int getResSupport(List<String> resList) {

        boolean flag;   //
        int support = 0;

        for (HashSet s : datas) {    //s是每一行数据，即事务
            flag = true;
            for (String str : resList) {    //str是n项集中每一个项
                if (!s.contains(str)) {
                    flag = false;   //只要在数据库的每一行事务没有n项集中某一个项，那么支持度就不会增加
                    break;
                }
            }
            if (flag) { //只有事务中完全包括了n项集中的每一个项，支持度才会+1
                support++;
            }
        }

        return support;
    }


    /**
     * 从n-1项集 获取 候选n项集  单独一行 不带支持度
     * 当两个n-1项集只有一个不同元素时 ，才会生成n项集
     *
     * @param arr
     * @param i
     * @param j
     * @return
     */
    private static List<String> getResList(List<List<String>> arr, int i, int j) {

        int flag = 0;   //记录两个n-1项集有几个共同元素

        List<String> l1 = arr.get(i);
        List<String> l2 = arr.get(j);

        List<String> resList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (String s1 : l1) {
            set.add(s1);
        }
        for (String s2 : l2) {
            if (set.contains(s2)) {
                flag++;
            } else {
                set.add(s2);
            }
        }

        for (String s : set) {
            resList.add(s);
        }

        if (flag == l1.size() - 1 && set.size() == l1.size() + 1){
            Collections.sort(resList,new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if(o1 == null || o2 == null){
                        return -1;
                    }
                    if(o1.length() > o2.length()){
                        return 1;
                    }
                    if(o1.length() < o2.length()){
                        return -1;
                    }
                    if(o1.compareTo(o2) > 0){
                        return 1;
                    }
                    if(o1.compareTo(o2) < 0){
                        return -1;
                    }
                    if(o1.compareTo(o2) == 0){
                        return 0;
                    }
                    return 0;
                }
            });
            return resList;
        }

        return null;

    }


    /**
     * 剪枝
     *
     * @param map
     * @return
     */
    private static HashMap<List<String>, Integer> prune(HashMap<List<String>, Integer> map) {

        for (Iterator<Map.Entry<List<String>, Integer>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<List<String>, Integer> item = it.next();
            if (item.getValue() < sup) {    //达不到支持度的去掉
                it.remove();
            }
        }
        return map;
    }


    /**
     * 创建候选1-项集
     */
    private static HashMap getOneItemset() {

        //候选 1项集，key是候选一项集List；value是对应的支持度
        HashMap<List<String>, Integer> map = new HashMap<>();

        for (Transaction temp : dataList) {         //遍历数据库中的事务
            for (String str : temp.getItems()) {    //遍历事务中的项

                List<String> list = new ArrayList<>();
                list.add(str);

                if (!map.containsKey(list)) {       //如果map中没有key 说明key第一次出现
                    map.put(list, 1);               //就把key加进去，value值设置为1
                } else {                            //否则支持度加一
                    map.put(list, map.get(list) + 1);
                }
            }
        }

        System.out.println("候选的频繁1-项集有 " + map.size() + " 个");
        return prune(map);
    }


    /**
     * 把文件中的内容加载进数据库
     *
     * @throws Exception
     */
    public static void dataManage() throws Exception {

        File file = new File("E:\\课程\\数据挖掘\\算法\\DataMining\\data\\retail.dat");

        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);

        Transaction t = null;     //dataList中的每个事物，即文件中每一行的信息
        String lineData = null;   //文件中每一行信息
        int lineNum = 0;          //文件的行号

        while ((lineData = bufferedReader.readLine()) != null) {
            lineNum++;  //行号自增1

            t = new Transaction();    //数据转换成事务
            t.setTid(lineNum);
            t.setItems(lineData.split(" "));
            dataList.add(t);        //把事务加到数据库中
        }

        datas = new ArrayList<>();  //数据库
        HashSet<String> set = null; //事务

        //把数据库存入list
        for (Transaction tr : dataList) {
            set = new HashSet<>();
            for (String s : tr.getItems()) {
                set.add(s);
            }
            datas.add(set);
        }

    }


    public static double getSup_percent() {
        return sup_percent;
    }

    public static void setSup_percent(double sup_percent) {
        Apriori.sup_percent = sup_percent;
    }

    public static int getSup() {
        return sup;
    }

    public static void setSup(int sup) {
        Apriori.sup = sup;
    }
}