## Java8新特性

java7和java8方法区与堆的说明：

java7之前:
		方法区位于永久代(PermGen)，永久代和堆相互隔离，永久代的大小在启动JVM时可以设置一个固定值，不可变； 

java7中:
		存储在永久代的部分数据就已经转移到Java Heap或者Native memory。

但永久代仍存在于JDK 1.7中，并没有完全移除，譬如符号引用(Symbols)转移到了native memory；

字符串常量池(interned strings)转移到了Java heap；

类的静态变量(class statics)转移到了Java heap。 

java8中：
		取消永久代，方法存放于元空间(Metaspace)，元空间仍然与堆不相连，但与堆共享物理内存，

逻辑上可认为在堆中 。

**Native memory：**
		本地内存，也称为C-Heap，是供JVM自身进程使用的。当Java Heap空间不足时会触发GC，

但Native memory空间不够却不会触发GC。