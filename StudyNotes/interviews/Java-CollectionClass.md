## Java集合类

### ArrayList 和 LinkedList 的区别

1、ArrayList底层使用数组存储元素，因此在查询的时候速度快，直接返回该位置元素即可，时间复杂度为O(1)；而LinkedList底层使用的是双向链表
存储元素，在查询时需要从头或者尾遍历至查询元素，时间复杂度为O(n/2)
2、由于存储方式的不同，ArrayList插入或者删除元素之后，需要移动插入或者删除位置之后的所有元素，速度比较慢，时间复杂度为O(n),而LinkedList
只需要移动‘指针’(更改前驱和后驱节点指向地址)即可，时间复杂度为O(1)
3、综上，ArrayList适用于较多查询，获取某位置的元素的场景，LinkedList适用于频繁插入删除，较少查询的场景
ArrayList使用数组存储元素，每次扩容后容量为之前容量的1.5倍，而且扩容后会有一个复制所有元素的操作，会比较费时间；
由于LinkedList使用双向链表存储数据，因此不需要扩容机制
考虑到空间LinkedList使用空间大于ArrayList 因为ArrayList每个位置只存储元素本身，LinkedList存储了元素本身和前一节点及后一节点的地址

### List Set Map 的区别

List和Set是Collection的子接口，Map不是Collection的子接口
List：可以允许重复元素出现，可以插入多个null元素，是一个有序容器，保持了每个元素的插入顺序，输出顺序同插入顺序
	常见的实现类有ArrayList、LinkedList、Vector，ArrayList提供了使用索引随意访问，底层使用数组，优查询，劣增删，LinkedList常用于
	多新增和删除场景，底层使用双向链表
Set：不允许重复元素，无序容器，只允许有一个null元素，Set常见的实现类有HashSet，LinkedHashSet以及TreeSet。最流行的是基于HashMap实现的HashSet
	(HashSet就是一个没有Value的HashMap)，TreeSet还实现了SortedSet接口，因此TreeSet是根据其compare()和compareTo()方法排序的有序容器
Map：Map不是Collection的子接口，Map每个Entry都持有两个对象，一个键一个值，可以持有相同的值对象，但是键对象必须要唯一
	常见的实现类有HashMap，LinkedHashMap，HashTable，TreeMap

### HashMap、LinkedHashMap和TreeMap之间的区别

HashMap 线程不安全，数据无序，数据结构是 数组+链表+红黑树（在JDK8中如果链表长度大于8的时候链表转换为红黑树）
LinkedHashMap 线程不安全，数据有序，数据结构为双向链表+HashMap
TreeMap 线程不安全，数据有序且可以对数据进行排序，数据结构为红黑树
HashMap是最常用的Map，它根据hashCode值存储数据，根据键可以直接获取值，具有很快的访问速度。HashMap只允许一条键为null的记录，
允许多条值为null的记录，HashMap不支持线程同步，即任意时刻可以有多个线程同时操作同一个HashMap，可能会导致数据不一致，如需要同步可以使用
Collections.synchronizedMap(HashMap map)方法使HashMap具有同步能力
LinkedHashMap保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先插入的数据会先得到，遍历时会比HashMap慢，有HashMap的全部特性
TreeMap能够把它保存的记录根据键排序，默认是按升序排序，也可以指定排序的比较器。当用Iteraor遍历TreeMap时，得到的记录是排过序的。
TreeMap的键和值都不能为空。它的get或put操作的时间复杂度是O(log(n))

### HashMap和Hashtable的区别

1、继承的父类不同，HashMap和Hashtable都实现了Map接口，但是HashMap继承了AbstractMap类，而Hashtable继承了Dictionary类
2、线程的安全性不同，HashMap是线程不安全的，Hashtable是线程安全的，方法都是同步的
3、是否提供contains() 方法，HashMap删除contains() 方法，由containsValue()和containsKey()方法代替
	Hashtable保留了contains()方法，同时有containsValue()和containsKey()方法，其中contains()方法和containsValue()方法功能相同
4、key和value是否允许null值
	HashMap中，null可以作为键，这样的键只有一个；可以有一个或多个键所对应的值为null。当get()方法返回null值时，
	可能是 HashMap中没有该键，也可能使该键所对应的值为null。因此，在HashMap中不能由get()方法来判断HashMap中是否存在某个键，
	而应该用containsKey()方法来判断。
	Hashtable中，key和value都不允许出现null值。但是如果在Hashtable中有类似put(null,null)的操作，编译同样可以通过，
	因为key和value都是Object类型，但运行时会抛出NullPointerException异常，这是JDK的规范规定的
5、两个遍历方式的内部实现上不同
    Hashtable、HashMap都使用了 Iterator。而由于历史原因，Hashtable还另外使用了Enumeration的方式 。
6、hash值不同
      哈希值的使用不同，HashTable直接使用对象的hashCode。而HashMap重新计算hash值。
      hashCode是jdk根据对象的地址或者字符串或者数字算出来的int类型的数值。
      Hashtable计算hash值，直接用key的hashCode()，而HashMap重新计算了key的hash值，Hashtable在求hash值对应的位置索引时，用取模运算，
	  而HashMap在求位置索引时，则用与运算，且这里一般先用hash&0x7FFFFFFF后，再对length取模，&0x7FFFFFFF的目的是为了将负的hash值
	  转化为正值，因为hash值有可能为负数，而&0x7FFFFFFF后，只有符号外改变，而后面的位都不变
7、内部实现使用的数组初始化和扩容方式不同
      Hashtable在不指定容量的情况下的默认容量为11，而HashMap为16，Hashtable不要求底层数组的容量一定要为2的整数次幂，而HashMap则要求一定为2的整数次幂。
      Hashtable扩容时，将容量变为原来的2倍加1，而HashMap扩容时，将容量变为原来的2倍。
      Hashtable和HashMap它们两个内部实现方式的数组的初始大小和扩容的方式。HashTable中hash数组默认大小是11，增加的方式是 old*2+1。

### ConcurrentHashMap

JDK5之后提供了ConcurrentHashMap来替代HashTable。ConcurrentHashMap是对HashTable的优化。ConcurrentHashMap(采用分段锁，线程安全)

1. 底层实现采用 分段数组+链表
2. 默认将整个Map分成16片（Segment，可调参数）
3. Hashtable进行修改操作时，要锁住整张表，以保证数据安全；而ConcurrentHashMap只需锁住要修改数据所在的那一分片（锁分离技术）
4. 有时候某些操作需要涉及到多个分片，如获取Map的size、查找某个值是否存在，此时只需要按顺序将每一片锁住，操作完毕后，再按顺序将锁释放
5. ConcurrentHashMap是段内插入前扩容，即插入前检查当前有效存储数组的hash值个数是否超过负载因子（默认为总容量的75%，可调参数），
   若超过，进行段内扩容

