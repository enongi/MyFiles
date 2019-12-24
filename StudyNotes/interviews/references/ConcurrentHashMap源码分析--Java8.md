## ConcurrentHashMap源码分析--Java8

[zxiaofan](https://yq.aliyun.com/users/jy37bcxxlmvkq) 

​    本文首写于有道云笔记，并在小组分享会分享，先整理发布，希望和大家交流探讨。[云笔记地址](https://yq.aliyun.com/go/articleRenderRedirect?url=http%3A%2F%2Fnote.youdao.com%2Fshare%2F%3Fid%3Ddde7a10b98aee57676408bc475ab0680%26amp%3Btype%3Dnote)



概述：

1、设计首要目的：维护并发可读性（get、迭代相关）；次要目的：使空间消耗比HashMap相同或更好，且支持多线程高效率的初始插入（empty table）。

2、HashTable线程安全，但采用synchronized，多线程下效率低下。线程1put时，线程2无法put或get。

实现原理：

锁分离：

​    在HashMap的基础上，将数据分段存储，ConcurrentHashMap由多个Segment组成，每个Segment都有把锁。Segment下包含很多Node，也就是我们的键值对了。

**如果还停留在锁分离、Segment，那已经out了。**

Segment虽保留，但已经简化属性，仅仅是为了兼容旧版本。

- **CAS算法**；unsafe.compareAndSwapInt(this, valueOffset, expect, update);  CAS(Compare And Swap)，意思是如果valueOffset位置包含的值与expect值相同，则更新valueOffset位置的值为update，并返回true，否则不更新，返回false。
- 与Java8的HashMap有相通之处，底层依然由**“数组”+链表+红黑树**；
- 底层结构存放的是**TreeBin**对象，而不是TreeNode对象；
- CAS作为知名无锁算法，那ConcurrentHashMap就没用锁了么？当然不是，hash值相同的链表的头结点还是会synchronized上锁。 

private static final int MAXIMUM_CAPACITY = 1 << 30; // 2的30次方=1073741824



private static final intDEFAULT_CAPACITY = 16;

static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8; // MAX_VALUE=2^31-1=2147483647

private static finalint DEFAULT_CONCURRENCY_LEVEL = 16;

private static final float LOAD_FACTOR = 0.75f;

static final int TREEIFY_THRESHOLD **= 8;** // 链表转树阀值，大于8时

static final int UNTREEIFY_THRESHOLD **= 6;** //树转链表阀值，小于等于6（tranfer时，lc、hc=0两个计数器分别++记录原bin、新binTreeNode数量，<=UNTREEIFY_THRESHOLD 则untreeify(lo)）。【仅在扩容tranfer时才可能树转链表】

static final int MIN_TREEIFY_CAPACITY = 64;

private static final int MIN_TRANSFER_STRIDE = 16;

private static int RESIZE_STAMP_BITS = 16;

private static final int MAX_RESIZERS **= (1 << (32 -** **RESIZE_STAMP_BITS****)) - 1;** // 2^15-1，help resize的最大线程数

private static final int RESIZE_STAMP_SHIFT **= 32 -** **RESIZE_STAMP_BITS****;** // 32-16=16，sizeCtl中记录size大小的偏移量

static final int MOVED   **= -1;** // hash for forwarding nodes（forwarding nodes的hash值）、标示位

static final int TREEBIN  **= -2;** // hash for roots of trees（树根节点的hash值）

static final int RESERVED **= -3;** // hash for transient reservations（ReservationNode的hash值）

static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

static final int NCPU **= Runtime.getRuntime().availableProcessors();** // 可用处理器数量



 /**

  \* Table initialization and resizing control. When negative, the

  \* table is being initialized or resized: -1 for initialization,

  \* else -(1 + the number of active resizing threads). Otherwise,

  \* when table is null, holds the initial table size to use upon

  \* creation, or 0 for default. After initialization, holds the

  \* next element count value upon which to resize the table.

  */

private transient volatile int sizeCtl;

sizeCtl是控制标识符，不同的值表示不同的意义。



- 负数代表正在进行初始化或扩容操作 
- -1代表正在初始化 
- -N 表示有N-1个线程正在进行扩容操作 
- 正数或0代表hash表还没有被初始化，这个数值表示初始化或下一次进行扩容的大小，类似于扩容阈值。它的值始终是当前ConcurrentHashMap容量的0.75倍，这与loadfactor是对应的。实际容量>=sizeCtl，则扩容。

部分构造函数：

```java
public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
  if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            thrownew IllegalArgumentException();
  if (initialCapacity < concurrencyLevel)   // Use at least as many bins
            initialCapacity = concurrencyLevel;   // as estimated threads
  long size = (long)(1.0 + (long)initialCapacity / loadFactor);
  int cap = (size >= (long)MAXIMUM_CAPACITY) ?
            MAXIMUM_CAPACITY : tableSizeFor((int)size);
  this.sizeCtl = cap;
}
```



**concurrencyLevel：**

​    concurrencyLevel，能够同时更新ConccurentHashMap且不产生锁竞争的最大线程数，在Java8之前实际上就是ConcurrentHashMap中的分段锁个数，即Segment[]的数组长度。正确地估计很重要，当低估，数据结构将根据额外的竞争，从而导致线程试图写入当前锁定的段时阻塞；相反，如果高估了并发级别，你遇到过大的膨胀，由于段的不必要的数量; 这种膨胀可能会导致性能下降，由于高数缓存未命中。

​    在Java8里，仅仅是为了**兼容旧版本而保留**。唯一的作用就是保证构造map时初始容量不小于concurrencyLevel。

源码122行：

Also, for compatibility with previous  versions of this class, constructors may optionally specify an expected {@code concurrencyLevel} as an additional hint for internal sizing. 

源码482行：

 Mainly: We leave untouched but unused constructor arguments refering to concurrencyLevel .……

​    ……

1、重要属性：

1.1 Node：

```java
static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val; // Java8增加volatile，保证可见性
        volatile Node<K,V> next;
 
        Node(int hash, K key, V val, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }
 
        public final K getKey()       { return key; }
        public final V getValue()     { return val; }
        // HashMap调用Objects.hashCode()，最终也是调用Object.hashCode()；效果一样
        public final int hashCode()   { returnkey.hashCode() ^ val.hashCode(); }
        public final String toString(){ returnkey + "=" + val; }
        public final V setValue(V value) { // 不允许修改value值，HashMap允许
            throw new UnsupportedOperationException();
        }
        // HashMap使用if (o == this)，且嵌套if；concurrent使用&&
        public final boolean equals(Object o) {
            Object k, v, u; Map.Entry<?,?> e;
            return ((oinstanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }
 
        /**
         * Virtualized support for map.get(); overridden in subclasses.
         */
        Node<K,V> find(inth, Object k) { // 增加find方法辅助get方法
            Node<K,V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h &&
                        ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        returne;
                } while ((e = e.next) != null);
            }
            returnnull;
        }
    }
```

1.2 TreeNode



```java
// Nodes for use in TreeBins，链表>8，才可能转为TreeNode.
// HashMap的TreeNode继承至LinkedHashMap.Entry；而这里继承至自己实现的Node，将带有next指针，便于treebin访问。
    static final class TreeNode<K,V> extends Node<K,V> { 
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
 
        TreeNode(inthash, K key, V val, Node<K,V> next,
                 TreeNode<K,V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }
 
        Node<K,V> find(inth, Object k) {
            return findTreeNode(h, k, null);
        }
 
        /**
         * Returns the TreeNode (or null if not found) for the given key
         * starting at given root.
         */ // 查找hash为h，key为k的节点
        final TreeNode<K,V> findTreeNode(int h, Object k, Class<?> kc) {
            if (k != null) { // 比HMap增加判空
                TreeNode<K,V> p = this;
                do  {
                    intph, dir; K pk; TreeNode<K,V> q;
                    TreeNode<K,V> pl = p.left, pr = p.right;
                    if ((ph = p.hash) > h)
                        p = pl;
                    elseif (ph < h)
                        p = pr;
                    elseif ((pk = p.key) == k || (pk != null && k.equals(pk)))
                        returnp;
                    elseif (pl == null)
                        p = pr;
                    elseif (pr == null)
                        p = pl;
                    elseif ((kc != null ||
                              (kc = comparableClassFor(k)) != null) &&
                             (dir = compareComparables(kc, k, pk)) != 0)
                        p = (dir < 0) ? pl : pr;
                    elseif ((q = pr.findTreeNode(h, k, kc)) != null)
                        returnq;
                    else
                        p = pl;
                } while (p != null);
            }
            return null;
        }
    }
// 和HashMap相比，这里的TreeNode相当简洁；ConcurrentHashMap链表转树时，并不会直接转，正如注释（Nodes for use in TreeBins）所说，只是把这些节点包装成TreeNode放到TreeBin中，再由TreeBin来转化红黑树。
```



1.3 TreeBin



```
// TreeBin用于封装维护TreeNode，包含putTreeVal、lookRoot、UNlookRoot、remove、balanceInsetion、balanceDeletion等方法，这里只分析其构造函数。
// 当链表转树时，用于封装TreeNode，也就是说，ConcurrentHashMap的红黑树存放的时TreeBin，而不是treeNode。
TreeBin(TreeNode<K,V> b) {
    super(TREEBIN, null, null, null);//hash值为常量TREEBIN=-2,表示roots of trees
    this.first = b;
    TreeNode<K,V> r = null;
    for (TreeNode<K,V> x = b, next; x != null; x = next) {
        next = (TreeNode<K,V>)x.next;
        x.left = x.right = null;
        if (r == null) {
            x.parent = null;
            x.red = false;
            r = x;
        }
        else {
            K k = x.key;
            inth = x.hash;
            Class<?> kc = null;
            for (TreeNode<K,V> p = r;;) {
                intdir, ph;
                K pk = p.key;
                if ((ph = p.hash) > h)
                    dir = -1;
                elseif (ph < h)
                    dir = 1;
                elseif ((kc == null &&
                          (kc = comparableClassFor(k)) == null) ||
                         (dir = compareComparables(kc, k, pk)) == 0)
                    dir = tieBreakOrder(k, pk);
                    TreeNode<K,V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    x.parent = xp;
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    r = balanceInsertion(r, x);
                    break;
                }
            }
        }
    }
    this.root = r;
    assert checkInvariants(root);
}
```





1.4 treeifyBin



```
/**
* Replaces all linked nodes in bin at given index unless table is
* too small, in which case resizes instead.链表转树
*/
private final void treeifyBin(Node<K,V>[] tab, int index) {
        Node<K,V> b; intn, sc;
    if (tab != null) {
        if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
            tryPresize(n << 1); // 容量<64，则table两倍扩容，不转树了
        else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
            synchronized (b) { // 读写锁
                if (tabAt(tab, index) == b) {
                    TreeNode<K,V> hd = null, tl = null;
                    for (Node<K,V> e = b; e != null; e = e.next) {
                        TreeNode<K,V> p =
                            new TreeNode<K,V>(e.hash, e.key, e.val,
                                              null, null);
                        if ((p.prev = tl) == null)
                            hd = p;
                        else
                            tl.next = p;
                        tl = p;
                    }
                    setTabAt(tab, index, new TreeBin<K,V>(hd));
                }
            }
        }
    }
}
```



1.5 ForwardingNode



```
// A node inserted at head of bins during transfer operations.连接两个table
// 并不是我们传统的包含key-value的节点，只是一个标志节点，并且指向nextTable，提供find方法而已。生命周期：仅存活于扩容操作且bin不为null时，一定会出现在每个bin的首位。
static final class ForwardingNode<K,V> extends Node<K,V> {
    final Node<K,V>[] nextTable;
    ForwardingNode(Node<K,V>[] tab) {
        super(MOVED, null, null, null); // 此节点hash=-1，key、value、next均为null
        this.nextTable = tab;
    }
 
    Node<K,V> find(int h, Object k) {
        // 查nextTable节点，outer避免深度递归
        outer: for (Node<K,V>[] tab = nextTable;;) {
            Node<K,V> e; intn;
            if (k == null || tab == null || (n = tab.length) == 0 ||
                (e = tabAt(tab, (n - 1) & h)) == null)
                returnnull;
            for (;;) { // CAS算法多和死循环搭配！直到查到或null
                int eh; K ek;
                if ((eh = e.hash) == h &&
                    ((ek = e.key) == k || (ek != null && k.equals(ek))))
                    returne;
                if (eh < 0) {
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode<K,V>)e).nextTable;
                        continue outer;
                    }
                    else
                        return e.find(h, k);
                }
                if ((e = e.next) == null)
                    return null;
            }
        }
    }
}
```



1.6  3个原子操作（调用频率很高）



```
@SuppressWarnings("unchecked") // ASHIFT等均为private static final
static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) { // 获取索引i处Node
    return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }
    // 利用CAS算法设置i位置上的Node节点（将c和table[i]比较，相同则插入v）。
    static final <K,V> boolean casTabAt(Node<K,V>[] tab, int i,
                                        Node<K,V> c, Node<K,V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }
    // 设置节点位置的值，仅在上锁区被调用
    static final <K,V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v) {
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }
```





1.7 Unsafe

```
//在源码的6277行到最后，有着ConcurrentHashMap中极为重要的几个属性（SIZECTL），unsafe静态块控制其修改行为。Java8中，大量运用CAS进行变量、属性的无锁修改，大大提高性能。
// Unsafe mechanics
private static final sun.misc.Unsafe U;
private static final long SIZECTL;
private static final long TRANSFERINDEX;
private static final long BASECOUNT;
private static final long CELLSBUSY;
private static final long CELLVALUE;
private static final long ABASE;
private static final int ASHIFT;
 
static {
    try {
    U = sun.misc.Unsafe.getUnsafe();
    Class<?> k = ConcurrentHashMap.class;
    SIZECTL = U.objectFieldOffset (k.getDeclaredField("sizeCtl"));
    TRANSFERINDEX=U.objectFieldOffset(k.getDeclaredField("transferIndex"));
    BASECOUNT = U.objectFieldOffset (k.getDeclaredField("baseCount"));
    CELLSBUSY = U.objectFieldOffset (k.getDeclaredField("cellsBusy"));
    Class<?> ck = CounterCell.class;
    CELLVALUE = U.objectFieldOffset (ck.getDeclaredField("value"));
    Class<?> ak = Node[].class;
    ABASE = U.arrayBaseOffset(ak);
    intscale = U.arrayIndexScale(ak);
    if ((scale & (scale - 1)) != 0)
        thrownew Error("data type scale not a power of two");
    ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
    } catch (Exception e) {
    thrownew Error(e);
    }
}
```





1.8 扩容相关

  tryPresize在putAll以及treeifyBin中调用

```
private final void tryPresize(int size) {
        // 给定的容量若>=MAXIMUM_CAPACITY的一半，直接扩容到允许的最大值，否则调用函数扩容
        int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
            tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        while ((sc = sizeCtl) >= 0) { //没有正在初始化或扩容，或者说表还没有被初始化
            Node<K,V>[] tab = table; int n;
           if(tab == null || (n = tab.length) == 0) {
                n = (sc > c) ? sc : c; // 扩容阀值取较大者
         // 期间没有其他线程对表操作，则CAS将SIZECTL状态置为-1，表示正在进行初始化
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (table == tab) {
                            @SuppressWarnings("unchecked")
                            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                            table = nt;
                            sc = n - (n >>> 2); //无符号右移2位，此即0.75*n
                        }
                    } finally {
                        sizeCtl = sc; // 更新扩容阀值
                    }
                }
            }// 若欲扩容值不大于原阀值，或现有容量>=最值，什么都不用做了
            else if (c <= sc || n >= MAXIMUM_CAPACITY)
                break;
            else if (tab == table) { // table不为空，且在此期间其他线程未修改table
                int rs = resizeStamp(n);
                if (sc < 0) {
                    Node<K,V>[] nt;//RESIZE_STAMP_SHIFT=16,MAX_RESIZERS=2^15-1
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                }
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                                             (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
            }
        }
    }
private static final int tableSizeFor(int c){//和HashMap一样,返回>=n的最小2的自然数幂
  int n = c - 1;
  n |= n >>> 1;
  n |= n >>> 2;
  n |= n >>> 4;
  n |= n >>> 8;
  n |= n >>> 16;
  return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
/**
* Returns the stamp bits for resizing a table of size n.
* Must be negative when shifted left by RESIZE_STAMP_SHIFT.
*/
static final int resizeStamp(int n) { // 返回一个标志位
    return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
}// numberOfLeadingZeros返回n对应32位二进制数左侧0的个数，如9（1001）返回28
// RESIZE_STAMP_BITS=16,(左侧0的个数)|(2^15)
```



**ConcurrentHashMap无锁多线程扩容，减少扩容时的时间消耗。**

**transfer扩容操作****：**单线程构建两倍容量的nextTable；允许多线程复制原table元素到nextTable。

1. 为每个内核均分任务，并保证其不小于16；
2. 若nextTab为null，则初始化其为原table的2倍；
3. 死循环遍历，直到finishing。

- 节点为空，则插入ForwardingNode；
- 链表节点（fh>=0），分别插入nextTable的i和i+n的位置；【逆序链表？？】
- TreeBin节点（fh<0），判断是否需要untreefi，分别插入nextTable的i和i+n的位置；【逆序树？？】
- finishing时，nextTab赋给table，更新sizeCtl为新容量的0.75倍 ，完成扩容。

**以上说的都是单线程，多线程又是如何实现的呢？**

​    遍历到ForwardingNode节点((fh = f.hash) == MOVED)，说明此节点被处理过了，直接跳过。这是控制并发扩容的核心 。由于给节点上了锁，只允许当前线程完成此节点的操作，处理完毕后，将对应值设为ForwardingNode（fwd），其他线程看到forward，直接向后遍历。如此便完成了多线程的复制工作，也解决了线程安全问题。

private transient volatile Node<K,V>[] nextTable; //仅仅在扩容使用，并且此时非空

```
// 将table每一个bin（桶位）的Node移动或复制到nextTable
// 只在addCount(long x, int check)、helpTransfer、tryPresize中调用
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
    int n = tab.length, stride; 
    // 每核处理的量小于16，则强制赋值16
    if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE; // subdivide range
    if (nextTab == null) {      // initiating
        try {
            @SuppressWarnings("unchecked")
            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1]; //两倍
            nextTab = nt;
        } catch (Throwable ex) {   // try to cope with OOME
            sizeCtl = Integer.MAX_VALUE;
            return;
        }
        nextTable = nextTab;
        transferIndex = n;
    }
    int nextn = nextTab.length;
    //连节点指针,标志位，fwd的hash值为-1，fwd.nextTable=nextTab。
    ForwardingNode<K,V> fwd= new ForwardingNode<K,V>(nextTab);
    boolean advance= true;//并发扩容的关键属性,等于true,说明此节点已经处理过
    boolean finishing = false; // to ensure sweep before committing nextTab
    for (int i = 0, bound = 0;;) { // 死循环
        Node<K,V> f; int fh;
        while (advance) { // 控制--i，遍历原hash表中的节点
            int nextIndex, nextBound;
            if (--i >= bound || finishing)
                advance = false;
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
           }//TRANSFERINDEX 即用CAS计算得到的transferIndex
            else if (U.compareAndSwapInt
                     (this, TRANSFERINDEX, nextIndex,
                      nextBound = (nextIndex > stride ?
                                   nextIndex - stride : 0))) {
                bound = nextBound;
                i = nextIndex - 1;
                advance = false;
            }
        }
        if (i < 0 || i >= n || i + n >= nextn) {
            int sc;
            if (finishing) { // 所有节点复制完毕
                nextTable = null;
                table = nextTab;
                sizeCtl = (n << 1) - (n >>> 1); //扩容阀值设为原来的1.5倍，即现在的0.75倍
                return; // 仅有的2个跳出死循环出口之一
            }//CAS更新扩容阈值,sc-1表明新加入一个线程参与扩容
            if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                    return;// 仅有的2个跳出死循环出口之一
                finishing = advance = true;
                i = n; // recheck before commit
            }
        }
       else if ((f = tabAt(tab, i)) == null) //该节点为空，则插入ForwardingNode
            advance = casTabAt(tab, i, null, fwd);
        //遍历到ForwardingNode节点，说明此节点被处理过了，直接跳过。这是控制并发扩容的核心 
       else if ((fh = f.hash) == MOVED) // MOVED=-1，hash for fwd
            advance = true; // already processed
       else {
            synchronized (f) { //上锁
                if (tabAt(tab, i) == f) {
                    Node<K,V> ln, hn; //ln原位置节点，hn新位置节点
                    if (fh >= 0) { // 链表
                        int runBit = fh & n; // f.hash & n
                        Node<K,V> lastRun = f; // lastRun和p两个链表，逆序？？
                        for (Node<K,V> p = f.next; p != null; p = p.next) {
                            int b = p.hash & n; // f.next.hash & n
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        }
                        else {
                            hn = lastRun;
                            ln = null;
                        }
                        for (Node<K,V> p = f; p != lastRun; p = p.next) {
                            int ph = p.hash; K pk = p.key; V pv = p.val;
                            if ((ph & n) == 0) // 和HashMap确定扩容后的节点位置一样
                                ln = new Node<K,V>(ph, pk, pv, ln);
                            else
                                hn = new Node<K,V>(ph, pk, pv, hn); //新位置节点
                        }//类似HashMap，为何i+n？参见HashMap的笔记
                        setTabAt(nextTab, i, ln);//在nextTable[i]插入原节点
                        setTabAt(nextTab, i + n, hn);//在nextTable[i+n]插入新节点
                        //在nextTable[i]插入forwardNode节点，表示已经处理过该节点 
                        setTabAt(tab, i, fwd);
                        //设置advance为true 返回到上面的while循环中 就可以执行--i操作
                        advance = true;
                    }
                    else if (f instanceof TreeBin) { //树
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> lo = null, loTail = null;
                        TreeNode<K,V> hi = null, hiTail = null;
                        //lc、hc=0两计数器分别++记录原、新bin中TreeNode数量
                        int lc = 0, hc = 0;
                        for (Node<K,V> e = t.first; e != null; e = e.next) {
                            int h = e.hash;
                            TreeNode<K,V> p = new TreeNode<K,V>
                                (h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                if ((p.prev = loTail) == null)
                                    lo = p;
                                else
                                    loTail.next = p;
                                loTail = p;
                                ++lc;
                            }
                            else {
                                if ((p.prev = hiTail) == null)
                                    hi = p;
                                else
                                    hiTail.next = p;
                                hiTail = p;
                                ++hc;
                            }
                        }//扩容后树节点个数若<=6，将树转链表
                        ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                            (hc != 0) ? new TreeBin<K,V>(lo) : t;
                        hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                            (lc != 0) ? new TreeBin<K,V>(hi) : t;
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }
}
// 协助扩容方法。多线程下，当前线程检测到其他线程正进行扩容操作，则协助其一起扩容；（只有这种情况会被调用）从某种程度上说，其“优先级”很高，只要检测到扩容，就会放下其他工作，先扩容。
// 调用之前，nextTable一定已存在。
final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
    Node<K,V>[] nextTab; intsc;
    if (tab != null && (finstanceof ForwardingNode) &&
        (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
        intrs = resizeStamp(tab.length); //标志位
        while (nextTab == nextTable && table == tab &&
               (sc = sizeCtl) < 0) {
            if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                sc == rs + MAX_RESIZERS || transferIndex <= 0)
                break;
            if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                transfer(tab, nextTab);//调用扩容方法，直接进入复制阶段
                break;
            }
        }
        return nextTab;
    }
    return table;
}
```


2、 put相关：

理一下put的流程：

①**判空**：null直接抛空指针异常；

②**hash**：计算h=key.hashcode；调用spread计算hash=(h ^(h >>>16))& HASH_BITS；

③**遍历table**

- 若table为空，则初始化，仅设置相关参数；
- @@@计算当前key存放位置，即table的下标i=(n - 1) & hash；
- 若待存放位置为null，casTabAt无锁插入；
- 若是forwarding nodes（检测到正在扩容），则helpTransfer（帮助其扩容）；
- else（待插入位置非空且不是forward节点，即碰撞了），将头节点上锁（保证了线程安全）：区分链表节点和树节点，分别插入（遇到hash值与key值都与新节点一致的情况，只需要更新value值即可。否则依次向后遍历，直到链表尾插入这个结点）；
- 若链表长度>8，则treeifyBin转树（Note：若length<64,直接tryPresize,两倍table.length;不转树）。

④**addCount(1L, binCount)。**

**Note：**

1、put操作共计两次hash操作，再利用“与&”操作计算Node的存放位置。

2、ConcurrentHashMap不允许key或value为null。

3、**addCount(longx,intcheck)方法：**

  ①利用CAS快速更新baseCount的值；

  ②check>=0.则检验是否需要扩容；if sizeCtl<0（正在进行初始化或扩容操作）【nexttable null等情况break；如果有线程正在扩容，则协助扩容】；else if 仅当前线程在扩容，调用协助扩容函数，注其参数nextTable为null。



public V put(K key, V value) {

​    return putVal(key, value, false);

}



```
final V <span style="background-color: rgb(255, 255, 51);">putVal</span>(K key, V value, boolean onlyIfAbsent) {
    // 不允许key、value为空
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode()); //返回(h^(h>>>16))&HASH_BITS
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) { // 死循环，直到插入成功
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable(); // table为空，初始化table
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {// 索引处无值
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                break;  // no lock when adding to empty bin
        }
        else if ((fh = f.hash) == MOVED) // MOVED=-1;//hash for forwarding nodes
            tab = helpTransfer(tab, f); //检测到正在扩容，则帮助其扩容
        else {
            V oldVal = null;
            synchronized (f) { // 节点上锁（hash值相同的链表的头节点）
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) { // 链表节点
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;// hash和key相同，则修改value
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||(ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent) //仅putIfAbsent()方法中onlyIfAbsent为true
                                    e.val = value; //putIfAbsent()包含key则返回get，否则put并返回
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) { //已遍历到链表尾部，直接插入
                                pred.next = new Node<K,V>(hash, key, value, null);
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) { // 树节点
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            if (binCount != 0) {
                if (binCount >= TREEIFY_THRESHOLD)//实则是>8,执行else,说明该桶位本就有Node
                    treeifyBin(tab, i);//若length<64,直接tryPresize,两倍table.length;不转树
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    addCount(1L, binCount);
    return null;
}
```



```
// Initializes table, using the size recorded in sizeCtl.
private final Node<K,V>[] <span style="background-color: rgb(255, 255, 51);">initTable</span>() { // 仅仅设置参数，并未实质初始化
    Node<K,V>[] tab; intsc;
    while ((tab = table) == null || tab.length == 0) {
        if ((sc = sizeCtl) < 0) // 其他线程正在初始化，此线程挂起
            Thread.yield(); // lost initialization race; just spin
        //CAS方法把sizectl置为-1，表示本线程正在进行初始化
        elseif (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            try {
                if ((tab = table) == null || tab.length == 0) {
                   intn = (sc > 0) ? sc : DEFAULT_CAPACITY;//DEFAULT_CAPACITY=16
                    @SuppressWarnings("unchecked")
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    table = tab = nt;
                    sc = n - (n >>> 2); // 扩容阀值，0.75*n
                }
            } finally {
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```

3、 get、contains相关



```
public V <span style="background-color: rgb(255, 255, 51);">get</span>(Object key) {
     Node<K,V>[] tab; Node<K,V> e, p; intn, eh; K ek;
     inth = spread(key.hashCode()); 
     if ((tab = table) != null && (n = tab.length) > 0 &&
         (e = tabAt(tab, (n - 1) & h)) != null) {//tabAt(i),获取索引i处Node
         if ((eh = e.hash) == h) {
             if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                 returne.val;
         }
         elseif (eh < 0) // 树
             return (p = e.find(h, key)) != null ? p.val : null;
         while ((e = e.next) != null) { // 链表
             if (e.hash == h &&
                 ((ek = e.key) == key || (ek != null && key.equals(ek))))
                 returne.val;
         }
     }
     return null;
}
public boolean containsKey(Object key) {return get(key) != null;}
public boolean containsValue(Object value) {}
```

理一下get的流程：

①spread计算hash值；

②table不为空；

③tabAt(i)处桶位不为空；

④check first，是则返回当前Node的value；否则分别根据树、链表查询。

4、 Size相关：

​    由于ConcurrentHashMap在统计size时可能正被多个线程操作，而我们又不可能让他停下来让我们计算，所以只能计量一个估计值。

计数辅助：

| // Table of counter cells. When non-null, size is a power of 2private transient volatile CounterCell[] counterCells; |
| ------------------------------------------------------------ |
| @sun.misc.Contended static final class CounterCell {  volatile long value;  CounterCell(long x) { value = x; }} |
| final long sumCount(){  CounterCell as[] = counterCells;  long sum = baseCount;  if(as != null){    for(int i = 0; i < as.length; i++){     CounterCell a;      if((a = as[i]) != null)       sum += a.value;    }  }  return sum;} |
| private final void fullAddCount(long x, boolean wasUncontended) {} |

| public int size() { // 旧版本方法，和推荐的mappingCount返回的值基本无区别  longn = sumCount();  return ((n < 0L) ? 0 :    (n > (long)Integer.MAX_VALUE) ? Integer.MAX_VALUE :    (int)n);} |
| ------------------------------------------------------------ |
| // 返回Mappings中的元素个数，官方建议用来替代size。此方法返回的是一个估计值；如果sumCount时有线程插入或删除，实际数量是和mappingCount不同的。since 1.8public long mappingCount() {  longn = sumCount();  return (n < 0L) ? 0L : n; // ignore transient negative values} |
| private transient volatile long baseCount; //ConcurrentHashMap中元素个数,基于CAS无锁更新,但返回的不一定是当前Map的真实元素个数。 |

5、remove、clear相关：



```
public void clear() { // 移除所有元素
    long delta = 0L; // negative number of deletions
    inti = 0;
    Node<K,V>[] tab = table;
    while (tab != null && i < tab.length) {
       intfh;
       Node<K,V> f = tabAt(tab, i);
       if (f == null) // 为空，直接跳过
           ++i;
       else if ((fh = f.hash) == MOVED) { //检测到其他线程正对其扩容
//则协助其扩容，然后重置计数器，重新挨个删除元素，避免删除了元素，其他线程又新增元素。
           tab = helpTransfer(tab, f);
           i = 0; // restart
       }
       else{
           synchronized (f) { // 上锁
               if (tabAt(tab, i) == f) { // 其他线程没有在此期间操作f
                  Node<K,V> p = (fh >= 0 ? f :
                               (finstanceof TreeBin) ?
                               ((TreeBin<K,V>)f).first : null);
                   while (p != null) { // 首先删除链、树的末尾元素，避免产生大量垃圾
                       --delta;
                       p = p.next;
                   }
                   setTabAt(tab, i++, null); // 利用CAS无锁置null
               }
           }
       }
    }
    if (delta != 0L)
       addCount(delta, -1); // 无实际意义，参数check<=1，直接return。
}
public V remove(Object key) { // key为null，将在计算hashCode时报空指针异常
    return replaceNode(key, null, null);
}
public boolean remove(Object key, Object value) {
    if (key == null)
        thrownew NullPointerException();
    returnvalue != null && replaceNode(key, null, value) != null;
}
```



```
// remove核心方法，注意，这里的cv才是key-value中的value！
final V replaceNode(Object key, V value, Object cv) {
    inthash = spread(key.hashCode());
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; intn, i, fh;
        if (tab == null || (n = tab.length) == 0 ||
            (f = tabAt(tab, i = (n - 1) & hash)) == null)
            break; // 该桶位第一个元素为空，直接跳过
        elseif ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f); // 先协助扩容再说
        else {
            V oldVal = null;
            booleanvalidated = false;
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        validated = true;
                       //pred没看出来有什么用，全是别人赋值给他，他却不影响其他参数
                        for (Node<K,V> e = f, pred = null;;) { 
                            K ek;
                            if (e.hash == hash &&((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))){//hash且可以相等
                                V ev = e.val;
                               // value为null或value和查到的值相等
                                if (cv == null || cv == ev ||
                                      (ev != null && cv.equals(ev))) {
                                    oldVal = ev;
                                    if (value != null) // replace中调用
                                        e.val = value;
                                    elseif (pred != null)
                                        pred.next = e.next;
                                    else
                                        setTabAt(tab, i, e.next);
                                }
                                break;
                            }
                            pred = e;
                            if ((e = e.next) == null)
                                break;
                        }
                    }
                    elseif (finstanceof TreeBin) { // 以树的方式find、remove
                        validated = true;
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> r, p;
                        if ((r = t.root) != null &&
                            (p = r.findTreeNode(hash, key, null)) != null) {
                            V pv = p.val;
                            if (cv == null || cv == pv ||
                                (pv != null && cv.equals(pv))) {
                                oldVal = pv;
                                if (value != null)
                                    p.val = value;
                                elseif (t.removeTreeNode(p))
                                    setTabAt(tab, i, untreeify(t.first));
                            }
                        }
                    }
                }
            }
            if (validated) {
                if (oldVal != null) {
                    if (value == null)
                        addCount(-1L, -1);
                    returnoldVal;
                }
                break;
            }
        }
    }
    return null;
}
public boolean replace(K key, V oldValue, V newValue) {}
```

6、其他函数：

public boolean isEmpty() {

  return sumCount() <= 0L; // ignore transient negative values

}