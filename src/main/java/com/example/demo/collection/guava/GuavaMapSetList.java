package com.example.demo.collection.guava;

import com.example.demo.collection.ObjectToMap.tea;
import com.google.common.base.Charsets;
import com.google.common.cache.*;
import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.*;
import com.google.common.reflect.*;
import com.google.common.util.concurrent.*;
import com.sun.istack.internal.Nullable;

import javax.swing.event.ChangeEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static com.google.common.hash.Hashing.md5;

/**
 * Created by lijun on 18-12-10.
 */


public class GuavaMapSetList {
    public static void main(String[] args) {
        //map();
        //mapInverse();
        //table();
        //mapClassTypeAndObject();
        //cacheObject();
        //cacheString();
        //MapSet();
        //manyThreads();
        //stream();
        //hashCodeAndBloomFilter();
        //eventBus();
        //reflect();


    }

    private static void reflect() {
        //类型工具TypeToken
        //获取原始类
        TypeToken<String> stringTok = TypeToken.of(String.class);
        TypeToken<Integer> intTok = TypeToken.of(Integer.class);
        //获取泛型类
        TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};
        //获取通配符类型
        TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};
        stringTok.getType();//获得类型
        stringListTok.getRawType();//获得运行时类型
        stringListTok.getTypes();//获取所有的超类和接口及自身类，可以使用classes()和interfaces()方法允许你只浏览超类和接口类
        stringListTok.isArray();//检查类是否为接口
        //通过已经给泛型赋值的类来解析原有类型使用这种泛型后其方法返回的应有类型
        TypeToken<Map<String, Integer>> mapToken = new TypeToken<Map<String, Integer>>() {};
        try {
            TypeToken<?> entrySetToken = mapToken.resolveType(Map.class.getMethod("entrySet").getReturnType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //将会返回TypeToken<Set<Map.Entry<String, Integer>>>
        //Guava提供Invokable封装Method和Constructor
        Invokable<List<String>, ?> invokable = null;
        try {
            invokable = new TypeToken<List<String>>(){}.method(List.class.getMethod("size"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        invokable.getReturnType(); // int
        invokable.isPublic();//判断方法是否是public
        invokable.isOverridable();//是否能被重写
        invokable.getParameters().get(0).isAnnotationPresent(Nullable.class);//方法的第一个参数是否被定义了注解@Nullable

        //包括提供了invoke方法放入实例和参数直接执行
        class MyInvocationHandler extends AbstractInvocationHandler {
            @Override
            protected Object handleInvocation(Object o, Method method, Object[] objects) throws Throwable {
                //加入前后处理方法
                return method.invoke(o,objects);
            }
        }
        //初始化类
        Reflection.initialize(tea.class);
        //提供动态代理
        tea tea = Reflection.newProxy(tea.class, new MyInvocationHandler());
        //ClassPath类路径扫描工具 注意：所以不要将它用于关键任务生产任务
        ClassPath classpath = null;//通过classLoader获取
        try {
            classpath = ClassPath.from(Map.class.getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过直接指定包路径来扫
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses("com.it.mypackage")) {
            classInfo.getName();//获得路径名
        }
    }

    private static void eventBus() {
        // 给予事件类中方法以@Subscribe注解
        class EventBusChangeRecorder {
            @Subscribe
            public void recordCustomerChange(ChangeEvent e) {
                //事件发生时做一些事
                System.out.println("触发事件");
            }
        }
        //在程序的某处创建事件总线并注册事件
        EventBus eventBus=new EventBus();
        eventBus.register(new EventBusChangeRecorder());
        // 在之后的程序中 提交发生的事件
        ChangeEvent event=new ChangeEvent(new EventBusChangeRecorder());
        eventBus.post(event);
        //需要异步执行可以使用EventBus的子类AsyncEventBus
    }

    private static void hashCodeAndBloomFilter() {
        //hash过滤器
        Funnel<tea> funnel = new Funnel<tea>() {

            @Override
            public void funnel(tea tea, PrimitiveSink primitiveSink) {
                primitiveSink
                        .putString(tea.getColor(), Charsets.UTF_8)
                        .putString(tea.getHomeland(),Charsets.UTF_8)
                        .putInt(tea.getWeiht());
            }
        };
        //使用md5算法计算hashcode
        HashFunction hf = md5();
        HashCode hashCode = hf.newHasher()
                //额外增加hash运算，可以用来放密码
                .putLong(123456789)
                .putString("数字", Charsets.UTF_8)
                .putObject(new tea(1, "张", "三"), funnel)
                .hash();
        System.out.println(hashCode);

        //BloomFilter布鲁姆过滤器
        BloomFilter<tea> friends = BloomFilter.create(funnel, 500, 0.01);
        tea dude=new tea(1,"aa","bb");

        //假设多人
        List<tea> friendsList= Lists.newArrayList(dude);
        for(tea friend : friendsList) {
            friends.put(friend);
        }

        // 很久以后

        if (friends.mightContain(dude)) {
            //dude不是朋友还运行到这里的概率为1%
            //在这儿，我们可以在做进一步精确检查的同时触发一些异步加载
            System.out.println("there still has tea object");
        }
    }

    private static void stream() {
        List<Integer> intTypeList= Lists.newArrayList();
        Random random=new Random();

        intTypeList.add( random.nextInt(50));
        List<Double> transform = Lists.transform(intTypeList, Integer::doubleValue);
        System.out.println(transform);
    }

    private static void manyThreads() {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        ListenableFuture<String> explosion = service.submit(new Callable() {
            public String call() {
                return "this guava callable";
            }
        });
        Futures.addCallback(explosion, new FutureCallback() {
            //调用成功后执行的方法
            @Override
            public void onSuccess(Object o) {
                System.out.println("success");
            }
            ////调用失败后执行的方法
            public void onFailure(Throwable thrown) {
                System.out.println("failure");
            }
        });
    }

    private static void MapSet() {
        Multiset<String> countMap = HashMultiset.create();
        countMap.add("aa",3);
        countMap.remove("aa", 1);
        Set<String> strings = countMap.elementSet();
        int aa = countMap.count("aa");
        Set<Multiset.Entry<String>> entries = countMap.entrySet();
        countMap.setCount("aa", 5);
        countMap.size();
        System.out.println(countMap);


        //Multimap系列接口集合 和Multiset很相似 可以理解成Multiset的map版本 对应的实现将上面的对应map改为Multimap
        //Multimap主要是为了一个键映射到多个值。换句话说，Multimap是把键映射到任意多个值的一般方式。
        //可以和传统的缓存Map<K, Collection<V>>进行对比
        Multimap<Integer, String> multimap = HashMultimap.create();
        //转换成Map<K, Collection<V>>格式 且对转换后的map做操作会影响原有的Multimap
        multimap.asMap();
        //添加键到单个值的映射
        multimap.put(1, "a");
        //添加多个值的映射
        multimap.putAll(2, Lists.newArrayList("a", "b", "c", "d"));
        //移除键到值的映射；如果有这样的键值并成功移除，返回true。
        multimap.remove(2, "b");
        //移除一个key所有的映射值
        multimap.removeAll(1);
        //替换原有的映射值集合
        multimap.replaceValues(2, Lists.newArrayList("a", "b", "c", "d"));
        System.out.println(multimap);
    }

    private static void cacheString() {
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                //设置缓存大小
                .maximumSize(1000)
                //设置到期时间
                .expireAfterWrite(10, TimeUnit.MINUTES)
                //设置缓存里的值两分钟刷新一次
                .refreshAfterWrite(2,TimeUnit.MINUTES)
                //开启缓存的统计功能
                .recordStats()
                //构建缓存
                .build(new CacheLoader<String, String>() {
                    //此处实现如果根据key找不到value需要去如何获取
                    @Override
                    public String load(String s) throws Exception {
                        return "fuck";
                    }

                    //如果批量加载有比反复调用load更优的方法则重写这个方法
                    @Override
                    public Map<String, String> loadAll(Iterable<? extends String> keys) throws Exception {
                        return super.loadAll(keys);
                    }
                });

        cache.put("aa", "init1");
        cache.put("bb", "init2");
        cache.put("cc", "init3");
        cache.put("dd", "init4");
        cache.put("ee", "init5");
        try {

            System.out.println(cache.get("aaa"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //除了在build的时候设置没有key的调用方法外我们还能在调用的时候手动写

        //缓存回收
        //除了不能超过大小和设定的时间自动回收外还可以调用方法手动回收
        cache.invalidate("aa");//个别清除
        cache.invalidateAll(Lists.newArrayList("cc","dd"));//批量清除
        cache.invalidateAll();//清除所有缓存项
        //清理的时机：在写操作时顺带做少量的维护工作，或者偶尔在读操作时做——如果写操作实在太少的话
        //如果想自己维护则可以调用Cache.cleanUp();
        cache.cleanUp();
        //另外有时候需要缓存中的数据做出变化重载一次,这个过程可以异步执行
        cache.refresh("aa");
        //还可以调用一下缓存的统计查看缓存的使用情况(需要在构建时开启)
        CacheStats cacheStats = cache.stats();
        System.out.println(cacheStats.hitRate());//缓存命中率
        System.out.println(cacheStats.averageLoadPenalty());//加载新值的平均时间，单位为纳秒
        cacheStats.evictionCount();//缓存项被回收的总数，不包括显式清除
    }
    private static void cacheObject() {
        LoadingCache<String, tea> cache = CacheBuilder.newBuilder()
                //设置缓存大小
                .maximumSize(1000)
                //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(8)
                //设置到期时间
                .expireAfterWrite(10, TimeUnit.MINUTES)
                //设置缓存里的值两分钟刷新一次
                .refreshAfterWrite(2,TimeUnit.MICROSECONDS)
                //开启缓存的统计功能
                .recordStats()
                //设置缓存容器的初始容量为10
                .initialCapacity(10)
                .removalListener(new RemovalListener<Object, Object>() {
                    @Override
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
                    }
                })
                //构建缓存
                .build(new CacheLoader<String, tea>() {
                    //此处实现如果根据key找不到value需要去如何获取
                    @Override
                    public tea load(String s) throws Exception {
                        tea tea = new tea();
                        tea.setWeiht(18);
                        tea.setHomeland("china");
                        tea.setColor("red");
                        return tea;
                    }

                    //如果批量加载有比反复调用load更优的方法则重写这个方法
                    @Override
                    public Map<String, tea> loadAll(Iterable<? extends String> keys) throws Exception {
                        return super.loadAll(keys);
                    }
                });
        tea tea1 = new tea();
        tea1.setWeiht(19);
        tea1.setHomeland("china");
        tea1.setColor("red");
        cache.put("aaa",tea1);
        try {

            tea aa = cache.get("aaa");
            System.out.println(aa.getWeiht());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //除了在build的时候设置没有key的调用方法外我们还能在调用的时候手动写
/*     调用的时候实现get处理
      String key = "bb";
        try {
            cacheObject.get(key, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (key == null)
                        return null;
                    return new tea();
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        //缓存回收
        //除了不能超过大小和设定的时间自动回收外还可以调用方法手动回收
        cache.invalidate("aa");//个别清除
        cache.invalidateAll(Lists.newArrayList("cc","dd"));//批量清除
        cache.invalidateAll();//清除所有缓存项
        //清理的时机：在写操作时顺带做少量的维护工作，或者偶尔在读操作时做——如果写操作实在太少的话
        //如果想自己维护则可以调用Cache.cleanUp();
        cache.cleanUp();
        //另外有时候需要缓存中的数据做出变化重载一次,这个过程可以异步执行
        cache.refresh("aa");
        //还可以调用一下缓存的统计查看缓存的使用情况(需要在构建时开启)
        CacheStats cacheStats = cache.stats();
        cacheStats.hitRate();//缓存命中率
        cacheStats.averageLoadPenalty();//加载新值的平均时间，单位为纳秒
        cacheStats.evictionCount();//缓存项被回收的总数，不包括显式清除
    }


    private static void mapClassTypeAndObject() {
        //键是类型，而值是符合键所指类型的对象
        ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create();
        numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
        System.out.println(numberDefaults);
    }

    private static void table() {
        Table<Integer, Integer, Integer> weightedGraph = HashBasedTable.create();
        weightedGraph.put(1, 2, 4);
        weightedGraph.put(1, 3, 20);
        weightedGraph.put(2, 3, 5);
        //返回某一行的列对应值的map
        System.out.println(weightedGraph.row(1));  // returns a Map mapping 2 to 4, 3 to 20
        //返回某一列的行对应值的map
        System.out.println(weightedGraph.column(3)); // returns a Map mapping 1 to 20, 2 to 5
        TreeRangeMap<Comparable, Object> rangeMap = TreeRangeMap.create();
    }

    private static void mapInverse() {
        BiMap<Integer, String> userId = HashBiMap.create();
        userId.put(1, "tom");
        userId.put(2, "cathy");
        System.out.println(userId);
        BiMap<String, Integer> inverse = userId.inverse();
        System.out.println(inverse);
        Integer tom = userId.inverse().get("tom");
        System.out.println(tom);
    }

    private static void map() {
        tea tea=new tea();
        tea.setColor("asdas");
        tea.setHomeland("asda");
        tea.setWeiht(15);
        tea tea1=new tea();
        tea.setColor("asdas");
        tea.setHomeland("asda");
        tea.setWeiht(15);
        tea tea2=new tea();
        tea.setColor("asdas");
        tea.setHomeland("asda");
        tea.setWeiht(15);
        tea tea3=new tea();
        tea.setColor("asdas");
        tea.setHomeland("asda");
        tea.setWeiht(15);
        ArrayList<com.example.demo.collection.ObjectToMap.tea> teas = new ArrayList<>();
        teas.add(tea);
        teas.add(tea1);
        teas.add(tea2);
        teas.add(tea3);
        Random rand = new Random();
        Integer integer = new Integer(rand.nextInt(250));
        System.out.println(teas);

        ImmutableMultimap<List<com.example.demo.collection.ObjectToMap.tea>, Integer> build =
                ImmutableMultimap.<List<tea>, Integer>builder().put(teas, integer).build();
        ImmutableList.Builder<Object> add = ImmutableList.builder().add(build.keys());
        System.out.println(add);
    }
}
